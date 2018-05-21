package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.Vector;

public class Idle extends Motion {

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		return new Outputs(0, 0, 0, 0, 0, 0, 0, 0);
	}

	@Override
	public boolean isDone() {
		return false;
	}

	@Override
	public String getMotion() {
		// TODO Auto-generated method stub
		return "Idle";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}

}
