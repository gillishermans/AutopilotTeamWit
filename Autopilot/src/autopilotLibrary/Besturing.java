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
	
	private ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	
	private float time = 0;
	
	private PIDController pidHor = new PIDController(5f,5,0f,(float) Math.PI/6, (float)- Math.PI/6);
	private PIDController pidRoll = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidVer = new PIDController(1,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidX = new PIDController(0.1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	private PIDController pidHeading = new PIDController(1.0f,1f,0f,(float) Math.PI/8, (float) -Math.PI/8);
	private PIDController pidTrust = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
	
	private FileWriter fw;
	private BufferedWriter bw;
	private boolean first = true;
	
	public Besturing(AutopilotConfig config) {
		this.config = config;
		this.beeldherkenning = new Beeldherkenning(config);
		this.totalMass = config.getEngineMass() + config.getTailMass() + (2* config.getWingMass());
//		try {
//			fw = new FileWriter("outputZ.txt");
//			bw = new BufferedWriter(fw);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs) {
		
	
	//BEELDHERKENNING -------------------------------------------------------------------------	
		//Run beeldherkenning en bereken de afstand en hoeken
		beeldherkenning.imageRecognition(inputs.getImage());
		
		ArrayList<Point> centerArray = beeldherkenning.getCenterArray();
		ArrayList<Float> radiusArray = beeldherkenning.getRadiusArray();
		//ArrayList<double[]> colorArray = beeldherkenning.getColorArray();
		
		//Geen kubus gevonden -> vlieg rechtdoor
		
		float horizontalAngle = 0;	
		float verticalAngle = 0;
				
		if(centerArray.isEmpty()){
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
			float vel = 0;
			float goal = 0;
			float outputHor = 0;
			if (inputs.getElapsedTime() == 0) {
				outputHor = (float) (Math.PI/6);
			}
			else {
				vel = (lastY-inputs.getY())/inputs.getElapsedTime();
				//Rechtdoor vliegen
				if (goal == 0) {
					outputHor = -pidHor.getOutput(goal,vel, inputs.getElapsedTime())/20;
				}
			}
			
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
			verStabInclination = 0;
			float outputRoll = 0;
			
			if (inputs.getElapsedTime() != 0 ) {
				outputRoll = pidRoll.getOutput(0,inputs.getRoll(), inputs.getElapsedTime())/10;
				rightWingInclination = rightWingInclination + outputRoll;
				leftWingInclination= leftWingInclination - outputRoll;
				System.out.println("Roll: " + inputs.getRoll() + " outputRoll: " + outputRoll);
			}
						
			lastX = inputs.getZ();
			
			
			
			/////////////////////////////////////////////////////////////////////
			
			//Naar file schrijven om makkelijker te analyseren
//			float velY = 0;
//			if (inputs.getElapsedTime() != 0) {
//				velY = (lastY-inputs.getY())/inputs.getElapsedTime();
//			}
//			this.lastY = inputs.getY();
//			//FileWriter fw;
//			try {
//				//fw = new FileWriter("outputZ.txt");
//				//BufferedWriter bw = new BufferedWriter(fw);
//				if (first) {
//					//System.out.println(velY);
//					bw.append(Float.toString(velY) + "\n");
//					bw.newLine();
//					if (time > 7) {
//						bw.close();
//						fw.close();
//						first = false;
//						//System.out.println("File Closed");
//					}
//				}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			/////////////////////////////////////////////////////////////////////
			getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
			time += inputs.getElapsedTime();
			
			
			int index = getPosList().size() -1;
			Vector speedVector = null;
			float speed = 0.0f;
			if(getPosList().size() <= 1) {speedVector = new Vector(0,0,0); speed = 10f;}
			else{
				speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
				speedVector = Vector.scalarProd(speedVector, 1/inputs.getElapsedTime());
				speed = Vector.norm(speedVector);
			}
			
			
			//Thrust & return
			
			//thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2))+15;
			if (inputs.getElapsedTime()==0) thrust = 80f;
			else thrust=pidTrust.getOutput(15, speed, inputs.getElapsedTime());
			
			//System.out.println("Thrust: " + thrust);
			System.out.println("thr " + thrust + "left " + leftWingInclination + "right " + rightWingInclination + "hor " + horStabInclination + "ver " + verStabInclination);
			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
			
			
	}
		
		//Kubus in zicht
		else{
			System.out.println("Kubus is in zicht");
			getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
			time += inputs.getElapsedTime();
			
			int index = getPosList().size() -1;
			Vector speedVector = new Vector(0,0,-10);
			float speed = 10f;
			if(getPosList().size() <= 1) {speedVector = new Vector(0,0,0); speed = 10f;}
			else{
				speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
				speedVector = Vector.scalarProd(speedVector, 1/inputs.getElapsedTime());
				speed = Vector.norm(speedVector);
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
			
			
			float vel = 0;
			//float goal = 3;
			float outputHor = 0;
			vel = (lastY-inputs.getY())/inputs.getElapsedTime();
				
			outputHor = pidVer.getOutput(0, verticalAngle, inputs.getElapsedTime());
			if (Math.abs(outputHor) > Math.PI/4) {
				System.out.println("Te grote Error");
				if (outputHor > 0) outputHor = (float) (Math.PI/4);
				else outputHor = (float) (-Math.PI/4);
			}

		
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
			
			if((Math.abs(horizontalAngle) < Math.abs(Math.PI/90))){ 
				float outputRoll = pidRoll.getOutput(0,inputs.getRoll(), inputs.getElapsedTime())/2;
				rightWingInclination = rightWingInclination + outputRoll;
				leftWingInclination= leftWingInclination - outputRoll;
				System.out.println("Roll: " + inputs.getRoll() + " outputRoll: " + outputRoll);
				
				float reqSpeed = totalMass * 17.142f ;
				thrust=pidTrust.getOutput(reqSpeed, speed, inputs.getElapsedTime());
				System.out.println("reqSpeed: " + reqSpeed);
				System.out.println("Speed: " + speed);
				//System.out.println("Thrust1: " + speed);
				if (inputs.getElapsedTime() == 0) thrust = 80;
				//thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2))+15;
				return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
			
			}
			
			float outputAngle = 0;
			
			//System.out.print(horizontalAngle);

			if (Math.abs(horizontalAngle) > Math.abs(Math.PI/90)) {
				outputAngle = pidHeading.getOutput(0, horizontalAngle, inputs.getElapsedTime())/10;
				rightWingInclination = rightWingInclination + outputAngle;
				leftWingInclination = leftWingInclination - outputAngle;
			}

			
			time += inputs.getElapsedTime();

			float reqSpeed = totalMass * 17.142f ;
			thrust=pidTrust.getOutput(reqSpeed, speed, inputs.getElapsedTime());
			System.out.println("reqSpeed: " + reqSpeed);
			System.out.println("Speed: " + speed);//System.out.println("Thrust1: " + speed);
			

			System.out.println("thr " + thrust + "left " + leftWingInclination + "right " + rightWingInclination + "hor " + horStabInclination + "ver " + verStabInclination);

			//In de eerste stap geven we standaard waarden door
			if (inputs.getElapsedTime() == 0) {
				rightWingInclination = (float) (Math.PI/20);
				leftWingInclination = (float) (Math.PI/20);
				thrust = 80f;
				horStabInclination = 0f;
				verStabInclination = 0f;
			}
			  
			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
		}		
	}
}
