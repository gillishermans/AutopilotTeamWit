package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import enums.OccupationEnum;
import enums.PhaseEnum;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import interfaces.Path;

public class Besturing implements Runnable {
	
	private AutopilotOutputs outputs = new Outputs(0, 0, 0, 0, 0, 0, 0, 0);
	
	private Vliegen vliegen;
	private Taxi taxi;

	private OccupationEnum occupation = OccupationEnum.FREE;
	private PhaseEnum state = PhaseEnum.TAXIEN;
	
	private Path path;
	private AutopilotConfig config;
	private AutopilotInputs autopilotInputs;
	
	private float lastLoopTime = 0;
	private float time = 0;
	
	int k = 5;
	
	private int startingAirport;
	private int startingGate;
	private int startingPointingTo;
	
	private Delivery delivery;
	private HashMap<Integer,Airport> airports = new HashMap<Integer,Airport>();
	private PackageHandler packageHandler;
	private int id;
	private ArrayList<Vector> posList = new ArrayList<Vector>();
	private Vector prevSpeedVector;

	private float totalMass;
	

	public Besturing(int id, int airport, int gate, int pointingToRunway, AutopilotConfig config, HashMap<Integer,Airport> airports, PackageHandler pH) {
		this.vliegen = new Vliegen(this);
		this.taxi = new Taxi(this);
		
		this.id = id;
		setConfig(config);
		startingAirport = airport;
		startingGate = gate;
		startingPointingTo = pointingToRunway;
		this.airports = airports;
		this.packageHandler = pH;
	}
	
	public void setConfig(AutopilotConfig config) {
		this.config = config;
		this.totalMass = config.getEngineMass() + config.getTailMass() + (2* config.getWingMass());
	}
	
	public void startBesturing(AutopilotInputs inputs) {
		System.out.println("inputs pos: " + inputs.getX() +" "+ inputs.getY() +" "+ inputs.getZ());
		setInputs(inputs);
		
		getPosList().add(new Vector(inputs.getX(),inputs.getY(),inputs.getZ()));
		
		int index = getPosList().size() -1;
		Vector speedVector = new Vector(0,0,-10);
		float speed = 10f;
		if(getPosList().size() <= 1) {speedVector = new Vector(0,0,0); speed = 10f;}
		else{
			speedVector = Vector.min(getPosList().get(index),getPosList().get(index -1));
			speedVector = Vector.scalarProd(speedVector, 1/getTime());
			speed = Vector.norm(speedVector);
		}
		
		prevSpeedVector = speedVector;
		
		if(occupation == OccupationEnum.FREE) outputs = new Outputs(0, 0, 0, 0, 0, 0, 0, 0);
		
		if(occupation == OccupationEnum.PICKING_UP){
			System.out.println("PCIKING UP ");
			if(airports.get(delivery.fromAirport).onAirport(inputs.getX(), inputs.getZ())){
				//At airport -> TAXI to gate
				state = PhaseEnum.TAXIEN;
				outputs = taxi.taxi(inputs,packageHandler.getStartingPosition(delivery),this);
				System.out.println("TAXI!!!! ");
			} else {
				//Wrong airport -> VLIEG to other airport

				
				if(!airports.get(delivery.toAirport).onEndRunway0(inputs.getX(), inputs.getZ()) && (state == PhaseEnum.TAXIEN || state == PhaseEnum.TEST)){
					state = PhaseEnum.TEST;
					outputs = taxi.taxi(inputs,airports.get(delivery.fromAirport).getStartRunway0Middle(),this);
				} else if(state == PhaseEnum.GO){
					state = PhaseEnum.VLIEGEN;
					outputs = vliegen.vliegen(inputs,packageHandler.getStartingPosition(delivery),speedVector,airports);
					System.out.println("VLIEGEN!!!! ");
				}

			}
			
		} else if (occupation == OccupationEnum.DELIVERING){
			if(airports.get(delivery.toAirport).onAirport(inputs.getX(), inputs.getZ())){
				//At airport -> TAXI to gate
				state = PhaseEnum.TAXIEN;
				outputs = taxi.taxi(inputs,packageHandler.getEndPosition(delivery),this);
			} else {
				//Wrong airport -> VLIEG to other airport
				state = PhaseEnum.VLIEGEN;
				outputs = vliegen.vliegen(inputs,packageHandler.getEndPosition(delivery),speedVector, airports);
			}
		}
		
		

//		switch(state) {
//		case VLIEGEN:
//			outputs = vliegen.vliegen(inputs);
//			break;
//		case TAXIEN:
//			outputs = taxi.taxi(inputs);
//			break;
//		}
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
		setTime(inputs);
	}

	
	public float[] getPosition(){
		return new float[]{autopilotInputs.getX(), autopilotInputs.getY(), autopilotInputs.getZ()};
	}
	
	public AutopilotOutputs getOutputs() {
		System.out.println(outputs.getThrust());
		return this.outputs;
	}

	public PhaseEnum getState() {
		return state;
	}
	
	public void setState(PhaseEnum state){
		this.state = state;
	}
	
	public OccupationEnum getOccupation() {
		return occupation;
	}

	public void assign(Delivery deliv) {
		this.delivery = deliv;
		occupation = OccupationEnum.PICKING_UP;
	}
	
	public void pickup(){
		occupation = OccupationEnum.DELIVERING;
	}
	
	public void deliver(){
		occupation = OccupationEnum.FREE;
		delivery = null;
	}
	
	public Delivery getDelivery(){
		return delivery;
	}
	
	public ArrayList<Vector> getPosList(){
		return this.posList;
	}
	
	public Vector getSpeedVector(){
		return prevSpeedVector;
	}
	
}
