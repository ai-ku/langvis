package ai.ku.math;

public class AIRectangle {

	private AIVector p1, p2, p3, p4;
	private AIVector max, min;

	public AIRectangle(AIVector p1, AIVector p2, AIVector p3, AIVector p4)
	{
		this.p1 = p1; 
		this.p2 = p2; 
		this.p3 = p3; 
		this.p4 = p4;
		
		setBoundValues();
	}

	public boolean intersects(AILine l)
	{
		boolean result = false;
		AIPlane plane = new AIPlane( p1, p2, p3 );
		AIVector p = plane.getIntersectionPoint( l );
		if( p != null )
		{
			double e = 0.001; // error
			result = ( (p.x-max.x) <= e && (p.x-min.x) >= -e ) && 
					 ( (p.y-max.y) <= e && (p.y-min.y) >= -e ) && 
					 ( (p.z-max.z) <= e && (p.z-min.z) >= -e );

			if( result )
			{
				boolean isBetweenX = ((p.x >= l.p1.x && p.x <= l.p2.x) || (p.x >= l.p2.x && p.x <= l.p1.x));
				boolean isBetweenY = ((p.y >= l.p1.y && p.y <= l.p2.y) || (p.y >= l.p2.y && p.y <= l.p1.y));
				boolean isBetweenZ = ((p.z >= l.p1.z && p.z <= l.p2.z) || (p.z >= l.p2.z && p.z <= l.p1.z));
				
				result = ( isBetweenX && isBetweenY && isBetweenZ );
			}	
		}	
		return result;
	}
	  
	public void setBoundValues()
	{		
		double minX = Math.min( p4.x, Math.min( p3.x, Math.min( p2.x, p1.x ) ) );
		double minY = Math.min( p4.y, Math.min( p3.y, Math.min( p2.y, p1.y ) ) );
		double minZ = Math.min( p4.z, Math.min( p3.z, Math.min( p2.z, p1.z ) ) );
		double maxX = Math.max( p4.x, Math.max( p3.x, Math.max( p2.x, p1.x ) ) );
		double maxY = Math.max( p4.y, Math.max( p3.y, Math.max( p2.y, p1.y ) ) );
		double maxZ = Math.max( p4.z, Math.max( p3.z, Math.max( p2.z, p1.z ) ) );

		min = new AIVector( minX, minY, minZ );
		max = new AIVector( maxX, maxY, maxZ );
	}
}
