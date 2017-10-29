package autopilotLibrary;
import api.AutopilotConfig;
import api.AutopilotInputs;
import api.AutopilotOutputs;
import api.Outputs;

public class Besturing {

	public Besturing(AutopilotConfig config) {
		this.config = config;
		this.beeldherkenning = new Beeldherkenning(config);
	}
	
	private AutopilotConfig config;
	private Beeldherkenning beeldherkenning;
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs){
		
		//Run beeldherkenning en bereken de afstand en hoeken
		beeldherkenning.imageRecognition(inputs.getImage());
		float distance = beeldherkenning.distanceToObject(beeldherkenning.getRadius()[0]);
		double horizontalAngle = beeldherkenning.horizontalAngle(beeldherkenning.getCenter());	
		double verticalAngle = beeldherkenning.verticalAngle(beeldherkenning.getCenter());
		
		float thrust = 0.001f;
		float leftWingInclination = 0.0f;
		float rightWingInclination = 0.0f;
		float horStabInclination = 0.0f;
		float verStabInclination = 0.0f;
		
		if (horizontalAngle >=0 ){
			 rightWingInclination=(float) (Math.PI/6.0);
			 leftWingInclination=(float) (Math.PI/6.0);
		}
		else{
			 rightWingInclination=(float) -(Math.PI/6.0);
			 leftWingInclination=(float) -(Math.PI/6.0);
		}
		
		// Beginsnelheid initialiseren met 100
		// AOA=> 1
		// thrust moet versnelling in de z-richting, veroorzaakt door vleugels teniet doen
		
		thrust=(float) (-2*Math.sin(rightWingInclination)*this.config.getWingLiftSlope()*1*Math.pow(100,2));
		horStabInclination=0;
		verStabInclination=0;
		AutopilotOutputs output = new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
		
		return output;
	}
	
	
	
	
}
