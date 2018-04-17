package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.stage.Stage;

public class AutopilotHandler {
	
	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private ArrayList<Luchthaven> luchthavens = new ArrayList<Luchthaven>();
	
	private GUI gui;
	
	//private HashMap<Integer,Thread> threads = new HashMap<Integer, Thread>();
	//private ExecutorService executorService = Executors.newFixedThreadPool(4);
	
	private Integer index = 0;
	
	private float luchthavenLength;
	private float luchthavenWidth;
	
	
	//Zal een threadpool initialiseren
	@SuppressWarnings("static-access")
	public AutopilotHandler() {
		//INIT GUI voor de autopilot
		initGUI();
//		for (int i = 0; i < 4; i++) {
//			threads.put(i, new Thread());
//		}
//		System.out.println("Autopilot created " + numberOfThreads +  " threads");
	}
	
	public void initGUI() {
		new JFXPanel();
		Platform.runLater(new Runnable() {
		    @Override
		    public void run() {
		    	try {
		    		gui = new GUI();
					gui.start(new Stage());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		    }
		});
	}
	
	//
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		System.out.println("DIT IS DRONE: " + drone);
		drones.get(drone).startBesturing(inputs);
	}
	
	public AutopilotOutputs completeTimeHasPassed(int drone) {
		System.out.println("DIT IS DRONE OUTPUT: " + drone);
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
		gui.addDrone(index);
		index++;
	}
	
	
	public void addLuchthaven() {
		luchthavens.add(new Luchthaven());
	}
	
	public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		gui.addPackage(fromAirport, fromGate, toAirport, toGate);
	}

}
