package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Landing extends Motion {
	
	private boolean first = true;

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		if (first) {
			System.out.println("START: " + inputs.getX() + " " + inputs.getY() + " " + inputs.getZ());
			first = false;
		}
		float thrust = pidThrust.getOutput(65f, speed, time); //TODO
		pidVelY.reset();
		pidPitch.reset();
		float outputVelY1 = -pidVelY.getOutput(-1f, speedVector.y,time); //TODO
		outputVelY1 = aoaController.aoaController(outputVelY1, (float) Math.PI/20);
		float leftWingInclination = -outputVelY1;
		float rightWingInclination = -outputVelY1;
		float outputPitch2 = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch2 = aoaController.aoaController(outputPitch2, (float) Math.PI/20);
		float horStabInclination = -outputPitch2;
		float verStabInclination = 0;
		checkDone(inputs);
		return new Outputs(thrust, leftWingInclination, rightWingInclination,horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	public void checkDone(AutopilotInputs inputs) {
		if (inputs.getY() < 1.4) {
			System.out.println("END: " + inputs.getX() + " " + inputs.getY() + " " + inputs.getZ());
			this.done = true;
		}
		else					 this.done = false;
	}

	@Override
	public String getMotion() {
		// TODO Auto-generated method stub
		return "Landing";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}

}
