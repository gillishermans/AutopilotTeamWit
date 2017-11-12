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
	private AutopilotConfig config;
	private Beeldherkenning beeldherkenning;
	
	public Besturing(AutopilotConfig config) {
		this.config = config;
		this.beeldherkenning = new Beeldherkenning(config);
	}
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs){
		
		//Run beeldherkenning en bereken de afstand en hoeken
		beeldherkenning.imageRecognition(inputs.getImage());
		
		ArrayList<Point> centerArray = beeldherkenning.getCenterArray();
		ArrayList<Float> radiusArray = beeldherkenning.getRadiusArray();
	  //ArrayList<double[]> colorArray = beeldherkenning.getColorArray();
		
		
		//Geen kubus gevonden -> vlieg rechtdoor
		if(centerArray.isEmpty()){
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
			thrust=(float) Math.abs(2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(9,2));
			return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
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
		double horizontalAngle = beeldherkenning.horizontalAngle(centerArray.get(shortestI));	
		double verticalAngle = beeldherkenning.verticalAngle(centerArray.get(shortestI));
		System.out.println(horizontalAngle);
		System.out.println(verticalAngle);
				
		
	//VERTICAAL
		if (verticalAngle == 0f){
			rightWingInclination = (float) (Math.PI/20);
			leftWingInclination = (float) (Math.PI/20);
			horStabInclination = 0f;
		}
		else if (verticalAngle > 0 ){
			float ratio =(float) ((float) verticalAngle / (Math.PI/3));
			rightWingInclination = (float) ((float) 2*ratio*(Math.PI/6));
			leftWingInclination = (float) ((float) 2*ratio*(Math.PI/6));
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

		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
	}

}

