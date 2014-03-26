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

import edu.cmu.cs.stage3.alice.core.property.BooleanProperty;
import edu.cmu.cs.stage3.alice.core.property.ObjectProperty;
import edu.cmu.cs.stage3.alice.core.property.StringProperty;

public class Print extends edu.cmu.cs.stage3.alice.core.Response {
	public final StringProperty text = new StringProperty( this, "text", null );
	public final ObjectProperty object = new ObjectProperty( this, "object", null, Object.class ) {
		protected boolean getValueOfExpression() {
			return true;
		}
	};
	public final BooleanProperty addNewLine = new BooleanProperty( this, "addNewLine", Boolean.TRUE );

	protected Number getDefaultDuration() {
		return new Double( 0 );
	}

	public String getPrefix() {
		String t = text.getStringValue();
		if( t != null ) {
			return null;
		} else {
			Object o = object.get();
			if( o != null ) {
				if( o instanceof edu.cmu.cs.stage3.alice.core.Element ) {
					return "the value of " + ((edu.cmu.cs.stage3.alice.core.Element)o).getTrimmedKey() + " is ";
				} else {
					return "the value of " + o + " is ";
				}
			} else {
				return null;
			}
		}
	}

	public class RuntimePrint extends RuntimeResponse {
		public void update( double t ) {
			super.update( t );
			String s = Print.this.text.getStringValue();
			Object o = Print.this.object.get();
			Object value = Print.this.object.getValue();
			String valueText;
			if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
				valueText = ((edu.cmu.cs.stage3.alice.core.Element)value).getTrimmedKey();
			} else {
				if( value != null ) {
					valueText = value.toString();
				} else {
					valueText = "None";
				}
			}

			String output;
			if( s != null ) {
				if( o != null ) {
					output = s + valueText;
				} else {
					output = s;
				}
			} else {
				if( o != null ) {
					output = Print.this.getPrefix() + valueText;
				} else {
					output = valueText;
				}
			}
			if( Print.this.addNewLine.booleanValue() ) {
				System.out.println( output );
			} else {
				System.out.print( output );
			}
		}
	}
}
