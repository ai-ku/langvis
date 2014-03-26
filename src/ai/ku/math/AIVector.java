package ai.ku.math;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

public class AIVector extends Vector3d{

	private static final long serialVersionUID = 1L;

	public AIVector() {
		this.x = this.y = this.z = 0;
	}
	
	public AIVector(Vector3d v) {
		this.x = v.x; this.y = v.y; this.z = v.z;
	}
	
	public AIVector(double x, double y, double z) {
		this.x = x; this.y = y; this.z = z;
	}

	public AIVector subtract(AIVector v) {
		 return new AIVector( this.x - v.x, this.y - v.y, this.z - v.z );
	}
	
	public AIVector add(AIVector v) {
		return new AIVector( this.x + v.x, this.y + v.y, this.z + v.z );
	}
	
	public AIVector multiply(double c) {
		return new AIVector( this.x * c, this.y * c, this.z * c );
	}

	public String toString() {
		return String.format("[%.2f,%.2f,%.2f]",x,y,z);
	}
	
	public static Vector3f toVector3f(Vector3d v) {
		return new Vector3f( (float)v.x, (float)v.y, (float)v.z );
	}
	
	public static Vector3d toVector3d(Vector3f v) {
		return new Vector3d( (float)v.x, (float)v.y, (float)v.z );
	}
	
}
