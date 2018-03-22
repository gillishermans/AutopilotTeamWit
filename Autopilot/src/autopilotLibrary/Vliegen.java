package autopilotLibrary;

import java.util.ArrayList;

import org.opencv.core.Point;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;

public class Vliegen {
	
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
	private PIDController pidVer = new PIDController(1f,1,0f,(float) Math.PI/6f, (float)- Math.PI/6f, 1);
	private PIDController pidRoll = new PIDController(4f,1,1.5f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidStab = new PIDController(3,1,1,(float) Math.PI/6, -(float) Math.PI/6,20);
	private PIDController pidHor = new PIDController(1f,1,0f,(float) Math.PI/6, (float)- Math.PI/6, 2);
	private PIDController pidRoll2 = new PIDController(1f,1,1.5f,(float) Math.PI/6, (float) -Math.PI/6, 2);
	private PIDController pidPitch = new PIDController(4f,0.5f,0.5f,(float) Math.PI/6f, (float) -Math.PI/6f, 1);
//	private PIDController pidX = new PIDController(0.1f,1,0,(float) Math.PI/6, (float) -Math.PI/6, 1);
	private PIDController pidHeading = new PIDController(0.5f,1f,0.5f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	private PIDController pidTrust = new PIDController(5f,5,1,2000, 0, 0.1f);
	
	private boolean resetHeading = false;
	private boolean resetStabilization = false;
	
	private float lastLoopTime = 0;
	private float time = 0;
	
	private float lastInclRight = 0;
	private float lastInclHor = 0;
	
	private Phase phase = Phase.INIT;
	private enum Phase {
		INIT,RIJDEN,OPSTIJGEN,STABILISEREN,KUBUS,GEENKUBUS,LANDEN,REMMEN,ROLL
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
	
	private float x = -50;
	private float y = 40;
	private float z = -2000;
	
	
	public Vliegen(Besturing besturing, Beeldherkenning beeldherkenning) {
		this.besturing = besturing;
		this.beeldherkenning = beeldherkenning;
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
		if(centerArray.isEmpty()) {
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
			
			
			//horStabInclination = outputVelY*10;
			
			//float aoa = (float) (getAngleOfAttack(speedVector,rightWingInclination) * 180 / Math.PI);
			
			//System.out.println(inputs.getElapsedTime());
			
//			verStabInclination = 0;
//			float outputRoll = 0;
//			horStabInclination = 0;
//			float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
//			float aoa = aoaController.getAngleOfAttack(speedVector,Math.abs(outputPitch));
			//System.out.println("AOA: "+ aoa*360/(2*Math.PI) + " " + -outputPitch*360/(2*Math.PI));
//			while (Math.abs(aoa) >= maxAOA) {
//				System.out.println("HorStab: " + -outputPitch*360/(2*Math.PI) + " " + getAngleOfAttack(speedVector,-outputPitch)*360/(2*Math.PI));
//				outputPitch = (2 * lastInclHor + outputPitch) /3;
//				System.out.println("HorStab1 " + j + ": " + -outputPitch*360/(2*Math.PI)+ " " + getAngleOfAttack(speedVector,-outputPitch)*360/(2*Math.PI));
//				aoa = getAngleOfAttack(speedVector,Math.abs(outputPitch));
//				j++;
//			}
//			horStabInclination = -outputPitch;
//			//System.out.println(-outputPitch*360/(2*Math.PI)  +" "+ getAngleOfAttack(speedVector,-outputPitch)*360/(2*Math.PI));
//				
//			lastInclHor = horStabInclination;
//			//rightWingInclination = outputPitch;
//			//leftWingInclination = outputPitch;
	
			
			
//			if (inputs.getElapsedTime() !=0 ) {
////				
//				outputRoll = pidRoll.getOutput(0,inputs.getRoll(), getTime());
//				rightWingInclination = rightWingInclination + outputRoll;
//				leftWingInclination= leftWingInclination - outputRoll;
//				//System.out.println("Roll: " + inputs.getRoll() + " outputRoll: " + outputRoll);
//			}
						
			//lastX = inputs.getZ();
			
			
			
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
		else{
			k = 3;
			//System.out.println("Kubus is in zicht");
			
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

//------VERTICAAL---------------------------------------------------------------------------------------------------			
			
			float vel = 0;
			//float goal = 3;
			float outputVer = 0;
			vel = (lastY-inputs.getY())/getTime();
			
			outputVer = pidVer.getOutput(0, verticalAngle, getTime());
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
			if((Math.abs(horizontalAngle) < Math.abs(Math.PI/90))) {
				if (resetHeading) {
					pidHeading.reset();
					pidRoll2.reset();
					resetHeading = false;
					System.out.println("Reset Heading");
				}
				float outputHor = pidHor.getOutput(0,inputs.getRoll(), getTime());
				rightWingInclination = rightWingInclination + outputHor;
				leftWingInclination= leftWingInclination - outputHor;
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
					pidHor.reset();
					resetStabilization = false;
					System.out.println("Reset Stab");
				}
				if (Math.abs(inputs.getRoll()) < Math.abs(maxRoll)) {
					float outputAngle = pidHeading.getOutput(0, horizontalAngle, getTime());
					rightWingInclination = rightWingInclination + outputAngle;
					leftWingInclination = leftWingInclination - outputAngle;
				}
				else { 
					//System.out.println("Roll te groot");
					float goal;
					if (inputs.getRoll() > 0)  goal = (float) maxRoll;
					else                       goal = -(float) maxRoll;
					float outputRoll = pidRoll2.getOutput(goal, inputs.getRoll(), getTime());
					rightWingInclination = rightWingInclination + outputRoll;
					leftWingInclination= leftWingInclination - outputRoll;	
				}
				resetHeading = true;
			}
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
			System.out.println(thrust);
			if (thrust > 2000) {
				thrust = 2000;
			}
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
			if (inputs.getZ() < -30000) {
				System.out.println("KUBUS");
				//System.out.println(inputs.getZ());
				phase = Phase.KUBUS;
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
		case KUBUS:
			float goal = (float) Math.PI/10;
			float maxRoll = (float) Math.PI/8;
			thrust = pidTrust.getOutput(65,speed,getTime());
			float heading = calculateHeading(inputs);
			heading = (float) (Math.PI/9);
			System.out.println("HEADING: " + toDegrees(inputs.getHeading()) + " Required: " + toDegrees(heading));
			//System.out.println("HEADING: " + toDegrees(heading));
			outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
			outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
			//System.out.println("MIN: " + toDegrees(heading - inputs.getHeading()));
			if (Math.abs(heading - Math.abs(inputs.getHeading())) < (float) Math.PI/360) {
				//System.out.println("DEGREE: " + toDegrees((float) (Math.PI/20 - inputs.getHeading())));
				outputRoll = pidStab.getOutput(0,inputs.getRoll(),getTime());
//				if (inputs.getRoll() > 0) System.out.print("GROTER ");
//				else System.out.print("KLEINER ");
//				System.out.println(toDegrees(inputs.getRoll()));
				if (first) {
					first = false;
					System.out.println("Stab");
				}
			} else {
				first = true;
				if (inputs.getRoll() > maxRoll) {
					outputRoll = pidRoll.getOutput(0, inputs.getRoll(), getTime());
				} else {
					if (inputs.getHeading() - heading < 0) {
						System.out.println("Erover");
						outputRoll = pidHeading.getOutput(heading, inputs.getHeading(), getTime());
					}
					else {
						System.out.println("Eronder");
						outputRoll = -pidHeading.getOutput(heading, inputs.getHeading(), getTime());
					}
				}
			}
			outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, (float) Math.PI / 20);
			leftWingInclination = -outputVelY - outputRoll;
			rightWingInclination = -outputVelY + outputRoll;
			//System.out.println(inputs.getHeading()*360/(2*Math.PI));
			//System.out.println("left: " + leftWingInclination + " right: " + rightWingInclination);
			outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
			horStabInclination = -outputPitch;
			verStabInclination = 0;
			break;
		case GEENKUBUS:
			if (first) {
				pidRoll.reset();
				//System.out.println("RESET");
				first = false;
			}
			thrust = pidTrust.getOutput(65f, speed, getTime());
			outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
			outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
			outputRoll = pidRoll.getOutput(0, inputs.getRoll(), getTime());
			System.out.println("ROLL: " + outputRoll);
			outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, (float) Math.PI/20);
			leftWingInclination = -outputVelY - outputRoll;
			rightWingInclination = -outputVelY + outputRoll;
			System.out.println("ROLL: " + outputRoll + "INCL: " + -outputVelY + "INCL: " + leftWingInclination + "INCR: " + rightWingInclination);
			outputPitch1 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
			outputPitch1 = aoaController.aoaController(outputPitch1, (float) Math.PI/25);
			horStabInclination = -outputPitch1;
			verStabInclination = 0f;
			if (!landen) {
				timeLanden = inputs.getElapsedTime();
				landen = true;
			}
			if (inputs.getElapsedTime() - timeLanden > 5000000f) {
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
	
	public float calculateHeading(AutopilotInputs inputs) {
		float currX = inputs.getX();
		float currZ = inputs.getZ();
		float b = -z -currZ;
		float c = -x - currX;
		float a = (float) Math.sqrt((b*b) + (c*c));
		float cos = ((a*a) + (b*b) - (c*c)) / (2*a*b);
		if (cos > 1) cos = -(1-cos);
		//System.out.println("A: " + a + "B: " + b + "C: " + c);
		//System.out.println("Heading: " + toDegrees(inputs.getHeading()) + " X: " + currX + " Z: " + currZ + " A: " + a + " B: " + b + " C: " + c + " " + cos + " " +toDegrees((float) Math.acos(cos)) + " " + toDegrees(inputs.getHeading() - (float) Math.acos(cos)));
		return (float) Math.acos(cos);
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
