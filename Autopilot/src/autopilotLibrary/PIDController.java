package autopilotLibrary;

public class PIDController {
	
	private float kp, ki, kd;
	
	private float errorSum = 0; 
	private float lastError = 0;
	
	private float maxOutput, minOutput, division;
	
	
	
	public PIDController(float kp, float ki, float kd, float max, float min, float division) {
		this.kp = kp;
		this.ki = ki;
		this.kd = kd;
		this.maxOutput = max;
		this.minOutput = min;
		this.division = division;
	}
	
	public float getOutput(float goal, float actual, float timePassed) {
		float error = goal - actual;
		errorSum += error * timePassed;
		float dError = Math.abs((error-lastError) / timePassed);
		//System.out.println("dError: " + dError + " Error: " + error);
		float output = kp * error + ki * errorSum + kd * dError;
		lastError = error;
		float outputDiv = output/division;
		if (outputDiv > this.maxOutput) {
			output = this.maxOutput;
			//System.out.println("Max");
		}
		if (outputDiv < this.minOutput) {
			output = this.minOutput;
			//System.out.println("Min");
		}
		else output = outputDiv;
		return output;
	}
	
	public void reset() {
		errorSum = 0;
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
	
	public void setMaxOutput(float max) {
		this.maxOutput = max;
	}
	
	public float getMaxOutput() {
		return this.maxOutput;
	}
	
	public void setMinOutput(float min) {
		this.minOutput = min;
	}
	
	public float getMinOutput() {
		return this.minOutput;
	}
	
}