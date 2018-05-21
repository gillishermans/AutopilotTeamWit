package autopilotLibrary;

import java.util.ArrayList;
import java.util.LinkedList;

import org.opencv.core.Point;

import enums.Dir;
import enums.PhaseEnum;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import interfaces.Path;
import motions.Ascend;
import motions.Braking;
import motions.CubeStab;
import motions.FullTurnL;
import motions.FullTurnR;
import motions.Idle;
import motions.ImageRecognition;
import motions.Init;
import motions.Landing;
import motions.Motion;
import motions.STurn;
import motions.Stabelize;
import motions.Straight;
import motions.Takeoff;
import util.MiniPID;
import util.PIDController;
import util.Vector;

public class Vliegen {

	private ArrayList<Vector> path = new ArrayList<Vector>();
	private boolean firstCube = true;
	
	private AutopilotConfig config;

	private String str = "STAB";
	
	private boolean done = false;

	private Besturing besturing;

	private Beeldherkenning beeldherkenning;

	private ArrayList<Point> centerArray;
	private ArrayList<Float> radiusArray;
	private ArrayList<Vector> posList = new ArrayList<Vector>();

	public ArrayList<Vector> getPosList() {
		return this.posList;
	}

	public ArrayList<Motion> motions = new ArrayList<Motion>();
	private ArrayList<Motion> landingSequence = new ArrayList<Motion>();

	// PID's VLIEGEN OBV BEELDHERKENNING
	

	private float lastLoopTime = 0;
	private float time = 0;
	
	private final float LEFT_OFFSET = 579;
	private final float RIGHT_OFFSET = 579;

	float heading;

	private PhaseEnum phase = PhaseEnum.INIT;

	private boolean left = false;

	private int motion = 0;
	private int land = 0;

	private float x;
	private float y;
	private float z;
	private int index = 0;

	private Motion currentMotion = new Init();
	//Cube1
	private Vector cube1 = new Vector(0,40,0);
	private Vector cube2 = new Vector(-1900,40,-2000);
	private Vector cube3 = new Vector(-2000,40,1000);
	private Vector cube4 = new Vector(2000,40,0);
	private Vector cube5 = new Vector(-1000,40,2000);
	private Vector cube6 = new Vector(2000, 0, -2000);
	private Vector cube7 = new Vector(4000, 40, 0);
	private Vector cube8 = new Vector(0, 40, -2000);
	private Vector cube9 = new Vector(500,40,-1500);
	
	private Vector cube;
	
	private LinkedList<Vector> cubes = new LinkedList<Vector>();
	
	private boolean behind = true;
	
	private boolean cubeDir = false;
	
	private boolean pathplan = true;
	
	private Dir dir = Dir.NORTH;
	
	private AutopilotOutputs lastOutput;
	

	public Vliegen(Besturing besturing, AutopilotConfig config) {
		//cube = cube6;
		//cubes.add(cube7);
		cube = cube9;
//		cube = cube1;
		cubes.add(cube1);
		cubes.add(cube3);
		//cubes.add(cube4);
		//cubes.add(cube5);
		this.besturing = besturing;
		this.config = config;
//		this.path.add(new Vector(100, 35, -2000));
//		this.path.add(new Vector(80, 30, -3000));
//		this.path.add(new Vector(10, 30, -4000));
//		this.path.add(new Vector(-10, 30, -5000)); 
		// this.beeldherkenning = beeldherkenning;
		//this.motions.add(new ImageRecognition(config, cube));
		this.motions.add(new Takeoff());
		this.motions.add(new Ascend());
		this.motions.add(new Stabelize());
//		this.motions.add(new FullTurnR((float) -Math.PI/2));
//		this.motions.add(new Straight(100, Dir.EAST));
//		this.motions.add(new FullTurnR((float) -Math.PI));
//		this.motions.add(new Straight(100, Dir.SOUTH));
//		this.motions.add(new FullTurnR((float) Math.PI/2));
		//this.motions.add(new FullTurnR(-(float) Math.PI/2));
		//this.motions.add(new FullTurnL((float) Math.PI/2));
//		this.motions.add(new Straight(430)); //430
//		this.motions.add(new FullTurnL((float) Math.PI/2));
//		this.motions.add(new Straight(990));
//		//this.motions.add(new FullTurnL((float) Math.PI));
//		this.motions.add(new ImageRecognition(config, new Vector(-2000,40,-2000)));
		this.landingSequence.add(new Landing());
		this.landingSequence.add(new Braking());
		this.landingSequence.add(new Idle());
	}
	
