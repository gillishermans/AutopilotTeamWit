package motions;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.PIDController;
import util.Vector;

public class FullTurnR extends Motion {
	
	private PIDController pidStab = new PIDController(3,1,1,(float) Math.PI/6, -(float) Math.PI/6,30);
	private PIDController pidMaxRoll = new PIDController(3f,1f,0f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidDraai = new PIDController(1f,1f,0f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	
	private final float maxRoll = (float) Math.PI/5;
	private final float maxRollAOA = (float) Math.PI/20;
	
	private float desiredHeading;
	
	private final static float X_OFFSET = 740;
	private final static float Z_OFFSET = 576;
	
	private float interval = 33;
	
	public FullTurnR(float heading) {
		this.desiredHeading = heading;
	}

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float outputRoll = 0;
		float thrust = pidThrust.getOutput(65f, speed, time);
		float outputVelY = pidVelY.getOutput(0, speedVector.y, time);
		outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
		if (Math.abs(desiredHeading - inputs.getHeading()) > (float) Math.PI/9) {
			//ALS OVER MAXROLL BRENG TERUG NAAR MAXROLL ANDERS GA NAAR HEADING
			outputRoll = pidMaxRoll.getOutput(-maxRoll,inputs.getRoll(), time);
//			}
//			else {
//				if (inputs.getHeading() >= 0) outputRoll = pidDraai.getOutput(desiredHeading, inputs.getHeading(), time);
//				else 						  outputRoll = pidDraai.getOutput(desiredHeading, inputs.getHeading(), time);
//			}
		} 
		else {
			if (Math.abs(desiredHeading - inputs.getHeading()) > Math.PI/720) {
				float maxRollTurn = Math.min((float) Math.PI/7, Math.abs(inputs.getHeading() - desiredHeading)* 4f);
				if (Math.abs(inputs.getRoll()) > maxRollTurn) {
					if (inputs.getRoll() > 0) outputRoll = pidMaxRoll.getOutput( maxRollTurn, inputs.getRoll(), time);
					else					  outputRoll = pidMaxRoll.getOutput(-maxRollTurn, inputs.getRoll(), time);
				}
				else {
					outputRoll = pidDraai.getOutput(desiredHeading, inputs.getHeading(), time);
				}
			}
				
			else {
				System.out.println("Actual FullTurnR: " + inputs.getX() + " " + inputs.getZ());
				this.done = true;
			}
			
		}
		//System.out.print(outputRoll + " " + outputVelY + " " + maxRollAOA + " ");
		outputRoll = aoaController.aoaRollController(outputVelY, outputRoll, maxRollAOA);
		//System.out.println(outputRoll);
		float leftWingInclination = outputVelY - outputRoll;
		float rightWingInclination = outputVelY + outputRoll;
		outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/25);
		float horStabInclination = -outputPitch;
		float verStabInclination = 0;
		//checkDone(inputs);
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	

	@Override
	public String getMotion() {
		return "FullTurnR";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		//Als heading == 0
		Vector output = new Vector(0,0,0);
		if (inputs.getHeading() > -Math.PI/360 && inputs.getHeading() < (float) Math.PI/360) {
			float x = inputs.getX() + X_OFFSET;
			float z = inputs.getZ() - Z_OFFSET;
			output = new Vector(x, inputs.getY(), z);
		}
		//Heading 90 graden
		else if (inputs.getHeading() > Math.PI/2 - Math.PI/360 && inputs.getHeading() < Math.PI/2 + (float) Math.PI/360) {
			float x = inputs.getX() - Z_OFFSET;
			float z = inputs.getZ() - X_OFFSET;
			output = new Vector(x, inputs.getY(), z);
		}
		//Heading 180/-180
		else if (Math.abs(inputs.getHeading()) > Math.PI - Math.PI/360 && Math.abs(inputs.getHeading()) < Math.PI + (float) Math.PI/360) {
			float x = inputs.getX() - X_OFFSET;
			float z = inputs.getZ() + Z_OFFSET;
			output = new Vector(x, inputs.getY(), z);
		}
		//Heading 270/-90
		else if (inputs.getHeading() > - Math.PI/2 - Math.PI/360 && inputs.getHeading() < - Math.PI/2 + (float) Math.PI/360) {
			float x = inputs.getX() + Z_OFFSET;
			float z = inputs.getZ() + X_OFFSET;
			output = new Vector(x, inputs.getY(), z);
		}
		System.out.print("Prediction FullTurnR: ");
		output.print();
		return output;
	}
	
	public static float getXOffset() {
		return X_OFFSET;
	}
	
	public static float getZOffset() {
		return Z_OFFSET;
	}

}
