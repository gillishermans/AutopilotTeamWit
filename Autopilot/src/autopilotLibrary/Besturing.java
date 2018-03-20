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
	private float frontBrakeForce=0.0f;
	private float leftBrakeForce=0.0f;
	private float rightBrakeForce=0.0f;
	private float totalMass;
	private AutopilotConfig config;
	private Beeldherkenning beeldherkenning;
	private ArrayList<Vector> posList = new ArrayList<Vector>(); 
	private float rechtdoorHoek;
	private float lastLoopTime = 0;
	private float draaing90 =9.776f;
	
	private ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	private float maxAOA = (float) Math.PI/18f; 
	
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	float outputVelY = 0;
	private float goalYspeed=0;
	private float time = 0;
	
	
	private boolean opstijgFase= true;
	private boolean vliegFase=false;
	private boolean landingsFase=false;
	
	private PIDController pidVelY = new PIDController(1f,1f,0f,(float) Math.PI/6f, (float)- Math.PI/6f, 20);
	private PIDController pidVer = new PIDController(10f,10,0f,(float) Math.PI/6f, (float)- Math.PI/6f, 1);
	private PIDController pidRoll = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6 ,20);
	private PIDController pidHor = new PIDController(1f,1,0f,(float) Math.PI/6, (float)- Math.PI/6, 2);
	private PIDController pidRoll2 = new PIDController(1f,1,0,(float) Math.PI/6, (float) -Math.PI/6, 2);
	private PIDController pidPitch = new PIDController(4f,0.5f,0.5f,(float) Math.PI/6f, (float) -Math.PI/6f, 1);
