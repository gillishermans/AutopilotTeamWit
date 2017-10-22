import java.io.*;

import api.AutopilotConfig;
import api.AutopilotConfigReader;
import api.AutopilotInputs;
import api.AutopilotInputsReader;
import api.AutopilotOutputs;
import api.AutopilotOutputsWriter;


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
		AutopilotOutputs output = Main.simulationStarted(config,input);
		
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
		AutopilotOutputs output = Main.timePassed(input);
		
		//Geef dataOuputStream terug
		DataOutputStream outputStream = null;
		AutopilotOutputsWriter.write(outputStream, output);
		
		return outputStream;
	}
	
	public static void simulationEnded(){
		
		//Start simulationEnded in Main
		Main.simulationEnded();
		
	}

}