package ai.ku.util;

import javax.vecmath.Matrix3f;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import com.bulletphysics.linearmath.Transform;

public class GeomUtils {

	public static Matrix4d aliceTransformIdentity() {
		Matrix4d tm = new Matrix4d();
		tm.m00 = 1;
		tm.m11 = 1;
		tm.m22 = 1;
		tm.m33 = 1;
		return tm;
	}
	
	public static Transform bulletTransformIdentity() {
		Transform t = new Transform();
		t.setIdentity();
		return t;
	}
	
	
	public static Matrix4d transformBulletToAlice(Transform t){
		
		Vector3f v = t.origin;
		Matrix3f m = t.basis;
		
		Matrix4d tm = new Matrix4d();
		
		Logger.log(tm.toString());
		
		tm.m00 = m.m00;
		tm.m01 = m.m01;
		tm.m02 = m.m02;
		tm.m03 = v.x;
		// tm.m03 = 0;
		
		tm.m10 = m.m10;
		tm.m11 = m.m11;
		tm.m12 = m.m12;
		tm.m13 = v.y;
		// tm.m13 = 0;
		
		tm.m20 = m.m20;
		tm.m21 = m.m21;
		tm.m22 = m.m22;
		tm.m23 = v.z;
		// tm.m23 = 0;
		
		tm.m30 = 0;
		tm.m31 = 0;
		tm.m32 = 0;
		tm.m33 = 1;
		
		Logger.log( tm.toString() );
		
		return tm;
	}
	
	public static Transform transformAliceToBullet(Matrix4d tm) {
		
		Transform t = new Transform();
		t.setIdentity();
		
		Vector3f v = new Vector3f( (float)tm.m03, (float)tm.m13, (float)tm.m23 );
		Matrix3f m = new Matrix3f();
		
		m.m00 = (float) tm.m00;
		m.m01 = (float) tm.m01;
		m.m02 = (float) tm.m02;
		
		m.m10 = (float) tm.m10;
		m.m11 = (float) tm.m11;
		m.m12 = (float) tm.m12;
		
		m.m20 = (float) tm.m20;
		m.m21 = (float) tm.m21;
		m.m22 = (float) tm.m22;
		
		t.basis.set(m);
		t.origin.set(v);
		
		/*
		boolean alt = true;
		
		Matrix4f m = new Matrix4f();
		
		m.m00 = (float) tm.m00;
		m.m01 = (float) tm.m01;
		m.m02 = (float) tm.m02;
		m.m03 = (alt) ? 0 : (float)tm.m30;
		
		m.m10 = (float) tm.m10;
		m.m11 = (float) tm.m11;
		m.m12 = (float) tm.m12;
		m.m13 = (alt) ? 0 : (float)tm.m31;
		
		m.m20 = (float) tm.m20;
		m.m21 = (float) tm.m21;
		m.m22 = (float) tm.m22;
		m.m23 = (alt) ? 0 : (float)tm.m32;
		
		m.m30 = (alt) ? (float) tm.m30 : 0;
		m.m31 = (alt) ? (float) tm.m31 : 0;
		m.m32 = (alt) ? (float) tm.m32 : 0;
		m.m33 = 1;
		
		t.set(m);
		*/
		
		return t;
	}
	
}
