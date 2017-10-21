import api.*;


public class Main implements Autopilot {

	public Main() {
		// TODO Auto-generated constructor stub
	}
	
	private Beeldherkenning beelherkenning = new Beeldherkenning();

	@Override
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		
		//Read config and start a timePassed
		
		AutopilotOutputs output = null;
		
		return output;
	}

	@Override
	public AutopilotOutputs timePassed(AutopilotInputs inputs) {

		AutopilotOutputs output = null;
		return output;
	}

	@Override
	public void simulationEnded() {
		// TODO Auto-generated method stub
		
	}
	
}
