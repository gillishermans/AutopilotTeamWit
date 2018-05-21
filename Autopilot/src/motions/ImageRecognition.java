package motions;

import java.util.ArrayList;

import org.opencv.core.Point;

import autopilotLibrary.Beeldherkenning;
import interfaces.AutopilotConfig;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;
import util.PIDController;
import util.Vector;

public class ImageRecognition extends Motion {
	
	private Beeldherkenning beeldherkenning;
	private Vector posCube;
	
	private float distance = Float.MAX_VALUE;
	
	private final float maxAOA = (float) Math.PI/20;
	
	private PIDController pidVerImage = new PIDController(1f, 1, 0f, (float) Math.PI / 6f, (float) -Math.PI / 6f, 1);
	private PIDController pidHorImage = new PIDController(1f, 1, 0f, (float) Math.PI / 6, (float) -Math.PI / 6, 1);
	private PIDController pidRollImage = new PIDController(1f,1f,0f,(float) Math.PI/60, (float) -Math.PI/60 ,20);
	private PIDController pidHeadingImage = new PIDController(0.5f, 1f, 0.5f, (float) Math.PI / 1, (float) -Math.PI / 1, 10);
	private PIDController pidStab = new PIDController(3f, 1, 0f, (float) Math.PI / 6f, (float) -Math.PI / 6f, 30);
	
	public ImageRecognition(AutopilotConfig config, Vector posCube) {
		this.beeldherkenning = new Beeldherkenning(config);
		this.posCube = posCube;
	}

	@Override
	public AutopilotOutputs getOutputs(AutopilotInputs inputs, float time) {
		////////////BEELDHERKENNING
		beeldherkenning.imageRecognition(inputs.getImage());
		// if (path.isEmpty()) phase = Phase.GEENKUBUS;

		ArrayList<Point> centerArray = beeldherkenning.getCenterArray();
		ArrayList<Float> radiusArray = beeldherkenning.getRadiusArray();
		
		ArrayList<Float> distanceArray = new ArrayList<Float>();
		if (centerArray.isEmpty()) {
			this.done = true;
			return new Outputs(0,0,0,0,0,0,0,0);
		}
		float shortest = beeldherkenning.distanceToObject(centerArray.get(0), radiusArray.get(0));
		//System.out.println("Shortest: " + shortest);
		int shortestI = 0;
		for (int i = 0; i < centerArray.size(); i++) {
			float distance = beeldherkenning.distanceToObject(centerArray.get(i), radiusArray.get(i));
			distanceArray.add(distance);
			if (distance < shortest) {
				shortest = distance;
				shortestI = i;
			}
		}

		// Beweeg naar dichtstbijzijnde kubus
		float horizontalAngle = beeldherkenning.horizontalAngle(centerArray.get(shortestI));
		float verticalAngle = beeldherkenning.verticalAngle(centerArray.get(shortestI));
		//System.out.println(horizontalAngle);
		
		///////////////////////////////////////////////////////////////////////
		
		float thrust = pidThrust.getOutput(65, speed, time);
		
		//Vertical
		float outputVer = pidVerImage.getOutput(0, verticalAngle, time);
		outputVer = aoaController.aoaController(outputVer, maxAOA);
		float outputRoll;
		//Horizontal
//		if (Math.abs(horizontalAngle) < (float) Math.PI/180) {
//			outputRoll = pidStab.getOutput(0, inputs.getHeading(), time);
//			System.out.println("STAB");
//			pidRollImage.reset();
//		}
//		else {
			float maxRollTurn = Math.min((float) Math.PI/18, Math.abs(horizontalAngle)* 4f);
			if (Math.abs(inputs.getRoll()) > maxRollTurn || distance(inputs.getX(), inputs.getZ(), posCube.x, posCube.z) < 10) {
				if (inputs.getRoll() > 0 ) outputRoll = pidRollImage.getOutput( maxRollTurn, inputs.getRoll(), time);
				else					   outputRoll = pidRollImage.getOutput(-maxRollTurn, inputs.getRoll(), time);
			}
			else {
				outputRoll = pidHorImage.getOutput(0, horizontalAngle, time);
			}
		//}
		outputRoll = aoaController.aoaRollController(outputVer, outputRoll, maxAOA);
		float leftWingInclination = outputVer - outputRoll;
		float rightWingInclination = outputVer + outputRoll;
		float outputPitch = pidPitch.getOutput(0, inputs.getPitch(), time);
		outputPitch = aoaController.aoaController(outputPitch, (float) Math.PI/25);
		float horStabInclination = -outputPitch;
		float verStabInclination = 0;
		
		//checkDone(inputs);
		//return new Outputs(0, 0, 0, 0, 0, 0,0,0);
		return new Outputs(thrust, leftWingInclination, rightWingInclination, horStabInclination, verStabInclination, 0,0,0);
	}

	@Override
	public boolean isDone() {
		return this.done;
	}
	
	public void checkDone(AutopilotInputs inputs) {
		float x = inputs.getX();
		float z = inputs.getZ();
		float newDistance = distance(x,z, posCube.x, posCube.y);
		if (newDistance > distance) {
			this.done = true;
		}
		else {
			this.done = false;
			distance = newDistance;
			System.out.println(distance);
		}
	}

	@Override
	public String getMotion() {
		return "IMAGERECOG";
	}

	@Override
	public Vector nextPos(AutopilotInputs inputs) {
		return posCube;
	}
	
	public float distance(float x1, float y1, float x2, float y2) {
		float x = (float) Math.pow(x2-x1,2);
		float y = (float) Math.pow(y2-y1,2);
		float xy = (float) Math.sqrt(x+y);
		//System.out.println("Distance: " + xy);
		return xy;
	}

}
