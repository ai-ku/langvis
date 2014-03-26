package ai.ku.physics;

import javax.vecmath.Vector3d;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import edu.cmu.cs.stage3.alice.core.Model;

public class AIMotionState extends MotionState{

	public final Transform graphicsWorldTrans = new Transform();
	public final Transform centerOfMassOffset = new Transform();
	public final Transform startWorldTrans = new Transform();

	private final Model model;

	public AIMotionState(Model m)
	{
		model = m;
		graphicsWorldTrans.setIdentity();
		centerOfMassOffset.setIdentity();
		startWorldTrans.setIdentity();
	}

	public AIMotionState(Model m, Transform startTrans)
	{
		model = m;
		this.graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		this.startWorldTrans.set(startTrans);
	} 

	public AIMotionState(Model m, Transform startTrans, Transform centerOfMassOffset)
	{
		model = m;
		this.graphicsWorldTrans.set(startTrans);
		this.centerOfMassOffset.set(centerOfMassOffset);
		this.startWorldTrans.set(startTrans);
	}  

	public Transform getWorldTransform(Transform t) {
		t.inverse(centerOfMassOffset);
		t.mul(graphicsWorldTrans);
		return t;
	}

	/*
	public void setWorldTransform(Transform t) {
		// Transform3D t3d = new Transform3D();
		// t3d.setTranslation(worldTrans.origin);
		// t3d.setRotationScale(worldTrans.basis);
		// SGUtils.setWorldTransform(tg, t3d);
		model.setPositionRightNow(AIVector.toVector3d(t.origin), model.getWorld());
		model.setOrientationRightNow(AIMatrix.toMatrix3d(t.basis), model.getWorld());
	}
	*/

	public void setWorldTransform(Transform t)
	{
	    Vector3d pos = model.getBoundingBox(model.getWorld()).getCenter();
	    model.moveRightNow( t.origin.x - pos.x, -t.origin.y - pos.y, t.origin.z - pos.z, model.getWorld() );
	    /*
	     *  SimpleVector pos = obj3d.getTransformedCenter();
	    model.translate(worldTrans.origin.x - pos.x, 
			  (-worldTrans.origin.y) - pos.y, 
			  (-worldTrans.origin.z) - pos.z);
	    */
	   
	    // following rotation may or maynot work. no working rotation to
	    // find out and fix.
	    /*
	    float[] ma = new float[4];
	    float[] dump = new float[16]; //obj3d.getRotationMatrix().getDump();
	    Matrix4f matrix4 = new Matrix4f();
	    
	    Matrix matrixGfx = new Matrix();
	    
	    matrix4.set(worldTrans.basis); // want to set current matrix by arg
	    
	    matrix4.getRow(0, ma);
	    dump[0] = ma[0]; dump[1] = ma[1]; dump[2] = ma[2]; dump[3] = ma[3];
	    matrix4.getRow(1, ma);
	    dump[4] = ma[0]; dump[5] = ma[1]; dump[6] = ma[2]; dump[7] = ma[3];
	    matrix4.getRow(2, ma);
	    dump[8] = ma[0]; dump[9] = ma[1]; dump[10] = ma[2]; dump[11] = ma[3];
	    matrix4.getRow(3, ma);
	    dump[12] = ma[0]; dump[13] = ma[1]; dump[14] = ma[2]; dump[15] = ma[3];

	    matrixGfx.setDump(dump);
	    matrixGfx.rotateX((float)Math.PI);
	    
	    obj3d.setRotationMatrix(matrixGfx);
	    */
	  }


}

/*
import javax.vecmath.Vector3f;
import javax.vecmath.Quat4f;
import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4f;

import com.threed.jpct.SimpleVector;
import com.threed.jpct.Object3D;
import com.threed.jpct.Matrix;

import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

public class JPCTBulletMotionState implements MotionState{
	public final Transform graphicsWorldTrans = new Transform();
	public final Transform centerOfMassOffset = new Transform();
	public final Transform startWorldTrans = new Transform();

  private Object3D obj3d;

  public JPCTBulletMotionState(Object3D obj)
  {
    obj3d = obj;
		graphicsWorldTrans.setIdentity();
		centerOfMassOffset.setIdentity();
		startWorldTrans.setIdentity();
  }

	public JPCTBulletMotionState(Object3D obj, Transform startTrans)
  {
    obj3d = obj;
    this.graphicsWorldTrans.set(startTrans);
		centerOfMassOffset.setIdentity();
		this.startWorldTrans.set(startTrans);
	}  

	public JPCTBulletMotionState(Object3D obj, Transform startTrans, Transform centerOfMassOffset)
  {
    obj3d = obj;
    this.graphicsWorldTrans.set(startTrans);
		this.centerOfMassOffset.set(centerOfMassOffset);
		this.startWorldTrans.set(startTrans);
	}  

  //public void getWorldTransform(Transform worldTrans){
  public Transform getWorldTransform(Transform worldTrans){
 		//worldTrans.set(graphicsWorldTrans);
    //worldTrans.inverse(centerOfMassOffset);
    //worldTrans.set(centerOfMassOffset);
		//worldTrans.mul(graphicsWorldTrans);

		worldTrans.inverse(centerOfMassOffset);
		worldTrans.mul(graphicsWorldTrans);

		//Matrix4f matrix4 = new Matrix4f(obj3d.getRotationMatrix().getDump());
		//matrix4.rotX((float)Math.PI);
		//matrix4.getRotationScale(worldTrans.basis);// want to place matrix in argument
    return worldTrans;
  }

  public void setWorldTransform(Transform worldTrans)
  {
    SimpleVector pos = obj3d.getTransformedCenter();

    obj3d.translate(worldTrans.origin.x - pos.x, 
		  (-worldTrans.origin.y) - pos.y, 
		  (-worldTrans.origin.z) - pos.z);



    // following rotation may or maynot work. no working rotation to
    // find out and fix.
    float[] ma = new float[4];
    float[] dump = new float[16]; //obj3d.getRotationMatrix().getDump();
    Matrix4f matrix4 = new Matrix4f();
    Matrix matrixGfx = new Matrix();

    matrix4.set(worldTrans.basis); // want to set current matrix by arg

    matrix4.getRow(0, ma);
    dump[0] = ma[0]; dump[1] = ma[1]; dump[2] = ma[2]; dump[3] = ma[3];
    matrix4.getRow(1, ma);
    dump[4] = ma[0]; dump[5] = ma[1]; dump[6] = ma[2]; dump[7] = ma[3];
    matrix4.getRow(2, ma);
    dump[8] = ma[0]; dump[9] = ma[1]; dump[10] = ma[2]; dump[11] = ma[3];
    matrix4.getRow(3, ma);
    dump[12] = ma[0]; dump[13] = ma[1]; dump[14] = ma[2]; dump[15] = ma[3];

    matrixGfx.setDump(dump);
    matrixGfx.rotateX((float)Math.PI);

    obj3d.setRotationMatrix(matrixGfx);
  }

}
 */