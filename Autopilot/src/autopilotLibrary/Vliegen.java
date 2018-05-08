package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;

import enums.PhaseEnum;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import interfaces.Path;

public class Vliegen {
	private HashMap<Integer,Airport> airports ;
	private ArrayList<Vector> path = new ArrayList<Vector>();
	private boolean firstCube = true;
	
	private AutopilotOutputs outputs;
	
	private String str = "niks";
	
	private Besturing besturing;
	private AOAController aoaController = new AOAController();
	
	private final float maxRoll = (float) (Math.PI/8);
	
	private ArrayList<Vector> posList = new ArrayList<Vector>();
	
	private float t;
	
	public ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	private float rightWingInclination,leftWingInclination,thrust,horStabInclination,verStabInclination;
	private float leftBrakeForce,rightBrakeForce,frontBrakeForce =0;
	
	private PIDController pidVelY = new PIDController(1f,1f,0f,(float) Math.PI/10f, (float) - Math.PI/10f, 20);
	//private PIDController pidVelX = new PIDController(1f,1f,1f, (float) Math.PI/20f, (float) - Math.PI/10f, 20);
	private PIDController pidPitch = new PIDController(2f,0.5f,0.5f,(float) Math.PI/6f, (float) -Math.PI/6f, 1);
	private PIDController pidTrust = new PIDController(5f,5,1,2000, 0, 0.1f);
	
