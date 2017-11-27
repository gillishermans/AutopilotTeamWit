package autopilotLibrary;
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
	
	private PIDController pidHor = new PIDController(2f,2,0.1f);
	private PIDController pidRoll = new PIDController(1,2,0);
	
	public Besturing(AutopilotConfig config) {
		this.config = config;
		this.beeldherkenning = new Beeldherkenning(config);
		this.totalMass = config.getEngineMass() + config.getTailMass() + (2* config.getWingMass());
	}
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs){
		
	
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
			float outputHor = 0;
			if (inputs.getElapsedTime() == 0) {
				outputHor = 1.2f;
			}
			else {
				vel = (lastY-inputs.getY())/inputs.getElapsedTime();
				outputHor = -pidHor.getOutput(0,vel, inputs.getElapsedTime())/20;
			}
			//System.out.println("Test: " + (lastY-inputs.getY()));
			//float output = pid.getOutput(0,vel, inputs.getElapsedTime());
			
			lastY = inputs.getY();
			
			rightWingInclination = outputHor;
			leftWingInclination = outputHor;
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
			
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2));
			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
//			
//			
	}
//		else{
//
//			//Zoek dichtsbijzijnde kubus
//			ArrayList<Float> distanceArray = new ArrayList<Float>();
//			float shortest = beeldherkenning.distanceToObject(centerArray.get(0),radiusArray.get(0));
//			int shortestI = 0;
//			for(int i =0;i < centerArray.size();i++){
//				float distance = beeldherkenning.distanceToObject(centerArray.get(i),radiusArray.get(i));
//				distanceArray.add(distance);
//				if(distance < shortest){
//					shortest = distance;
//					shortestI = i;
//				}
//			}
//			
//			//Beweeg naar dichtstbijzijnde kubus
//			horizontalAngle = beeldherkenning.horizontalAngle(centerArray.get(shortestI));	
//			verticalAngle = beeldherkenning.verticalAngle(centerArray.get(shortestI));
//			//System.out.println(horizontalAngle);
//			//System.out.println(verticalAngle);
//		}
//		
	//	NIEUW ALGORITME --------------------------------------------------------
		
		//Bewaar positie en tijd
		getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
		
				
		//Benader snelheid
		int index = getPosList().size() -1;
		Vector speedVector = null;
		float speed = 0.0f;
		if(getPosList().size() <= 1) speedVector = new Vector(0,0,0);
		else{
			speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
			
			speed = Vector.norm(speedVector)/inputs.getElapsedTime();
		}

		//HET ZOLTAN GILLIS ALGORITME
		if(getPosList().size()-1 == 1){
			float minE = 99999;
			for(float theta = 0.0f; theta < Math.PI; theta += Math.PI/360){
				float e = (float) Math.abs(((totalMass * -config.getGravity()) + 2*(Math.pow(speed,2) * -Math.atan2(speed * Math.cos(theta),speed * Math.sin(theta)) * config.getWingLiftSlope() * Math.cos(theta)))) ;
				System.out.println(2*(Math.pow(speed,2) * -Math.atan2(speed * Math.cos(theta),speed * Math.sin(theta)) * config.getWingLiftSlope() * Math.cos(theta)));
				System.out.println("hoek " + theta);
				System.out.println("ERROR= " +e);
				System.out.println("speed" + speed);
				if(e < minE){
					minE = e;
					rechtdoorHoek = theta;  
				}
			}
			System.out.println("MINE " + minE);
			System.out.println("RDhoek " + rechtdoorHoek);
			System.out.println("speed" + speed);
		}
				
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
		horStabInclination=inputs.getPitch();
		thrust=(((float) 2*convertToWorld(getLiftForce(rightWingInclination, speedVector, config.getWingLiftSlope()),inputs).z+
				convertToWorld(getLiftForce(horStabInclination, speedVector, config.getHorStabLiftSlope()),inputs).z)/totalMass)-0.6f;
	}
	
private Vector3f getLiftForce(float theta, Vector3f speedVector,float liftforce) {
		
		Vector3f AttackVector = new Vector3f(0f,(float) Math.sin(theta),-(float)Math.cos(theta));
		//Vector3f airspeed = this.getVelocityAirfoil();
		Vector3f axis = new Vector3f(1,0,0) ;
		//Vector3f Normal= new Vector3f(0,0,0);
		Vector3f Normal=new Vector3f(0,0,0);
		Vector3f.cross(axis, AttackVector,Normal); 
		//Vector3f projectedAirspeed = Vector3f.sub(airspeed, Vector3f.cross(( fysica.mul(airspeed,axis),axis),null);
		
		
		//dit moet nog aangepast worden wanneer x-as erbij komt 
		
		float angleOfAttack = (float) -Math.atan2(Vector3f.dot(speedVector,Normal), Vector3f.dot(speedVector,AttackVector));
		//Vector3f proj = new Vector3f(0,0,projectedAirspeed.getZ());
		Vector3f liftForce = (Vector3f) Normal.scale(angleOfAttack*liftforce*speedVector.length());
		//System.out.println("Lift: " + liftForce);
		return liftForce;
	}

public Vector3f convertToWorld(Vector3f Drone_vector,AutopilotInputs inputs){
		
		float heading = inputs.getHeading();
		float pitch = inputs.getPitch();
		float roll = inputs.getRoll();
		
	
		Matrix3f conversionMatrix = this.Rotation_matrix_Roll(roll);
		Vector3f worldVector=new Vector3f();
		Matrix3f.mul(this.Rotation_matrix_Pitch(pitch), this.Rotation_matrix_Roll(roll), conversionMatrix);
		Matrix3f.mul(this.Rotation_matrix_Heading(heading), conversionMatrix, conversionMatrix);
		Matrix3f.transform(conversionMatrix,Drone_vector,worldVector);
		
		return worldVector;
	}
	
public Matrix3f Rotation_matrix_Pitch(float pitch){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) 1;
		new_matrix.m01=(float) 0;
		new_matrix.m02=(float) 0;
		new_matrix.m10=(float) 0;
		new_matrix.m11=(float) Math.cos(pitch);
		new_matrix.m12=(float) Math.sin(pitch);
		new_matrix.m20=(float) 0;
		new_matrix.m21=(float) -Math.sin(pitch);
		new_matrix.m22=(float) Math.cos(pitch);
		
		return new_matrix;
	}
	
	public Matrix3f Rotation_matrix_Heading(float heading){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) Math.cos(heading);
		new_matrix.m01=(float) 0;
		new_matrix.m02=(float) Math.sin(heading);
		new_matrix.m10=(float) 0;
		new_matrix.m11=(float) 1;
		new_matrix.m12=(float) 0;
		new_matrix.m20=(float) -Math.sin(heading);
		new_matrix.m21=(float) 0;
		new_matrix.m22=(float) Math.cos(heading);
		
		return new_matrix;
	}
	
	public Matrix3f Rotation_matrix_Roll(float roll){
		
		Matrix3f new_matrix = new Matrix3f();
		new_matrix.m00=(float) Math.cos(roll);
		new_matrix.m01=(float) -Math.sin(roll);
		new_matrix.m02=(float) 0;
		new_matrix.m10=(float) Math.sin(roll);
		new_matrix.m11=(float) Math.cos(roll);
		new_matrix.m12=(float) 0;
		new_matrix.m20=(float) 0;
		new_matrix.m21=(float) 0;
		new_matrix.m22=(float) 1;
		
		return new_matrix;
	}

	}

