package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import enums.OccupationEnum;
import enums.PhaseEnum;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class AutopilotHandler {
	
	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private ArrayList<Luchthaven> luchthavens = new ArrayList<Luchthaven>();
	
	private GUIAutopilot gui;
	
	//private HashMap<Integer,Thread> threads = new HashMap<Integer, Thread>();
	//private ExecutorService executorService = Executors.newFixedThreadPool(4);
	
	private Integer packageIndex = 0;
	private Integer droneIndex = 0;
	
	private float luchthavenLength;
	private float luchthavenWidth;
	
	
	//Zal een threadpool initialiseren
	public AutopilotHandler() {
		//INIT GUI voor de autopilot
		this.gui = new GUIAutopilot();
//		for (int i = 0; i < 4; i++) {
//			threads.put(i, new Thread());
//		}
//		System.out.println("Autopilot created " + numberOfThreads +  " threads");
	}
	
	
	//
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		System.out.println("DIT IS DRONE: " + drone);
		drones.get(drone).startBesturing(inputs);
	}
	
	public AutopilotOutputs completeTimeHasPassed(int drone) {
		System.out.println("DIT IS DRONE OUTPUT: " + drone);
		Besturing b = drones.get(drone);
		PhaseEnum s = b.getState();
		updateStateDrone(drone);
		updateOccupationDrone(drone);
		return b.getOutputs();
	}
	
	
	//ZAL ALLE AUTOPILOTS AANMAKEN EN BIJHOUDEN
	//ALLE LUCHTHAVENS
	
	public void setLuchthavenConfig(float length, float width) {
		this.luchthavenLength = length;
		this.luchthavenWidth = width;
	}
	
	public void addDrone() {
		Besturing drone = new Besturing();
		drones.put(droneIndex, drone);
		//threads.put(index, new Thread(drone));
		gui.addDrone(droneIndex);
		droneIndex++;
	}
	

	public void addLuchthaven() {
		luchthavens.add(new Luchthaven());
	}
	
	public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		gui.addToDo(packageIndex, fromAirport, fromGate, toAirport, toGate);
		packageIndex++;
	}
	
	public void updateStateDrone(Integer i) {
		
		gui.changeStateDrone(drones.get(i).getState(), i);
	}
	
	public void updateOccupationDrone(Integer i) {
		gui.changeJobDrone(drones.get(i).getOccupation(), i);
	}
	
	public void assignJob(Integer indexPackage, Integer indexDrone) {
		
	}

}
