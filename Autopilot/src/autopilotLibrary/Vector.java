package autopilotLibrary;

public class Vector {

	private float x;
	private float y;
	private float z;
	
	
	public Vector(float x,float y,float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public static Vector min(Vector a, Vector b){
		return new Vector(a.x - b.x,a.y - b.y, a.z - b.z);
	}
	
	public static Vector scalarProd(Vector a, Float b){
		return new Vector(a.x * b,a.y * b,a.z * b);
	}
	
	public static float norm(Vector a){
		return (float) Math.sqrt(Math.pow(Math.abs(a.x), 2) + Math.pow(Math.abs(a.y), 2) + Math.pow(Math.abs(a.z), 2));
	}

}
