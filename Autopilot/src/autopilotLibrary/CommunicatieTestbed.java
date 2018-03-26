package autopilotLibrary;
import interfaces.*;


public class CommunicatieTestbed implements Autopilot{

	public CommunicatieTestbed() {
		besturing = new Besturing();
	}
	
	private static Besturing besturing;

	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		//Lees config en start een timePassed
		besturing.setConfig(config);
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
		besturing.setPath(path);
	}
	
}
