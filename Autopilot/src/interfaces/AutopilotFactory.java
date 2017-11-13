package interfaces;

import autopilotLibrary.CommunicatieTestbed;

public class AutopilotFactory {

	public AutopilotFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public static Autopilot createAutopilot(){
		return (Autopilot) new CommunicatieTestbed();
	}

}
