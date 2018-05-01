package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;

import enums.OccupationEnum;

public class PackageHandler {

	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private ArrayList<Airport> airports = new ArrayList<Airport>();
	private ArrayList<Delivery> packages = new ArrayList<Delivery>();
	
	public PackageHandler(HashMap<Integer,Besturing> drones, ArrayList<Airport> airports) {
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
			getClosestPackage(drone).assign(drone);
		}
	}
	
	/**
	 * Gets the closest delivery to a drone.
	 */
	private Delivery getClosestPackage(int drone){
		Besturing d = drones.get(drone);
		float[] dronePos = d.getPosition();
		float[] droneGeneralPos = new float[]{dronePos[0],dronePos[2]};
		Delivery closestDelivery = null;
		float closest = 999999999;
		for(Delivery delivery : packages){
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
	private float [] getStartingPosition(Delivery delivery){
		Airport ap = airports.get(delivery.fromAirport);
		return new float[]{ap.getX(),ap.getZ()} ;
	}
	
	/**
	 * Returns the distance between a drone and a delivery.
	 */
	private float distance(float[] dronePos, float[] deliveryPos){
		 return (float) Math.sqrt(Math.pow((dronePos[0] - deliveryPos[0]), 2) + Math.pow((dronePos[1] - deliveryPos[1]), 2));
	}
	
}
