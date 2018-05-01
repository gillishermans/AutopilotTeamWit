package autopilotLibrary;

public class Vector {

	public float x;
	public float y;
	public float z;
	
	
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
	
	public Vector crossProd(Vector a, Vector b) {
		return new Vector (a.y*b.z - b.y*a.z,
				           a.z*b.x - b.z*a.x,
				           a.x*b.y - b.x*a.y);
	}
	
	public Vector product(float s, Vector v) {
		return new Vector(s * v.x, s*v.y, s*v.z);
	}
	
	public static float scalairProd(Vector a, Vector b) {
		return a.x*b.x + a.y*b.y + a.z*b.z;
	}
	
	public Vector sum(Vector a, Vector b) {
		return new Vector(a.x + b.x, a.y + b.y, a.z + b.y);
	}
	
	public float lengthSquared(Vector a) {
		return a.x*a.x + a.y*a.y + a.z*a.z;
	}

}
