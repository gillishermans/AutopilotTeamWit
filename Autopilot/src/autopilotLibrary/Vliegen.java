package autopilotLibrary;

import java.util.ArrayList;

import org.opencv.core.Point;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import interfaces.Path;

public class Vliegen {
	
	private Path path;
	private Besturing besturing;
	private Beeldherkenning beeldherkenning;
	private AOAController aoaController = new AOAController();
	
	private ArrayList<Point> centerArray;
	private ArrayList<Float> radiusArray;
	private ArrayList<Vector> posList = new ArrayList<Vector>();
	
	public ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	private float rightWingInclination,leftWingInclination,thrust,horStabInclination,verStabInclination;
	private float leftBrakeForce,rightBrakeForce,frontBrakeForce;
	
	private PIDController pidVelY = new PIDController(1f,1f,0f,(float) Math.PI/10f, (float)- Math.PI/10f, 20);
	private PIDController pidPitch = new PIDController(4f,0.5f,0.5f,(float) Math.PI/6f, (float) -Math.PI/6f, 1);
//	private PIDController pidX = new PIDController(0.1f,1,0,(float) Math.PI/6, (float) -Math.PI/6, 1);
	private PIDController pidTrust = new PIDController(5f,5,1,2000, 0, 0.1f);
	
	//PID's VLIEGEN NAAR POSITIE
	private PIDController pidRoll = new PIDController(4f,1,1.5f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidStab = new PIDController(3,1,1,(float) Math.PI/6, -(float) Math.PI/6,20);
	private PIDController pidHeading = new PIDController(0.5f,1f,0.5f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	//PID's VLIEGEN OBV BEELDHERKENNING
	private PIDController pidVerImage = new PIDController(1f,1,0f,(float) Math.PI/6f, (float)- Math.PI/6f, 1);
	private PIDController pidHorImage = new PIDController(1f,1,0f,(float) Math.PI/6, (float)- Math.PI/6, 1);
	private PIDController pidRollImage = new PIDController(1f,1,1.5f,(float) Math.PI/6, (float) -Math.PI/6, 1);
	private PIDController pidHeadingImage = new PIDController(0.5f,1f,0.5f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	
	private boolean resetHeading = false;
	private boolean resetStabilization = false;
	
	private float lastLoopTime = 0;
	private float time = 0;
	
	private float lastInclRight = 0;
	private float lastInclHor = 0;
	
	private Phase phase = Phase.INIT;
	private enum Phase {
		INIT,RIJDEN,OPSTIJGEN,STABILISEREN,KUBUS,GEENKUBUS,LANDEN,REMMEN,ROLL,POSITIE
	}
	
	private boolean first = true;
	
	private int k = 5;
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	private float outputVelY = 0;
	private float goalYspeed=0;
	
	private boolean landen = false;
	private float timeLanden = 0;
	
	private boolean pos = true;
	private boolean left = false;
	private boolean forward = true;
	
	private float x;
	private float y;
	private float z;
	private int index = 0;
	
	private float interval = 90;
	
	
	public Vliegen(Besturing besturing) {
		this.besturing = besturing;
		//this.beeldherkenning = beeldherkenning;
	}
	
	public void setBeeldherkenning(Beeldherkenning beeldherkenning) {
		this.beeldherkenning = beeldherkenning;
	}
	
	public void setPath(Path path) {
		this.path = path;
	}
	
	public AutopilotOutputs vliegen(AutopilotInputs inputs) {
		setTime(inputs);
		float horizontalAngle = 0;	
		float verticalAngle = 0;
		beeldherkenning.imageRecognition(inputs.getImage());
		
		centerArray = beeldherkenning.getCenterArray();
		radiusArray = beeldherkenning.getRadiusArray();
		//ArrayList<double[]> colorArray = beeldherkenning.getColorArray();
		
		int j = 0;
		
		getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
		//time += inputs.getTime();
		
		int index = getPosList().size() -1;
		Vector speedVector = new Vector(0,0,-10);
		float speed = 10f;
		if(getPosList().size() <= 1) {speedVector = new Vector(0,0,0); speed = 10f;}
		else{
			speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
			speedVector = Vector.scalarProd(speedVector, 1/getTime());
			speed = Vector.norm(speedVector);
		}
		//Geen kubus gevonden -> vlieg rechtdoor
		if(centerArray.isEmpty() || inputs.getZ() > -1750) {
			if (phase == Phase.KUBUS) {
				pidVelY.reset();
				pidPitch.reset();
				phase = Phase.GEENKUBUS;
				System.out.println("GEEN KUBUS");
			}
			k = 3;
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
			float vel = 0;
			
			if (getTime() == 0) {
				outputVelY = (float) (0);
			}
			else {
				vel = (lastY-inputs.getY())/getTime();
				//Rechtdoor vliegen
				//outputVelY = -pidVelY.getOutput(goalYspeed,vel, getTime());
				//System.out.println("AoA: " + getAngleOfAttack(speedVector,outputVelY)*360/(2*Math.PI));
//				while (Math.abs(getAngleOfAttack(speedVector,outputVelY)) >= maxAOA) {
//					outputVelY = (outputVelY +  2 *lastInclRight) / 3;
//					System.out.println("Vleugel "+ j + ": " + getAngleOfAttack(speedVector,outputVelY)*360/(2*Math.PI) + " " + outputVelY*360/(2*Math.PI));
//					j++;
//				}
			}
			
			
			lastInclRight = outputVelY;
			lastY = inputs.getY();
			
			rightWingInclination = outputVelY;
			leftWingInclination = outputVelY;
			
			
			
			
			
			/////////////////////////////////////////////////////////////////////
			
			//Naar file schrijven om makkelijker te analyseren
//			float velY = 0;
//			if (inputs.getElapsedTime() != 0) {
//				velY = (lastY-inputs.getY())/getTime();
//			}
//			this.lastY = inputs.getY();
//			//FileWriter fw;
//			try {
//				//fw = new FileWriter("outputZ.txt");
//				//BufferedWriter bw = new BufferedWriter(fw);
//				if (first && inputs.getElapsedTime() >= 10) {
//					//System.out.println(vel);
//					float tijd = inputs.getElapsedTime() -10;
//					bw.append(Float.toString(inputs.getRoll()) + " " + tijd + "\n");
//					bw.newLine();
//					if (inputs.getElapsedTime() > 20) {
//						bw.close();
//						fw.close();
//						first = false;
//						System.out.println("File Closed");
//					}
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}

	}
		
		//Kubus in zicht
		else {
			k = 3;
			//System.out.println("Kubus is in zicht");
			if (phase == Phase.POSITIE) {
				phase = Phase.KUBUS;
				System.out.println("KUBUS");
			}
			//Zoek dichtsbijzijnde kubus
			ArrayList<Float> distanceArray = new ArrayList<Float>();
			float shortest = beeldherkenning.distanceToObject(centerArray.get(0),radiusArray.get(0));
			int shortestI = 0;
			for(int i =0;i < centerArray.size();i++){
				float distance = beeldherkenning.distanceToObject(centerArray.get(i),radiusArray.get(i));
				distanceArray.add(distance);
				if(distance < shortest){
					shortest = distance;
					shortestI = i;
				}
			}
			
			//Beweeg naar dichtstbijzijnde kubus
			horizontalAngle = beeldherkenning.horizontalAngle(centerArray.get(shortestI));	
			verticalAngle = beeldherkenning.verticalAngle(centerArray.get(shortestI));
			//System.out.println(horizontalAngle);

//------VERTICAAL---------------------------------------------------------------------------------------------------			
			
			float outputVer = 0;

			
			outputVer = pidVerImage.getOutput(0, verticalAngle, getTime());
			//System.out.println(outputVer);
			outputVer = aoaController.aoaController(outputVer, (float) Math.PI/20);
//			System.out.println(speedVector.x + ", " + speedVector.y + ", " + speedVector.z);
//			while (Math.abs(getAngleOfAttack(speedVector,outputVer)) >= maxAOA) {
//				outputVer = (outputVer +  2 *lastInclRight) / 3;
//				System.out.println("Vleugel "+ j + ": " + getAngleOfAttack(speedVector,outputVer)*360/(2*Math.PI) + " " + outputVer*360/(2*Math.PI));
//			}
//			
//			lastInclRight = outputVer;
	

		
			lastY = inputs.getY();
			
			rightWingInclination = outputVer;
			leftWingInclination = outputVer;
			
//------HORIZONTAAL--------------------------------------------------------------------------------------------------
			
			//float maxRoll = (float) (Math.PI/8)*(1-inputs.getPitch());
			//float maxRoll = (float) (Math.PI/15);
			float maxRoll = (float) (Math.PI/4);
			float outputHor = 0;
			if((Math.abs(horizontalAngle) < Math.abs(Math.PI/90))) {
				if (resetHeading) {
					pidHeadingImage.reset();
					pidRollImage.reset();
					resetHeading = false;
					System.out.println("Reset Heading");
				}
				outputHor = pidHorImage.getOutput(0,inputs.getRoll(), getTime());
				resetStabilization = true;
//				float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime())/20;
//				horStabInclination = outputPitch;
			}
//			if (Math.abs(horizontalAngle) < Math.abs(Math.PI/90)) {
//				float outputAngle = pidHeading.getOutput(0, horizontalAngle, getTime())/20;
//				if(Math.abs(inputs.getRoll()) > Math.abs(Math.PI/6)) outputAngle = 0;
//				if(horizontalAngle > 0) verStabInclination = verStabInclination - outputAngle ;
//				else verStabInclination = verStabInclination + outputAngle;
//			}
			else {
				if (resetStabilization) {
					pidHorImage.reset();
					resetStabilization = false;
					System.out.println("Reset Stab");
				}
				if (Math.abs(inputs.getRoll()) < Math.abs(maxRoll)) {
					outputHor = pidHeadingImage.getOutput(0, horizontalAngle, getTime());
				}
				else { 
					//System.out.println("Roll te groot");
					float goal;
					if (inputs.getRoll() > 0)  goal = (float) maxRoll;
					else                       goal = -(float) maxRoll;
					outputHor = pidRollImage.getOutput(goal, inputs.getRoll(), getTime());	
				}
				resetHeading = true;
			}
			outputHor = aoaController.aoaRollController(-outputVer, outputHor, (float) Math.PI / 20);
			leftWingInclination = outputVer - outputHor;
			rightWingInclination = outputVer + outputHor;
			float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
			horStabInclination = -outputPitch;
		}
		//float reqSpeed = totalMass * 17.142f ;
		
		
		frontBrakeForce = 0;
		rightBrakeForce = 0;
		leftBrakeForce = 0;
		
		//System.out.println(thrust);
		if (getTime() == 0) phase = Phase.INIT;
		
		
		switch(phase) {
		case INIT: //eerste stap, standaard waarden doorgeven
			System.out.println("INIT");
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			thrust = 80f;
			horStabInclination = 0f;
			verStabInclination = 0f;
			phase = Phase.RIJDEN;
			//System.out.println("HEADING: " + toDegrees(calculateHeading(inputs)));
			System.out.println("RIJDEN");
			break;
		case RIJDEN:
			thrust = 2000;
			leftWingInclination = (float) - Math.PI/60;
			rightWingInclination = leftWingInclination;
			horStabInclination = 0;
			verStabInclination = 0;
			if (speedVector.z < -40) { 
				phase = Phase.OPSTIJGEN;
				System.out.println("OPSTIJGEN");
			}
			break;
		case OPSTIJGEN:
			thrust = 2000;
			leftWingInclination = (float) Math.PI/20;
			rightWingInclination = leftWingInclination;
			float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
			horStabInclination = -outputPitch;
			verStabInclination = 0;
			if (inputs.getY() > 40) {
				System.out.println("STABILISEREN");
				phase = Phase.STABILISEREN;
				pidPitch.reset();
			}
			break;
		case STABILISEREN:
			thrust = pidTrust.getOutput(65f, speed, getTime());
			float outputVelY = pidVelY.getOutput(0,speedVector.y, getTime());
			//System.out.print(toDegrees(outputVelY) + " ");
			outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
			//System.out.println(toDegrees(outputVelY) + " " + getTime());
			leftWingInclination = outputVelY;
			rightWingInclination = outputVelY;
			float outputPitch1 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch1 = aoaController.aoaController(outputPitch1, (float) Math.PI/20);
			horStabInclination = -outputPitch1;
			//System.out.println("Pitch: " + horStabInclination*360/(2*Math.PI));
			verStabInclination = 0f;
			if (inputs.getZ() < -1000) {
				System.out.println("POSITIE");
				//System.out.println(inputs.getZ());
				phase = Phase.POSITIE;
				setNextPos();
			}
			break;
		case LANDEN:
			thrust = pidTrust.getOutput(65f, speed, getTime());
			pidVelY.reset();
			pidPitch.reset();
			float outputVelY1 = -pidVelY.getOutput(-1f,speedVector.y,getTime());
			outputVelY1 = aoaController.aoaController(outputVelY1, (float) Math.PI/20);
			leftWingInclination = -outputVelY1;
			rightWingInclination = -outputVelY1;
			float outputPitch2 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch2 = aoaController.aoaController(outputPitch2, (float) Math.PI/20);
			horStabInclination = -outputPitch2;
			verStabInclination = 0;
			if (inputs.getY() < 1.5f) {
				System.out.println("REMMEN");
				phase = Phase.REMMEN;
			}
			break;
		case ROLL:
			thrust = pidTrust.getOutput(65f, speed, getTime());
			pidPitch.reset();
			outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
			outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
			float outputRoll = pidRoll.getOutput((float) (Math.PI/36), inputs.getRoll(), getTime());
			leftWingInclination = -outputVelY - outputRoll;
			rightWingInclination = -outputVelY + outputRoll;
			System.out.println("left: " + leftWingInclination + " right: " + rightWingInclination);
			outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
			horStabInclination = -outputPitch;
			verStabInclination = 0;
			if (inputs.getElapsedTime() > 40) {
				System.out.println("ROLL STABILISATIE");
				phase = Phase.GEENKUBUS;
				first = true;
			}
			break;
		case POSITIE:
			if (distance(new Vector(inputs.getX(), inputs.getY(), inputs.getZ()), new Vector(x,y,z)) < 0.5) {
				setNextPos();
				pos = true;
			}
			if (pos) {
				if (z < inputs.getZ()) forward = true;
				else				   forward = false;
				if (x > inputs.getX()) left = false;
				else                   left = true;
				pos = false;
				System.out.println(forward);
			}
			float goal = (float) Math.PI/10;
			float maxRoll = (float) Math.PI/8;
			thrust = pidTrust.getOutput(65,speed,getTime());
			float heading = calculateHeading(inputs);
			//System.out.println(toDegrees(heading));
			//heading = (float) (Math.PI/9.5);
			//System.out.println("HEADING: " + toDegrees(inputs.getHeading()) + " Required: " + toDegrees(heading));
			//System.out.println("HEADING: " + toDegrees(heading));
			outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
			outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
			//System.out.println("MIN: " + toDegrees(heading - inputs.getHeading()));
			if (Math.abs(heading - inputs.getHeading()) < (float) Math.PI/interval) {
				//System.out.println("DEGREE: " + toDegrees((float) (Math.PI/20 - inputs.getHeading())));
				if (interval < 360) {
					interval = interval + 1;
					//System.out.println(interval);
				}
//				if (Math.abs(inputs.getRoll()) > maxRoll) {
//					System.out.print("  Yoooowwww");
//					if (inputs.getRoll() > 0) outputRoll = pidRoll.getOutput(maxRoll, inputs.getRoll(), getTime());
//					else                      outputRoll = pidRoll.getOutput(-maxRoll, inputs.getRoll(), getTime());
//				}
				outputRoll = pidStab.getOutput(0,inputs.getRoll(),getTime());
//				if (inputs.getRoll() > 0) System.out.print("GROTER ");
//				else System.out.print("KLEINER ");
//				System.out.println(toDegrees(inputs.getRoll()));
				if (first) {
					first = false;
					System.out.println("Stab");
				}
			} else {
				if (!first) {
					first = true;
					//System.out.println("Verder");
					pidHeading.reset();
				}
				if (Math.abs(inputs.getRoll()) > maxRoll) {
					if (inputs.getRoll() > 0) outputRoll = pidRoll.getOutput(maxRoll, inputs.getRoll(), getTime());
					else                      outputRoll = pidRoll.getOutput(-maxRoll, inputs.getRoll(), getTime());
				} else {
					if (inputs.getHeading() - heading < 0) {
						//System.out.println("Erover");
						outputRoll = pidHeading.getOutput(heading, inputs.getHeading(), getTime());
					}
					else {
						//System.out.println("Eronder");
						outputRoll = pidHeading.getOutput(heading, inputs.getHeading(), getTime());
					}
				}
			}
			//System.out.println(toDegrees(heading - inputs.getHeading()));
			outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, (float) Math.PI / 20);
			leftWingInclination = -outputVelY - outputRoll;
			rightWingInclination = -outputVelY + outputRoll;
			//System.out.println("Incl: " + toDegrees(leftWingInclination) + " outputVel: " + toDegrees(-outputVelY) + " Roll: "  + toDegrees(outputRoll));
			//System.out.println(inputs.getHeading()*360/(2*Math.PI));
			//System.out.println("left: " + leftWingInclination + " right: " + rightWingInclination);
			outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
			horStabInclination = -outputPitch;
			verStabInclination = 0;
			//System.out.println(leftWingInclination + " " + rightWingInclination + " " + horStabInclination + " " + thrust);
			break;
		case KUBUS:
			thrust = pidTrust.getOutput(65f, speed, getTime());
			if (inputs.getZ() < 2000) phase = Phase.KUBUS;
			//System.out.println(leftWingInclination + " " + rightWingInclination + " " + horStabInclination + " " + thrust);
			break;
		case GEENKUBUS:
			if (first) {
				pidRoll.reset();
				//System.out.println("RESET");
				first = false;
			}
			//System.out.println(leftWingInclination + " " + rightWingInclination + " " + horStabInclination + " " + thrust);
			thrust = pidTrust.getOutput(65f, speed, getTime());
			outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
			outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
			outputRoll = pidRoll.getOutput(0, inputs.getRoll(), getTime());
			outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, (float) Math.PI/20);
			leftWingInclination = -outputVelY - outputRoll;
			rightWingInclination = -outputVelY + outputRoll;
			//System.out.println("ROLL: " + outputRoll + "INCL: " + -outputVelY + "INCL: " + leftWingInclination + "INCR: " + rightWingInclination);
			outputPitch1 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch1 = aoaController.aoaController(outputPitch1, (float) Math.PI/25);
			horStabInclination = -outputPitch1;
			verStabInclination = 0f;
			if (!landen) {
				timeLanden = inputs.getElapsedTime();
				landen = true;
			}
			if (inputs.getElapsedTime() - timeLanden > 3f) {
				System.out.println("LANDEN");
				phase = Phase.LANDEN;
			}
			break;
		case REMMEN:
			thrust = 0;
			leftWingInclination = - (float) Math.PI/60;
			rightWingInclination = leftWingInclination;
			horStabInclination = 0;
			verStabInclination = 0;
			frontBrakeForce = 1600;
			rightBrakeForce = 1600;
			leftBrakeForce = 1600;
		}
	
	
		//System.out.println("thr " + thrust + "left " + leftWingInclination + "right " + rightWingInclination + "hor " + horStabInclination + "ver " + verStabInclination);
		//System.out.println("HorSTAB " + k + ": " + getAngleOfAttack(speedVector,rightWingInclination)*360/(2*Math.PI));
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	
		
	}
	
