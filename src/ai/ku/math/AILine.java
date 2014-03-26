package ai.ku.math;

public class AILine {
	
	AIVector dir, p1,p2;
	
	public AILine(AIVector p1, AIVector p2)
	{
		this.dir = p1.subtract(p2);
		this.p1 = p1;
		this.p2 = p2;
	}
}