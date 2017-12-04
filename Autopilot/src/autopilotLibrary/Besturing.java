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
	private PIDController pidHeading = new PIDController(1.0f,0.1f,0f,(float) Math.PI/8, (float) -Math.PI/8);
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
					//System.out.println("Output: " + outputHor);
				}
			}
			
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
			verStabInclination = 0;
//			if (inputs.getElapsedTime() != 0 ) {
//				float outputRoll = pidRoll.getOutput(0,inputs.getRoll(), inputs.getElapsedTime());
//				rightWingInclination = rightWingInclination + outputRoll;
//				leftWingInclination= leftWingInclination - outputHor;
//			}
			
			
			
			
			// Draaien
			//if (time > 2) verStabInclination = 0.0f;
		
//			if (time > 1 && time <=3) {
//				verStabInclination = 0;
//				float outputRoll = pidRoll.getOutput((float) Math.PI/18, inputs.getRoll(), inputs.getElapsedTime())/10;
//				System.out.println("Roll: " + outputRoll + " Incl: " + outputHor);
//				rightWingInclination = rightWingInclination + outputRoll;
//				leftWingInclination = leftWingInclination - outputRoll;
//				
//			}
//			if (time > 3 && time <= 9) {
//				verStabInclination = 0f;
//				float outputRoll = pidRoll.getOutput(0, inputs.getRoll(), inputs.getElapsedTime())/10;
//				//System.out.println("Roll: " + outputRoll + " Incl: " + outputHor);
//				rightWingInclination = rightWingInclination + outputRoll;
//				leftWingInclination = leftWingInclination - outputRoll;
//				//verStabInclination = 0.2f;
//			}
//			if (time > 9) {
//				verStabInclination = 0;
//				float outputRoll = pidRoll.getOutput(-(float) Math.PI/18, inputs.getRoll(), inputs.getElapsedTime())/10;
//				System.out.println("Roll: " + outputRoll + " Incl: " + outputHor);
//				rightWingInclination = rightWingInclination + outputRoll;
//				leftWingInclination = leftWingInclination - outputRoll;
//			}
//			if (time > 11) verStabInclination = -0.2f;
//			time += inputs.getElapsedTime();
//			float outputRoll = 0;
			
//			if (time > 4 && time <=6) {
//				verStabInclination = 0;
//				vel = (lastX-inputs.getX())/inputs.getElapsedTime();
//				//float outputX = -pidX.getOutput(0, vel, inputs.getElapsedTime())/2500;
//				outputRoll = pidRoll.getOutput(0,inputs.getRoll(), inputs.getElapsedTime());
//				leftWingInclination = -outputRoll;
//				//System.out.println("Output: " + outputX);
//				//verStabInclination = outputX;
//			}
//			if (time > 6) {
//				//verStabInclination = -pidX.getOutput(0, vel, inputs.getElapsedTime())/2500;
//				verStabInclination = 0.2f;
//				//System.out.println("Go");
////				
//			}
//			if (time > 8) {
//				verStabInclination = 0;
//				vel = (lastX-inputs.getX())/inputs.getElapsedTime();
////				//float outputX = -pidX.getOutput(0, vel, inputs.getElapsedTime())/2500;
//				//pidRoll = new PIDController(2f,1,0,(float) Math.PI/6, (float) -Math.PI/6);
//				outputRoll = pidRoll.getOutput(0,inputs.getRoll(), inputs.getElapsedTime());
//				rightWingInclination = outputRoll;
//				leftWingInclination=outputHor;
//			}
//			if (time > 100000) {
//				float outputH = pidHeading.getOutput(0f,inputs.getHeading(),inputs.getElapsedTime());
//				verStabInclination = -outputH;
//				System.out.println(outputH);
//				
//			}
//			System.out.println(time);
			lastX = inputs.getZ();
			
			
			
			/////////////////////////////////////////////////////////////////////
			
			//Naar file schrijven om makkelijker te analyseren
