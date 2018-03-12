package autopilotLibrary;
import interfaces.*;


public class CommunicatieTestbed implements Autopilot{

	public CommunicatieTestbed() {
		// TODO Auto-generated constructor stub
	}
	
	private static Besturing besturing;

	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		
		//Lees config en start een timePassed
		besturing = new Besturing(config);
		AutopilotOutputs output = timePassed(inputs);
		return output;
	}

	public AutopilotOutputs timePassed(AutopilotInputs inputs){

		//Start het besturingsalgoritme
		AutopilotOutputs output = besturing.startBesturing(inputs);
		return output;
	}

	public void simulationEnded() {
		
		//Stop de simulatie
		
	}


	@Override
	public void setPath(Path path) {
		// TODO Auto-generated method stub
		
	}
	
}
