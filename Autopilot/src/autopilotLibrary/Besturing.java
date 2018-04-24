package autopilotLibrary;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import enums.OccupationEnum;
import enums.PhaseEnum;

//import org.opencv.core.Point;

import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import interfaces.Path;

public class Besturing implements Runnable {
	
	private AutopilotOutputs outputs = new Outputs(0, 0, 0, 0, 0, 0, 0, 0);
	
	private Vliegen vliegen;
	private Taxi taxi;
	
	private Path path;
	
	private OccupationEnum occupation = OccupationEnum.FREE;

	private float thrust = 0.00f;
	private float leftWingInclination = 0.0f;
	private float rightWingInclination = 0.0f;
	private float horStabInclination = 0.0f;
	private float verStabInclination = 0.0f;
	private float frontBrakeForce=0.0f;
	private float leftBrakeForce=0.0f;
	private float rightBrakeForce=0.0f;
	private float totalMass;
	private AutopilotConfig config;
	private ArrayList<Vector> posList = new ArrayList<Vector>(); 
	private float rechtdoorHoek;
	private float lastLoopTime = 0;
	private float draaing90 = 9.776f;
	
	private ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	private PhaseEnum state = PhaseEnum.VLIEGEN; // PAS AAN ALS JE WILT TAXIEN
	
	
	private AutopilotInputs autopilotInputs;
	
	private float maxAOA = (float) Math.PI/18f; 
	
	private float lastX = 0;
	private float lastY = 0;
	private float lastZ = 0;
	float outputVelY = 0;
	private float goalYspeed=0;
	private float time = 0;
	
	int k = 5;
	
	
	public Besturing() {
		this.vliegen = new Vliegen(this);
		this.taxi = new Taxi(this);
		System.out.println(vliegen.distance(new Vector(0,40, -1000), new Vector(280, 40,-2000)));
	}
	
	public void setConfig(AutopilotConfig config) {
		this.config = config;
		this.totalMass = config.getEngineMass() + config.getTailMass() + (2* config.getWingMass());
	}
	
	public void startBesturing(AutopilotInputs inputs) {
		//AutopilotOutputs outputs = new Outputs(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f);
		setTime(inputs);
		switch(state) {
		case VLIEGEN:
			outputs = vliegen.vliegen(inputs);
			break;
		case TAXIEN:
			outputs = taxi.taxi(inputs);
			break;
		}
	}
	
	public void setTime(AutopilotInputs inputs) {
		double time1 = inputs.getElapsedTime();
		float elapTime = (float)(time1 - lastLoopTime);
		lastLoopTime = (float) time1;
		this.time = elapTime;
	}
	
	public float getTime() {
		return this.time;
	}
	
	public void setPath(Path path) {
		this.path = path;
		//vliegen.setPath(path);
	}

	@Override
	public void run() {
		startBesturing(autopilotInputs);
	}
	
	public void setInputs(AutopilotInputs inputs) {
		this.autopilotInputs = inputs;
	}
	
	public AutopilotOutputs getOutputs() {
		System.out.println(outputs.getThrust());
		return this.outputs;
	}

	public PhaseEnum getState() {
		if (state == PhaseEnum.TAXIEN) {
			return state;
		}
		else {
			return vliegen.getPhase();
		}
	}
	
	public OccupationEnum getOccupation() {
		return occupation;
	}
	
}
