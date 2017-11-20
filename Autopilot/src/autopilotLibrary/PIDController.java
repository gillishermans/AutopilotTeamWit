package autopilotLibrary;

public class PIDController {
	
	private float kp, ki, kd;
	
	private float errorSum, lastError;
	
	
	
	public PIDController(float kp, float ki, float kd) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
	}
	
	public float getOutput(float goal, float actual, float timePassed) {
		float error = goal - actual;
		errorSum += error * timePassed;
		float dError = (error-lastError) / timePassed;
		
		float output = kp * error + ki * errorSum + kd * dError;
		
		lastError = error;
		return output;
	}
	
	//getters & setters
	
	public void setKp(float kp) {
		this.kp = kp;
	}
	
	public float getKp() {
		return this.kp;
	}
	
	public void setKi(float ki) {
		this.ki = ki;
	}
	
	public float getKi() {
		return this.ki;
	}
	
	public void setKd(float kd) {
		this.kd = kd;
	}
	
	public float getKd() {
		return this.kd;
	}
	
}
