package autopilotLibrary;
import java.io.*;

import interfaces.AutopilotConfig;
import interfaces.AutopilotConfigReader;
import interfaces.AutopilotInputs;
import interfaces.AutopilotInputsReader;
import interfaces.AutopilotOutputs;
import interfaces.AutopilotOutputsWriter;


//Neemt een DataInputStream van Testbed aan, roept de main functie aan met de gegeven input
//en geeft output terug in DataInputStream
public class Stream {

	public Stream() {
		// TODO Auto-generated constructor stub
	}
	
	public static DataOutputStream simulationStarted(java.io.DataInputStream configStream,java.io.DataInputStream inputStream) throws IOException{
		
		//Lees configStream en inputStream in
		AutopilotConfig config = AutopilotConfigReader.read(configStream);
		configStream.close();
		AutopilotInputs input = AutopilotInputsReader.read(inputStream);
		inputStream.close();
		
		//Start simulatie
		AutopilotOutputs output = CommunicatieTestbed.simulationStarted(config,input);
		
		//Geef dataOuputStream terug
		DataOutputStream outputStream = null;
		AutopilotOutputsWriter.write(outputStream, output);
		
		return outputStream;
	}
	
	public static DataOutputStream timePassed(java.io.DataInputStream inputStream) throws IOException{
		
		//Lees inputStream in
		AutopilotInputs input = AutopilotInputsReader.read(inputStream);
		inputStream.close();
		
		//Start timePassed stap
		AutopilotOutputs output = CommunicatieTestbed.timePassed(input);
		
		//Geef dataOuputStream terug
		DataOutputStream outputStream = null;
		AutopilotOutputsWriter.write(outputStream, output);
		
		return outputStream;
	}
	
	public static void simulationEnded(){
		
		//Start simulationEnded in Main
		CommunicatieTestbed.simulationEnded();
		
	}

}
