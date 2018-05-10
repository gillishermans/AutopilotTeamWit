package autopilotLibrary;

import enums.OccupationEnum;
import interfaces.AutopilotInputs;
import interfaces.AutopilotOutputs;
import interfaces.Outputs;

public class Taxi {
	private OccupationEnum occupation;
	private Besturing besturing;
	
	private PIDController pidlefttwheel= new PIDController(1f,1f,1f,2486f,0f,1f);
	private PIDController pidrightwheel= new PIDController(1f,1f,1f,2486f,0f,1f);
	private PIDController pidVertax = new PIDController(10,10,0,(float) Math.PI/10, (float) -Math.PI/10, 2);
	
	private float bestemmingX = 300;
	private float bestemmingZ = -50;
	
	private float rightWingInclination,leftWingInclination,thrust,horStabInclination,verStabInclination;
	private float leftBrakeForce,rightBrakeForce,frontBrakeForce;
	
	private float turnTime;
	private boolean ZPART;
	
	private float lastLoopTime = 0;
	private final float draaing90 = 9.776f;
	private Vector prevSpeedVector;
	
	public Taxi(Besturing besturing) {
		this.besturing = besturing;
	}
	public AutopilotOutputs turn (AutopilotInputs inputs, float hoek){
		//float hoek =(float) Math.atan((doel[1]-inputs.getZ())/(doel[0]-inputs.getX()));
		leftBrakeForce=0;
		rightBrakeForce=300000;
		frontBrakeForce=0;
		thrust=200000;
		if (inputs.getHeading() == hoek + Math.PI){
			leftBrakeForce=0;
			rightBrakeForce=0;
			frontBrakeForce=0;
			thrust=0;
		}
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
		
	public AutopilotOutputs drive (AutopilotInputs inputs, float [] doel){
		float distance =(float) Math.sqrt(Math.pow((doel[0]-inputs.getX()),2)+Math.pow((doel[1]-inputs.getZ()), 2));
		leftBrakeForce=0;
		rightBrakeForce=0;
		frontBrakeForce=0;
		thrust=200;
		if (distance<2){
			leftBrakeForce=3000;
			rightBrakeForce=3000;
			frontBrakeForce=3000;
			thrust=0;
		}
		
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	public AutopilotOutputs taxi(AutopilotInputs inputs, float[] doel,Besturing besturing) {		
		
		float afstand;
		afstand = (float) Math.sqrt(Math.pow((doel[0]-inputs.getX()),2)+Math.pow((doel[1]-inputs.getZ()), 2));
		
		if(occupation == OccupationEnum.PICKING_UP){
			
		}
		// draai functie => willekeurige hoek naar doelfunctie 
		//rijfunctie tot vliegtuig op doelfunctie is beland
		//alinieerfunctie => GO enum terug geven
		
		
		leftBrakeForce=
		rightBrakeForce=0;
		frontBrakeForce=0;
		thrust=2000;
		if (Math.abs(inputs.getHeading()) >= Math.PI/2){
			rightBrakeForce=0000;
			leftBrakeForce=0000;
			frontBrakeForce=0000;
			thrust=0;
		}
		
		
////		if (getTime() == 0) {
//		rightWingInclination = (float) (Math.PI/20);
//		leftWingInclination = (float) (Math.PI/20);
//		thrust = 0f;
//		horStabInclination = 0f;
//		verStabInclination = 0f;
//		//}
//		leftBrakeForce=3000;
//		rightBrakeForce=3000;
//		frontBrakeForce=3000;
//		thrust=0;
//		if (inputs.getElapsedTime() < 9.762){
//			if( doel[0]-inputs.getX() >0){
//				thrust=200;//rechts draaien
//				leftBrakeForce=3000;
//				rightBrakeForce=0;
//				frontBrakeForce=0;
//			}
//			
//			else if (doel[0]-inputs.getX()<0){//links draaien 
//				thrust=200;
//				leftBrakeForce=0;
//				rightBrakeForce=3000;
//				frontBrakeForce=0;
//			}
//		}	
//		else if (!ZPART && inputs.getElapsedTime()>14 ){
//			if (Math.abs(bestemmingX-inputs.getX()) > Math.abs(3*bestemmingX/100) ){
//			thrust=200;
//			leftBrakeForce=0;
//			rightBrakeForce=0;
//			frontBrakeForce=0;	
//			}
//			else {
//				ZPART=true;
//				this.turnTime=inputs.getElapsedTime();
//			}
//		}
//		
//		if (ZPART && inputs.getElapsedTime()> turnTime+3 ){
//			//
//			if (inputs.getElapsedTime()< this.turnTime+12.912){
//				if( (bestemmingZ-inputs.getZ())*(bestemmingX-inputs.getX())>0){ //rechts draaien 
//					thrust=200;
//					leftBrakeForce=3000;
//					rightBrakeForce=0;
//					frontBrakeForce=0;
//				}
//				else if ((bestemmingX-inputs.getX())*(bestemmingZ-inputs.getZ())<0){ //links draaien 
//					System.out.print(turnTime);
//					thrust=200;
//					leftBrakeForce=0;
//					rightBrakeForce=3000;
//					frontBrakeForce=0;
//				}
//			
//			}
//			else if (inputs.getElapsedTime()>turnTime+ 15.912  ) {
//				if (Math.abs(bestemmingZ-inputs.getZ()) > Math.abs(3*bestemmingZ/100) ){
//				thrust=200;
//				leftBrakeForce=0;
//				rightBrakeForce=0;
//				frontBrakeForce=0;	
//				}
//		
//			}
//		}
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);
	}
	
	public AutopilotOutputs draaiFunctie(AutopilotInputs inputs){
		float goal;
		goal=(float) Math.atan(bestemmingX/bestemmingZ);
		float goalgraad;
		float turntime;
		goalgraad= Math.abs((float) (goal*180/Math.PI));
		
		
		turntime =(float) (0.6348738114821205 + 0.37783121152918275*goalgraad - 0.01634711016193711*Math.pow(goalgraad, 2) + 0.0005014392630037885*Math.pow(goalgraad, 3) - 0.00000841026261600979*Math.pow(goalgraad,4) + 7.075264799883*Math.pow(goalgraad, 5)*Math.pow(10, -8) - 2.3312240521*Math.pow(goalgraad, 6)*Math.pow(10, -10));
		turntime= Math.min(turntime,draaing90);
		if (bestemmingZ<0){
			if (inputs.getElapsedTime()<turntime){
				if ( goal > 0 ) {					///links draaien
					thrust=200 ;
					leftBrakeForce=0;
					rightBrakeForce=3000;
				}
			else {							//rechts draaien
					thrust=200 ;
					leftBrakeForce=3000;
					rightBrakeForce=0;
				}
			}
			else  {
				thrust=0;
				leftBrakeForce=3000;
				rightBrakeForce=3000;
				frontBrakeForce=3000;
			}
		}
		else if( bestemmingZ>0){
			if (inputs.getElapsedTime()<14.04){ //tijd nodig voor 180 graden te draaien 
				thrust=200;
				leftBrakeForce=0;
				rightBrakeForce=3000;
			}
			else if (inputs.getElapsedTime()<18 && inputs.getElapsedTime()>14){
				thrust=0;
				leftBrakeForce=3000;
				rightBrakeForce=3000;
				frontBrakeForce=3000;
			}
			else if (inputs.getElapsedTime()>15){ //algoritme opnieuw
				goal=(float) Math.atan((bestemmingX-inputs.getX())/(bestemmingZ-inputs.getZ()));
				goalgraad= Math.abs((float) (goal*180/Math.PI));
				turntime =(float) (0.6348738114821205 + 0.37783121152918275*goalgraad - 0.01634711016193711*Math.pow(goalgraad, 2) + 0.0005014392630037885*Math.pow(goalgraad, 3) - 0.00000841026261600979*Math.pow(goalgraad,4) + 7.075264799883*Math.pow(goalgraad, 5)*Math.pow(10, -8) - 2.3312240521*Math.pow(goalgraad, 6)*Math.pow(10, -10));
				if (inputs.getElapsedTime()<18+turntime){
					if ( goal > 0 ) {					///links draaien
						thrust=200 ;
						leftBrakeForce=0;
						rightBrakeForce=3000;
						frontBrakeForce=0;
					}
					else {							//rechts draaien
						thrust=200 ;
						leftBrakeForce=3000;
						rightBrakeForce=0;
						frontBrakeForce=0;
					}
				}
				else  {
					thrust=0;
					leftBrakeForce=3000;
					rightBrakeForce=3000;
					frontBrakeForce=3000;
					
					}
			}
	}
		
		
		return new Outputs(thrust,leftWingInclination , rightWingInclination, horStabInclination, verStabInclination, frontBrakeForce, rightBrakeForce, leftBrakeForce);	
	}
	
	public float getTime() {
		return besturing.getTime();
	}
	
}
	