	//PID's VLIEGEN NAAR POSITIE
	private PIDController pidRoll = new PIDController(4f,1,1.5f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidStab = new PIDController(3,1,1,(float) Math.PI/6, -(float) Math.PI/6,20);
	private PIDController pidHeading = new PIDController(0.5f,1f,0.5f,(float) Math.PI/1, (float) -Math.PI/1, 10);
	
	//PID's VLIEGEN OBV BEELDHERKENNING
	private boolean resetHeading = false;
	private boolean resetStabilization = false;
	
	private float lastLoopTime = 0;
	private float time = 0;
	
	private float lastDistance = 0;
	
	private float lastInclRight = 0;
	private float lastInclLeft = 0;
	
	private PhaseEnum phase = PhaseEnum.INIT;
	
	
	private boolean first = true;
	
	private int k = 5;
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	private float outputVelY = 0;
	private float goalYspeed=0;
	
	private boolean landen = false;
	private float timeLanden = 0;
	
	private boolean pos = true;
	private boolean left = false;
	private boolean forward = true;
	private boolean poscube = true;
	
	private final float maxRollAOA = (float) Math.PI/20;
	
	private float x;
	private float y;
	private float z;
	private int index = 0;
	
	private float lastOutputHor;
	
	private float interval = 90;
	
	
	public Vliegen(Besturing besturing) {

		this.besturing = besturing;
	}
	
	public void setPath(Path path) {
		for (int i= 0; i < path.getX().length; i++) {
			this.path.add(new Vector(path.getX()[i], path.getY()[i], path.getZ()[i]));
		}
	}
	
	/**
	 * Initialiseer drone.
	 */
	public AutopilotOutputs init(AutopilotInputs inputs ){
		
		System.out.println("INIT");
		rightWingInclination = (float) (Math.PI/20);
		leftWingInclination = (float) (Math.PI/20);
		thrust = 80f;
		horStabInclination = 0f;
		verStabInclination = 0f;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	/**
	 * Versnellen om op te stijgen.
	 */
	public AutopilotOutputs rijden(AutopilotInputs inputs ){
		
		thrust = 2000;
		leftWingInclination = (float)  Math.PI/60;
		rightWingInclination = leftWingInclination;
		horStabInclination =  (float )Math.PI/60;
		verStabInclination = 0;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	/**
	 * Opstijgen.
	 */
	public AutopilotOutputs opstijgen(AutopilotInputs inputs ){
		
		thrust = 2000;
		leftWingInclination = (float) Math.PI/20;
		rightWingInclination = leftWingInclination;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
		horStabInclination = -outputPitch;
		verStabInclination = 0;
		
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	/**
	 * Drone stabiliseren.
	 */
	public AutopilotOutputs stabiliseren(AutopilotInputs inputs, float speed, Vector speedVector  ){
		
		thrust = pidTrust.getOutput(65f, speed, getTime());
		float outputVelY = pidVelY.getOutput(0,speedVector.y, getTime());
		outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		leftWingInclination = outputVelY;
		rightWingInclination = outputVelY;
		float outputPitch1 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
		outputPitch1 = aoaController.aoaController(outputPitch1, (float) Math.PI/20);
		horStabInclination = -outputPitch1;
		verStabInclination = 0f;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	/**
	 * Draai inzetten.
	 */
	public AutopilotOutputs  draai(AutopilotInputs inputs, float speed , Vector speedVector, float teken ){
		float outputRoll;
		float i =0;
		i=+1;
		float maxRoll = (float) Math.PI/20;
		thrust = pidTrust.getOutput(65,speed,getTime());
		float heading = calculateHeading(inputs);
		outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
		//outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		if (Math.abs(heading - inputs.getHeading()) < (float) Math.PI/interval) {
			if (interval < 360) {
				interval = interval + 1;
			}
			outputRoll = pidStab.getOutput(0,inputs.getRoll(),getTime());
			if (first) {
				first = false;
				System.out.println("Stab");
			}
		} 
		else {
			if (!first) {
				first = true;
				
				pidHeading.reset();
			}
			if (Math.abs(inputs.getRoll()) > maxRoll) {
				str = "MAXROLL";
				if (inputs.getRoll() > 0) outputRoll = pidRoll.getOutput(maxRoll, inputs.getRoll(), getTime());
				else                      outputRoll = pidRoll.getOutput(-maxRoll, inputs.getRoll(), getTime());
			} else {
					outputRoll = pidHeading.getOutput(heading, inputs.getHeading(), getTime());
				}
			}
		outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, (float) Math.PI / 20);
		leftWingInclination = -outputVelY - teken*outputRoll;
		rightWingInclination = -outputVelY + teken*outputRoll;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
		//outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/20);
		horStabInclination = -outputPitch;
		verStabInclination = 0;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	public AutopilotOutputs  Stabiliseren1 (AutopilotInputs inputs, float speed , Vector speedVector ){
		thrust = pidTrust.getOutput(65,speed,getTime());
		//System.out.println("STABILISEREN");
		outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
		float outputRoll = pidRoll.getOutput(0,inputs.getRoll(),getTime());
		leftWingInclination = -outputVelY - outputRoll;
		rightWingInclination = -outputVelY + outputRoll;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), getTime());
		horStabInclination = -outputPitch;
		verStabInclination = 0;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	public AutopilotOutputs  geenkubus(AutopilotInputs inputs, float speed , Vector speedVector ){
		float outputRoll;
		if (first) {
			pidRoll.reset();
			first = false;
		}
		thrust = pidTrust.getOutput(65f, speed, getTime());
		outputVelY = -pidVelY.getOutput(0,speedVector.y, getTime());
		outputVelY = aoaController.aoaController(outputVelY, (float) Math.PI/20);
		outputRoll = pidRoll.getOutput(0, inputs.getRoll(), getTime());
		outputRoll = aoaController.aoaRollController(-outputVelY, outputRoll, maxRollAOA);
		leftWingInclination = -outputVelY - outputRoll;
		rightWingInclination = -outputVelY + outputRoll;
		float outputPitch1 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
		outputPitch1 = aoaController.aoaController(outputPitch1, (float) Math.PI/25);
		horStabInclination = -outputPitch1;
		verStabInclination = 0f;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);

	}
	
	public AutopilotOutputs  Landen(AutopilotInputs inputs,float speed, Vector speedVector ){
		thrust = pidTrust.getOutput(65f, speed, getTime());
		pidVelY.reset();
		pidPitch.reset();
		float outputVelY1 = -pidVelY.getOutput(-1.5f,speedVector.y,getTime());
		outputVelY1 = aoaController.aoaController(outputVelY1, (float) Math.PI/20);
		leftWingInclination = -outputVelY1;
		rightWingInclination = -outputVelY1;
		float outputPitch2 = pidPitch.getOutput(0, inputs.getPitch(), getTime());
		outputPitch2 = aoaController.aoaController(outputPitch2, (float) Math.PI/20);
		horStabInclination = -outputPitch2;
		verStabInclination = 0;
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	public AutopilotOutputs  Remmen (AutopilotInputs inputs ){
		thrust = 0;
		leftWingInclination = - (float) Math.PI/60;
		rightWingInclination = leftWingInclination;
		horStabInclination = 0;
		verStabInclination = 0;
		frontBrakeForce = 1600;
		rightBrakeForce = 1600;
		leftBrakeForce = 1600;
		
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);

	}
	
	/**
	 * Hoofdfunctie: bepaalt welke stap van het vliegprocess moet gebeuren.
	 */
	public AutopilotOutputs vliegen(AutopilotInputs inputs, float[] doel,Vector speedVector, HashMap<Integer,Airport> airports ){
		
		setTime(inputs);
		float horizontalAngle = 0;	
		float verticalAngle = 0;
		
		int j = 0;
		
		float speed = Vector.norm(speedVector);

		//Geen kubus gevonden -> vlieg rechtdoor
		//System.out.println(distance(new Vector(inputs.getX(), inputs.getY(), inputs.getZ()), new Vector(x,y,z)));
		
		frontBrakeForce = 0;
		rightBrakeForce = 0;
		leftBrakeForce = 0;
		
		if (getTime() == 0) phase = PhaseEnum.INIT;
		
		switch(phase) {
		case INIT: 
			this.init(inputs);
			phase = PhaseEnum.RIJDEN;
			System.out.println("RIJDEN");
			break;
			
		case RIJDEN:
			this.rijden(inputs);
			if (getTime() < 3) { 
				phase = PhaseEnum.OPSTIJGEN;
				System.out.println("OPSTIJGEN");
			}
			break;
			
		case OPSTIJGEN:
			this.opstijgen(inputs);
			
			if (inputs.getY() > 40) {
				System.out.println("STABILISEREN");
				phase = PhaseEnum.STABILISEREN;
				pidPitch.reset();
			}
			break;
			
		case STABILISEREN:
			this.stabiliseren(inputs, speed, speedVector);
			if (inputs.getZ() < -800) {
				System.out.println("POSITIE");
				phase = PhaseEnum.RECHTDOOR;
				t = getTime();
				//setNextPos();
			}
			break;
			
		case LANDEN:
			this.Landen(inputs, speed, speedVector);
			if (inputs.getY() < 1.5f) {
				System.out.println("REMMEN");
				phase = PhaseEnum.REMMEN;
			}
			break;
			
		case LINKS:
			this.draai(inputs, speed, speedVector,1);
			if (inputs.getZ() < -1000) {
				phase = PhaseEnum.STABILISEREN1;
				t = getTime();
			}
			break;
			
		case STABILISEREN1:
			this.Stabiliseren1(inputs, speed, speedVector);
			System.out.print("hier" + this.airports.get(1).getX());
			if (Math.abs(inputs.getZ() - this.airports.get(1).getX())<1000){
				System.out.println("LANDEN");
				phase = PhaseEnum.LANDEN;
			}
			break;
			
		case RECHTS:
			this.draai(inputs, speed, speedVector, -1);
			break;
			
		case RECHTDOOR:
			this.geenkubus(inputs, speed, speedVector);
			if (!landen) {
				timeLanden = inputs.getElapsedTime();
				landen = true;
			}
			if (inputs.getElapsedTime() - timeLanden > 3.5f) {
				System.out.println("LANDEN");
				phase = PhaseEnum.LANDEN;
			}
			break;
			
		case REMMEN:
			this.Remmen(inputs);
		}
		
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	
	public void setNextPos() {
		if (!path.isEmpty()) {
			if (firstCube) {
				firstCube = false;
			}
			else {
				path.remove(0);
			}
			
			if (path.isEmpty()) {
				phase = PhaseEnum.RECHTDOOR;
				System.out.println("GEEN KUBUS");
			} else {
				x = path.get(0).x;
				y = path.get(0).y;
				z = path.get(0).z;
				System.out.println("VOLGENDE KUBUS OP: " + x + " " + y + " " + z + " " + index);
			}
		}
		else {
			phase = PhaseEnum.RECHTDOOR;
			System.out.println("GEEN KUBUS MEER");
		}
	}
	
	public float distance(Vector v1, Vector v2) {
		float x1 = (float) (Math.pow(v1.x - v2.x, 2));
		float y1 = (float) (Math.pow(v1.y - v2.y, 2));
		float z1 = (float) (Math.pow(v1.z - v2.z, 2));
		float total = Math.abs(x1 + y1 + z1);
		return (float) Math.sqrt(total);
	}
	
	public float calculateHeading(AutopilotInputs inputs) {
		float currX = inputs.getX();
		float currZ = inputs.getZ();
		float b = -z + currZ;
		float c = -x + currX;
		float a = (float) Math.sqrt((b*b) + (c*c));
		float cos = ((a*a) + (b*b) - (c*c)) / (2*a*b);
		if (cos > 1) cos = -(1-cos);
		if (left) return (float) (Math.acos(cos));
		else      return -(float) Math.acos(cos);
	}
	
	public float toDegrees(float r) {
		return (float) (r*360/(2*Math.PI));
	}
	
	public void setTime(AutopilotInputs inputs) {
		double time1 = inputs.getElapsedTime();
		float elapTime = (float)(time1 - lastLoopTime);
		lastLoopTime = (float) time1;
		this.time = elapTime;
	}
	
	public float getTime() {
		return time;
	}

	public PhaseEnum getPhase() {
		return this.phase;
	}
	
}
