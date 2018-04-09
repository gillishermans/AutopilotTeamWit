package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;

public class AutopilotHandler {
	
	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private ArrayList<Luchthaven> luchthavens = new ArrayList<Luchthaven>();
	
	//private HashMap<Integer,Thread> threads = new HashMap<Integer, Thread>();
	//private ExecutorService executorService = Executors.newFixedThreadPool(4);
	
	private Integer index = 0;
	
	private float luchthavenLength;
	private float luchthavenWidth;
	
	
	//Zal een threadpool initialiseren
	public AutopilotHandler() {
//		for (int i = 0; i < 4; i++) {
//			threads.put(i, new Thread());
//		}
//		System.out.println("Autopilot created " + numberOfThreads +  " threads");
	}
	
	//
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		drones.get(drone).startBesturing(inputs);
	}
	
	public AutopilotOutputs completeTimeHasPassed(int drone) {
		return drones.get(drone).getOutputs();
		
	}
	
	
	//ZAL ALLE AUTOPILOTS AANMAKEN EN BIJHOUDEN
	//ALLE LUCHTHAVENS
	
	public void setLuchthavenConfig(float length, float width) {
		this.luchthavenLength = length;
		this.luchthavenWidth = width;
	}
	
	public void addDrone() {
		Besturing drone = new Besturing();
		drones.put(index, drone);
		//threads.put(index, new Thread(drone));
		index++;
	}
	
	public void addLuchthaven() {
		luchthavens.add(new Luchthaven());
	}

}
