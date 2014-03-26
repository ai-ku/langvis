/*
 * Copyright (c) 1999-2003, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice",
 *    nor may "Alice" appear in their name, without prior written
 *    permission of Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    "This product includes software developed by Carnegie Mellon University"
 */

package edu.cmu.cs.stage3.alice.core.response;

import edu.cmu.cs.stage3.alice.core.property.ReferenceFrameProperty;
import edu.cmu.cs.stage3.alice.core.property.Vector3Property;

public abstract class AbstractPointAtAnimation extends OrientationAnimation {
	public final ReferenceFrameProperty target = new ReferenceFrameProperty( this, "target", null );
	public final Vector3Property offset = new Vector3Property( this, "offset", null );
	public final Vector3Property upGuide = new Vector3Property( this, "upGuide", null );

	public abstract class RuntimeAbstractPointAtAnimation extends RuntimeOrientationAnimation {
		private edu.cmu.cs.stage3.alice.core.ReferenceFrame m_target;
		private javax.vecmath.Vector3d m_offset;
		private javax.vecmath.Vector3d m_upGuide;
		private boolean m_onlyAffectYaw;
		protected abstract boolean onlyAffectYaw();
		public void prologue( double t ) {
			super.prologue( t );
			m_target = AbstractPointAtAnimation.this.target.getReferenceFrameValue();
			m_offset = AbstractPointAtAnimation.this.offset.getVector3Value();
			m_upGuide = AbstractPointAtAnimation.this.upGuide.getVector3Value();
			m_onlyAffectYaw = onlyAffectYaw();
            if( m_target == null ) {
                throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "target value must not be null.", null, AbstractPointAtAnimation.this.target );
            }
            if( m_target == m_subject ) {
                throw new edu.cmu.cs.stage3.alice.core.SimulationPropertyException( "target value must not be equal to the subject value.", getCurrentStack(), AbstractPointAtAnimation.this.target );            
            }
		}
		protected edu.cmu.cs.stage3.math.Matrix33 getTargetMatrix33() {
			return m_subject.calculatePointAt( m_target, m_offset, m_upGuide, m_asSeenBy, m_onlyAffectYaw );
		}
		protected edu.cmu.cs.stage3.math.Quaternion getTargetQuaternion() {
			return getTargetMatrix33().getQuaternion();
		}
		public void update( double t ) {
			//for now we will need to calculate target quaternion every frame
			markTargetQuaternionDirty();
			super.update( t );
		}
	}
}
