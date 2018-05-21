package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Takeoff extends Motion {

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = 2000;
		float leftWingInclination = (float) - Math.PI/60;
		float rightWingInclination = leftWingInclination;
		float horStabInclination = 0;
		float verStabInclination = 0;
		checkDone();
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination,0,0,0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	private void checkDone() {
		if (speedVector.z < -40) done = true;
		else                     done = false;
	}

	@Override
	public String getMotion() {
		// TODO Auto-generated method stub
		return "Takeoff";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}

}
