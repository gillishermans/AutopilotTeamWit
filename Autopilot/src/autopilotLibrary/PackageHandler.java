package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import enums.OccupationEnum;

public class PackageHandler {

	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private HashMap<Integer,Airport> airports = new HashMap<Integer,Airport>();
	private ArrayList<Delivery> packages = new ArrayList<Delivery>();
	
	public PackageHandler(HashMap<Integer,Besturing> drones, HashMap<Integer,Airport> airports) {
		this.drones = drones;
		this.airports = airports;
	}
	
	/**
	 * Add new package.
	 */
	public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate){
		packages.add(new Delivery(fromAirport, fromGate, toAirport, toGate));
	}
	
	/**
	 * Updates a drone's package delivery status.
	 */
	public void update(int drone){
		if(drones.get(drone).getOccupation() == OccupationEnum.FREE){
			if(getClosestPackage(drone) != null) assign(drone, getClosestPackage(drone));
		}
	}
	
	/**
	 * Updates all drones package delivery status.
	 */
	public void update(HashMap<Integer, Besturing> drones) {
		for(Delivery d : getFreePackages()){
			assign(getClosestDrone(d,drones),d);
		}
	}
	
	/**
	 * Assigns a package to a drone.
	 */
	private void assign(int drone, Delivery deliv){
		deliv.assign(drone);
		drones.get(drone).assign(deliv);
		System.out.println("ASSIGN PACKAGE DRONE " + drone + " APGATE" + deliv.fromAirport + "." + deliv.fromGate);
	}
	
	/**
	 * Gets the closest drone to a delivery.
	 */
	private int getClosestDrone(Delivery d, HashMap<Integer, Besturing> drones){
		
		float[] deliveryGeneralPos = getStartingPosition(d);
		int closestDrone = 0;
		float closest = distance(deliveryGeneralPos,drones.get(0).getPosition());
		for(int drone : drones.keySet()){
			System.out.println("CHECK DRONE " + drone);
			System.out.println("DISTANCE " + distance(deliveryGeneralPos,drones.get(drone).getPosition()));
			if(distance(deliveryGeneralPos,drones.get(drone).getPosition()) < closest){
				closest = distance(deliveryGeneralPos,drones.get(drone).getPosition());
				closestDrone = drone;
			}
		}
		return closestDrone;
	}
	
	/**
	 * Gets the closest delivery to a drone.
	 */
	private Delivery getClosestPackage(int drone){
		
		if(getFreePackages().size() == 0) return null;
		
		Besturing d = drones.get(drone);
		float[] dronePos = d.getPosition();
		float[] droneGeneralPos = new float[]{dronePos[0],dronePos[2]};
		Delivery closestDelivery = getFreePackages().get(0);
		float closest = distance(droneGeneralPos,getStartingPosition(closestDelivery));
		System.out.println("PACKAGE AMOUNT/ " + getFreePackages().size());
		for(Delivery delivery : getFreePackages()){
			if(distance(droneGeneralPos,getStartingPosition(delivery)) < closest){
				closest = distance(droneGeneralPos,getStartingPosition(delivery));
				closestDelivery = delivery;
			}
		}
		return closestDelivery;
	}
	
	/**
	 * Gets the starting  position of a delivery.
	 */
	public float[] getStartingPosition(Delivery delivery){
		Airport ap = airports.get(delivery.fromAirport);
		return ap.getMiddleGate(delivery.fromGate) ;
	}
	
	/**
	 * Gets the end goal position of a delivery.
	 */
	public float[] getEndPosition(Delivery delivery){
		Airport ap = airports.get(delivery.toAirport);
		return ap.getMiddleGate(delivery.toGate) ;
	}
	
	/**
	 * Returns the distance between a drone and a delivery.
	 */
	private float distance(float[] dronePos, float[] deliveryPos){
		 return (float) Math.sqrt(Math.pow((dronePos[0] - deliveryPos[0]), 2) + Math.pow((dronePos[1] - deliveryPos[1]), 2));
	}
	
	/**
	 * Get a list of free packages.
	 */
	private List<Delivery> getFreePackages(){
		List<Delivery> free = new ArrayList<Delivery>();
		for(Delivery d : packages){
			if(d.isOpen()) free.add(d);
		}
		return free;
	}
}
