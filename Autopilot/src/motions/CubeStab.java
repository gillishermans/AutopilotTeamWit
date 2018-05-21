package motions;

import enums.Dir;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.PIDController;
import util.Vector;

public class CubeStab extends Motion {
	
	private PIDController pidMaxRoll = new PIDController(3f,1f,0f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidDraai = new PIDController(1f,1f,0.5f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private float maxAOA = (float) Math.PI/20;
	
	private float maxRoll = Float.MAX_VALUE;
	
	private float leftIncl = 0;
	private float rightIncl = 0;
	private float horStab = 0;
	
	private float heading;
	
	private float firstX;
	private float firstZ;
	
	private boolean first = true;
	private int times = 0;
	private Dir dir;
	
	public CubeStab(Dir dir) {
		this.dir = dir;
		if (dir == Dir.NORTH) {
			this.heading = 0;
		}
		else if (dir == Dir.EAST) {
			this.heading = (float) -Math.PI/2;
		}
		else if (dir == Dir.WEST) {
			this.heading = (float) Math.PI/2;
		}
		else if (dir == Dir.SOUTH) {
			this.heading = (float) Math.PI;
		}
	}

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = pidThrust.getOutput(65, speed, time);
		float outputVer = pidVelY.getOutput(0, speedVector.y, time);
		outputVer = aoaController.aoaController(outputVer, maxAOA);
		float tempMaxRoll = Math.abs(inputs.getHeading() - heading) * 4;
		if (tempMaxRoll < maxRoll) maxRoll = tempMaxRoll;
		if (maxRoll < (float) Math.PI/1000) {
			maxRoll = (float) (Math.PI/1000);
		}
		float outputRoll;
		if (Math.abs(inputs.getRoll()) > maxRoll) {
			if (inputs.getRoll() > 0) outputRoll = pidMaxRoll.getOutput( maxRoll, inputs.getRoll(), time);
			else                      outputRoll = pidMaxRoll.getOutput(-maxRoll, inputs.getRoll(), time);
		}
		else {
			if (dir == Dir.SOUTH && inputs.getHeading() < 0) {
				outputRoll = pidDraai.getOutput(-heading, inputs.getHeading(), time);
			}
			else {
				outputRoll = pidDraai.getOutput(heading, inputs.getHeading(), time);
			}
		}
		if (inputs.getHeading() == 0) this.done = true;
		outputRoll = aoaController.aoaRollController(outputVer, outputRoll, maxAOA);
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, maxAOA);
		float leftWingInclination = 0;
		float rightWingInclination = 0;
		float horStabInclination = 0;
		//Mooiere overgang
		if (first) {
			leftWingInclination = (outputVer - outputRoll + 2 * leftIncl) / 3;
			rightWingInclination = (outputVer + outputRoll + 2 *rightIncl) / 3;
			horStabInclination = (-outputPitch + horStab) / 2;
			if (times <= 40) {
				this.leftIncl = leftWingInclination;
				this.rightIncl = rightWingInclination;
				this.horStab = horStabInclination;
				times++;
			}
			else {
				first = false;
				System.out.println("OVERGANG GESTOPT");
				
			}
		}
		else {
			leftWingInclination = (outputVer - outputRoll);
			rightWingInclination = (outputVer + outputRoll);
			horStabInclination = (-outputPitch);
		}
		float verStabInclination = 0;
		this.leftIncl = 0;
		if (distance(inputs.getX(), inputs.getZ(), firstX, firstZ) > 200) {
			done = true;
			System.out.println("----------------------------------------------------------------------------");
		}
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}
	
	public float distance(float x1, float y1, float x2, float y2) {
		float x = (float) Math.pow(x2-x1,2);
		float y = (float) Math.pow(y2-y1,2);
		float xy = (float) Math.sqrt(x+y);
		//System.out.println("Distance: " + xy);
		return xy;
	}
	
	public void setInit(float leftIncl, float rightIncl, float horStab) {
		this.leftIncl = leftIncl;
		this.rightIncl = rightIncl;
		this.horStab = horStab;
	}

	@Override
	public boolean isDone() {
		return done;
	}

	@Override
	public String getMotion() {
		return "StabAfterCube";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		System.out.println("OP NAAR DE VOLGENDE KUBUS");
		this.firstX = inputs.getX();
		this.firstZ = inputs.getZ();
		return new Vector(0,0,0);
	}

}
