package ai.ku.model;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.vecmath.Vector3d;

import ai.ku.constraint.AIConstraintRectangle;
import ai.ku.constraint.AIConstraintRules;
import ai.ku.constraint.AIRelation;
import ai.ku.drawing.AIPoint;
import ai.ku.drawing.AIRect;
import ai.ku.util.Logger;
import ai.ku.util.Scale;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Matrix33;

public class AIModel implements Comparable<AIModel>{

	private Model model;
	private Scale scale;
	private AIModel ground;
	private ArrayList<AIModel> childs;
	private Model box;
	private World w;
	
	// Cassowary Constraint Solver 
	private AIConstraintRectangle rectangle = null;
	private CopyOnWriteArrayList<AIRelation> relationList;
	private AIConstraintRules rules;
	
	public AIModel(Model model) {
		this.setModel(model);
		this.setWorld(model.getWorld());
		childs = new ArrayList<AIModel>();
		
		// Cassowary Constraint Solver 
		relationList = new CopyOnWriteArrayList<AIRelation>();
		rules = new AIConstraintRules();
		
		scale = new Scale(1,1,1);
	}

	public int getChildCount() { return childs.size(); }
	public AIModel getChild(int i) { return childs.get(i); }
	public void addChild(AIModel amodel) { childs.add(amodel); }
	public void removeChild(AIModel amodel) { childs.remove(amodel); }
	public AIModel getGround() { return ground; }
	public void setGround(AIModel ground) { this.ground = ground; }
	public Model getModel() { return model; }
	public void setModel(Model model) { this.model = model; }
	public World getWorld() { return w; }
	public void setWorld(World w) { this.w = w; }
	public Model getBox() { return box; }
	public void setBox(Model box) { this.box = box; }
	
	// Cassowary Constraint Solver 
	public CopyOnWriteArrayList<AIRelation> getRelationList() { return this.relationList; }
	public void setRelationList(CopyOnWriteArrayList<AIRelation> list) { this.relationList = list; }
	public AIConstraintRules getRules() { return this.rules; }
	public void setRules(AIConstraintRules rules) { this.rules = rules; }

	public void initializeRectangle(double centerX, double centerY, double width, double depth) { rectangle = new AIConstraintRectangle(centerX, centerY, width, depth); }
	public AIConstraintRectangle getRectangle() { return rectangle; };
	public void setRectangle(AIConstraintRectangle rectangle) {this.rectangle = rectangle; }; 
	
	public boolean isLargerThan(AIModel other) {  
		return ( this.getBBVolume() >= other.getBBVolume() );
	}

	private double getBBVolume() {
		Box bb = this.getBoundingBox();
		double amount = bb.getWidth() * bb.getHeight() * bb.getDepth();
		return amount;
	}

	public Box getBoundingBox() {
		return model.getBoundingBox(w);
	}

	public int compareTo(AIModel other) {

		double a = this.getBBVolume();
		double b = other.getBBVolume();

		if( a < b )
			return -1;
		else if( a > b )
			return 1;

		return 0;
	}

	public void setupBox() {
		Model box = this.getBox();
		Model m = this.getModel();
		if( box != null ) {
			Box bm = m.getBoundingBox(w);
			Box bb = box.getBoundingBox(w);
			ModelModifier.getInstance().modify( box, new Scale(
					bm.getWidth()  / bb.getWidth(),
					bm.getHeight() / bb.getHeight(),
					bm.getDepth()  / bb.getDepth()) );
			box.setPositionRightNow(bm.getCenter(),w);
		}
	}
	
	public Vector3d getTop() { return this.getBoundingBox().getCenterOfTopFace(); }
	public Vector3d getBottom() { return this.getBoundingBox().getCenterOfBottomFace(); }
	public Vector3d getCenter() { return this.getBoundingBox().getCenter(); }
	public Vector3d getPosition() { return this.getBoundingBox().getCenter(); }
	public Vector3d position() { return this.getPosition(); }
	
	public double distance(AIModel other) { 
		Vector3d a = this.position(); 
		Vector3d b = other.position();
		return Math.sqrt( Math.pow((a.x - b.x),2) + Math.pow((a.y - b.y),2) + Math.pow((a.z - b.z),2) );
	};
	
	public void move(Vector3d v) { model.moveRightNow(v, w); }
	public void setPosition(Vector3d v) { model.setPositionRightNow(v, w); }
	public void setOrientation(AIModel o) { model.setOrientationRightNow(o.getModel().getOrientationAsForwardAndUpGuide(w),w); }

	public String name() { return this.getModel().getRepr(); }
	public double width() { return this.getModel().getBoundingBox().getWidth(); }
	public double depth() { return this.getModel().getBoundingBox().getDepth(); }
	public double height() { return this.getModel().getBoundingBox().getHeight(); }
	
	public Scale getScale() {
		return scale;
	}

	public void setScale(Scale scale) {
		this.scale = scale;
	}
	
	public AIRect getDrawRectWithReferenceRect(AIRect r) {
		
		double w = r.getWidth();
		double h = r.getHeight();
		
		Box b = this.getBoundingBox();
		AIRect rect = new AIRect(512*b.getWidth()/w,512*b.getDepth()/h);
		
		Vector3d sp = this.getPosition(); // position
		Vector3d gp = this.getGround().getPosition();  // ground position
		
		AIPoint d = new AIPoint(256+(int)(512*(gp.x-sp.x)/w),256+(int)(512*(gp.z-sp.z)/h));
		rect.setCenter(d);
		
		return rect;
	}
	
	public AIRect getDrawRectWithReferenceBox(Box box) {
		
		double w = box.getWidth()*2;
		double h = box.getDepth()*2;
		// double s = Math.max(w, h);
		
		Box b = this.getBoundingBox();
		AIRect rect = new AIRect(512*b.getWidth()/w,512*b.getDepth()/h);
		
		Vector3d sp = this.getPosition(); // position
		Vector3d gp = box.getCenter();  // ground position
		
		AIPoint d = new AIPoint(256+(int)(512*(sp.x-gp.x)/w),256+(int)(512*(sp.z-gp.z)/h));
		rect.setCenter(d);
		
		return rect;
	}

	public AIRect getDrawRect() {
		
		Box gb = this.getGround().getBoundingBox();
		double w = gb.getWidth();
		double h = gb.getDepth();
		
		w = Math.max(w, h);
		h = w;
		
		Box b = this.getBoundingBox();
		AIRect rect = new AIRect(512*b.getWidth()/w,512*b.getDepth()/h);
		
		Vector3d sp = this.getPosition(); // position
		Vector3d gp = this.getGround().getPosition();  // ground position
		
		AIPoint d = new AIPoint(256-(int)(512*(sp.x-gp.x)/w),256+(int)(512*(sp.z-gp.z)/h));
		rect.setCenter(d);
		
		// Matrix44 m = this.getModel().getLocalTransformation();
		Matrix33 m = this.getModel().getOrientationAsAxes();
		double angle = Math.atan2(m.m02, m.m00);
		rect.setRotation(angle);
		
		Matrix33 mm = this.getGround().getModel().getOrientationAsAxes();
		// Matrix44 mm = this.getGround().getModel().getLocalTransformation();
		
		Logger.log("Ground orientation: "+Math.toRadians(Math.atan2(mm.m02, mm.m00)));
		Logger.log(""+this.getModel().getRepr()+" rotation:");
		Logger.log("Angle in radians: "+Math.toRadians(angle));
		Logger.log("Angle in degrees: "+Math.toDegrees(angle));
		
		return rect;
	}

	
}
