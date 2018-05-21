package motions;

import autopilotLibrary.AOAController;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import util.PIDController;
import util.Vector;


public abstract class Motion {
	
	protected PIDController pidPitch = new PIDController(4f,0.5f,0.5f,(float) Math.PI/6f, (float) -Math.PI/6f, 1);
	protected PIDController pidThrust = new PIDController(5f,5,1,2000, 0, 0.1f);
	protected PIDController pidVelY = new PIDController(1f,1f,0f,(float) Math.PI/10f, (float)- Math.PI/10f, 20);
	
	protected AOAController aoaController = new AOAController();
	
	protected Vector speedVector;
	protected float speed;
	
	protected boolean done;

	
	public abstract AutopilotOutputs getOutputs(AutopilotInputs inputs, float time);
	public abstract boolean isDone();
	public abstract String getMotion();
	public abstract Vector nextPos(AutopilotInputs inputs);


	public void setSpeedVector(Vector v) {
		this.speedVector = v;
	}
	
	public void setSpeed(float s) {
		this.speed = s;
	}
	
	
	
}
