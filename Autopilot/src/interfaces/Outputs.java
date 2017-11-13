package interfaces;

public class Outputs implements AutopilotOutputs {

	public Outputs(float thrust,float leftWingInclination,float rightWingInclination,float horStabInclination,float verStabInclination) {
		this.thrust = thrust;
		this.leftWingInclination = leftWingInclination;
		this.rightWingInclination = rightWingInclination;
		this.horStabInclination = horStabInclination;
		this.verStabInclination = verStabInclination;
		
	}
	
	private float thrust;
	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;

	@Override
	public float getThrust() {
		return this.thrust;
	}

	@Override
	public float getLeftWingInclination() {
		return this.leftWingInclination;
	}

	@Override
	public float getRightWingInclination() {
		return this.rightWingInclination;
	}

	@Override
	public float getHorStabInclination() {
		return this.horStabInclination;
	}

	@Override
	public float getVerStabInclination() {
		return this.verStabInclination;
	}


}
