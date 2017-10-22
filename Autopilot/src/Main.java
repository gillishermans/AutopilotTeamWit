import api.*;


public class Main{

	public Main() {
		// TODO Auto-generated constructor stub
	}
	

	public static AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		
		//Lees config en start een timePassed
		
		return null;
	}

	public static AutopilotOutputs timePassed(AutopilotInputs inputs){

		//Start het besturingsalgoritme
		Besturing besturing = new Besturing();
		AutopilotOutputs output = besturing.startBesturing(inputs);
		
		return output;
	}

	public static void simulationEnded() {
		
		//Stop de simulatie
		
	}
	
}
