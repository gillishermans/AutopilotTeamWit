import api.*;


public class CommunicatieTestbed{

	public CommunicatieTestbed() {
		// TODO Auto-generated constructor stub
	}
	
	private static Besturing besturing;

	public static AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		
		//Lees config en start een timePassed
		besturing = new Besturing(config);
		AutopilotOutputs output = timePassed(inputs);
		
		return output;
	}

	public static AutopilotOutputs timePassed(AutopilotInputs inputs){

		//Start het besturingsalgoritme
		AutopilotOutputs output = besturing.startBesturing(inputs);
		
		return output;
	}

	public static void simulationEnded() {
		
		//Stop de simulatie
		
	}
	
}
