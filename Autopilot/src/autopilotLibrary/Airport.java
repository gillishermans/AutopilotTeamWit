package autopilotLibrary;

public class Airport {

	private float L,W;
	private float centerX,centerZ;
	private float orientation;
	
	public Airport(float L, float W, float centerX, float centerZ,float centerToRunway0X, float centerToRunway0Z) {
		this.L = L;
		this.W = W;
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.orientation = this.getRadians(L, W, centerToRunway0X, centerToRunway0Z);
	}
	
	/**
	 * Checks if a position is on gate 0.
	 */
	public boolean onGate0(float x, float z){
		float[] point = getInvertedRotatedPoint(x, z);
		float[] a = getInvertedRotatedPoint(getStartGate0()[0], getStartGate0()[1]);
		float[] b = getInvertedRotatedPoint(getEndGate0()[0], getEndGate0()[1]);
		boolean onGateX = false;
		boolean onGateZ = false;
		if(a[0] < b[0]){
			if(x >= a[0] && x <= b[0]) onGateX = true;
		}
		else if(x <= a[0] && x >= b[0]) onGateX = true;
		
		if(a[1] < b[1]){
			if(z >= a[1] && z <= b[1]) onGateZ = true;
		}
		else if(z <= a[1] && z >= b[1]) onGateZ = true;
		
		return (onGateX && onGateZ);
	}
	
	/**
	 * Checks if a position is on gate 1.
	 */
	public boolean onGate1(float x, float z){
		float[] point = getInvertedRotatedPoint(x, z);
		float[] a = getInvertedRotatedPoint(getStartGate1()[0], getStartGate1()[1]);
		float[] b = getInvertedRotatedPoint(getEndGate1()[0], getEndGate1()[1]);
		boolean onGateX = false;
		boolean onGateZ = false;
		if(a[0] < b[0]){
			if(x >= a[0] && x <= b[0]) onGateX = true;
		}
		else if(x <= a[0] && x >= b[0]) onGateX = true;
		
		if(a[1] < b[1]){
			if(z >= a[1] && z <= b[1]) onGateZ = true;
		}
		else if(z <= a[1] && z >= b[1]) onGateZ = true;
		
		return (onGateX && onGateZ);
	}
	
	/**
	 * Checks if a position is on the airport.
	 */
	public boolean onAirport(float x, float z){
		float[] point = getInvertedRotatedPoint(x, z);
		float[] a = getInvertedRotatedPoint(getStartRunway0Corner()[0], getStartRunway0Corner()[1]);
		float[] b = getInvertedRotatedPoint(getEndRunway1Corner()[0], getEndRunway1Corner()[1]);

		boolean onAirportX = false;
		boolean onAirportZ = false;
		if(a[0] < b[0]){
			if(point[0] >= a[0] && point[0] <= b[0]) onAirportX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onAirportX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onAirportZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onAirportZ = true;
		
		return (onAirportX && onAirportZ);
		
	}
	
	/**
	 * Calculates rotated position of a position according to the orientation.
	 */
	public float[] getRotatedPoint(float x, float z){
		float angle = getOrientation();
		
		float rotatedX = (float) (getX() + (x  * Math.cos(angle)) - (z * Math.sin(angle)));
		float rotatedZ = (float) (getZ() + (x  * Math.sin(angle)) + (z * Math.cos(angle)));

		return new float []{rotatedX, rotatedZ};
	}
	
	/**
	 * Calculates invertedrotated position of a position according to the orientation.
	 */
	public float[] getInvertedRotatedPoint(float x, float z){
		float angle = -getOrientation();
		
		float rotatedX = (float) (getX() + (x  * Math.cos(angle)) - (z * Math.sin(angle)));
		float rotatedZ = (float) (getZ() + (x  * Math.sin(angle)) + (z * Math.cos(angle)));

		return new float []{rotatedX, rotatedZ};
	}

	/**
	 * Gets the start position corner of gate 0.
	 */
	public float[] getStartGate0(){
		return getRotatedPoint(-getW()/2,-getW());
	}
	
	/**
	 * Gets the start position corner of gate 1.
	 */
	public float[] getStartGate1(){
		return getRotatedPoint(-getW()/2, 0);
	}
	
	/**
	 * Gets the end position corner of gate 0.
	 */
	public float[] getEndGate0(){
		return getRotatedPoint(getW()/2,0);
	}
	
	/**
	 * Gets the end position corner of gate 1.
	 */
	public float[] getEndGate1(){
		return getRotatedPoint(getW()/2,getW());
	}
	
	/**
	 * Gets the middle position of gate 0.
	 */
	public float[] getMiddleGate0(){
		return getRotatedPoint(0,-getW()/2);
	}
	
	/**
	 * Gets the middle position of gate 1.
	 */
	public float[] getMiddleGate1(){
		return getRotatedPoint(0,getW()/2);
	}
	
	/**
	 * Gets start of runway 0 corner (bottom left).
	 */
	public float[] getStartRunway0Corner(){
		return getRotatedPoint(-(getL()+(getW()/2)), -getW());
	}
	/**
	 * Gets end of runway 0 corner (top left).
	 */
	public float[] getEndRunway0Corner(){
		return getRotatedPoint(-(getL()+(getW()/2)), getW());
	}
	/**
	 * Gets start of runway 1 corner (bottom right).
	 */
	public float[] getStartRunway1Corner(){
		return getRotatedPoint((getL()+(getW()/2)), -getW());
	}
	/**
	 * Gets end of runway 1 corner (top right).
	 */
	public float[] getEndRunway1Corner(){
		return getRotatedPoint((getL()+(getW()/2)), getW());
	}
	
	/**
	 * Returns the x distance from the center of the airport to runway 0.
	 * 	(to middle of runway 0)
	 */
	public float getCenterToRunway0X(){
		return getX() -  getStartRunway0Corner()[0];
	}
	
	/**
	 * Returns the z distance from the center of the airport to runway 0.
	 * 	(to start of runway 0)
	 */
	public float getCenterToRunway0Z(){
		return getZ() - getStartRunway0Corner()[1];
	}
	
	
	public float getL(){
		return this.L;
	}
	
	public float getW(){
		return this.W;
	}
	
	public float getX(){
		return this.centerX;
	}
	
	public float getZ(){
		return this.centerZ;
	}
	
	public float getRadians (float L, float W, float centerToRunway0x, float centerToRunway0z){
		Vector vector1 = new Vector(L + (W/2),0,W); 
		Vector vector2 = new Vector (centerToRunway0x,0,centerToRunway0z);
		float radian = (float) Math.acos(Vector.scalairProd(vector1, vector2)/(Vector.norm(vector1)*Vector.norm(vector2)));
		return radian;
	}
	
	public float getOrientation() {
		return orientation;
	}

	public float[] getMiddleGate(int gate) {
		if(gate == 0) return getMiddleGate0();
		else return getMiddleGate1();
	}

}
