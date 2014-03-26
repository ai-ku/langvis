package ai.ku.util;

public class Scale {
	
	public double x;
	public double y;
	public double z;
	
	public Scale(double x,double y,double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Scale(String s) {
		String xs = s.substring(s.indexOf("[")+1, s.indexOf(","));
		String ys = s.substring(s.indexOf(",")+1,s.lastIndexOf(","));
		String zs = s.substring(s.lastIndexOf(",")+1,s.indexOf("]"));
		this.x = Double.parseDouble(xs);
		this.y = Double.parseDouble(ys);
		this.z = Double.parseDouble(zs);
	}
	
	public String toString() {
		return "["+x+","+y+","+z+"]";
	}
	
	public Scale inverse() {
		return new Scale(1.0/x,1.0/y,1.0/z);
	}
}