	public void setNextPos() {
		if (index < path.getX().length) {
			this.x = path.getX()[index];
			this.y = path.getY()[index];
			this.z = path.getZ()[index];
			this.index = this.index + 1;
			System.out.println("VOLGENDE KUBUS OP: " + x + " " + y + " " + z + " " + index);
		}
		else phase = Phase.GEENKUBUS;
	}
	
	public float distance(Vector v1, Vector v2) {
		float x1 = (float) (Math.pow(v1.x - v2.x, 2));
		float y1 = (float) (Math.pow(v1.y - v2.y, 2));
		float z1 = (float) (Math.pow(v1.z - v2.z, 2));
		//System.out.println(x1 + " " + y1 + " " + z1);
		float total = Math.abs(x1 + y1 + z1);
		//System.out.println(total);
		return (float) Math.sqrt(total);
	}
	
	public float calculateHeading(AutopilotInputs inputs) {
		float currX = inputs.getX();
		float currZ = inputs.getZ();
		//System.out.println(currZ);
		float b = -z + currZ;
		float c = -x + currX;
		float a = (float) Math.sqrt((b*b) + (c*c));
		float cos = ((a*a) + (b*b) - (c*c)) / (2*a*b);
		//System.out.println(cos);
		if (cos > 1) cos = -(1-cos);
		//System.out.println("A: " + a + "B: " + b + "C: " + c);
		//System.out.println("Heading: " + toDegrees(inputs.getHeading()) + " X: " + currX + " Z: " + currZ + " A: " + a + " B: " + b + " C: " + c + " " + cos + " " +toDegrees((float) Math.acos(cos)) + " " + toDegrees(inputs.getHeading() - (float) Math.acos(cos)));
		if (left) return (float) (Math.acos(cos));
		else      return -(float) Math.acos(cos);
	}
	
	public float toDegrees(float r) {
		return (float) (r*360/(2*Math.PI));
	}
	
	public void setTime(AutopilotInputs inputs) {
		double time1 = inputs.getElapsedTime();
		float elapTime = (float)(time1 - lastLoopTime);
		lastLoopTime = (float) time1;
		this.time = elapTime;
	}
	
	public float getTime() {
		return time;
	}
	
}