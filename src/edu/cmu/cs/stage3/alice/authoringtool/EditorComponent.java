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

package edu.cmu.cs.stage3.alice.authoringtool;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jason Pratt
 * @deprecated  now using TabbedEditorComponent
 */
public class EditorComponent extends javax.swing.JPanel {
	protected AuthoringTool authoringTool;
	protected javax.swing.JPanel editorPanel = new javax.swing.JPanel();
	protected EditLabel editLabel = new EditLabel();
	protected HighlightButton backButton = new HighlightButton();
	protected HighlightButton forwardButton = new HighlightButton();
	protected HighlightButton editSceneButton = new HighlightButton();
	protected javax.swing.AbstractAction backAction;
	protected javax.swing.AbstractAction forwardAction;
	protected javax.swing.AbstractAction editSceneAction;
	protected javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
	protected edu.cmu.cs.stage3.alice.authoringtool.Editor activeEditor = null;
	protected java.lang.reflect.Method activeEditorSetMethod = null;
	protected java.util.HashMap cachedEditors = new java.util.HashMap();
	protected EditStack editStack = new EditStack();
	private edu.cmu.cs.stage3.alice.authoringtool.util.Configuration authoringToolConfig = edu.cmu.cs.stage3.alice.authoringtool.util.Configuration.getLocalConfiguration(AuthoringTool.class.getPackage());

	protected final edu.cmu.cs.stage3.alice.core.event.ChildrenListener deletionListener = new edu.cmu.cs.stage3.alice.core.event.ChildrenListener() {
		public void childrenChanging( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev ) {}
		public void childrenChanged( edu.cmu.cs.stage3.alice.core.event.ChildrenEvent ev ) {
			if( (ev.getChangeType() == edu.cmu.cs.stage3.alice.core.event.ChildrenEvent.CHILD_REMOVED) && (ev.getChild() == EditorComponent.this.getElementBeingEdited()) ) {
				EditorComponent.this.editElement( null );
				ev.getParent().removeChildrenListener( this );
			}
		}
	};

	public EditorComponent( AuthoringTool authoringTool ) {
		this.authoringTool = authoringTool;
		actionInit();
		jbInit();
		guiInit();
	}

