import api.AutopilotConfig;

public class Config implements AutopilotConfig {

	public Config() {
		
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

}

