package autopilotLibrary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import enums.OccupationEnum;

public class PackageHandler {

	private HashMap<Integer,Besturing> drones = new HashMap<Integer,Besturing>();
	private HashMap<Integer,Airport> airports = new HashMap<Integer,Airport>();
	private ArrayList<Delivery> packages = new ArrayList<Delivery>();
	private AutopilotHandler autopilotHandler;
	
	public PackageHandler(AutopilotHandler autopilotHandler, HashMap<Integer,Besturing> drones, HashMap<Integer,Airport> airports) {
		this.autopilotHandler = autopilotHandler;
		this.drones = drones;
		this.airports = airports;
	}
	
	/**
	 * Add new package.
	 */
	public void deliverPackage(int id, int fromAirport, int fromGate, int toAirport, int toGate){
		Delivery newPackage = new Delivery(id, fromAirport, fromGate, toAirport, toGate);
		packages.add(newPackage);
		airports.get(fromAirport).setPackageGate(newPackage, fromGate);
	}
		
	/**
	 * Updates all drones package delivery status.
	 */
	public void update(HashMap<Integer, Besturing> drones) {
		for(Delivery d : getFreePackages()){
			if(getClosestDrone(d,drones) != -1){
				assign(getClosestDrone(d,drones),d);
			}
		}
		checkPickup();
		checkDelivery();
	}
	
	/**
	 * Check for pickup of any packages.
	 */
	public void checkPickup() {
		 for(Besturing d : drones.values()){
			 for(Airport ap : airports.values()){
				if(ap.isPackageGate0() && ap.onGate0(d.getPosition()[0], d.getPosition()[2])){
					//System.out.println("ON GATE 0 + PACKAGE AVAILABLE");
					if(ap.getPackageGate0() == d.getDelivery() && Vector.length(d.getSpeedVector()) < 1.0){
						pickup(d, ap, 0, ap.getPackageGate0());
						return;
					}
				}
				if(ap.isPackageGate1() && ap.onGate1(d.getPosition()[0], d.getPosition()[2])){
					//System.out.println("ON GATE 1 + PACKAGE AVAILABLE");
					if(ap.getPackageGate1() == d.getDelivery() && Vector.length(d.getSpeedVector()) < 1.0){
						pickup(d, ap, 1, ap.getPackageGate1());
						return;
					}
				}
			 }
		 }
	}
	
	/**
	 * The given drone picks up the given delivery at the given airport and gate.
	 */
	private void pickup(Besturing drone, Airport ap, int gate, Delivery deliv){
		drone.pickup();
		ap.setPackageGate(null, gate);
	}
	
	/**
	 * Check for pickup of any deliveries.
	 */
	public boolean checkDelivery() {
		 for(Besturing d : drones.values()){
			 if (d.getOccupation() == OccupationEnum.DELIVERING) {
				 for(Airport ap : airports.values()){
					if(d.getDelivery().toAirport == ap.getId() && ap.onGate0(d.getPosition()[0], d.getPosition()[2])){
						if(Vector.length(d.getSpeedVector()) < 1.0) {
							//System.out.println("PACKAGE DELIVERED ON GATE 0");
							deliver(d, d.getDelivery());
							return true;
						}
					}
					if(d.getDelivery().toAirport == ap.getId() && ap.onGate1(d.getPosition()[0], d.getPosition()[2])){
						if(Vector.length(d.getSpeedVector()) < 1.0) {
							//System.out.println("PACKAGE DELIVERED ON GATE 1");
							deliver(d, d.getDelivery());
							return true;
						}
					}
				 }
			 }
		 }
		 return false;
	}
	
	/**
	 * The given drone delivers the given delivery.
	 */
	private void deliver(Besturing drone, Delivery deliv){
		packages.remove(deliv);
		drone.deliver();
		autopilotHandler.completeJob(deliv.getId());
	}
	
	/**
	 * Assigns a package to a drone.
	 */
	private void assign(int drone, Delivery deliv){
		deliv.assign(drone);
		drones.get(drone).assign(deliv);
		autopilotHandler.assignJob(deliv.getId(),drone);
	}
	
	/**
	 * Gets the closest drone to a delivery.
	 */
	private int getClosestDrone(Delivery d, HashMap<Integer, Besturing> drones){
		float[] deliveryGeneralPos = getStartingPosition(d);
		HashMap<Integer, Besturing> freeDrones = getFreeDrones(drones);
		int closestDrone = -1;
		float closest = 999999999999f;
		for(int drone : freeDrones.keySet()){
			float[] dronePos = freeDrones.get(drone).getPosition();
			float[] droneGeneralPos = new float[]{dronePos[0],dronePos[2]};
			if(distance(droneGeneralPos,deliveryGeneralPos) < closest){
				closest = distance(droneGeneralPos,deliveryGeneralPos);
				closestDrone = drone;
			}
		}
		return closestDrone;
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
	
	/**
	 * Returns a list of all free drones with no task.
	 */
	private HashMap<Integer, Besturing> getFreeDrones(HashMap<Integer, Besturing> drones){
		HashMap<Integer, Besturing> free = new HashMap<Integer, Besturing>();
		for(int drone : drones.keySet()){
			if(drones.get(drone).getOccupation() == OccupationEnum.FREE){
				free.put(drone, drones.get(drone));
			}
		}
		return free;
	}
}
