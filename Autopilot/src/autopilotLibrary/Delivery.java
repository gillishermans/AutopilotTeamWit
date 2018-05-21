package autopilotLibrary;

import enums.DeliveryEnum;

public class Delivery {
	int fromAirport, fromGate, toAirport, toGate;
	int droneId;
	int id;
	DeliveryEnum state; 
	
	public Delivery(int id, int fromAirport, int fromGate, int toAirport, int toGate){
		this.id = id;
		this.fromAirport = fromAirport;
		this.fromGate= fromGate;
		this.toAirport = toAirport;
		this.toGate = toGate;
		state = DeliveryEnum.OPEN;
	}
	
	/**
	 * Assigns a drone to a delivery.
	 */
	public void assign(int drone){
		state = DeliveryEnum.TAKEN;
		droneId = drone;
	}
	
	/**
	 * Unassigns a drone to a delivery.
	 */
	public void unassign(){
		state = DeliveryEnum.OPEN;
		droneId = -1;
	}
	
	/**
	 * Checks if a delivery is open.
	 */
	public boolean isOpen() {
		if(state == DeliveryEnum.OPEN) return true;
		else return false;
	}
	
	public int getId(){
		return id;
	}
}