//			float velZ = 0;
//			if (inputs.getElapsedTime() != 0) {
//				velZ = (lastZ-inputs.getZ())/inputs.getElapsedTime();
//			}
//			this.lastZ = inputs.getZ();
//			//FileWriter fw;
//			try {
//				//fw = new FileWriter("outputZ.txt");
//				//BufferedWriter bw = new BufferedWriter(fw);
//				if (first) {
//					System.out.println(velZ);
//					bw.append(Float.toString(velZ) + "\n");
//					bw.newLine();
//					if (time > 7) {
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
			
			/////////////////////////////////////////////////////////////////////
			getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
			//time += inputs.getElapsedTime();
			
			
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
			thrust=pidTrust.getOutput(15, speed, inputs.getElapsedTime());
			if (inputs.getElapsedTime()==0) thrust = 80f;
			//System.out.println("Thrust: " + thrust);
			System.out.println("Kubus is niet in zicht");
			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
			
			
	}
		
		//Kubus in zicht
		else{
			System.out.println("Kubus is in zicht");
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
			
			
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
			float vel = 0;
			//float goal = 3;
			float outputHor = 0;
			if (inputs.getElapsedTime() == 0) {
				outputHor = (float) (Math.PI/20);
			}
			else {
				vel = (lastY-inputs.getY())/inputs.getElapsedTime();
				
				outputHor = pidVer.getOutput(0, verticalAngle, inputs.getElapsedTime());
				if (Math.abs(outputHor) > Math.PI/4) {
					System.out.println("Te grote Error");
					if (outputHor > 0) outputHor = (float) (Math.PI/4);
					else outputHor = (float) (-Math.PI/4);
				}
				//System.out.println("Output1: " + outputHor);
		
//				if (Math.abs(outputHor) > config.getMaxAOA()) {       //Met AOA (werkt nog niet)
//					if (outputHor > 0) {
//						outputHor =  config.getMaxAOA()+(float) Math.PI/160;
//						System.out.println("MaxAOA");
//					} else {
//						outputHor =  config.getMaxAOA()-(float) Math.PI/160;
//					}
//				}
//				System.out.println("Output: " + outputHor);
			}
			//System.out.println("Test: " + (lastY-inputs.getY()));
			//float output = pid.getOutput(0,vel, inputs.getElapsedTime());
			
		
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
			//System.out.print(horizontalAngle);
			if (time > 1) { float outputAngle = pidHeading.getOutput(0, horizontalAngle, inputs.getElapsedTime())/45;
			//System.out.println("Angle: " + outputAngle);
			//float outputRoll = pidRoll.getOutput(0, inputs.getRoll(), inputs.getElapsedTime())/10;
			//System.out.println("Roll: " + outputRoll + " Incl: " + outputHor);
				rightWingInclination = rightWingInclination + outputAngle;
				leftWingInclination = leftWingInclination - outputAngle;
				//System.out.println("Roll: " + inputs.getRoll() + " Output: " + outputAngle);
			}
//			//System.out.print(outputAngle + " ");
//			if (horizontalAngle > 0) {
//				verStabInclination = -outputAngle;
//			}
//			else {
//				verStabInclination = -outputAngle;
//			}
//			System.out.println(" " + verStabInclination);
//			if (time > 10) {
//				System.out.println("Roll: " + inputs.getRoll());
//				float outputRoll = pidRoll.getOutput(0,inputs.getRoll(), inputs.getElapsedTime())*2;
//				rightWingInclination = outputRoll;
//				if (inputs.getElapsedTime() == 0) rightWingInclination = (float) Math.PI/20;
//			}

			if (inputs.getElapsedTime() == 0) {
				verStabInclination = 0;
			}
			//System.out.println(verStabInclination);
			//verStabInclination = 0.2f;
			
			
//			time += inputs.getElapsedTime();
//			float outputRoll = 0;
//			if (time > 5) {
//				verStabInclination = 0;
//				outputRoll = pidRoll.getOutput(0, inputs.getRoll(), inputs.getElapsedTime());
//				rightWingInclination = outputRoll;
//				
//			}
			

			//System.out.println("Hor: " + outputHor + " Roll: " + outputRoll);
			//System.out.println("  Vel: " + vel);
			//System.out.println("Thrust:" + (float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2))+10);
			
			float reqSpeed = totalMass * 17.12f ;
			thrust=pidTrust.getOutput(reqSpeed, speed, inputs.getElapsedTime());
			System.out.println("reqSpeed: " + reqSpeed);
			System.out.println("Speed: " + speed);
			//System.out.println("Thrust1: " + speed);
			if (inputs.getElapsedTime() == 0) thrust = 80;
			//thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2))+15;
			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
			
			//System.out.println(horizontalAngle);
			//System.out.println(verticalAngle);
		}
		
	//	NIEUW ALGORITME --------------------------------------------------------
		
		//Bewaar positie en tijd
