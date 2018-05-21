package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enums.DeliveryEnum;
import enums.OccupationEnum;
import enums.PhaseEnum;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class AutopilotHandler {
	
	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private HashMap<Integer,Airport> airports = new HashMap<Integer,Airport>();
	
	private GUIAutopilot gui;
	
	//private HashMap<Integer,Thread> threads = new HashMap<Integer, Thread>();
	//private ExecutorService executorService = Executors.newFixedThreadPool(4);
	
	private Integer packageIndex = 0;
	private Integer droneIndex = 0;
	
	private float airportLength;
	private float airportWidth;
	
	private PackageHandler packageHandler;
	
	
	//Zal een threadpool initialiseren
	public AutopilotHandler() {
		//INIT GUI voor de autopilot
		this.gui = new GUIAutopilot();
//		for (int i = 0; i < 4; i++) {
//			threads.put(i, new Thread());
//		}
//		System.out.println("Autopilot created " + numberOfThreads +  " threads");
		
		packageHandler = new PackageHandler(this, drones, airports);
	}
	
	
	/**
	 * Starts a time passed for a drone with the given inputs.
	 */
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		drones.get(drone).startBesturing(inputs,drones);
	}
	
	/**
	 * Completes a time passed for a drone and returns outputs.
	 */
	public AutopilotOutputs completeTimeHasPassed(int drone) {
		Besturing b = drones.get(drone);
		PhaseEnum s = b.getState();
		packageHandler.update(drones);
		updateStateDrone(drone);
		updateOccupationDrone(drone);
		return b.getOutputs();
	}
	
	/**
	 * Sets the airport configuration parameters.
	 */
	public void setAirportConfig(float length, float width) {
		this.airportLength = length;
		this.airportWidth = width;
	}
	
	/**
	 * Adds an airport at with the given parameters.
	 */
	public void addAirport(float centerX, float centerZ, float centerToRunway0X, float centerToRunway0Z) {
		int key = airports.size(); 
		airports.put(key, new Airport(key,airportLength,airportWidth,centerX,centerZ,centerToRunway0X,centerToRunway0Z));
	}
	
	/**
	 * Adds a drone with the given parameters.
	 */
	public void addDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config) {
		Besturing drone = new Besturing(droneIndex,airport, gate, pointingToRunway, config, airports, packageHandler);
		drones.put(droneIndex, drone);
		//threads.put(index, new Thread(drone));
		gui.addDrone(droneIndex);
		droneIndex++;
	}
	
	/**
	 * Starts a new package delivery request from an airport to gate to another.
	 */
	public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		gui.addPackage(packageIndex, fromAirport, fromGate, toAirport, toGate);
		packageHandler.deliverPackage(packageIndex,fromAirport, fromGate, toAirport, toGate);
		//gui.addToDo(packageIndex, fromAirport, fromGate, toAirport, toGate);
		packageIndex++;
	}
	
	public void updateStateDrone(Integer i) {
		gui.changeStateDrone(drones.get(i).getState(), i);
	}
	
	public void updateOccupationDrone(Integer i) {
		gui.changeJobDrone(drones.get(i).getOccupation(), i);
	}
	
	public void assignJob(Integer indexPackage, Integer indexDrone) {
		gui.changeStatePackage(DeliveryEnum.TAKEN,indexPackage,indexDrone);
	}
	
	public void completeJob(Integer indexPackage){
		gui.completeDelivery(indexPackage);
	}

}
