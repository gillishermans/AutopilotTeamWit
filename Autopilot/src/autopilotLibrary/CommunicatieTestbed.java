package autopilotLibrary;
import interfaces.*;


public class CommunicatieTestbed implements Autopilot{

	private static Besturing besturing;
	private GUIAutopilot gui;
	
	public CommunicatieTestbed() {
		this.besturing = new Besturing();
		this.gui = new GUIAutopilot();
	}
	
	
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		//Lees config en start een timePassed
		besturing.setConfig(config);
		AutopilotOutputs output = timePassed(inputs);
		return output;
	}

	public AutopilotOutputs timePassed(AutopilotInputs inputs){
		//Start het besturingsalgoritme
		AutopilotOutputs output = besturing.startBesturing(inputs);
		updateGUI(output);
		return output;
	}

	public void simulationEnded() {
		
		//Stop de simulatie
		
	}
	
	public void updateGUI(AutopilotOutputs output) {
		gui.setLeftInclination(output.getLeftWingInclination());
		gui.setRightInclination(output.getRightWingInclination());
		gui.setHorizontalStabInclination(output.getHorStabInclination());
		gui.setVerticalStabInclination(output.getVerStabInclination());
		gui.setState(besturing.getPhase());
	}


	@Override
	public void setPath(Path path) {
		besturing.setPath(path);
	}
	
}