//		getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
//		
//				
//		//Benader snelheid
//		int index = getPosList().size() -1;
//		Vector speedVector = null;
//		float speed = 0.0f;
//		if(getPosList().size() <= 1) speedVector = new Vector(0,0,0);
//		else{
//			speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
//			
//			speed = Vector.norm(speedVector)/inputs.getElapsedTime();
//		}
//
//		//HET ZOLTAN GILLIS ALGORITME
//		if(getPosList().size()-1 == 1){
//			float minE = 99999;
//			for(float theta = 0.0f; theta < Math.PI; theta += Math.PI/360){
//				float e = (float) Math.abs(((totalMass * -config.getGravity()) + 2*(Math.pow(speed,2) * -Math.atan2(speed * Math.cos(theta),speed * Math.sin(theta)) * config.getWingLiftSlope() * Math.cos(theta)))) ;
//				System.out.println(2*(Math.pow(speed,2) * -Math.atan2(speed * Math.cos(theta),speed * Math.sin(theta)) * config.getWingLiftSlope() * Math.cos(theta)));
//				System.out.println("hoek " + theta);
//				System.out.println("ERROR= " +e);
//				System.out.println("speed" + speed);
//				if(e < minE){
//					minE = e;
//					rechtdoorHoek = theta;  
//				}
//			}
//			System.out.println("MINE " + minE);
//			System.out.println("RDhoek " + rechtdoorHoek);
//			System.out.println("speed" + speed);
//		}
				
	//VERTICAAL - NIEUWE FYSICA	
		
			//Comments

//		//Benadering hoeken van vleugels om rechtdoor te vliegen
//		float rechtRange = (float) ((Math.PI)/180);
//		
//		//verticalAngle = inputs.getPitch();
//		//System.out.println("speed" + speed);
//		//System.out.println("VertAngle" + verticalAngle);
//		//Tussen -2 en 2 graden -> redelijk rechtdoor
//		if ( -rechtRange < verticalAngle && verticalAngle < rechtRange){
//			rechtdoorHoek = (float) (Math.PI/20);
//			rightWingInclination = (float) (rechtdoorHoek);
//			leftWingInclination = (float) (rechtdoorHoek);
//			horStabInclination = (float) -Math.PI/90;
//			System.out.println("Hello1");
//		}
//		
//		else if (verticalAngle > rechtRange ){
//			float ratio =(float) ((float) verticalAngle / (Math.PI/3)); 
//			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/6));
//			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/6));
//			horStabInclination = (float) ((float) 2*ratio*(-Math.PI/30));
//			System.out.println("Hello2");
//		}
//		else{
//			System.out.println("Hello");
//			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
//			rightWingInclination = (float) ((float) -2*ratio*(Math.PI/3));
//			leftWingInclination = (float) ((float) -2*ratio*(Math.PI/3));
//			//horStabInclination = (float) ((float) ratio*(Math.PI/1000));
//		}
//		
		
		

//	//VERTICAAL
//		if (verticalAngle == 0f){
//			rightWingInclination = (float) (Math.PI/20);
//			leftWingInclination = (float) (Math.PI/20);
//			horStabInclination = 0f;
//		}
//		else if (verticalAngle > 0 ){
//			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
//			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/6));
//			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/6));
//			//horStabInclination = (float) ((float) 2*ratio*(-Math.PI/30));
//		}
//		
//		else{
//			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
//			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
//			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
//			//horStabInclination = (float) ((float) ratio*(Math.PI/1000));
//		}
		
		
//	//HORIZONTAAL
//		if (horizontalAngle == 0f){
//			verStabInclination=0;
//		}
//		else if (horizontalAngle > 0 ) {
//			verStabInclination= (float) -Math.PI/10; 
//		}
//		
//		else{
//			verStabInclination= (float) Math.PI/10; 
//		}
		
		
		// Beginsnelheid initialiseren met 100
		// AOA=> 1
		// thrust moet versnelling in de z-richting, veroorzaakt door vleugels teniet doen
		//thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(10,2));//*Math.pow(100,2));
		//thrust = 100f;
		
		
		
		//return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination);
		//horStabInclination=inputs.getPitch();
		//thrust=(((float) 2*convertToWorld(getLiftForce(rightWingInclination, speedVector, config.getWingLiftSlope()),inputs).z+
				//convertToWorld(getLiftForce(horStabInclination, speedVector, config.getHorStabLiftSlope()),inputs).z)/totalMass)-0.6f;
		//return null;
	}
	
