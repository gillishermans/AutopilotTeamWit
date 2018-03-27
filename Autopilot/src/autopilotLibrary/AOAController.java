package autopilotLibrary;

public class AOAController {

	public float getAngleOfAttack(Vector speed, float incl) {
		
		Vector vector = new Vector(0,0,0);
		
		Vector normal = vector.crossProd(this.getAxisVector(),this.getAttackVector(incl));
		Vector airspeed = speed;
		Vector axis = this.getAxisVector();
//		
		Vector projectedAirspeed = 	vector.sum(airspeed, vector.product(-1*vector.scalairProd(axis, airspeed)/vector.lengthSquared(axis),axis));	
		float angleOfAttack = (float) -Math.atan2(vector.scalairProd(projectedAirspeed,normal), 
				vector.scalairProd(projectedAirspeed,this.getAttackVector(incl)));
		
		return angleOfAttack;
	}
	
	public Vector getAxisVector() {
		return new Vector(1,0,0);
	}
	
	public Vector getAttackVector(float incl) {
		return new Vector((float) Math.sin(0),
				          (float) Math.sin(incl),
				          (float)-Math.cos(incl));
	}
	
	public float aoaController(float incl, float max) {
		if (Math.abs(incl) > max) {
			if (incl > 0) return max;
			else          return -max;
		}
		return incl;
	}
	
	public float aoaRollController(float incl, float roll, float max) {
		float roll1 = 0;
		if (incl > 0) {
			if (roll > 0) {
				if (incl + roll > max) return max - incl;
				else return roll;
			} else {
				if (incl - roll > max) return max + roll;
				else return roll;
			}
		}
		
		else
			if (incl - roll < -max) return -max - incl;
			else {
				return roll;
			}
	}
	
}
