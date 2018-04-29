package autopilotLibrary;

import enums.DeliveryEnum;

public class Delivery {
	int fromAirport, fromGate, toAirport, toGate;
	int droneId;
	DeliveryEnum state; 
	
	public Delivery(int fromAirport, int fromGate, int toAirport, int toGate){
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
	 * Checks if a delivery is open.
	 */
	public boolean isOpen() {
		if(state == DeliveryEnum.OPEN) return true;
		else return false;
	}
	


}
