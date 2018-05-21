package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Init extends Motion {

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float rightWingInclination = (float) (Math.PI/20);
		float leftWingInclination = (float) (Math.PI/20);
		float thrust = 80f;
		float horStabInclination = 0f;
		float verStabInclination = 0f;
		
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		return true;
	}

	@Override
	public String getMotion() {
		// TODO Auto-generated method stub
		return "Init";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	


}
