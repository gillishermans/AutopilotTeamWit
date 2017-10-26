package autopilotLibrary;
import api.AutopilotConfig;
import api.AutopilotInputs;
import api.AutopilotOutputs;
import api.Outputs;

public class Besturing {

	public Besturing(AutopilotConfig config) {
		this.config = config;
	}
	
	private AutopilotConfig config;
	private Beeldherkenning beeldherkenning = new Beeldherkenning();
	
	public AutopilotOutputs startBesturing(AutopilotInputs inputs){
		
		//Run beeldherkenning en bereken de afstand en hoeken
		beeldherkenning.imageRecognition(inputs.getImage());
		float distance = beeldherkenning.distanceToObject(beeldherkenning.getRadius()[0]);
		double horizontalAngle = beeldherkenning.horizontalAngle(beeldherkenning.getCenter());	
		double verticalAngle = beeldherkenning.verticalAngle(beeldherkenning.getCenter());
		
		float thrust = 1.0f;
		float leftWingInclination = 1.0f;
		float rightWingInclination = 1.0f;
		float horStabInclination = 1.0f;
		float verStabInclination = 1.0f;
		
		if (verticalAngle>0){
			leftWingInclination=1.0f;//Max;
			rightWingInclination=1.0f;//Max;
		}
		else {
			leftWingInclination =1.0f;//MIN;
			rightWingInclination =1.0f;//MIN;
		}
		
		
		
		AutopilotOutputs output = new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination);
		
		return output;
	}
	
	
	
	
}
