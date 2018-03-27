package interfaces;

public class Outputs implements AutopilotOutputs {

	public Outputs(float thrust,float leftWingInclination,float rightWingInclination,float horStabInclination,float verStabInclination,
			float frontBrakeForce, float leftBrakeForce, float rightBrakeForce) {
		this.thrust = thrust;
		this.leftWingInclination = leftWingInclination;
		this.rightWingInclination = rightWingInclination;
		this.horStabInclination = horStabInclination;
		this.verStabInclination = verStabInclination;
		this.frontBrakeForce = frontBrakeForce;
		this.leftBrakForce = leftBrakeForce;
		this.rightBrakeForce = rightBrakeForce;
		
	}
	
	private float thrust;
	private float leftWingInclination;
	private float rightWingInclination;
	private float horStabInclination;
	private float verStabInclination;
	private float frontBrakeForce;
	private float leftBrakForce;
	private float rightBrakeForce;

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

	@Override
	public float getFrontBrakeForce() {
		return this.frontBrakeForce;
	}

	@Override
	public float getLeftBrakeForce() {
		return this.leftBrakForce;
	}

	@Override
	public float getRightBrakeForce() {
		return rightBrakeForce;
	}


}
