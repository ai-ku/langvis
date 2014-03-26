package ai.ku.math;

import javax.vecmath.Matrix3d;
import javax.vecmath.Matrix3f;

public class AIMatrix {

	public static Matrix3f toMatrix3f(Matrix3d m) {
		Matrix3f matrix = new Matrix3f();
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++)
				matrix.setElement( i, j, (float) m.getElement(i,j) );
		
		return matrix;
	}
	
	public static Matrix3d toMatrix3d(Matrix3f m) {
		Matrix3d matrix = new Matrix3d();
		for( int i = 0; i < 3; i++ )
			for( int j = 0; j < 3; j++)
				matrix.setElement( i, j, m.getElement(i,j) );
		
		return matrix;
	}
	
}
