package ai.ku.math;

public class AIPlane {

	private double coefA, coefB, coefC, coefD;

	public AIPlane(double coeffA, double coeffB, double coeffC, double coeffD)
	{
		this.coefA = coeffA;
		this.coefB = coeffB;
		this.coefC = coeffC;
		this.coefD = coeffD;
	}

	public AIPlane(AIVector p1, AIVector p2, AIVector p3)
	{
		// A = y1 (z2 - z3) + y2 (z3 - z1) + y3 (z1 - z2) 
		// B = z1 (x2 - x3) + z2 (x3 - x1) + z3 (x1 - x2) 
		// C = x1 (y2 - y3) + x2 (y3 - y1) + x3 (y1 - y2) 
		// D = -x1(y2*z3 - y3*z2) - x2(y3*z1 - y1*z3) - x3(y1*z2 - y2*z1)
		this.coefA =  p1.y * (p2.z - p3.z) + p2.y * (p3.z - p1.z) + p3.y * (p1.z - p2.z);
		this.coefB =  p1.z * (p2.x - p3.x) + p2.z * (p3.x - p1.x) + p3.z * (p1.x - p2.x);
		this.coefC =  p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y);
		this.coefD = -p1.x * (p2.y * p3.z - p3.y * p2.z) - p2.x * (p3.y * p1.z - p1.y * p3.z) - p3.x * (p1.y * p2.z - p2.y * p1.z);
	}

	public boolean isPointOnPlane(AIVector p)
	{
		double s = coefA * p.x + coefB * p.y + coefC * p.z + coefD;
		return ( s == 0 ) ? true : false;
	}

	public AIVector getIntersectionPoint(AILine l)//Check only plane and line intersect on a point
	{
		AIVector intersectionPoint = null;
		// parametric value 
		// t = -(A*x0 + B*y0 + C*z0 + D) / (A*xd + B*yd + C*zd)
		double t = (-coefD - coefA * l.p1.x - coefB * l.p1.y - coefC * l.p1.z) / (l.dir.x * coefA + l.dir.y * coefB + l.dir.z * coefC);
		// x = l.pointOnTheLine.getX() + t * l.direction.getX() // x value of intersection point
		if( !( Double.isNaN(t) ) )
			intersectionPoint = l.p1.add(l.dir.multiply(t));
		
		return intersectionPoint;
	}

	public double getCoefficientA() { return coefA; }
	public double getCoefficientB() { return coefB; }
	public double getCoefficientD() { return coefD; }
	public double getCoefficientC() { return coefC; }
	public void setCoefficientA( double cA ) { this.coefA = cA; }
	public void setCoefficientB( double cB ) { this.coefB = cB; }
	public void setCoefficientC( double cC ) { this.coefC = cC; }	
	public void setCoefficientD( double cD ) { this.coefD = cD; }

}