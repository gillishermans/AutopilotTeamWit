package autopilotLibrary;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.opencv.core.Point;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;

public class Besturing {

	private float thrust = 0.00f;
	private float leftWingInclination = 0.0f;
	private float rightWingInclination = 0.0f;
	private float horStabInclination = 0.0f;
	private float verStabInclination = 0.0f;
	private float totalMass;
	private AutopilotConfig config;
	private Beeldherkenning beeldherkenning;
	private ArrayList<Vector> posList = new ArrayList<Vector>(); 
	private float rechtdoorHoek;
	private float lastLoopTime = 0;
	
	private ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	private float maxAOA = (float) Math.PI/12; 
	
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	
	private float time = 0;
	
	private PIDController pidVelY = new PIDController(10f,10f,0f,(float) Math.PI/6f, (float)- Math.PI/6f, 20);
	private PIDController pidVer = new PIDController(10f,10,0f,(float) Math.PI/6f, (float)- Math.PI/6f, 1);
	private PIDController pidRoll = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6 ,20);
	private PIDController pidHor = new PIDController(1f,1,0f,(float) Math.PI/6, (float)- Math.PI/6, 2);
	private PIDController pidRoll2 = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6, 2);
	private PIDController pidPitch = new PIDController(4f,1f,0f,(float) Math.PI/6f, (float) -Math.PI/6f, 1);
//	private PIDController pidVer1 = new PIDController(1,1,0,(float) Math.PI/6, (float) -Math.PI/6, 2);
//	private PIDController pidX = new PIDController(0.1f,1,0,(float) Math.PI/6, (float) -Math.PI/6, 1);
	private PIDController pidHeading = new PIDController(1.0f,1f,0f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	private PIDController pidTrust = new PIDController(5f,5,0,2000, 0, 0.1f);
	
//	private FileWriter fw;
//	private BufferedWriter bw;
	private boolean first = true;
	
	
	private boolean resetHeading = false;
	private boolean resetStabilization = false;
	
	public Besturing(AutopilotConfig config) {
		this.config = config;
		this.beeldherkenning = new Beeldherkenning(config);
		this.totalMass = config.getEngineMass() + config.getTailMass() + (2* config.getWingMass());
//		try {
//			fw = new FileWriter("outputroll.txt");
//			bw = new BufferedWriter(fw);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs) {
		setTime(inputs);
	
	//BEELDHERKENNING -------------------------------------------------------------------------	
		//Run beeldherkenning en bereken de afstand en hoeken
		beeldherkenning.imageRecognition(inputs.getImage());
		
		ArrayList<Point> centerArray = beeldherkenning.getCenterArray();
		ArrayList<Float> radiusArray = beeldherkenning.getRadiusArray();
		//ArrayList<double[]> colorArray = beeldherkenning.getColorArray();
		
		float horizontalAngle = 0;	
		float verticalAngle = 0;
		
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
		
		//System.out.println(speedVector.y);
		//Geen kubus gevonden -> vlieg rechtdoor
		if(centerArray.isEmpty()){
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
			float vel = 0;
			float goal = 3;
			float outputVelY = 0;
			if (getTime() == 0) {
				outputVelY = (float) (Math.PI/6);
			}
			else {
				vel = (lastY-inputs.getY())/getTime();
				//Rechtdoor vliegen
				outputVelY = -pidVelY.getOutput(-goal,vel, getTime());
				while (Math.abs(getAngleOfAttack(speedVector,outputVelY)) > maxAOA) {
					outputVelY = 9 * outputVelY / 10;
					System.out.println("max AoA");
				}
			}
			
			lastY = inputs.getY();
			
			rightWingInclination = outputVelY;
			leftWingInclination = outputVelY;
			
			
			//horStabInclination = outputVelY*10;
			
			float aoa = (float) (getAngleOfAttack(speedVector,rightWingInclination) * 180 / Math.PI);
			
			//System.out.println(inputs.getElapsedTime());
			
			verStabInclination = 0;
			float outputRoll = 0;
			horStabInclination = 0;
			if (inputs.getElapsedTime() > 0.5) {
				if (first) {
					System.out.println("Pitch");
					first = false;
				}
				float outputPitch = pidPitch.getOutput((float) Math.PI/60, inputs.getPitch(), getTime());
				while (Math.abs(getAngleOfAttack(speedVector,outputPitch)) > maxAOA) {
					System.out.println("Max AOA Hor");
					outputPitch = 9 * outputPitch / 10;
				}
				horStabInclination = -outputPitch;
				//rightWingInclination = outputPitch;
				//leftWingInclination = outputPitch;
			}
			
			
//			if (inputs.getElapsedTime() !=0 ) {
////				
//				outputRoll = pidRoll.getOutput(0,inputs.getRoll(), getTime());
//				rightWingInclination = rightWingInclination + outputRoll;
//				leftWingInclination= leftWingInclination - outputRoll;
//				//System.out.println("Roll: " + inputs.getRoll() + " outputRoll: " + outputRoll);
//			}
						
			lastX = inputs.getZ();
			
			
			
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
			System.out.println(outputVer + " " + Math.PI/6);
			if (Math.abs(outputVer) > Math.PI/2) {
				System.out.println("Te grote Error");
				if (outputVer > 0) outputVer = (float) (Math.PI/2);
				else outputVer = (float) (-Math.PI/2);
			}
	

		
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
		
		//Speed hangt af van massa
		float reqSpeed = totalMass * 17.142f ;
		thrust = pidTrust.getOutput(65f, speed, getTime());
		
		//System.out.println(thrust);

		
		//In de eerste stap geven we standaard waarden door
		if (getTime() == 0) {
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			thrust = 80f;
			horStabInclination = 0f;
			verStabInclination = 0f;
		}
		
		System.out.println(thrust);
		//thrust = 7000;
		//horStabInclination = (float) (Math.PI/20);
		//System.out.println("thr " + thrust + "left " + leftWingInclination + "right " + rightWingInclination + "hor " + horStabInclination + "ver " + verStabInclination);
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
		
	}
	
	public void setTime(AutopilotInputs inputs) {
		double time1 = inputs.getElapsedTime();
		float elapTime = (float)(time1 - lastLoopTime);
		lastLoopTime = (float) time1;
		this.time = elapTime;
		
	}
	
	public float getTime() {
		return this.time;
	}
	
	public float getAngleOfAttack(Vector speed, float incl) {
		
		Vector vector = new Vector(0,0,0);
		
		Vector normal = vector.crossProd(this.getAxisVector(),this.getAttackVector(incl));
		Vector airspeed = speed;
		Vector axis = this.getAxisVector();
//		
		Vector projectedAirspeed = airspeed;
//		
		float angleOfAttack = (float) -Math.atan2(vector.scalairProd(projectedAirspeed,normal), 
				vector.scalairProd(projectedAirspeed,this.getAttackVector(incl)));
		
		return angleOfAttack;
	}
	
	public Vector getAxisVector() {
		return new Vector(1,0,0);
	}
	
	public Vector getAttackVector(float incl) {
		return new Vector((float) Math.sin(0),
				          (float) Math.sin(incl),
				          (float)-Math.cos(incl));
	}
	
	
}
