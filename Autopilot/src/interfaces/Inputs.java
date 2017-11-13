package interfaces;

public class Inputs implements AutopilotInputs {

	public Inputs (byte[] image, float x, float y, float z, float heading, float pitch, float roll, float elapsedTime) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.z = z;
		this.heading = heading;
		this.pitch = pitch;
		this.roll = roll;
		this.elapsedTime = elapsedTime;
	}
	
	private byte[] image;
	private float x;
	private float y; 
	private float z;
	private float heading;
	private float pitch;
	private float roll;
	private float elapsedTime;
	
	
	@Override
	public byte[] getImage() {
		// TODO Auto-generated method stub
		return image;
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return y;
	}

	@Override
	public float getZ() {
		// TODO Auto-generated method stub
		return z;
	}

	@Override
	public float getHeading() {
		// TODO Auto-generated method stub
		return heading;
	}

	@Override
	public float getPitch() {
		// TODO Auto-generated method stub
		return pitch;
	}

	@Override
	public float getRoll() {
		// TODO Auto-generated method stub
		return roll;
	}

	@Override
	public float getElapsedTime() {
		// TODO Auto-generated method stub
		return elapsedTime;
	}

}
