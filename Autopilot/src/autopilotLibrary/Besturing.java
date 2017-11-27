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
	
	private PIDController pid = new PIDController(1,0,0);
	
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
//			rightWingInclination = (float) (Math.PI/20);
//			leftWingInclination = (float) (Math.PI/20);
//			horStabInclination = 0f;
//			
//			float vel = lastY-inputs.getY()/inputs.getElapsedTime();
//			
//			float output = pid.getOutput(0,vel, inputs.getElapsedTime());
//			System.out.println("Output: " + output);
//			
//			lastY = inputs.getY();
//			
//			rightWingInclination = output;
//			leftWingInclination = output;
//			
//			thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2));
//			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
			
			verticalAngle = -inputs.getPitch();
			
		}
		else{

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
			//System.out.println(verticalAngle);
		}
		
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
			speedVector = Vector.scalarProd(speedVector, 1/inputs.getElapsedTime());
			speed = Vector.norm(speedVector);
		}

		//HET ZOLTAN GILLIS ALGORITME
		if(getPosList().size()-1 == 1){
			float minE = 99999;
			for(float theta = 0.0f; theta < Math.PI/4; theta += Math.PI/9999){
				float e = (float) Math.abs(((config.getWingMass() * -config.getGravity()) + 2*(Math.pow(speed,2) * -Math.atan2(speed * Math.cos(theta),speed * Math.sin(theta)) * config.getWingLiftSlope() * Math.cos(theta)))) ;
				System.out.println(2*(Math.pow(speed,2) * -Math.atan2(speed * Math.cos(theta),speed * Math.sin(theta)) * config.getWingLiftSlope() * Math.cos(theta)));
				System.out.println("hoek " + theta);
				System.out.println("ERROR= " +e);
				if(e < minE){
					minE = e;
					rechtdoorHoek = theta;  
				}
			}
			System.out.println("MINE " + minE);
			System.out.println("RDhoek " + rechtdoorHoek);
		}
				
	//VERTICAAL - NIEUWE FYSICA	
		//Benadering hoeken van vleugels om rechtdoor te vliegen
				float rechtRange = (float) ((Math.PI)/180);
				boolean turning = false;
				
			if(turning != true){	

		
		
		
		System.out.println("speed" + speed);
		System.out.println("VertAngle" + verticalAngle);
		//Tussen -2 en 2 graden -> redelijk rechtdoor
		if ( -rechtRange < verticalAngle && verticalAngle < rechtRange){
			rechtdoorHoek = (float) (Math.PI/7 );
			rightWingInclination = (float) (rechtdoorHoek);
			leftWingInclination = (float) (rechtdoorHoek);
			horStabInclination = (float) inputs.getPitch();
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination - inputs.getPitch())*this.config.getWingLiftSlope()*-Math.atan2(speed * Math.cos(rightWingInclination),speed * Math.sin(rightWingInclination))*Math.pow(speedVector.z,2));

		}
	
		else if (verticalAngle > rechtRange ){
			float ratio =(float) ((float) verticalAngle / (Math.PI/3)); 
			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/7));
			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/7));
			//horStabInclination = (float) ((float) 2*ratio*(-Math.PI/30));
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination - inputs.getPitch())*this.config.getWingLiftSlope()*-Math.atan2(speed * Math.cos(rightWingInclination),speed * Math.sin(rightWingInclination))*Math.pow(speedVector.z,2));
			thrust += inputs.getPitch() * thrust;
		}
		
		else {
			float ratio =(float) ((float) verticalAngle / (Math.PI/4));
			rightWingInclination = (float) ((float) -2*ratio*(Math.PI/3));
			leftWingInclination = (float) ((float) -2*ratio*(Math.PI/3));
			//horStabInclination = (float) ((float) ratio*(Math.PI/1000));
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination - inputs.getPitch())*this.config.getWingLiftSlope()*-Math.atan2(speed * Math.cos(rightWingInclination),speed * Math.sin(rightWingInclination))*Math.pow(speedVector.z,2));

		}
		
		//Pitch te hoog
		if (inputs.getPitch() > Math.PI/6){
			//rightWingInclination = (float) (rechtdoorHoek);
			//leftWingInclination = (float) (rechtdoorHoek);
			horStabInclination = (float) ( Math.PI/6);
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination - inputs.getPitch())*this.config.getWingLiftSlope()*-Math.atan2(speed * Math.cos(rightWingInclination),speed * Math.sin(rightWingInclination))*Math.pow(speedVector.z,2));
		}
		//Pitch te laag
		if (inputs.getPitch() < -Math.PI/6){
			//rightWingInclination = (float) (rechtdoorHoek);
			//leftWingInclination = (float) (rechtdoorHoek);
			horStabInclination = (float) (- Math.PI/6);
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination - inputs.getPitch())*this.config.getWingLiftSlope()*-Math.atan2(speed * Math.cos(rightWingInclination),speed * Math.sin(rightWingInclination))*Math.pow(speedVector.z,2));
		}
		
		System.out.println("INCL" + rightWingInclination);
		
		}
		
	//HORIZONTAAL - NIEUWE FYSICA
		
		int turnCount = 0;
		int turn = 0;
		
		if (-rechtRange < horizontalAngle && horizontalAngle < rechtRange) turn = 0;
		else if (horizontalAngle > 0) turn = 1;
		else if (horizontalAngle < 0) turn = 2;

		
		if(inputs.getRoll() > Math.PI/5 && inputs.getRoll() < Math.PI/4){
			rightWingInclination = 0;
			leftWingInclination =  0;
		}
		if(turning == true);
		else if(turn ==0) {
			verStabInclination=0;
		}
		else if (turn == 1) {
			rightWingInclination = -rightWingInclination;
			leftWingInclination =  leftWingInclination;
			//verStabInclination= (float) Math.PI/10;
			turnCount ++;
			turning = true;
		}
		else if (turn == 2){
			rightWingInclination = (float) rightWingInclination;
			leftWingInclination = (float) -leftWingInclination;
			//verStabInclination= (float) -Math.PI/10;
			turnCount ++;
			turning = true;
		}
		
		
		
		
		// Beginsnelheid initialiseren met 100
		// AOA=> 1
		// thrust moet versnelling in de z-richting, veroorzaakt door vleugels teniet doen
		//if(inputs.getPitch() > 0) thrust += (float) (thrust * 0.05);
		
		
		
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination);
	}

}

