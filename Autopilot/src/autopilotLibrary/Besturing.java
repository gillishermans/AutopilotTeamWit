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
	private ArrayList<Float> timeList = new ArrayList<Float>();
	private float rechtdoorHoek;
	
	private ArrayList<Vector> getPosList(){
		return this.posList;
	}
	private ArrayList<Float> getTimeList(){
		return this.timeList;
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
		
//		//Geen kubus gevonden -> vlieg rechtdoor
//				
//		if(centerArray.isEmpty()){
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
//		}
//		
//
//		//Zoek dichtsbijzijnde kubus
//		ArrayList<Float> distanceArray = new ArrayList<Float>();
//		float shortest = beeldherkenning.distanceToObject(centerArray.get(0),radiusArray.get(0));
//		int shortestI = 0;
//		for(int i =0;i < centerArray.size();i++){
//			float distance = beeldherkenning.distanceToObject(centerArray.get(i),radiusArray.get(i));
//			distanceArray.add(distance);
//			if(distance < shortest){
//				shortest = distance;
//				shortestI = i;
//			}
//		}
//		
//		//Beweeg naar dichtstbijzijnde kubus
//		double horizontalAngle = beeldherkenning.horizontalAngle(centerArray.get(shortestI));	
//		double verticalAngle = beeldherkenning.verticalAngle(centerArray.get(shortestI));
//		//System.out.println(horizontalAngle);
//		//System.out.println(verticalAngle);
		
		
	//	NIEUW ALGORITME --------------------------------------------------------
		
		//Bewaar positie en tijd
		getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
		getTimeList().add(inputs.getElapsedTime());
		
				
		//Benader snelheid
		int index = getPosList().size() -1;
		Vector speedVector = null;
		float speed = 0.0f;
		if(getTimeList().size() <= 1) speedVector = new Vector(0,0,0);
		else{
			speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
			
			speed = Vector.norm(speedVector)/inputs.getElapsedTime();
		}
				
		//HET ZOLTAN GILLIS ALGORITME
		if(getTimeList().size()-1 == 1){
			float minE = 99999;
			for(float theta = 0.0f; theta < Math.PI/36; theta += Math.PI/360){
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
		}
				
		
				
	//VERTICAAL - NIEUWE FYSICA	
		
		System.out.println("hoek " + rechtdoorHoek);
		System.out.println("speed" + speed);

		//Benadering hoeken van vleugels om rechtdoor te vliegen
		float rechtRange = (float) ((Math.PI *2)/180);
		
		//Tussen -2 en 2 graden -> redelijk rechtdoor
		//if ( -rechtRange < verticalAngle && verticalAngle < rechtRange){
		rightWingInclination = (float) (rechtdoorHoek);
		leftWingInclination = (float) (rechtdoorHoek);
			horStabInclination = (float) -Math.PI/90;
		//}
		
//		else if (verticalAngle > rechtRange ){
//			float ratio =(float) ((float) verticalAngle / (Math.PI/3)); 
//			rightWingInclination = (float) ((float) (Math.PI/6));
//			leftWingInclination = (float) ((float) (Math.PI/6));
//			//horStabInclination = (float) ((float) 2*ratio*(-Math.PI/30));
//		}
//		else{
//			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
//			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
//			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
//			//horStabInclination = (float) ((float) ratio*(Math.PI/1000));
//		}
		

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
		thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2));//*Math.pow(100,2));
		//thrust = 100f;
		
//		leftWingInclination;
//		rightWingInclination;
		
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination);
	}

}

