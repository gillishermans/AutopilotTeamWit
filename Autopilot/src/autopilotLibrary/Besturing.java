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
	
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	
	private float time = 0;
	
	private PIDController pidHor = new PIDController(5f,5,0f,(float) Math.PI/6, (float)- Math.PI/6);
	private PIDController pidRoll = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidRoll2 = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidPitch = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidVer = new PIDController(1,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidX = new PIDController(0.1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidHeading = new PIDController(1.0f,1f,0f,(float) Math.PI/8, (float) -Math.PI/8);
	private PIDController pidTrust = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	
	private FileWriter fw;
	private BufferedWriter bw;
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
		
		//Geen kubus gevonden -> vlieg rechtdoor
		if(centerArray.isEmpty()){
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
			float vel = 0;
			float goal = 0;
			float outputHor = 0;
			if (getTime() == 0) {
				outputHor = (float) (Math.PI/6);
			}
			else {
				vel = (lastY-inputs.getY())/getTime();
				//Rechtdoor vliegen
				if (goal == 0) {
					outputHor = -pidHor.getOutput(goal,vel, getTime())/20;
				}
			}
			
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
			verStabInclination = 0;
			float outputRoll = 0;
		
			
			if (inputs.getElapsedTime() !=0 ) {
//				
				outputRoll = pidRoll.getOutput(0,inputs.getRoll(), getTime())/20;
				rightWingInclination = rightWingInclination + outputRoll;
				leftWingInclination= leftWingInclination - outputRoll;
				//System.out.println("Roll: " + inputs.getRoll() + " outputRoll: " + outputRoll);
			}
						
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
			float outputHor = 0;
			vel = (lastY-inputs.getY())/getTime();
				
			outputHor = pidVer.getOutput(0, verticalAngle, getTime());
			if (Math.abs(outputHor) > Math.PI/2) {
				System.out.println("Te grote Error");
				if (outputHor > 0) outputHor = (float) (Math.PI/2);
				else outputHor = (float) (-Math.PI/2);
			}
	

		
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
			
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
				float outputRoll = pidRoll.getOutput(0,inputs.getRoll(), getTime())/2;
				rightWingInclination = rightWingInclination + outputRoll;
				leftWingInclination= leftWingInclination - outputRoll;
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
					pidRoll.reset();
					resetStabilization = false;
					System.out.println("Reset Stab");
				}
				if (Math.abs(inputs.getRoll()) < Math.abs(maxRoll)) {
					float outputAngle = pidHeading.getOutput(0, horizontalAngle, getTime())/10;
					rightWingInclination = rightWingInclination + outputAngle;
					leftWingInclination = leftWingInclination - outputAngle;
				}
				else { 
					//System.out.println("Roll te groot");
					float goal;
					if (inputs.getRoll() > 0)  goal = (float) maxRoll;
					else                       goal = -(float) maxRoll;
					float outputRoll = pidRoll2.getOutput(goal, inputs.getRoll(), getTime())/2;
					rightWingInclination = rightWingInclination + outputRoll;
					leftWingInclination= leftWingInclination - outputRoll;	
				}
				resetHeading = true;
			}
		}
		
		//Speed hangt af van massa
		float reqSpeed = totalMass * 17.142f ;
		thrust=pidTrust.getOutput(reqSpeed, speed, getTime());

		
		//In de eerste stap geven we standaard waarden door
		if (getTime() == 0) {
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			thrust = 80f;
			horStabInclination = 0f;
			verStabInclination = 0f;
		}
		
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
	
	
}
