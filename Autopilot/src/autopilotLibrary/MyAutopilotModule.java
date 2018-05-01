package autopilotLibrary;
import interfaces.*;


public class MyAutopilotModule implements AutopilotModule {
	
	private static AutopilotHandler handler;

	public MyAutopilotModule() {
		handler = new AutopilotHandler();
	}

	@Override
	public void defineAirportParams(float length, float width) {
		handler.setAirportConfig(length, width);
	}

	@Override
	public void defineAirport(float centerX, float centerZ, float centerToRunway0X, float centerToRunway0Z) {
		handler.addAirport(centerX,centerZ,centerToRunway0X,centerToRunway0Z);
		
	}

	@Override
	public void defineDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config) {
		handler.addDrone(airport, gate, pointingToRunway, config);
	}

	@Override
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		handler.startTimeHasPassed(drone, inputs);
		
	}

	@Override
	public AutopilotOutputs completeTimeHasPassed(int drone) {
		return handler.completeTimeHasPassed(drone);
	}

	@Override
	public void deliverPackage(int fromAirport, int fromGate, int toAirport, int toGate) {
		handler.deliverPackage(fromAirport, fromGate, toAirport, toGate);
	}
	
	@Override
	public void simulationEnded() {
		
		//Stop de simulatie
		
	}
}
