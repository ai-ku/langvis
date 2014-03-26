package ai.ku.physics;

import javax.vecmath.Vector3d;

import com.bulletphysics.linearmath.MatrixUtil;
import com.bulletphysics.linearmath.MotionState;
import com.bulletphysics.linearmath.Transform;

import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;

public class JPCTBulletMotionState extends MotionState{

	public final Transform centerOfMassOffset = new Transform();
	private Model obj3d;
	
	public JPCTBulletMotionState(Model obj)
	{
		obj3d = obj;
		centerOfMassOffset.setIdentity();
	}

	public JPCTBulletMotionState(Model obj, Transform startTrans)
	{
		obj3d = obj;
		setGraphicFromTransform(startTrans);
		centerOfMassOffset.setIdentity();
	}  

	public JPCTBulletMotionState(Model obj, Transform startTrans, Transform centerOfMassOffset)
	{
		obj3d = obj;
		setGraphicFromTransform(startTrans);
		this.centerOfMassOffset.set(centerOfMassOffset);
	}  


	public Transform getWorldTransform(Transform worldTrans){
		setTransformFromGraphic(worldTrans);
		return worldTrans;
	}

	public void setWorldTransform(Transform worldTrans)
	{
		setGraphicFromTransform(worldTrans);
	}


	private void setTransformFromGraphic(Transform tran)
	{
		// SimpleVector p = obj3d.getTransformedCenter();
		Vector3d p = obj3d.getBoundingBox(obj3d.getWorld()).getCenter();
		tran.origin.set( (float)p.x, -((float)p.y), -((float)p.z) ); // not sure if translation or position

		//Matrix matrixGfx = obj3d.getRotationMatrix();
		//matrixGfx.rotateX((float)Math.PI);
		// MatrixUtil.getOpenGLSubMatrix(tran.basis, matrixGfx.getDump());
	}

	private void setGraphicFromTransform(Transform t)
	{
		// SimpleVector pos = obj3d.getTransformedCenter();
		World w = obj3d.getWorld();
		
		Vector3d pos = obj3d.getBoundingBox(w).getCenter();
		/*
		obj3d.translate(tran.origin.x - pos.x,
				(-tran.origin.y) - pos.y, 
				(-tran.origin.z) - pos.z);
				*/
		obj3d.moveRightNow( t.origin.x - pos.x, -t.origin.y - pos.y, t.origin.z - pos.z, w );

		
		
		float[] ma = new float[4];
		// float[] dump = obj3d.getRotationMatrix().getDump(); //new float[16]; 
		double[] dump = obj3d.getTransformation(w).getArray(false);
		// Matrix matrixGfx = new Matrix();
		// MatrixUtil.getOpenGLSubMatrix(t.basis, dump);

		// matrixGfx.setDump(dump);
		// matrixGfx.rotateX((float)Math.PI);

		// obj3d.setRotationMatrix(matrixGfx);
	}

}