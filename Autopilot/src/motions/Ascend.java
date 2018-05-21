package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Ascend extends Motion {

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = 2000;
		float leftWingInclination = (float) Math.PI/20;
		float rightWingInclination = leftWingInclination;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
		float horStabInclination = -outputPitch;
		float verStabInclination = 0;
		checkDone(inputs);
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	private void checkDone(AutopilotInputs inputs) {
		if (inputs.getY() > 40) this.done = true;
		else                    this.done = false;
	}

	@Override
	public String getMotion() {
		return "Ascend";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}

}
