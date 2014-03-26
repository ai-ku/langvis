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

package edu.cmu.cs.stage3.alice.authoringtool.util;

import java.awt.Color;
import java.awt.Dimension;

/**
 * @author Jason Pratt
 */
public class ElementTreeCellRenderer extends javax.swing.JPanel implements javax.swing.tree.TreeCellRenderer {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7898622288721965400L;
	
	protected DnDGroupingPanel dndPanel = new DnDGroupingPanel();
	protected javax.swing.JPanel elementPanel = new javax.swing.JPanel();
	protected javax.swing.JLabel iconLabel = new javax.swing.JLabel();
	protected javax.swing.JLabel elementLabel = new javax.swing.JLabel();
	protected java.awt.Color selectedColor = Color.orange;// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "objectTreeSelected" );
	protected java.awt.Color bgColor = Color.white;// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "objectTreeBackground" );
	protected java.awt.Color disabledColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "objectTreeDisabled" );
	protected java.awt.Color textColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "objectTreeText" );
	protected java.awt.Color disabledTextColor = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "objectTreeDisabledText" );
	protected java.awt.Color selectedTextColor = Color.black;// edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getColor( "objectTreeSelectedText" );

	public ElementTreeCellRenderer() {
		setOpaque( false );
		setLayout( new java.awt.GridBagLayout() );
		add( dndPanel, new java.awt.GridBagConstraints( 0, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 1, 0, 0, 0 ), 0, 0 ) );

		int x = this.getLocation().x;
		int y = this.getLocation().y;
		
		dndPanel.setBorder( javax.swing.BorderFactory.createEmptyBorder( 1, 1, 1, 1 ) );
		dndPanel.setSize(new Dimension(100,60));
		dndPanel.setBounds(x, y, 100, 60);
		dndPanel.setBackground( bgColor );
		elementPanel.setLayout( new java.awt.GridBagLayout() );
		elementPanel.setOpaque( false );
		elementPanel.setSize(new Dimension(100,60));
		iconLabel.setOpaque( false );
		iconLabel.setSize(new Dimension(100,60));
		elementLabel.setFont( new java.awt.Font("Calibri", java.awt.Font.BOLD, 16) );
		elementLabel.setBackground( selectedColor );
		elementLabel.setBorder( javax.swing.BorderFactory.createEmptyBorder( 0, 1, 0, 1 ) );
		elementLabel.setOpaque( false );
		elementLabel.setSize(new Dimension(100,60));
		elementPanel.add( iconLabel, new java.awt.GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 3, 0, 1 ), 0, 0 ) );
		elementPanel.add( elementLabel, new java.awt.GridBagConstraints( 1, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.CENTER, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 0, 1, 0, 3 ), 0, 0 ) );
		dndPanel.add( elementPanel, java.awt.BorderLayout.CENTER );
		// dndPanel.setSize(new Dimension((int) getPreferredSize().getWidth(),20));
		// this.setSize((int) getPreferredSize().getWidth(),20);
	}

	public java.awt.Component getTreeCellRendererComponent( javax.swing.JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus ) {
		if( value instanceof edu.cmu.cs.stage3.alice.core.Element ) {
			edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)value;

			boolean inScope = true;
			javax.swing.tree.TreeModel treeModel = tree.getModel();
			if( treeModel instanceof ScopedElementTreeModel ) {
				inScope = ((ScopedElementTreeModel)treeModel).isElementInScope( element );
			}

			javax.swing.ImageIcon icon = edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getIconForValue( element );
			if( inScope ) {
				dndPanel.setBackground( bgColor );
				iconLabel.setIcon( icon );
				// Dimension d = dndPanel.getSize();
				// dndPanel.setSize(new Dimension(d.width,iconLabel.getHeight()));
			} else {
				dndPanel.setBackground( disabledColor );
				iconLabel.setIcon( edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getDisabledIcon( icon ) );
			}

			elementLabel.setText( element.name.getStringValue() );
			if( selected ) {
				elementLabel.setOpaque( true );
				if( inScope ) {
					elementLabel.setForeground( selectedTextColor );
				} else {
					elementLabel.setForeground( disabledTextColor );
				}
			} else {
				elementLabel.setOpaque( false );
				if( inScope ) {
					elementLabel.setForeground( textColor );
				} else {
					elementLabel.setForeground( disabledTextColor );
				}
			}
		} else {
			edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool.showErrorDialog( "Error: not an Element: " + value, null );
		}
		this.doLayout();
		return this;
	}

	public void setBackgroundColor( java.awt.Color color ) {
		bgColor = color;
		dndPanel.setBackground( color );
	}
}