package autopilotLibrary;
import interfaces.*;


public class MyAutopilotModule implements AutopilotModule {

	public MyAutopilotModule() {
		AutopilotHandler handler = new AutopilotHandler();
	}

	@Override
	public void defineAirportParams(float length, float width) {
		// TODO Auto-generated method stub
	}

	@Override
	public void defineAirport(float centerX, float centerZ, float centerToRunway0X, float centerToRunway0Z) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void defineDrone(int airport, int gate, int pointingToRunway, AutopilotConfig config) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void startTimeHasPassed(int drone, AutopilotInputs inputs) {
		// TODO Auto-generated method stub
		
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
