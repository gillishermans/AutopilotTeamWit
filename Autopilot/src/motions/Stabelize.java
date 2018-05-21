package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Stabelize extends Motion {

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = (float) pidThrust.getOutput(65, speed, time);
		float outputVelY = (float) pidVelY.getOutput(0, speedVector.y, time);
		outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		float leftWingInclination = outputVelY;
		float rightWingInclination = outputVelY;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
		float horStabInclination = -outputPitch;
		float verStabInclination = 0f;
		checkDone(inputs);
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	private void checkDone(AutopilotInputs inputs) {
		if (inputs.getZ() < -1000) {
			this.done = true;
			//System.out.println("After Straight: " + inputs.getX() + " " + inputs.getZ());
		}
		else					   this.done = false;
	}

	@Override
	public String getMotion() {
		return "Stabelize";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		return new Vector(0,38, -1000);
	}

}
