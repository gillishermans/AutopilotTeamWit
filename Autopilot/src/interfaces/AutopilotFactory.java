package interfaces;

import autopilotLibrary.MyAutopilotModule;

public class AutopilotFactory {

	public AutopilotFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public static AutopilotModule createAutopilotModule() {
		return new MyAutopilotModule();
	}

}
