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
	private PhaseEnum state = PhaseEnum.WAITING;
	
	private Path path;
	private AutopilotConfig config;
	private AutopilotInputs autopilotInputs;
	
	private float lastLoopTime = 0;
	private float time = 0;
	
	int k = 5;
	
	private Delivery delivery;
	private HashMap<Integer,Airport> airports = new HashMap<Integer,Airport>();
	public PackageHandler packageHandler;
	int id;
	private ArrayList<Vector> posList = new ArrayList<Vector>();
	private Vector prevSpeedVector;
	public boolean go = false; 
	private boolean startNoodRem = false;
	
	public Besturing(int id, int airport, int gate, int pointingToRunway, AutopilotConfig config, HashMap<Integer,Airport> airports, PackageHandler pH) {
		this.id = id;
		setConfig(config);
		
		this.airports = airports;
		this.packageHandler = pH;
		
		this.vliegen = new Vliegen(this);
		this.taxi = new Taxi(this);
	}
	
	public void setConfig(AutopilotConfig config) {
		this.config = config;
	}
	
	private Vector updateSpeedVector(AutopilotInputs inputs){
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
		return speedVector;
	}
	
	/**
	 * Checks if the drone is in a flying state.
	 */
	private boolean isFlying(){
		if(state == PhaseEnum.WAITING || state == PhaseEnum.TAXIEN || state == PhaseEnum.NOODREM || state == PhaseEnum.DRAAIEN) return false;
		else return true;	
	}
	
	private boolean noodRemMogelijk(){
		if(state == PhaseEnum.WAITING || state == PhaseEnum.TAXIEN 
				||state == PhaseEnum.NOODREM || state == PhaseEnum.DRAAIEN
				||state == PhaseEnum.REMMEN) return true;
		else return false;
	}
	
	/**
	 * Start besturing: beslis wat te doen adhv staat, positie, doel.
	 */
	public void startBesturing(AutopilotInputs inputs,HashMap<Integer,Besturing> drones) {
		setInputs(inputs);
		
		Vector speedVector = updateSpeedVector(inputs);
		
		if(occupation == OccupationEnum.FREE) outputs = new Outputs(0, 0, 0, 0, 0, 0, 0, 0);
		
		if(occupation == OccupationEnum.PICKING_UP){
			
			pickingUp(inputs, speedVector);
			
		} else if (occupation == OccupationEnum.DELIVERING){
			
			droppingOff(inputs, speedVector);
			
		}
		
		if(startNoodRem){
			noodRem(inputs,drones);
		}
		startNoodRem = true;
	}
	
	private void noodRem(AutopilotInputs inputs,HashMap<Integer,Besturing> drones){
		for(Besturing d : drones.values()){
			if(d.id != this.id && distance(this,d) < 40){
				float hoek = (float) ((Math.atan2(d.getPosition()[2]-this.getPosition()[2],d.getPosition()[0]-this.getPosition()[0])+ Math.PI/2+2*Math.PI) % (2*Math.PI));
				if(noodRemMogelijk() && ((2*Math.PI+inputs.getHeading()) % (2*Math.PI) >= 0.7*hoek) && (2*Math.PI+inputs.getHeading()) % (2*Math.PI)<= 1.3*hoek){
					outputs = new Outputs(0, 0, 0, 0, 0, 10000, 10000, 10000);
					System.out.println("NOODREM");
					state = PhaseEnum.NOODREM;
				}
			}
		}
	}
	
	private float distance(Besturing d1, Besturing d2){
		return (float) Math.sqrt(Math.pow((d1.getPosition()[0]-d2.getPosition()[0]),2)+Math.pow((d1.getPosition()[2]-d2.getPosition()[2]), 2));
	}
	
	/**
	 * The pick up autopilot loop.
	 */
	private void pickingUp(AutopilotInputs inputs, Vector speedVector){
		
		//Als startsignaal wordt gegeven of al aan het vliegen: vlieg
		if(go || isFlying()){
			if (state==PhaseEnum.DRAAIEN||state==PhaseEnum.WAITING|| state==PhaseEnum.NOODREM){
			state = PhaseEnum.INIT;
			}
			outputs = vliegen.vliegen(inputs,packageHandler.getStartingPosition(delivery),speedVector,airports,this);
			go = false;
			if(Vector.length(speedVector) < 1.0 && state == PhaseEnum.REMMEN){
				state = PhaseEnum.TAXIEN;
				System.out.println("REM GEDAAN");
			}
		}
		
		//Als pakket op zelfde luchthaven: taxi naar doelpositie
		else if(airports.get(delivery.fromAirport).onAirport(inputs.getX(), inputs.getZ())){
			state = PhaseEnum.TAXIEN;
			outputs = taxi.taxi(inputs,packageHandler.getStartingPosition(delivery),this,speedVector);
		}
		//Als pakket op andere luchthaven: taxi naar start runway en orienteer drone in correcte richting
		else {
			
			//state = PhaseEnum.TAXIEN;
			
			//Runway from which to start take-off.
			int goalRunway = getRunwayStart(packageHandler.getStartingPosition(delivery));
			//The currentAirport the drone is on.
			int currentAirport = getOnAirport(inputs.getX(), inputs.getZ());
			
			//Not on runway -> taxi to runway
			this.taxi.overgang=false;
			if(!airports.get(currentAirport).onRunway(goalRunway,inputs.getX(), inputs.getZ()) && (state == PhaseEnum.TAXIEN || state == PhaseEnum.NOODREM || state == PhaseEnum.WAITING || state == PhaseEnum.DRAAIEN)){
				state = PhaseEnum.TAXIEN;
				outputs = taxi.taxi(inputs,airports.get(currentAirport).getMiddleRunwayStart(goalRunway),this,speedVector);
			
			//On runway start -> turn to given angle	
			} else {
				//from here to go / flying
				state = PhaseEnum.DRAAIEN;
				outputs = taxi.turn(inputs, airports.get(currentAirport).getRunwayTakeOffAngle(goalRunway),this);
			}
			
		}
	}
	
	/**
	 * The drop off autopilot loop.
	 */
	private void droppingOff(AutopilotInputs inputs, Vector speedVector){
		
		//Als startsignaal wordt gegeven of al aan het vliegen: vlieg
		if(go || isFlying()){
			if (state==PhaseEnum.DRAAIEN||state==PhaseEnum.WAITING|| state==PhaseEnum.TEST){
				state = PhaseEnum.INIT;
			}
			outputs = vliegen.vliegen(inputs,packageHandler.getEndPosition(delivery),speedVector,airports,this);
			go = false;
			if(Vector.length(speedVector) < 1.0 && state == PhaseEnum.REMMEN){
				state = PhaseEnum.TAXIEN;
				System.out.println("REM GEDAAN");
			}
		}
		
		//Als pakket op zelfde luchthaven: taxi naar doelpositie
		else if(airports.get(delivery.toAirport).onAirport(inputs.getX(), inputs.getZ())){
			state = PhaseEnum.TAXIEN;
			outputs = taxi.taxi(inputs,packageHandler.getEndPosition(delivery),this,speedVector);
		}
		//Als pakket op andere luchthaven: taxi naar start runway en orienteer drone in correcte richting
		else {
			
			//Runway from which to start take-off.
			int goalRunway = getRunwayStart(packageHandler.getEndPosition(delivery));
			//The currentAirport the drone is on.
			int currentAirport = getOnAirport(inputs.getX(), inputs.getZ());
			
			this.taxi.overgang=false;
			//System.out.println("on runway: " + airports.get(currentAirport).onRunway(goalRunway,inputs.getX(),inputs.getZ()));
			//Not on runway -> taxi to runway
			if(!airports.get(currentAirport).onRunway(goalRunway,inputs.getX(), inputs.getZ()) && (state == PhaseEnum.TAXIEN || state == PhaseEnum.TEST || state == PhaseEnum.WAITING || state == PhaseEnum.DRAAIEN)){
				state = PhaseEnum.TAXIEN;
				outputs = taxi.taxi(inputs,airports.get(currentAirport).getMiddleRunwayStart(goalRunway),this,speedVector);
			
			//On runway start -> turn to given angle	
			} else {
				//System.out.println("HOEK "+ airports.get(currentAirport).getRunwayTakeOffAngle(goalRunway));
				state = PhaseEnum.DRAAIEN;
				outputs = taxi.turn(inputs, airports.get(currentAirport).getRunwayTakeOffAngle(goalRunway),this);
			}
			
		}
	}
	
	/**
	 * Gets the runway id that a drone should leave from to pickup/drop off the given delivery.
	 */
	private int getRunwayStart(float[] delivPos){
		if(delivPos[1] < autopilotInputs.getZ()) return 1;
		else return 0;	
	}
	
	/**
	 * Gets the airport the given position is at.
	 */
	private int getOnAirport(float x, float z){
		for(Airport ap : airports.values()){
			if(ap.onAirport(x, z)) return ap.getId();
		}
		return -1;
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
		//startBesturing(autopilotInputs);
	}
	
	public void setInputs(AutopilotInputs inputs) {
		this.autopilotInputs = inputs;
		setTime(inputs);
	}

	
	public float[] getPosition(){
		return new float[]{autopilotInputs.getX(), autopilotInputs.getY(), autopilotInputs.getZ()};
	}
	
	public AutopilotOutputs getOutputs() {
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