//	
//	private PIDController pidX = new PIDController(0.1f,1,0,(float) Math.PI/6, (float) -Math.PI/6, 1);
	private PIDController pidHeading = new PIDController(1.0f,1f,0f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	private PIDController pidTrust = new PIDController(5f,5,1,2000, 0, 0.1f);
	private PIDController pidlefttwheel= new PIDController(1f,1f,1f,2486f,0f,1f);
	private PIDController pidrightwheel= new PIDController(1f,1f,1f,2486f,0f,1f);
	private PIDController pidVertax = new PIDController(10,10,0,(float) Math.PI/10, (float) -Math.PI/10, 2);
	
	
	//taxieën
	private float bestemmingX=1000;
	private float bestemmingZ=-50;
	
//	private FileWriter fw;
//	private BufferedWriter bw;
	
	int k = 5;
	
	private float lastInclRight = 0;
	private float lastInclHor = 0;
	
	private boolean resetHeading = false;
	private boolean resetStabilization = false;
	private boolean ZPART =false;
	private float turnTime=0;
	
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
		
		
////////////////////////////Taxieën////////////////////////////
//In de eerste stap geven we standaard waarden door
		if (opstijgFase) {
			float afstand;
			afstand = (float) Math.sqrt(Math.pow((bestemmingX-inputs.getX()),2)+Math.pow((bestemmingZ-inputs.getZ()), 2));
			
			if (getTime() == 0) {
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			thrust = 0f;
			horStabInclination = 0f;
			verStabInclination = 0f;
			}
			leftBrakeForce=3000;
			rightBrakeForce=3000;
			frontBrakeForce=3000;
			thrust=0;
			if (inputs.getElapsedTime() < 9.762){
				if( bestemmingX >0){
					thrust=200;//rechts draaien
					leftBrakeForce=3000;
					rightBrakeForce=0;
					frontBrakeForce=0;
				}
				
				else if (bestemmingX<0){//links draaien 
					thrust=200;
					leftBrakeForce=0;
					rightBrakeForce=3000;
					frontBrakeForce=0;
				}
			}	
			else if (!ZPART && inputs.getElapsedTime()>14 ){
				if (Math.abs(bestemmingX-inputs.getX()) > Math.abs(3*bestemmingX/100) ){
				thrust=200;
				leftBrakeForce=0;
				rightBrakeForce=0;
				frontBrakeForce=0;	
				}
				else {
					ZPART=true;
					this.turnTime=inputs.getElapsedTime();
				}
			}
			
			if (ZPART && inputs.getElapsedTime()> turnTime+3 ){
				//
				if (inputs.getElapsedTime()< this.turnTime+12.912){
					if( bestemmingZ*bestemmingX>0){ //rechts draaien 
						thrust=200;
						leftBrakeForce=3000;
						rightBrakeForce=0;
						frontBrakeForce=0;
					}
					else if (bestemmingX*bestemmingZ<0){ //links draaien 
						System.out.print(turnTime);
						thrust=200;
						leftBrakeForce=0;
						rightBrakeForce=3000;
						frontBrakeForce=0;
					}
				
				}
				else if (inputs.getElapsedTime()>turnTime+ 15.912  ) {
					if (Math.abs(bestemmingZ-inputs.getZ()) > Math.abs(3*bestemmingZ/100) ){
					thrust=200;
					leftBrakeForce=0;
					rightBrakeForce=0;
					frontBrakeForce=0;	
					}
			
				}
			}
		}
//			else if (inputs.getElapsedTime()<30){
//			thrust= this.draaiFunctie(inputs).getThrust();
//			leftBrakeForce=this.draaiFunctie(inputs).getLeftBrakeForce();
//			rightBrakeForce=this.draaiFunctie(inputs).getRightBrakeForce();
//			frontBrakeForce=this.draaiFunctie(inputs).getLeftBrakeForce();
//			}
//			
//			else if (afstand > 5/141.421356*Math.sqrt(Math.pow(bestemmingX, 2)+Math.pow(bestemmingZ, 2)) && inputs.getElapsedTime()>30){
//			leftBrakeForce=0;
//			rightBrakeForce=0;
//			frontBrakeForce=0;
//			thrust=200;
//			System.out.print(afstand);
//			}
//			
//		else {
//				thrust=0;
//				leftBrakeForce=3000;
//				rightBrakeForce=3000;
//				frontBrakeForce=3000;
//			}
		
		else if (vliegFase) {
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
					outputVelY = -pidVelY.getOutput(goalYspeed,vel, getTime());
					//System.out.println("AoA: " + getAngleOfAttack(speedVector,outputVelY)*360/(2*Math.PI));
	//				while (Math.abs(getAngleOfAttack(speedVector,outputVelY)) >= maxAOA) {
	//					outputVelY = (outputVelY +  2 *lastInclRight) / 3;
	//					System.out.println("Vleugel "+ j + ": " + getAngleOfAttack(speedVector,outputVelY)*360/(2*Math.PI) + " " + outputVelY*360/(2*Math.PI));
	//					j++;
	//				}
				}
				
				j = 0;
				
				lastInclRight = outputVelY;
				lastY = inputs.getY();
				
				rightWingInclination = outputVelY;
				leftWingInclination = outputVelY;
				
				
				//horStabInclination = outputVelY*10;
				
				//float aoa = (float) (getAngleOfAttack(speedVector,rightWingInclination) * 180 / Math.PI);
				
				//System.out.println(inputs.getElapsedTime());
				
				verStabInclination = 0;
				float outputRoll = 0;
				horStabInclination = 0;
				float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
				float aoa = getAngleOfAttack(speedVector,Math.abs(outputPitch));
				System.out.println("AOA: "+ aoa*360/(2*Math.PI) + " " + -outputPitch*360/(2*Math.PI));
	//			while (Math.abs(aoa) >= maxAOA) {
	//				System.out.println("HorStab: " + -outputPitch*360/(2*Math.PI) + " " + getAngleOfAttack(speedVector,-outputPitch)*360/(2*Math.PI));
	//				outputPitch = (2 * lastInclHor + outputPitch) /3;
	//				System.out.println("HorStab1 " + j + ": " + -outputPitch*360/(2*Math.PI)+ " " + getAngleOfAttack(speedVector,-outputPitch)*360/(2*Math.PI));
	//				aoa = getAngleOfAttack(speedVector,Math.abs(outputPitch));
	//				j++;
	//			}
				horStabInclination = -outputPitch;
				//System.out.println(-outputPitch*360/(2*Math.PI)  +" "+ getAngleOfAttack(speedVector,-outputPitch)*360/(2*Math.PI));
					
				lastInclHor = horStabInclination;
				//rightWingInclination = outputPitch;
				//leftWingInclination = outputPitch;
		
				
				
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
			float reqSpeed = totalMass * 17.142f ;
			thrust = pidTrust.getOutput(65f, speed, getTime());
			
		}
		
		else if (landingsFase) { 

			lastY = inputs.getY();
			outputVelY = -pidVelY.getOutput(-2,(lastY-inputs.getY())/getTime(), getTime());
			lastInclRight = outputVelY;
			rightWingInclination = outputVelY;
			leftWingInclination = outputVelY;
			}
		
		//System.out.println("thr " + thrust + "left " + leftWingInclination + "right " + rightWingInclination + "hor " + horStabInclination + "ver " + verStabInclination);
		//System.out.println("HorSTAB " + k + ": " + getAngleOfAttack(speedVector,rightWingInclination)*360/(2*Math.PI));
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
		
	}
	
	
	
	public AutopilotOutputs draaiFunctie(AutopilotInputs inputs){
		float goal;
		goal=(float) Math.atan(bestemmingX/bestemmingZ);
		float goalgraad;
		float turntime;
		goalgraad= Math.abs((float) (goal*180/Math.PI));
		
		
		turntime =(float) (0.6348738114821205 + 0.37783121152918275*goalgraad - 0.01634711016193711*Math.pow(goalgraad, 2) + 0.0005014392630037885*Math.pow(goalgraad, 3) - 0.00000841026261600979*Math.pow(goalgraad,4) + 7.075264799883*Math.pow(goalgraad, 5)*Math.pow(10, -8) - 2.3312240521*Math.pow(goalgraad, 6)*Math.pow(10, -10));
		turntime= Math.min(turntime,draaing90);
		if (bestemmingZ<0){
			if (inputs.getElapsedTime()<turntime){
				if ( goal > 0 ) {					///links draaien
					thrust=200 ;
					leftBrakeForce=0;
					rightBrakeForce=3000;
				}
			else {							//rechts draaien
					thrust=200 ;
					leftBrakeForce=3000;
					rightBrakeForce=0;
				}
			}
			else  {
				thrust=0;
				leftBrakeForce=3000;
				rightBrakeForce=3000;
				frontBrakeForce=3000;
			}
		}
		else if( bestemmingZ>0){
			if (inputs.getElapsedTime()<14.04){ //tijd nodig voor 180 graden te draaien 
				thrust=200;
				leftBrakeForce=0;
				rightBrakeForce=3000;
			}
			else if (inputs.getElapsedTime()<18 && inputs.getElapsedTime()>14){
				thrust=0;
				leftBrakeForce=3000;
				rightBrakeForce=3000;
				frontBrakeForce=3000;
			}
			else if (inputs.getElapsedTime()>15){ //algoritme opnieuw
				goal=(float) Math.atan((bestemmingX-inputs.getX())/(bestemmingZ-inputs.getZ()));
				goalgraad= Math.abs((float) (goal*180/Math.PI));
				turntime =(float) (0.6348738114821205 + 0.37783121152918275*goalgraad - 0.01634711016193711*Math.pow(goalgraad, 2) + 0.0005014392630037885*Math.pow(goalgraad, 3) - 0.00000841026261600979*Math.pow(goalgraad,4) + 7.075264799883*Math.pow(goalgraad, 5)*Math.pow(10, -8) - 2.3312240521*Math.pow(goalgraad, 6)*Math.pow(10, -10));
				if (inputs.getElapsedTime()<18+turntime){
					if ( goal > 0 ) {					///links draaien
						thrust=200 ;
						leftBrakeForce=0;
						rightBrakeForce=3000;
						frontBrakeForce=0;
					}
					else {							//rechts draaien
						thrust=200 ;
						leftBrakeForce=3000;
						rightBrakeForce=0;
						frontBrakeForce=0;
					}
				}
				else  {
					thrust=0;
					leftBrakeForce=3000;
					rightBrakeForce=3000;
					frontBrakeForce=3000;
					
					}
			}
	}
		
		
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);	
	}
	
	public void setTime(AutopilotInputs inputs) {
		double time1 = inputs.getElapsedTime();
		float elapTime = (float)(time1 - lastLoopTime);
		lastLoopTime = (float) time1;
		//turnTime+=elapTime;
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
		Vector projectedAirspeed = 	vector.sum(airspeed, vector.product(-1*vector.scalairProd(axis, airspeed)/vector.lengthSquared(axis),axis));	
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
