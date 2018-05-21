package motions;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.PIDController;
import util.Vector;

public class STurn extends Motion {
	
	private Sturn turn = Sturn.FIRST;
	private enum Sturn {
		FIRST, STAB, SECOND
	}
	
	private PIDController pidStab = new PIDController(3,1,1,(float) Math.PI/6, -(float) Math.PI/6,30);
	private PIDController pidHeading = new PIDController(0.5f,1f,0.5f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	private PIDController pidRoll = new PIDController(4f,1,1.5f,(float) Math.PI/60, (float) -Math.PI/60 ,50);
	
	private float heading;
	private float turnTime;
	
	
	public STurn() {
		
	}
	
	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = pidThrust.getOutput(65f, speed, time); 
		float outputVelY = pidVelY.getOutput(0, speedVector.y, time); 
		outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		float outputVerStab = 0;
		float outputRoll = 0;
		
		//float dsrdHeading = calculateHeading(inputs, xCoo[i], yCoo[i]);
		if (turn == Sturn.FIRST) heading = (float) (Math.PI/15);
		if (turn == Sturn.SECOND) heading = 0;
		//float maxRollTurn = (float) Math.PI/34;
		float maxRollTurn = Math.min((float) Math.PI/9, Math.abs(inputs.getHeading() - heading)* 3f);
		switch(turn) {
		case FIRST:
			//System.out.println(inputs.getElapsedTime() + " " + inputs.getX() + " " + inputs.getZ());
			if (Math.abs(heading - inputs.getHeading()) < (float) Math.PI/90) {
				outputRoll = pidStab.getOutput(0, inputs.getRoll(), time);
				turn = Sturn.STAB;
				turnTime = inputs.getElapsedTime();
				pidHeading.reset();
			}
			else {
				if (Math.abs(inputs.getRoll()) > maxRollTurn) {
					if (inputs.getRoll() > 0) outputRoll = pidRoll.getOutput( maxRollTurn, inputs.getRoll(), time);
					else                      outputRoll = pidRoll.getOutput(-maxRollTurn, inputs.getRoll(), time);
				}
				else {
					outputRoll = pidHeading.getOutput(heading, inputs.getHeading(), time);
				}
			}
			break;
		case STAB: 
			outputRoll = pidStab.getOutput(0, inputs.getRoll(), time);
			if (inputs.getElapsedTime() - turnTime > 2) {
				System.out.println("LAATSTE: " + inputs.getElapsedTime() + " " + inputs.getX()+ " " + inputs.getZ());
				System.out.println("-----------------");
				turn = Sturn.SECOND;
				turnTime = inputs.getElapsedTime();
				pidRoll.reset();
			}
			break;
		case SECOND:
			if (Math.abs(inputs.getHeading()) < (float) Math.PI/360) {
				outputRoll = pidStab.getOutput(0, inputs.getRoll(), time);
				//outputVerStab = -pidVerStab.getOutput(0, speedVector.x, getTime());
			}
			else {
				if (Math.abs(inputs.getRoll()) > maxRollTurn) {
					if (inputs.getRoll() > 0) outputRoll = pidRoll.getOutput( maxRollTurn, inputs.getRoll(), time);
					else                      outputRoll = pidRoll.getOutput(-maxRollTurn, inputs.getRoll(), time);
				}
				else {
					outputRoll = pidHeading.getOutput(0, inputs.getHeading(), time);
				}
			}
			if (inputs.getElapsedTime() - turnTime >= 7 &&  inputs.getElapsedTime() - turnTime <= 7.05) System.out.println("LAATSTE: " + inputs.getElapsedTime() + " " + inputs.getX()+ " " + inputs.getZ());
			break;
		}
	
		//str = str + " " + toDegrees(dsrdHeading) + " " + inputs.getHeading();
		//outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, maxRollAOA);
		float leftWingInclination = outputRoll;
		float rightWingInclination = outputRoll;
		float verStabInclination = outputVerStab;
		float horStabInclination = pidPitch.getOutput(inputs.getPitch(), 0, time);
		horStabInclination = -aoaController.aoaController(horStabInclination,(float) Math.PI/20);
		return new Outputs(thrust, leftWingInclination, rightWingInclination,horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getMotion() {
		return "STurn";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		return null;
	}

}
