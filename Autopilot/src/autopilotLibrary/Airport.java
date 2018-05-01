package autopilotLibrary;

public class Airport {

	private float L,W;
	private float centerX,centerZ;
	private float centerToRunway0X;
	private float centerToRunway0Z;
	
	public Airport(float L, float W, float centerX, float centerZ,float centerToRunway0X, float centerToRunway0Z) {
		this.L = L;
		this.W = W;
		this.centerX = centerX;
		this.centerZ = centerZ;
		this.centerToRunway0X = centerToRunway0X;
		this.centerToRunway0Z = centerToRunway0Z;
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
	
	public float getCenterToRunway0X(){
		return this.centerToRunway0X;
	}
	
	public float getCenterToRunway0Z(){
		return this.centerToRunway0Z;
	}

}
