package autopilotLibrary;
import java.util.ArrayList;

import org.opencv.core.Point;

import api.AutopilotConfig;
import api.AutopilotInputs;
import api.AutopilotOutputs;
import api.Outputs;

public class Besturing {

	float thrust = 0.00f;
	float leftWingInclination = 0.0f;
	float rightWingInclination = 0.0f;
	float horStabInclination = 0.0f;
	float verStabInclination = 0.0f;
	
	public Besturing(AutopilotConfig config) {
		this.config = config;
		this.beeldherkenning = new Beeldherkenning(config);
	}
	
	private AutopilotConfig config;
	private Beeldherkenning beeldherkenning;
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs){
		
		//Run beeldherkenning en bereken de afstand en hoeken
		beeldherkenning.imageRecognition(inputs.getImage());
		
		ArrayList<Point> centerArray = beeldherkenning.getCenterArray();
		ArrayList<Float> radiusArray = beeldherkenning.getRadiusArray();
		ArrayList<double[]> colorArray = beeldherkenning.getColorArray();
		
		float distance = 0f;
		double horizontalAngle = 0f;
		double verticalAngle = 0f;
		
		if(!centerArray.isEmpty()){
			distance = beeldherkenning.distanceToObject(centerArray.get(0),radiusArray.get(0));
			horizontalAngle = beeldherkenning.horizontalAngle(centerArray.get(0));	
			verticalAngle = beeldherkenning.verticalAngle(centerArray.get(0));
		}
		
		
		
	//DEMO 3	
//		if (verticalAngle == -0f || (verticalAngle < (Math.PI)/(180))&& verticalAngle > -(Math.PI)/(180)){
//			rightWingInclination = (float) (Math.PI/20);
//			leftWingInclination = (float) (Math.PI/20);
//			horStabInclination = 0f;
//			
//		}
//		else if (verticalAngle >0 ){
//			 rightWingInclination+=(float) (Math.PI/30.0);
//			 leftWingInclination+=(float) (Math.PI/30.0);
//			 
//			 System.out.println("winginclination" + leftWingInclination);
//			 
//			 if (rightWingInclination > Math.PI/4) {
//				 rightWingInclination = (float) (Math.PI/4);
//				 leftWingInclination = (float) (Math.PI/4);
//				 horStabInclination = (float) (Math.PI/15);
//			 }
//		}
////		else if (verticalAngle < -(0.05*Math.PI)){
////			rightWingInclination = (float) (Math.PI/20);
////			leftWingInclination = (float) (Math.PI/20);
////			horStabInclination = 0f;
////		}
//		else{
//			 rightWingInclination+=(float) -(Math.PI/180.0);
//			 leftWingInclination+=(float) -(Math.PI/180.0);
//			 horStabInclination -= Math.PI/240;
////			 System.out.println("horstabinclination" + horStabInclination);
////			 if (horStabInclination > 0) 
////				 horStabInclination -= Math.PI/720;
////			 if (rightWingInclination < -Math.PI/4) {
////				 rightWingInclination = (float) -(Math.PI/4);
////				 leftWingInclination = (float) -(Math.PI/4);
////			 }	 
//		}
		
		//VERTICAAL
		if (verticalAngle == 0f){
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
		}
		else if (verticalAngle > 0 ){
			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
			//horStabInclination = (float) ((float) 2*ratio*(-Math.PI/30));
		}
		
		else{
			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/4));
			//horStabInclination = (float) ((float) ratio*(Math.PI/1000));
		}
		
		
		
		//HORIZONTAAL
		if (horizontalAngle == 0f){
			verStabInclination=0;
		}
		else if (horizontalAngle > 0 ){
			verStabInclination= (float) -Math.PI/10; 
		}
		
		else{
			verStabInclination= (float) Math.PI/10; 
		}
		
		
		
		// Beginsnelheid initialiseren met 100
		// AOA=> 1
		// thrust moet versnelling in de z-richting, veroorzaakt door vleugels teniet doen
		
		thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2));//*Math.pow(100,2));
		//thrust = 100f;

		AutopilotOutputs outputs = new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
		
		return outputs;
	}

}