	public void pathPlanner(AutopilotInputs inputs) {
		switch(dir) {
		case NORTH:
			System.out.println("PATHPLANNING...");
			//KUBUS VOOR DRONE EN BINNEN DE 10M
			if(Math.abs(cube.x - inputs.getX()) <= 10 && inputs.getZ() > cube.z) {
				System.out.println("KUBUS LIGT VOOR DE DRONE");
				float dis = (Math.abs(inputs.getZ() - cube.z) - 200);
				motions.add(new Straight(dis, dir, new Vector(cube.x, cube.y, cube.z + 200)));
				motions.add(new ImageRecognition(config, cube));
				motions.add(new CubeStab(dir));
				cubeDir = true;
				pathplan = false;
			}
			else if (Math.abs(cube.x - inputs.getX()) <= 800 && inputs.getZ() - cube.z < 750) {
				System.out.println("TE KORT, OMWEG MAKEN");
				motions.add(new Straight(100, dir));
				if (cube.x > inputs.getX()) {
					motions.add(new FullTurnL((float) Math.PI/2));
					dir = Dir.WEST;
				}
				else {
					motions.add(new FullTurnR((float) -Math.PI/2));
					dir = Dir.EAST;
				}
				pathplan = false;
			}
			//LINKS
			else if (cube.x <= inputs.getX()) {
				System.out.println("LINKS"); //TODO
				if (cube.z > inputs.getZ()) {
					System.out.println(cube.z + " " + inputs.getZ());
					float dis = 0;
					//3 Bochten nodig
					if (cube.x + 1340 > inputs.getX()) {
						System.out.println("3 bochten naar links");
						if (cube.z - 900 < inputs.getZ()) dis = Math.abs(cube.z - 900 - inputs.getZ() + 100);
						else 							  dis = 100;
						System.out.println("Dis: " + dis);
					}
					//2 Bochten
					else {
						System.out.println("2 Bochten naar links");
						if (cube.z - 160 < inputs.getZ()) dis = Math.abs(cube.z - 160 - inputs.getZ() + 100);
						else 							  dis = 100;
						
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float)Math.PI/2));
				}
				//1 BOCHT NODIG Buiten interval
				else if (Math.abs(inputs.getZ() - cube.z) > LEFT_OFFSET){
					System.out.println("1 Bocht naar links");
					float dis = Math.abs(cube.z - inputs.getZ()) - LEFT_OFFSET;
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float) Math.PI/2));
				}
				dir = Dir.WEST;
				pathplan = false;
			}
			else {
				System.out.println("RECHTS"); //TODO
				if (cube.z > inputs.getZ()) {
					float dis = 0;
					//3 Bochten nodig
					if (cube.x - 1340 < inputs.getX()) {
						System.out.println("3 RECHTSE BOCHTEN Resterend");
						if (cube.z - 900 < inputs.getZ()) dis = Math.abs(cube.z - 900 - inputs.getZ() + 100);
						else 							  dis = 100;
						System.out.println("Dis: " + dis);
					}
					//2 Bochten
					else {
						System.out.println("2 RECHTSE BOCHTEN RESTEREND");
						if (cube.z - 160 < inputs.getZ()) dis = Math.abs(cube.z - 160 - inputs.getZ() + 100);
						else 							  dis = 100;
						
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float)-Math.PI/2));
				}
				//1 BOCHT NODIG
				else {
					System.out.println("1 BOCHT RESTEREND");
					float dis = Math.abs(cube.z - inputs.getZ()) - RIGHT_OFFSET;
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float) -Math.PI/2));
				}
				dir = Dir.EAST;
				pathplan = false;
			}
			break;
		case EAST:
			if (Math.abs(inputs.getZ() - cube.z) < 10 && cube.x > inputs.getX()) {
				System.out.println("GEEN BOCHTEN MEER");
				float dis = (Math.abs(cube.x - inputs.getX()) - 200);
				System.out.println("Dis: " + dis + " cZ: " + cube.z + " dZ: " + inputs.getZ());
				motions.add(new Straight(dis, dir,new Vector(cube.x - 200, cube.y, cube.z)));
				motions.add(new ImageRecognition(config, cube));
				motions.add(new CubeStab(dir));
				cubeDir = true;
				pathplan = false;
			}
			//LINKS
			else if (cube.z < inputs.getZ()) {
				float dis;
				//ACHTER
				if (cube.x < inputs.getX()) {
					if (cube.z + 1340 > inputs.getZ()) {
						//3 BOCHTEN
						System.out.println("3 LINKSE BOCHTEN RESTEREND");
						if (cube.x + 900 < inputs.getX()) dis = Math.abs(cube.x + 900 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					else {
						System.out.println("2 LINKSE BOCHTEN RESTEREND");
						if (cube.x + 160 < inputs.getX()) dis = Math.abs(cube.x + 160 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL(0));
				}
				else {
					System.out.println("1 LINKSE BOCHT RESTEREND");
					dis = 0;
					if (cube.x < inputs.getX()) dis = 0;
					else 						dis = (Math.abs(cube.x - inputs.getX()) - LEFT_OFFSET);
					System.out.println(dis);
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float) 0));
					
				}
				dir = Dir.NORTH;
			}
			//RECHTS
			else {
				float dis = 0;
				if (cube.x < inputs.getX()) {
					if (cube.z - 1340 < inputs.getZ()) {
						//3 BOCHTEN
						System.out.println("3 RECHTSE BOCHTEN RESTEREND");
						if (cube.x + 900 > inputs.getX()) dis = Math.abs(cube.x + 900 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					else {
						System.out.println("2 RECHTSE BOCHTEN RESTEREND");
						if (cube.x + 160 > inputs.getX()) dis = Math.abs(cube.x + 160 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float) -Math.PI));
				}
				else {
					System.out.println("1 RECHTSE BOCHT RESTEREND");
					dis = 0;
					if (cube.x < inputs.getX()) dis = 0;
					else 						dis = (Math.abs(cube.x - inputs.getX()) - LEFT_OFFSET);
					//System.out.println(dis);
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float) -Math.PI));
					
				}
				
				dir = Dir.SOUTH;
				pathplan = false;
			}
			break;
		case SOUTH:
			if (Math.abs(inputs.getX() - cube.x) < 10 && inputs.getZ() < cube.z) {
				System.out.println("OP NAAR DE KUBUS");
				float dis = (Math.abs(cube.z - inputs.getZ()) - 200);
				motions.add(new Straight(dis, dir, new Vector(cube.x, cube.y, cube.z - 200)));
				motions.add(new ImageRecognition(config, cube));
				motions.add(new CubeStab(dir));
				pathplan = false;
				cubeDir = true;
			}
			//LINKS
			else if (cube.x >= inputs.getX()) {
				//KUBUS LIGT ACHTER
				float dis;
				if (inputs.getZ() > cube.z) {
					//3 bochten
					if (cube.x + 1340 > inputs.getX()) {
						System.out.println("3 LINKSE BOCHTEN RESTEREND");
						if (cube.z + 900 > inputs.getZ()) dis = Math.abs(cube.z + 900 - inputs.getZ() + 100);
						else 							  dis = 100;
					}
					else {
						System.out.println("2 LINKSE BOCHTEN RESTEREND");
						if (cube.z + 160 > inputs.getZ()) dis = Math.abs(cube.z + 160 - inputs.getZ() + 100);
						else 							  dis = 100;
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float) -Math.PI/2));
				}
				else {
					System.out.println("1 LINKSE BOCHT RESTEREND");
					dis = 0;
					if (cube.z < inputs.getZ()) dis = 0;
					else 						dis = (Math.abs(cube.z - inputs.getZ()) - LEFT_OFFSET);
					System.out.println(dis);
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float) -Math.PI/2));
				}
				dir = Dir.EAST;
				pathplan = false;
			}
			//RECHTS
			else if (cube.x < inputs.getX()) {
				float dis = 0;
				if (inputs.getZ() > cube.z) {
					//3 bochten
					if (cube.x + 1340 > inputs.getX()) {
						System.out.println("3 RECHTS BOCHTEN RESTEREND");
						if (cube.z + 900 > inputs.getZ()) dis = Math.abs(cube.z + 900 - inputs.getZ() + 100);
						else                              dis = 100;
					}
					else {
						System.out.println("2 BOCHTEN RESTEREND");
						if (cube.z + 160 > inputs.getZ()) dis = Math.abs(cube.z + 160 - inputs.getZ() + 100);
						else                              dis = 100;
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float) Math.PI/2));
				}
				else {
					System.out.println("1 BOCHTEN RESTEREND");
					if (cube.z < inputs.getZ()) dis = 0;
					else 						dis = (Math.abs(cube.z - inputs.getZ()) - LEFT_OFFSET);
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float) Math.PI/2));
				}
				dir = Dir.WEST;
				pathplan = false;
			}
			break;
		case WEST:
			//GEEN BOCHTEN MEER
			if (Math.abs(inputs.getZ() - cube.z) < 10 && inputs.getX() > cube.x) {
				System.out.println("GEEN BOCHTEN MEER");
				float dis = (Math.abs(cube.x - inputs.getX()) - 200);
				System.out.println("Dis: " + dis + " cZ: " + cube.z + " dZ: " + inputs.getZ());
				motions.add(new Straight(dis, dir, new Vector(cube.x + 200, cube.y, cube.z)));
				motions.add(new ImageRecognition(config, cube));
				motions.add(new CubeStab(dir));
				cubeDir = true;
				pathplan = false;
			}
			//LINKS
			else if (cube.z > inputs.getZ()) {
				float dis;
				//ACHTER
				if (cube.x > inputs.getX()) {
					if (cube.z + 1340 > inputs.getZ()) {
						//3 BOCHTEN
						System.out.println("3 LINKSE BOCHTEN RESTEREND");
						if (cube.x - 900 < inputs.getX()) dis = Math.abs(cube.x - 900 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					//2 BOCHTEN
					else {
						System.out.println("2 LINKSE BOCHTEN RESTEREND");
						if (cube.x - 160 < inputs.getX()) dis = Math.abs(cube.x - 160 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float)  Math.PI));
				}
				//VOOR
				else {
					System.out.println("1 LINKSE BOCHT RESTEREND");
					dis = 0;
					if (cube.x > inputs.getX()) dis = 0;
					else 						dis = (Math.abs(cube.x - inputs.getX()) - LEFT_OFFSET);
					System.out.println(dis);
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnL((float) Math.PI));
					
				}
				dir = Dir.SOUTH;
			}
			//RECHTS
			else {
				float dis = 0;
				//ACHTER
				if (cube.x > inputs.getX()) {
					if (cube.z + 1340 > inputs.getZ()) {
						//3 BOCHTEN
						System.out.println("3 RECHTSE BOCHTEN RESTEREND");
						if (cube.x + 900 < inputs.getX()) dis = Math.abs(cube.x + 900 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					else {
						System.out.println("2 RECHTSE BOCHTEN RESTEREND");
						if (cube.x + 160 < inputs.getX()) dis = Math.abs(cube.x + 160 - inputs.getX() + 100);
						else 							  dis = 100;
					}
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR(0));
				}
				else {
					System.out.println("1 RECHTSE BOCHT RESTEREND");
					dis = 0;
					if (cube.x > inputs.getX()) dis = 0;
					else 						dis = (Math.abs(cube.x - inputs.getX()) - LEFT_OFFSET);
					System.out.println(dis);
					motions.add(new Straight(dis, dir));
					motions.add(new FullTurnR((float) 0));
					
				}
				
				dir = Dir.NORTH;
				pathplan = false;
			}
			break;
		}
	}

	public void setBeeldherkenning(Beeldherkenning beeldherkenning) {
		this.beeldherkenning = beeldherkenning;
	}

	public void setPath(Path path) {
		for (int i = 0; i < path.getX().length; i++) {
			this.path.add(new Vector(path.getX()[i], path.getY()[i], path.getZ()[i]));
		}
	}

	public AutopilotOutputs vliegen(AutopilotInputs inputs) {
		setTime(inputs);
		float horizontalAngle = 0;
		float verticalAngle = 0;
		beeldherkenning.imageRecognition(inputs.getImage());
		// if (path.isEmpty()) phase = Phase.GEENKUBUS;

		centerArray = beeldherkenning.getCenterArray();
		radiusArray = beeldherkenning.getRadiusArray();
		// ArrayList<double[]> colorArray = beeldherkenning.getColorArray();

		int j = 0;

		getPosList().add(new Vector(inputs.getX(), inputs.getY(), inputs.getZ()));
		// time += inputs.getTime();

		int index = getPosList().size() - 1;
		Vector speedVector = new Vector(0, 0, -10);
		float speed = 10f;
		if (getPosList().size() <= 1) {
			speedVector = new Vector(0, 0, 0);
			speed = 10f;
		} else {
			speedVector = Vector.min(getPosList().get(index), getPosList().get(index - 1));
			speedVector = Vector.scalarProd(speedVector, 1 / getTime());
			speed = Vector.norm(speedVector);
		}

		if (currentMotion.isDone()) {
			if (!cubeDir) {
				if (currentMotion instanceof FullTurnL || currentMotion instanceof FullTurnR || currentMotion instanceof Stabelize) {
					System.out.print(dir + ": ");
					pathPlanner(inputs);
				}
			}
			else {
				if (currentMotion instanceof CubeStab && cubes.isEmpty()) {
					this.done = true;
				}
				if (currentMotion instanceof CubeStab && !cubes.isEmpty()) {
					System.out.println(dir);
					cube = cubes.removeFirst();
					pathPlanner(inputs);
					this.cubeDir = false;
				}
			}
			
			
			if (done) {
				System.out.println("Geen Kubussen meer");
				currentMotion = landingSequence.get(land);
				land++;
			}
			else {
				currentMotion = motions.get(motion);
				if (currentMotion instanceof CubeStab) {
					((CubeStab) currentMotion).setInit(lastOutput.getLeftWingInclination(), lastOutput.getRightWingInclination(), lastOutput.getHorStabInclination());
				}
				currentMotion.nextPos(inputs);
				motion++;
			}
		}
		currentMotion.setSpeed(speed);
		currentMotion.setSpeedVector(speedVector);

		AutopilotOutputs output = currentMotion.getOutputs(inputs, getTime());
		lastOutput = output;

		// System.out.println(phase + " " + str + " thr " + thrust + " left " +
		// leftWingInclination + " right " + rightWingInclination + " hor " +
		// horStabInclination + " hor " + horizontalAngle + " roll " +
		// inputs.getRoll());
		// System.out.println("HorSTAB " + k + ": " +
		// getAngleOfAttack(speedVector,rightWingInclination)*360/(2*Math.PI));
		return output;
	}

	public void setNextPos() {
		if (!path.isEmpty()) {
			if (firstCube) {
				firstCube = false;
			} else {
				path.remove(0);
			}

			if (path.isEmpty()) {
				phase = PhaseEnum.LANDEN;
				System.out.println("GEEN KUBUS");
			} else {
				x = path.get(0).x;
				y = path.get(0).y;
				z = path.get(0).z;
				System.out.println("VOLGENDE KUBUS OP: " + x + " " + y + " " + z + " " + index);
			}
		} else {
			phase = PhaseEnum.LANDEN;
			System.out.println("GEEN KUBUS MEER");
		}
	}

	// HULPFUNCTIES
	public float distance(Vector v1, Vector v2) {
		float x1 = (float) (Math.pow(v1.x - v2.x, 2));
		float y1 = (float) (Math.pow(v1.y - v2.y, 2));
		float z1 = (float) (Math.pow(v1.z - v2.z, 2));
		// System.out.println(x1 + " " + y1 + " " + z1);
		float total = Math.abs(x1 + y1 + z1);
		// System.out.println(total);
		return (float) Math.sqrt(total);
	}

	public float calculateHeading(AutopilotInputs inputs, float x, float z) {
		float currX = inputs.getX();
		float currZ = inputs.getZ();
		// System.out.println(currZ);
		float b = -z + currZ;
		float c = -x + currX;
		float a = (float) Math.sqrt((b * b) + (c * c));
		float cos = ((a * a) + (b * b) - (c * c)) / (2 * a * b);
		// System.out.println(cos);
		if (cos > 1)
			cos = -(1 - cos);
		// System.out.println("A: " + a + "B: " + b + "C: " + c);
		// System.out.println("Heading: " + toDegrees(inputs.getHeading()) + " X: " +
		// currX + " Z: " + currZ + " A: " + a + " B: " + b + " C: " + c + " " + cos + "
		// " +toDegrees((float) Math.acos(cos)) + " " + toDegrees(inputs.getHeading() -
		// (float) Math.acos(cos)));
		if (left)
			return (float) (Math.acos(cos));
		else
			return -(float) Math.acos(cos);
	}

	public float toDegrees(float r) {
		return (float) (r * 360 / (2 * Math.PI));
	}

	public void setTime(AutopilotInputs inputs) {
		double time1 = inputs.getElapsedTime();
		float elapTime = (float) (time1 - lastLoopTime);
		lastLoopTime = (float) time1;
		this.time = elapTime;
	}

	public float getTime() {
		return time;
	}

	public String getPhase() {
		return this.currentMotion.getMotion();

	}

	public String getInfo() {
		return this.str;
	}

	public void setStab() {
		this.str = "STAB";
	}

	public void setHeading() {
		this.str = "HEADING";
	}

	public void setConfig(AutopilotConfig config) {
		this.config = config;
	}

	public Vector getCube() {
		return cube;
	}

}
