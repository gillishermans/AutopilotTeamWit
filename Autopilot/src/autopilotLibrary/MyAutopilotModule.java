package autopilotLibrary;
import interfaces.*;


public class MyAutopilotModule implements AutopilotModule {
	
	private static AutopilotHandler handler;

	public MyAutopilotModule() {
		handler = new AutopilotHandler();
	}

	@Override
	public void defineAirportParams(float length, float width) {
		handler.setLuchthavenConfig(length, width);
	}

	@Override
	public void defineAirport(float centerX, float centerZ, float centerToRunway0X, float centerToRunway0Z) {
		handler.addLuchthaven();
		
	}

	@Override
	public void defineDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config) {
		handler.addDrone();
		
	}

	@Override
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		handler.startTimeHasPassed(drone, inputs);
		
	}

	@Override
	public AutopilotOutputs completeTimeHasPassed(int drone) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void simulationEnded() {
		
		//Stop de simulatie
		
	}
}