	private void actionInit() {
		backAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				EditorComponent.this.back();
			}
		};

		forwardAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				EditorComponent.this.forward();
			}
		};

		editSceneAction = new javax.swing.AbstractAction() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				EditorComponent.this.editElement( EditorComponent.this.authoringTool.getWorld() );
			}
		};

		backAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "back" );
		backAction.putValue( javax.swing.Action.NAME, "" );
		backAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Back to previous item" );
		backAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "backArrow" ) );
		backAction.setEnabled( false );

		forwardAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "forward" );
		forwardAction.putValue( javax.swing.Action.NAME, "" );
		forwardAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Forward to next item" );
		forwardAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "forwardArrow" ) );
		forwardAction.setEnabled( false );

		editSceneAction.putValue( javax.swing.Action.ACTION_COMMAND_KEY, "editScene" );
		editSceneAction.putValue( javax.swing.Action.NAME, "" );
		editSceneAction.putValue( javax.swing.Action.SHORT_DESCRIPTION, "Edit Scene" );
		editSceneAction.putValue( javax.swing.Action.SMALL_ICON, AuthoringToolResources.getIconForString( "scene" ) );
	}

	private void guiInit() {
		setBackground( edu.cmu.cs.stage3.alice.scenegraph.Color.valueOf( authoringTool.getConfig().getValue( "backgroundColor" ) ).createAWTColor() );
		editorPanel.setLayout( new java.awt.BorderLayout() );
		centerPanel.add( editorPanel, java.awt.BorderLayout.CENTER );
		buttonPanel.setLayout( new java.awt.GridLayout( 1, 0, 0, 0 ) );

		backButton.setAction( backAction );
		forwardButton.setAction( forwardAction );
		editSceneButton.setAction( editSceneAction );

		buttonPanel.setOpaque( false );
		buttonPanel.add( backButton );
		buttonPanel.add( forwardButton );
		buttonPanel.add( editSceneButton );
		topPanel.add( buttonPanel, new java.awt.GridBagConstraints( 0, 0, 1, 1, 0.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.VERTICAL, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
		topPanel.add( editLabel, new java.awt.GridBagConstraints( 1, 0, 1, 1, 1.0, 0.0, java.awt.GridBagConstraints.WEST, java.awt.GridBagConstraints.NONE, new java.awt.Insets( 2, 2, 2, 2 ), 0, 0 ) );
	}

	public edu.cmu.cs.stage3.alice.authoringtool.Editor loadEditor( Class editorClass ) {
		edu.cmu.cs.stage3.alice.authoringtool.Editor editor = null;

		if( editorClass != null ) {
			editor = (Editor)cachedEditors.get( editorClass );
			if( editor == null ) {
				try {
					editor = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getEditorFromClass( editorClass );
					if( editor == null ) {
						AuthoringTool.showErrorDialog( "Can't create editor of type " + editorClass.getName(), null );
					} else {
						cachedEditors.put( editorClass, editor );
						authoringTool.addAuthoringToolStateListener( editor );
						editor.setAuthoringTool( authoringTool );
					}
				} catch( Throwable t ) {
					AuthoringTool.showErrorDialog( "Error while creating editor of type " + editorClass.getName(), t );
				}
			}
		}

		return editor;
	}

	public void editElement( edu.cmu.cs.stage3.alice.core.Element element ) {
		editElement( element, true );
	}

	public void editElement( edu.cmu.cs.stage3.alice.core.Element element, Class editorClass ) {
		editElement( element, editorClass, true );
	}

	protected void editElement( edu.cmu.cs.stage3.alice.core.Element element, boolean performPush ) {
		if( element == null ) {
			editElement( null, null, performPush );
		} else {
			Class bestEditorClass = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getBestEditor( element.getClass() );
			if( bestEditorClass == null ) {
				AuthoringTool.showErrorDialog( "No editor found for " + element.getClass().getName(), null );
			}
			editElement( element, bestEditorClass, performPush );
		}
	}

	protected void editElement( edu.cmu.cs.stage3.alice.core.Element element, Class editorClass, boolean performPush ) {
		if( (getElementBeingEdited() != null) && (getElementBeingEdited().getParent() != null) ) {
			getElementBeingEdited().getParent().removeChildrenListener( deletionListener );
		}

		edu.cmu.cs.stage3.alice.authoringtool.Editor editor = loadEditor( editorClass );

		// if we have a new editor...
		if( activeEditor != editor ) {
			// clean up old editor if necessary
			if( (activeEditor != null) ) {
				try {
					activeEditorSetMethod.invoke( activeEditor, new Object[] { null } );
				} catch( java.lang.reflect.InvocationTargetException ite ) {
					AuthoringTool.showErrorDialog( "Error while cleaning editor.", ite );
				} catch( IllegalAccessException iae ) {
					AuthoringTool.showErrorDialog( "Error while cleaning editor.", iae );
				}
			}

			// setup new editor
			editorPanel.removeAll();
			activeEditor = editor;
			if( activeEditor != null ) {
				activeEditorSetMethod = edu.cmu.cs.stage3.alice.authoringtool.util.EditorUtilities.getSetMethodFromClass( editorClass );
				editorPanel.add( java.awt.BorderLayout.CENTER, activeEditor.getJComponent() );
			} else {
				activeEditorSetMethod = null;
			}
			editorPanel.revalidate();
			editorPanel.repaint();
		}

		// if the new editor isn't null, start editing the element
		if( (activeEditor != null) && (activeEditor.getObject() != element) ) {
			try {
				activeEditorSetMethod.invoke( activeEditor, new Object[] { element } );
				if( performPush && (element != null) ) {
					editStack.push( new EditItem( element, editorClass ) );
					updateActions();
				}
				if( element != null ) {
					editLabel.setText( AuthoringToolResources.getReprForValue( element ) );
					if( element.getParent() != null ) {
						element.getParent().addChildrenListener( deletionListener );
					}
				}
			} catch( java.lang.reflect.InvocationTargetException ite ) {
				AuthoringTool.showErrorDialog( "Error while setting up editor.", ite );
			} catch( IllegalAccessException iae ) {
				AuthoringTool.showErrorDialog( "Error while setting up editor.", iae );
			}
		}
	}

	public edu.cmu.cs.stage3.alice.core.Element getElementBeingEdited() {
		if( activeEditor != null ) {
			return (edu.cmu.cs.stage3.alice.core.Element)activeEditor.getObject();
		} else {
			return null;
		}
	}

	public edu.cmu.cs.stage3.alice.authoringtool.Editor getActiveEditor() {
		return activeEditor;
	}

	public void back() {
		editStack.undo();
		updateActions();
	}

	public void forward() {
		editStack.redo();
		updateActions();
	}

	public boolean canGoBack() {
		return editStack.getCurrentUndoableRedoableIndex() > 0;
	}

	public boolean canGoForward() {
		return editStack.getCurrentUndoableRedoableIndex() != (editStack.size() - 1);
	}

	protected void updateActions() {
		backAction.setEnabled( canGoBack() );
		forwardAction.setEnabled( canGoForward() );
		editSceneAction.setEnabled( getElementBeingEdited() != authoringTool.getWorld() );
	}

	class EditStack extends edu.cmu.cs.stage3.alice.authoringtool.util.DefaultUndoRedoStack {
		public edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable undo() {
			edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur = super.undo();
			edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable newItem = editStack.getCurrentUndoableRedoable();
			if( ur != null ) {
				editElement( ((EditItem)newItem).getElement(), ((EditItem)newItem).getEditorClass(), false );
			}
			return ur;
		}

		public edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable redo() {
			edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable ur = super.redo();
			if( ur != null ) {
				editElement( ((EditItem)ur).getElement(), ((EditItem)ur).getEditorClass(), false );
			}
			return ur;
		}
	}

	class EditItem implements edu.cmu.cs.stage3.alice.authoringtool.util.UndoableRedoable {
		protected edu.cmu.cs.stage3.alice.core.Element element;
		protected Class editorClass;

		public EditItem( edu.cmu.cs.stage3.alice.core.Element element, Class editorClass ) {
			this.element = element;
			this.editorClass = editorClass;
		}

		public void undo() {}
		public void redo() {}

		public Object getAffectedObject() {
			return element;
		}

		public Object getContext() {
			return EditorComponent.this;
		}

		public edu.cmu.cs.stage3.alice.core.Element getElement() {
			return element;
		}

		public Class getEditorClass() {
			return editorClass;
		}
	}

	public class EditLabel extends javax.swing.JLabel implements java.awt.dnd.DropTargetListener {
		public EditLabel() {
			int fontSize = Integer.parseInt(authoringToolConfig.getValue("fontSize"));
			setFont( new java.awt.Font( "SansSerif", java.awt.Font.BOLD, (int)( 18 * (fontSize / 12.0)) ) );
			setForeground( java.awt.Color.black );
			setText( "drop here" );
			setOpaque( false );
			this.setDropTarget( new java.awt.dnd.DropTarget( this, this ) );
		}

		// DropTargetListener interface
		protected void checkDrag( java.awt.dnd.DropTargetDragEvent dtde ) {
			//TODO: feedback
			//TODO: better detection
			if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor ) ) {
				dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );
			} else if( AuthoringToolResources.safeIsDataFlavorSupported(dtde, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ResponsePrototypeReferenceTransferable.responsePrototypeReferenceFlavor ) ) {
				dtde.acceptDrag( java.awt.dnd.DnDConstants.ACTION_MOVE );
			} else {
				dtde.rejectDrag();
			}
		}

		public void dragEnter( java.awt.dnd.DropTargetDragEvent dtde ) {
			checkDrag( dtde );
		}

		public void dragOver( java.awt.dnd.DropTargetDragEvent dtde ) {
			checkDrag( dtde );
		}

		public void dropActionChanged( java.awt.dnd.DropTargetDragEvent dtde ) {
			checkDrag( dtde );
		}

		public void dragExit( java.awt.dnd.DropTargetEvent dte ) {
			//TODO: feedback
		}

		public void drop( java.awt.dnd.DropTargetDropEvent dtde ) {
			java.awt.datatransfer.Transferable transferable = dtde.getTransferable();

			try {
				if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor ) ) {
					dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
					edu.cmu.cs.stage3.alice.core.Element element = (edu.cmu.cs.stage3.alice.core.Element)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.ElementReferenceTransferable.elementReferenceFlavor );
					EditorComponent.this.editElement( element );
					dtde.dropComplete( true );
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor ) ) {
					dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype callToUserDefinedResponsePrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedResponsePrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedResponsePrototypeReferenceTransferable.callToUserDefinedResponsePrototypeReferenceFlavor );
					EditorComponent.this.editElement( callToUserDefinedResponsePrototype.getActualResponse() );
					dtde.dropComplete( true );
				} else if( AuthoringToolResources.safeIsDataFlavorSupported(transferable, edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor ) ) {
					dtde.acceptDrop( java.awt.dnd.DnDConstants.ACTION_MOVE );
					edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype callToUserDefinedQuestionPrototype = (edu.cmu.cs.stage3.alice.authoringtool.util.CallToUserDefinedQuestionPrototype)transferable.getTransferData( edu.cmu.cs.stage3.alice.authoringtool.datatransfer.CallToUserDefinedQuestionPrototypeReferenceTransferable.callToUserDefinedQuestionPrototypeReferenceFlavor );
					EditorComponent.this.editElement( callToUserDefinedQuestionPrototype.getActualQuestion() );
					dtde.dropComplete( true );
				} else {
					dtde.rejectDrop();
					dtde.dropComplete( false );
				}
			} catch( java.awt.datatransfer.UnsupportedFlavorException e ) {
				AuthoringTool.showErrorDialog( "Drop didn't work: bad flavor", e );
				dtde.dropComplete( false );
			} catch( java.io.IOException e ) {
				AuthoringTool.showErrorDialog( "Drop didn't work: IOException", e );
				dtde.dropComplete( false );
			} catch( Throwable t ) {
				AuthoringTool.showErrorDialog( "Drop didn't work.", t );
				dtde.dropComplete( false );
			}
		}
	}

	class HighlightButton extends javax.swing.JButton {
		protected javax.swing.border.Border highlightBorder = new HighlightBorder( true );
		protected javax.swing.border.Border pressedBorder = new HighlightBorder( false );
		protected java.awt.Insets insets = new java.awt.Insets( 4, 4, 4, 4 );

		public HighlightButton() {
			this.setHorizontalTextPosition( javax.swing.SwingConstants.CENTER );
			this.setVerticalTextPosition( javax.swing.SwingConstants.BOTTOM );
			this.setOpaque( false );
			this.setFocusPainted( false );
			this.setContentAreaFilled( false );
			this.setRolloverEnabled( true );
			this.setBorder( null );

			this.getModel().addChangeListener(
				new javax.swing.event.ChangeListener() {
					public void stateChanged( javax.swing.event.ChangeEvent ev ) {
						HighlightButton.this.updateBorder();
					}
				}
			);
		}

		public java.awt.Insets getInsets() {
			return insets;
		}

		protected void updateBorder() {
			if( getModel().isEnabled() ) {
				if( getModel().isPressed() ) {
					if( getModel().isArmed() ) {
						setBorder( pressedBorder );
					} else {
						setBorder( highlightBorder );
					}
				} else if ( getModel().isRollover() ) {
					setBorder( highlightBorder );
				} else {
					setBorder( null );
				}
			} else {
				setBorder( null );
			}
		}

		class HighlightBorder extends javax.swing.border.AbstractBorder {
			protected java.awt.Color highlightColor = java.awt.Color.white;
			protected java.awt.Color shadowColor = java.awt.Color.gray;
			protected boolean raised;

			public HighlightBorder( boolean isRaised ) {
				this.raised = isRaised;
			}

			public void setRaised( boolean isRaised ) {
				this.raised = isRaised;
			}

			public boolean isRaised() {
				return raised;
			}

			public void paintBorder( java.awt.Component c, java.awt.Graphics g, int x, int y, int width, int height ) {
				java.awt.Color oldColor = g.getColor();

				if( raised ) {
					g.setColor( highlightColor );
					g.drawLine( x, y, x + width - 1, y );
					g.drawLine( x, y, x, y + height - 1 );

					g.setColor( shadowColor );
					g.drawLine( x + 1, y + height - 1, x + width - 1, y + height - 1 );
					g.drawLine( x + width - 1, y + 1, x + width - 1, y + height - 1 );
				} else {
					g.setColor( shadowColor );
					g.drawLine( x, y, x + width - 2, y );
					g.drawLine( x, y, x, y + height - 2 );

					g.setColor( highlightColor );
					g.drawLine( x, y + height - 1, x + width - 1, y + height - 1 );
					g.drawLine( x + width - 1, y, x + width - 1, y + height - 1 );
				}

				g.setColor( oldColor );
			}

			public java.awt.Insets getBorderInsets( java.awt.Component c ) {
				return new java.awt.Insets( 1, 1, 1, 1 );
			}

			public java.awt.Insets getBorderInsets( java.awt.Component c, java.awt.Insets insets ) {
				insets.left = insets.top = insets.right = insets.bottom = 1;
				return insets;
			}

			public boolean isBorderOpaque() {
				return true;
			}
		}
	}

	//////////////////
	// Autogenerated
	//////////////////

	BorderLayout borderLayout1 = new BorderLayout();
	JPanel topPanel = new JPanel();
	JPanel centerPanel = new JPanel();
	BorderLayout borderLayout2 = new BorderLayout();
	GridBagLayout gridBagLayout1 = new GridBagLayout();
	Border border1;
	Border border2;
	Border border3;

	private void jbInit() {
		border1 = new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(142, 142, 142));
		border2 = BorderFactory.createEmptyBorder(2,8,8,8);
		border3 = BorderFactory.createEmptyBorder(2,2,2,2);
		this.setLayout(borderLayout1);
		centerPanel.setLayout(borderLayout2);
		topPanel.setLayout(gridBagLayout1);
		centerPanel.setBorder(border1);
		this.setBackground(new Color(204, 204, 204));
		this.setBorder(border2);
		topPanel.setBorder(border3);
		topPanel.setOpaque(false);
		this.add(topPanel, BorderLayout.NORTH);
		this.add(centerPanel, BorderLayout.CENTER);
	}
}