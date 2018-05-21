package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Braking extends Motion {
	

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = 0;
		float leftWingInclination = - (float) Math.PI/60;
		float rightWingInclination = leftWingInclination;
		float horStabInclination = 0;
		float verStabInclination = 0;
		float frontBrakeForce = 1600;
		float rightBrakeForce = 1600;
		float leftBrakeForce = 1600;
		checkDone();
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	private void checkDone() {
		if (this.speed < 0.5) this.done = true;
		else				  this.done = false;
	}

	@Override
	public String getMotion() {
		// TODO Auto-generated method stub
		return "Braking";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}

}
