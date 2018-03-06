package interfaces;

public class Config implements AutopilotConfig {

	public Config(float gravity, float wingX, float tailSize, float engineMass, float wingMass, float tailMass,
			float maxThrust, float maxAOA, float wingLiftSlope, float horStabLiftSlope, float verStabLiftSlope, 
			float horAngleOfView, float verAngleOfView, int nbColumns, int nbRows, String droneID, float wheelY,
			float frontWheelZ, float rearWheelZ, float rearWheelX, float tyreSlope, float dampSlope, float tyreRadius,
			float rMax, float fcMax) {

		this.gravity = gravity;
		this.wingX = wingX;
		this.tailSize = tailSize;
		this.engineMass = engineMass;
		this.wingMass = wingMass;
		this.tailMass = tailMass;
		this.maxThrust = maxThrust;
		this.maxAOA = maxAOA;
		this.wingLiftSlope = wingLiftSlope;
		this.horStabLiftSlope = horStabLiftSlope;
		this.verStabLiftSlope = verStabLiftSlope;
		this.horAngleOfView = horAngleOfView;
		this.verAngleOfView = verAngleOfView;
		this.nbColumns = nbColumns;
		this.nbRows = nbRows;
		this.droneID = droneID;
		this.wheelY = wheelY;
		this.frontWheelZ = frontWheelZ;
		this.rearWheelZ = rearWheelZ;
		this.rearWheelX = rearWheelX;
		this.tyreSlope = tyreSlope;
		this.dampSlope = dampSlope;
		this.tyreRadius = tyreRadius;
		this.rMax = rMax;
		this.fcMax = fcMax;
}
	
	private float gravity;
	private float wingX;
	private float tailSize;
	private float engineMass;
	private float wingMass;
	private float tailMass;
	private float maxThrust;
	private float maxAOA;
	private float wingLiftSlope;
	private float horStabLiftSlope;
	private float verStabLiftSlope;
	private float horAngleOfView;
	private float verAngleOfView;
	private int nbColumns;
	private int nbRows;
	private String droneID;
	private float wheelY;
	private float frontWheelZ;
	private float rearWheelZ;
	private float rearWheelX;
	private float tyreSlope;
	private float dampSlope;
	private float tyreRadius;
	private float rMax;
	private float fcMax;
	

	@Override
	public float getGravity() {
		// TODO Auto-generated method stub
		return gravity;
	}

	@Override
	public float getWingX() {
		// TODO Auto-generated method stub
		return wingX;
	}

	@Override
	public float getTailSize() {
		// TODO Auto-generated method stub
		return tailSize;
	}

	@Override
	public float getEngineMass() {
		// TODO Auto-generated method stub
		return engineMass;
	}

	@Override
	public float getWingMass() {
		// TODO Auto-generated method stub
		return wingMass;
	}

	@Override
	public float getTailMass() {
		// TODO Auto-generated method stub
		return tailMass;
	}

	@Override
	public float getMaxThrust() {
		// TODO Auto-generated method stub
		return maxThrust;
	}

	@Override
	public float getMaxAOA() {
		// TODO Auto-generated method stub
		return maxAOA;
	}

	@Override
	public float getWingLiftSlope() {
		// TODO Auto-generated method stub
		return wingLiftSlope;
	}

	@Override
	public float getHorStabLiftSlope() {
		// TODO Auto-generated method stub
		return horStabLiftSlope;
	}

	@Override
	public float getVerStabLiftSlope() {
		// TODO Auto-generated method stub
		return verStabLiftSlope;
	}

	@Override
	public float getHorizontalAngleOfView() {
		// TODO Auto-generated method stub
		return horAngleOfView;
	}

	@Override
	public float getVerticalAngleOfView() {
		// TODO Auto-generated method stub
		return verAngleOfView;
	}

	@Override
	public int getNbColumns() {
		// TODO Auto-generated method stub
		return nbColumns;
	}

	@Override
	public int getNbRows() {
		// TODO Auto-generated method stub
		return nbRows;
	}

	@Override
	public String getDroneID() {
		// TODO Auto-generated method stub
		return droneID;
	}

	@Override
	public float getWheelY() {
		// TODO Auto-generated method stub
		return wheelY;
	}

	@Override
	public float getFrontWheelZ() {
		// TODO Auto-generated method stub
		return frontWheelZ;
	}

	@Override
	public float getRearWheelZ() {
		// TODO Auto-generated method stub
		return rearWheelZ;
	}

	@Override
	public float getRearWheelX() {
		// TODO Auto-generated method stub
		return rearWheelX;
	}

	@Override
	public float getTyreSlope() {
		// TODO Auto-generated method stub
		return tyreSlope;
	}

	@Override
	public float getDampSlope() {
		// TODO Auto-generated method stub
		return dampSlope;
	}

	@Override
	public float getTyreRadius() {
		// TODO Auto-generated method stub
		return tyreRadius;
	}

	@Override
	public float getRMax() {
		// TODO Auto-generated method stub
		return rMax;
	}

	@Override
	public float getFcMax() {
		// TODO Auto-generated method stub
		return fcMax;
	}

}

