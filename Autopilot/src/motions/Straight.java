package motions;

import autopilotLibrary.CommunicatieTestbed;
import enums.Dir;
import gui.GUIAutopilot;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Inputs;
import interfaces.Outputs;
import util.PIDController;
import util.Vector;

public class Straight extends Motion {
	
	private float firstX, firstZ;
	private float heading;
	private float distance = 0;
	private Dir dir;
	
	private float target;
	private Vector output;
	
	
	private PIDController pidHeading = new PIDController(1f,1f,0.5f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	private PIDController pidStab = new PIDController(3,1,1,(float) Math.PI/6, -(float) Math.PI/6,30);
	private PIDController pidMaxRoll = new PIDController(3f,1f,0f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidVelX = new PIDController(0.5f,1f,0f,(float) Math.PI/10f, (float)- Math.PI/10f, 20);
	private PIDController pidVelZ = new PIDController(0.5f,1f,0f,(float) Math.PI/10f, (float)- Math.PI/10f, 20);
	//private PIDController pidRoll = new PIDController(4f,1,1.5f,(float) Math.PI/60, (float) -Math.PI/60 ,50);
	
	
	public Straight(float distance, Dir dir) {
		this.distance = distance;
		this.dir = dir;
		//System.out.println(heading);
	}
	
	public Straight(float distance, Dir dir, Vector output) {
		this.distance = distance;
		this.dir = dir;
		this.output = output;
	}

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		float thrust = (float) pidThrust.getOutput(65, speed, time);
		float outputVelY = (float) pidVelY.getOutput(40, inputs.getY(), time);
		float outputRoll;
		float outputVerStab;
		float maxRoll = (float) Math.PI/180;
		outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		heading = calculateAngle(inputs);
		String str = " ";
		if (Math.abs(Math.abs(inputs.getHeading()) - Math.abs(heading)) < (float) Math.PI/1000) {
			outputRoll = pidStab.getOutput(0, inputs.getRoll(), time);
			//System.out.println("STAB");
			str = "STAB";
		}
		else {
			
			if (Math.abs(inputs.getRoll()) > maxRoll) {
				maxRoll = getMaxRoll(inputs);
				//System.out.println("ROLL");
				str = "ROLL";
				if (inputs.getRoll() > 0) outputRoll = pidMaxRoll.getOutput( maxRoll, inputs.getRoll(), time);
				else 					  outputRoll = pidMaxRoll.getOutput(-maxRoll, inputs.getRoll(), time);
				pidHeading.reset();
			}
			else {
				//System.out.println("GEENROLL");
				str = "GEENROLL";
				pidMaxRoll.reset();
				if (maxRoll == 2000) {
					if (inputs.getHeading() > 0) outputRoll = pidHeading.getOutput( heading, inputs.getHeading(), time);
					else 						 outputRoll = pidHeading.getOutput(-heading, inputs.getHeading(), time);
				}
				else                             outputRoll = pidHeading.getOutput( heading, inputs.getHeading(), time);
			}
		}
		//System.out.println(str + " " + Math.abs(Math.abs(inputs.getHeading()) - Math.abs(heading)) * 360 / (2 * Math.PI) );
		//System.out.print(outputRoll + " ");
		outputRoll = aoaController.aoaRollController(outputVelY, outputRoll, (float) Math.PI/25);
		//proberen op dezelfde X/Z te blijven
		
		//System.out.println(outputRoll + " " + outputVerStab);
		//System.out.println(outputVerStab);
		//float leftWingInclination = outputVelY - (outputRoll + outputVerStab) / 2; //-
		//float rightWingInclination = outputVelY + (outputRoll + outputVerStab) / 2; //+
		float leftWingInclination = outputVelY - outputRoll;
		float rightWingInclination = outputVelY + outputRoll;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
		float horStabInclination = -outputPitch;
		float verStabInclination = 0;
		
		checkDone(inputs);
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}
	
	//Set when getOutputs() is first called
	public void setHeading(float heading) {
		this.heading = heading;
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	private void checkDone(AutopilotInputs inputs) {
		if (distance(firstX, firstZ, inputs.getX(), inputs.getZ()) > distance) {
			System.out.println("Actual Straight: " + inputs.getX() + " " + inputs.getZ());
			this.done = true;
		}
		else this.done = false;
	}
	
	public float distance(float x1, float y1, float x2, float y2) {
		float x = (float) Math.pow(x2-x1,2);
		float y = (float) Math.pow(y2-y1,2);
		float xy = (float) Math.sqrt(x+y);
		//System.out.println("Distance: " + xy);
		return xy;
	}

	@Override
	public String getMotion() {
		return "Straight";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		//SETUP
		this.firstX = inputs.getX();
		this.firstZ = inputs.getZ();
		Vector output = new Vector(0,0,0);
		if (dir == Dir.NORTH) {
			float z = inputs.getZ() - distance;
			output = new Vector(inputs.getX(), inputs.getY(), z);
			this.heading = 0;
			this.target = inputs.getX();
		}
		//Heading 90 graden
		else if (dir == Dir.WEST) {
			float x = inputs.getX() - distance;
			output = new Vector(x, inputs.getY(), inputs.getZ());
			this.heading = (float) (Math.PI/2);
			this.target = inputs.getZ();
		}
		//Heading 180/-180
		else if (dir == Dir.SOUTH) {
			float z = inputs.getZ() + distance;
			output = new Vector(inputs.getX(), inputs.getY(), z);
			this.heading = (float) (Math.PI);
			this.target = inputs.getX();
		}
		//Heading 270/-90
		else if (dir == Dir.EAST) {
			float x = inputs.getX() + distance;
			output = new Vector(x, inputs.getY(), inputs.getZ());
			this.heading = -(float) Math.PI/2;
			this.target = inputs.getZ();
		}
		//System.out.print("Prediction Straight: ");
		output.print();
		if (this.output == null) {
			this.output = output;
		}
		return output;
	}
	
	public float calculateAngle(AutopilotInputs inputs) {
		if (dir == Dir.NORTH) {
			float reqX = output.x;
			float x = Math.abs(reqX - inputs.getX());
			float z = Math.abs(output.z - inputs.getZ());
			float angle = (float) Math.atan(x/z);
			if (reqX > inputs.getX()) return -angle;
			else                      return angle;
		}
		else if (dir == Dir.WEST) {
			float reqZ = output.z;
			float z = Math.abs(reqZ - inputs.getZ());
			float x = (Math.abs(output.x - inputs.getX()));
			float angle = (float) Math.atan(z/x);
			if (reqZ > inputs.getZ()) return (float) (Math.PI/2 + angle);
			else                      return (float) (Math.PI/2 - angle);
		}
		else if (dir == Dir.SOUTH) {
			float reqX = output.x;
			float x = Math.abs(reqX - inputs.getX());
			float z = Math.abs(output.z - inputs.getZ());
			float angle = (float) Math.atan(x/z);
			if (inputs.getHeading() > 0) {
				if (reqX > inputs.getX()) return (float) (Math.PI + angle); // LINKS
				else                      return (float) (Math.PI - angle); // RECHTS
			}
			else {
				if (reqX > inputs.getX()) return (float) (-Math.PI + angle); // LINKS
				else                      return (float) (-Math.PI - angle); // RECHTS
			}
		}
		else if (dir == Dir.EAST) {
			float reqZ = output.z;
			float z = Math.abs(reqZ - inputs.getZ());
			float x = (Math.abs(output.x - inputs.getX()));
			float angle = (float) Math.atan(z/x);
			//System.out.println(angle);
			if (reqZ > inputs.getZ()) return (float) (-Math.PI/2 - angle); //RECHTS
			else                      return (float) (-Math.PI/2 + angle); //LINKS
		}
		
		return 0;
		
	}
	
	public static void main(String[] args) {
		Straight str = new Straight(100, Dir.NORTH);
		str.nextPos(new Inputs(null, 10, 0, 0, 0, 0, 0, 0));
		float angle = str.calculateAngle(new Inputs(null, 0, 0, 0, 0, 0, 0, 0));
		System.out.println(angle * 360 / (2 * Math.PI));
		
    }
	
	public float getMaxRoll(AutopilotInputs inputs) {
		float maxRoll = (float) Math.PI/180;
		if (Math.abs(inputs.getRoll()) > maxRoll) {
			if (dir == Dir.NORTH) {
				if (output.x > inputs.getX() && inputs.getRoll() > 0) {
					maxRoll = 0;
				}
				else if (output.x < inputs.getX() && inputs.getRoll() < 0) {
					maxRoll = 0;
				}
			}
			else if (dir == Dir.EAST) {
				if (output.z > inputs.getZ() && inputs.getRoll() > 0) {
					maxRoll = 0;
				}
				else if ( output.z < inputs.getZ() && inputs.getRoll() < 0) {
					maxRoll = 0;
				}
			}
			else if (dir == Dir.WEST) {
				if (output.z > inputs.getZ() && inputs.getRoll() < 0) {
					maxRoll = 0;
				}
				else if (output.z < inputs.getZ() && inputs.getRoll() > 0) {
					maxRoll = 0;
				}
			}
			else if (dir == Dir.SOUTH) {
				if (output.x > inputs.getX() && inputs.getRoll() < 0) {
					maxRoll = 0;
				}
				else if (output.x < inputs.getX() && inputs.getRoll() > 0) {
					maxRoll = 0;
				}
			}
		}
		return maxRoll;
	}
	

}
