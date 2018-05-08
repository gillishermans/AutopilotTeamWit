package autopilotLibrary;


public class Airport {

	private int id;
	private float L,W;
	private float centerX,centerZ;
	private float orientation;
	private Delivery packageGate0;
	private Delivery packageGate1;
	
	public Airport(int id, float L, float W, float centerX, float centerZ,float centerToRunway0X, float centerToRunway0Z) {
		this.id = id;
		this.L = L;
		this.W = W;
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.orientation = this.getRadians(centerToRunway0X, centerToRunway0Z);
	}
	
	public boolean onStartRunway0(float x, float z){
		float[] point = getInvertedRotatedPoint(x, z);
		
		float[] a = getInvertedRotatedPoint(getStartRunway0Corner()[0], getStartRunway0Corner()[1]);
		float[] b = getInvertedRotatedPoint(getStartRunway0Corner2()[0], getStartRunway0Corner2()[1]);
		
		boolean onRunwayX = false;
		boolean onRunwayZ = false;
		
		if(a[0] < b[0]){
			if(point[0] >= a[0] && point[0] <= b[0]) onRunwayX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onRunwayX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onRunwayZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onRunwayZ = true;
		
		return (onRunwayX && onRunwayZ);
	}

	public boolean onEndRunway0(float x, float z){
		
		float[] point = getInvertedRotatedPoint(x, z);
		
		float[] a = getInvertedRotatedPoint(getEndRunway0Corner()[0], getEndRunway0Corner()[1]);
		float[] b = getInvertedRotatedPoint(getEndRunway0Corner2()[0], getEndRunway0Corner2()[1]);
		
		boolean onRunwayX = false;
		boolean onRunwayZ = false;
		System.out.println("UNROTATEDPOINT: " + x +" "+ z);
		System.out.println("POINT: " + point[0] +" "+  point[1]);
		System.out.println("A: " + a[0] +" "+  a[1]);
		System.out.println("B: " + b[0] +" "+  b[1]);
		
		if(a[0] < b[0]){
			if(point[0] >= a[0] && point[0] <= b[0]) onRunwayX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onRunwayX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onRunwayZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onRunwayZ = true;
		
		return (onRunwayX && onRunwayZ);
	}
	public boolean onStartRunway1(float x, float z){
		float[] point = getInvertedRotatedPoint(x, z);
		
		float[] a = getInvertedRotatedPoint(getStartRunway1Corner()[0], getStartRunway1Corner()[1]);
		float[] b = getInvertedRotatedPoint(getStartRunway1Corner2()[0], getStartRunway1Corner2()[1]);
		
		boolean onRunwayX = false;
		boolean onRunwayZ = false;
		
		if(a[0] < b[0]){
			if(point[0] >= a[0] && point[0] <= b[0]) onRunwayX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onRunwayX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onRunwayZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onRunwayZ = true;
		
		return (onRunwayX && onRunwayZ);
	}
	public boolean onEndRunway1(float x, float z){
		float[] point = getInvertedRotatedPoint(x, z);
		
		float[] a = getInvertedRotatedPoint(getEndRunway1Corner()[0], getEndRunway1Corner()[1]);
		float[] b = getInvertedRotatedPoint(getEndRunway1Corner2()[0], getEndRunway1Corner2()[1]);
		
		boolean onRunwayX = false;
		boolean onRunwayZ = false;
		
		if(a[0] < b[0]){
			if(point[0] >= a[0] && point[0] <= b[0]) onRunwayX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onRunwayX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onRunwayZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onRunwayZ = true;
		
		return (onRunwayX && onRunwayZ);
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
			if(point[0] >= a[0] && point[0] <= b[0]) onGateX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onGateX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onGateZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onGateZ = true;
		
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
			if(point[0] >= a[0] && point[0] <= b[0]) onGateX = true;
		}
		else if(point[0] <= a[0] && point[0] >= b[0]) onGateX = true;
		
		if(a[1] < b[1]){
			if(point[1] >= a[1] && point[1] <= b[1]) onGateZ = true;
		}
		else if(point[1] <= a[1] && point[1] >= b[1]) onGateZ = true;
		
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
		System.out.println("ANGLE: " + getOrientation());
		System.out.println("X: " + getX() + " Z: " + getZ());
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
		System.out.println();
		System.out.println("MIDDLEGATE0 " + getRotatedPoint(0,-getW()/2)[0] + " " + getRotatedPoint(0,-getW()/2)[1]);
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
	public float[] getStartRunway0Corner2(){
		return getRotatedPoint(-(getL()+(getW()/2))+getL()/2, -getW()+getL());
	}
	public float[] getStartRunway0Middle() {
		return getRotatedPoint(-(getL()+(getW()/2))+getL()/4,-getW()+getL()/2);
	}
	
	/**
	 * Gets end of runway 0 corner (top left).
	 */
	public float[] getEndRunway0Corner(){
		return getRotatedPoint(-(getL()+(getW()/2)), getW());
	}
	public float[] getEndRunway0Corner2(){
		return getRotatedPoint(-(getL()+(getW()/2))+getL()/2, getW()-getL());
	}
	public float[] getEndRunway0Middle() {
		return getRotatedPoint(-(getL()+(getW()/2))+getL()/4,getW()-getL()/2);
	}
	
	
	/**
	 * Gets start of runway 1 corner (bottom right).
	 */
	public float[] getStartRunway1Corner(){
		return getRotatedPoint((getL()+(getW()/2)), -getW());
	}
	public float[] getStartRunway1Corner2(){
		return getRotatedPoint((getL()+(getW()/2))-getL()/2, -getW()+getL());
	}
	public float[] getStartRunway1Middle() {
		return getRotatedPoint((getL()+(getW()/2))-getL()/4,-getW()+getL()/2);
	}
	
	/**
	 * Gets end of runway 1 corner (top right).
	 */
	public float[] getEndRunway1Corner(){
		return getRotatedPoint((getL()+(getW()/2)), getW());
	}
	public float[] getEndRunway1Corner2(){
		return getRotatedPoint((getL()+(getW()/2))-getL()/2, getW()-getL());
	}
	public float[] getEndRunway1Middle() {
		return getRotatedPoint((getL()+(getW()/2))-getL()/4,getW()-getL()/2);
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
	
	public float getRadians (float centerToRunway0x, float centerToRunway0z){
		Vector vector1 = new Vector(getL() + (getW()/2),0,getW()); 
		Vector vector2 = new Vector (centerToRunway0x,0,centerToRunway0z);
		float radian = (float) Math.acos(Math.round(Vector.scalairProd(vector1, vector2)/(Vector.norm(vector1)*Vector.norm(vector2))));
		return radian;
	}
	
	public float getOrientation() {
		return orientation;
	}

	public float[] getMiddleGate(int gate) {
		if(gate == 0) return getMiddleGate0();
		else return getMiddleGate1();
	}
	

	public boolean isPackageGate1() {
		return packageGate1!=null;
	}

	public void setPackageGate1(Delivery Delivery) {
		this.packageGate1 = Delivery;
	}
	
	public Delivery getPackageGate1() {
		return this.packageGate1;
	}
	
	public boolean isPackageGate0() {
		return packageGate0!=null;
	}

	public void setPackageGate0(Delivery Delivery) {
		this.packageGate0 = Delivery;
	}
	
	public Delivery getPackageGate0() {
		return this.packageGate0;
	}
	
	public void setPackageGate(Delivery packageGate, int id){
		if(id == 0) setPackageGate0(packageGate);
		if(id == 1) setPackageGate1(packageGate);
		System.out.println("DELIVER PACKAGE AT GATE " + id);
	}
	
	public boolean isPackageGate(int id){
		if(id == 0) return isPackageGate0();
		if(id == 1) return isPackageGate1();
		else return true;
	}

	public int getId() {
		return id;
	}



}
