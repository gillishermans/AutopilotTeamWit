package interfaces;

public interface Autopilot {
    AutopilotOutputs simulationStarted(AutopilotConfig config, AutopilotInputs_v2 inputs);
    AutopilotOutputs timePassed(AutopilotInputs_v2 inputs);
    void setPath(Path path);
    void simulationEnded();
}