//private Vector3f getLiftForce(float theta, Vector3f speedVector,float liftforce) {
//		
//		Vector3f AttackVector = new Vector3f(0f,(float) Math.sin(theta),-(float)Math.cos(theta));
//		//Vector3f airspeed = this.getVelocityAirfoil();
//		Vector3f axis = new Vector3f(1,0,0) ;
//		//Vector3f Normal= new Vector3f(0,0,0);
//		Vector3f Normal=new Vector3f(0,0,0);
//		Vector3f.cross(axis, AttackVector,Normal); 
//		//Vector3f projectedAirspeed = Vector3f.sub(airspeed, Vector3f.cross(( fysica.mul(airspeed,axis),axis),null);
//		
//		
//		//dit moet nog aangepast worden wanneer x-as erbij komt 
//		
//		float angleOfAttack = (float) -Math.atan2(Vector3f.dot(speedVector,Normal), Vector3f.dot(speedVector,AttackVector));
//		//Vector3f proj = new Vector3f(0,0,projectedAirspeed.getZ());
//		Vector3f liftForce = (Vector3f) Normal.scale(angleOfAttack*liftforce*speedVector.length());
//		//System.out.println("Lift: " + liftForce);
//		return liftForce;
//	}
//
//public Vector3f convertToWorld(Vector3f Drone_vector,AutopilotInputs inputs){
//		
//		float heading = inputs.getHeading();
//		float pitch = inputs.getPitch();
//		float roll = inputs.getRoll();
//		
//	
//		Matrix3f conversionMatrix = this.Rotation_matrix_Roll(roll);
//		Vector3f worldVector=new Vector3f();
//		Matrix3f.mul(this.Rotation_matrix_Pitch(pitch), this.Rotation_matrix_Roll(roll), conversionMatrix);
//		Matrix3f.mul(this.Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);
//		Matrix3f.transform(conversionMatrix,Drone_vector,worldVector);
//		
//		return worldVector;
//	}
//	
//public Matrix3f Rotation_matrix_Pitch(float pitch){
//		
//		Matrix3f new_matrix = new Matrix3f();
//		new_matrix.m00=(float) 1;
//		new_matrix.m01=(float) 0;
//		new_matrix.m02=(float) 0;
//		new_matrix.m10=(float) 0;
//		new_matrix.m11=(float) Math.cos(pitch);
//		new_matrix.m12=(float) Math.sin(pitch);
//		new_matrix.m20=(float) 0;
//		new_matrix.m21=(float) -Math.sin(pitch);
//		new_matrix.m22=(float) Math.cos(pitch);
//		
//		return new_matrix;
//	}
//	
//	public Matrix3f Rotation_matrix_Heading(float heading){
//		
//		Matrix3f new_matrix = new Matrix3f();
//		new_matrix.m00=(float) Math.cos(heading);
//		new_matrix.m01=(float) 0;
//		new_matrix.m02=(float) Math.sin(heading);
//		new_matrix.m10=(float) 0;
//		new_matrix.m11=(float) 1;
//		new_matrix.m12=(float) 0;
//		new_matrix.m20=(float) -Math.sin(heading);
//		new_matrix.m21=(float) 0;
//		new_matrix.m22=(float) Math.cos(heading);
//		
//		return new_matrix;
//	}
//	
//	public Matrix3f Rotation_matrix_Roll(float roll){
//		
//		Matrix3f new_matrix = new Matrix3f();
//		new_matrix.m00=(float) Math.cos(roll);
//		new_matrix.m01=(float) -Math.sin(roll);
//		new_matrix.m02=(float) 0;
//		new_matrix.m10=(float) Math.sin(roll);
//		new_matrix.m11=(float) Math.cos(roll);
//		new_matrix.m12=(float) 0;
//		new_matrix.m20=(float) 0;
//		new_matrix.m21=(float) 0;
//		new_matrix.m22=(float) 1;
//		
//		return new_matrix;
//	}
//
	}
