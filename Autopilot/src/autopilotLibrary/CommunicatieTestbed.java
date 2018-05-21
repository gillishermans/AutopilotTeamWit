package autopilotLibrary;
import gui.GUIAutopilot;
import interfaces.*;


public class CommunicatieTestbed implements Autopilot{

	private static Besturing besturing;
	private GUIAutopilot gui;
	
	public CommunicatieTestbed() {
	}
	
	
	public AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs inputs) {
		//Lees config en start een timePassed
		this.besturing = new Besturing(config);
		//this.gui = new GUIAutopilot(this);
		besturing.setConfig();
		AutopilotOutputs output = timePassed(inputs);
		return output;
	}

	public AutopilotOutputs timePassed(AutopilotInputs inputs){
		//Start het besturingsalgoritme
		AutopilotOutputs output = besturing.startBesturing(inputs);
		//updateGUI(output);
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
		gui.setInfo(besturing.getInfo());
		gui.setCube(besturing.getCube());
	}


	@Override
	public void setPath(Path path) {
		System.out.println("PATH SET");
	}


	public void setStab() {
		besturing.setStab();
	}
	
	public void setHeading() {
		besturing.setHeading();
	}
	
}
