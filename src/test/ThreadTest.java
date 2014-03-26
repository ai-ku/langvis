package test;

import edu.cmu.cs.stage3.alice.scenegraph.*;
import edu.cmu.cs.stage3.math.Vector3;
import edu.cmu.cs.stage3.math.MathUtilities;


public class ThreadTest extends java.awt.Frame implements java.awt.event.WindowListener, java.awt.event.KeyListener, java.awt.event.MouseListener, java.awt.event.MouseMotionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5028369750499628153L;

	private edu.cmu.cs.stage3.alice.scenegraph.renderer.OnscreenRenderTarget m_renderTarget;

	private static long s_t0 = System.currentTimeMillis();
	private static double getTime() {
	    return (System.currentTimeMillis()-s_t0 ) * 0.001;
	}
	class RepaintThread extends Thread {
	    private boolean m_isRunning;
	    public void run() {
	        m_isRunning = true;
	        while( m_isRunning ) {
                if( m_renderTarget != null ) {
	                java.awt.Component c = m_renderTarget.getAWTComponent();
	                if( c != null ) {
	                    c.repaint();
	                }
                }
	            try {
	                sleep( 5 );
	            } catch( InterruptedException ie ) {
	                //pass
	            }
	        }
	    }
	}
	class YThread extends Thread {
	    private Transformable m_trans;
	    private boolean m_isRunning;
	    public YThread( Transformable trans ) {
	        m_trans = trans;
	    }
	    public void run() {
	        m_isRunning = true;
	        while( m_isRunning ) {
	            javax.vecmath.Matrix4d m = m_trans.getLocalTransformation();;
	            m.m31 = Math.sin( getTime() );
	            m_trans.setLocalTransformation( m );
	            try {
	                sleep( 5 );
	            } catch( InterruptedException ie ) {
	                //pass
	            }
	        }
	    }
	}

	class YawThread extends Thread {
	    private Transformable m_trans;
	    private boolean m_isRunning;
	    public YawThread( Transformable trans ) {
	        m_trans = trans;
	    }
	    public void run() {
	        m_isRunning = true;
	        while( m_isRunning ) {
	            javax.vecmath.AxisAngle4d aa = new javax.vecmath.AxisAngle4d( 1, 0, 0, getTime() );
	            javax.vecmath.Matrix4d rot = new javax.vecmath.Matrix4d();
	            rot.set( aa );
	            javax.vecmath.Matrix4d m = m_trans.getLocalTransformation();
	            m.m00 = rot.m00; m.m01 = rot.m01; m.m02 = rot.m02;
	            m.m10 = rot.m10; m.m11 = rot.m11; m.m12 = rot.m12;
	            m.m20 = rot.m20; m.m21 = rot.m21; m.m22 = rot.m22;
	            m_trans.setLocalTransformation( m );
	            try {
	                sleep( 5 );
	            } catch( InterruptedException ie ) {
	                //pass
	            }
	        }
	    }
	}
	
	public static javax.vecmath.Vector3d subtract( javax.vecmath.Tuple3d a, javax.vecmath.Tuple3d b ) {
		return new javax.vecmath.Vector3d( a.x-b.x, a.y-b.y, a.z-b.z );
	}
	private static javax.vecmath.Vector3d getDirection( Vertex3d vertexA, Vertex3d vertexB, Vertex3d vertexC ) {
		return Vector3.crossProduct( subtract( vertexC.position, vertexB.position ), subtract( vertexA.position, vertexB.position ) );
	}
	
	private static void calculateNormals( IndexedTriangleArray ita ) {
        int total = ita.getTriangleCount();
		if( total>0 ) {
		    Vertex3d[] vertices = ita.getVertices();
		    int[] indices = ita.getIndices();
	        int i=0;
			for( int lcv=0; lcv<total; lcv++ ) {
			    int a = indices[i++];
			    int b = indices[i++];
			    int c = indices[i++];
			    javax.vecmath.Vector3d normal = MathUtilities.normalizeV( getDirection( vertices[a], vertices[b], vertices[c] ) );
			    vertices[a].normal.set( normal );
			    vertices[b].normal.set( normal );
			    vertices[c].normal.set( normal );
			}
	    	ita.setVertices( vertices );
		}
	}
	
	public ThreadTest() {
        setTitle( getClass().getName() );
		addWindowListener( this );
        setLayout( new java.awt.BorderLayout() );
        setSize( new java.awt.Dimension( 320, 240 ) );
        show();

		Camera camera = initSceneGraph();
		edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory rendererTargetFactory = new edu.cmu.cs.stage3.alice.scenegraph.renderer.DefaultRenderTargetFactory(
//		        edu.cmu.cs.stage3.alice.scenegraph.renderer.directx7renderer.Renderer.class 
		);
		
		m_renderTarget = rendererTargetFactory.createOnscreenRenderTarget();
		System.err.println( m_renderTarget );
		
	    java.awt.Component awtComponent = m_renderTarget.getAWTComponent();
	    add( awtComponent );
		awtComponent.addMouseListener( this );
		awtComponent.addMouseMotionListener( this );
		awtComponent.addKeyListener( this );
	    invalidate();
	    doLayout();
	    m_renderTarget.addCamera( camera );
	    
	    new RepaintThread().start();
	}

    public Camera initSceneGraph() {
        Scene scene = new Scene();

        Background background = new Background();
        background.setColor( new Color( 0, 0, 1 ) );
        scene.setBackground( background );

        AmbientLight ambientLight = new AmbientLight();
        ambientLight.setParent( scene );
        ambientLight.setColor( new Color( 0.2, 0.2, 0.2 ) );

        Transformable sunVehicle = new Transformable();
        sunVehicle.setParent( scene );
        DirectionalLight sunLight = new DirectionalLight();
        sunLight.setColor( Color.WHITE );
        sunLight.setParent( sunVehicle );
        sunVehicle.setOrientation( new javax.vecmath.Vector3d( -1, -1, -1 ), edu.cmu.cs.stage3.math.Vector3.Y_AXIS, null );
   
		IndexedTriangleArray teddyITA = null;
		try {
		    teddyITA = edu.cmu.cs.stage3.alice.scenegraph.io.IndexedTriangleArrayIO.decode( "obj", new java.io.FileInputStream( "c:/samples/media/teddy.obj" ) );
//			if( teddyITA!=null ) {
//				//teddyITA.unshareVertices( null );
				calculateNormals( teddyITA );
//				//teddyITA.smoothNormals( Math.PI/3.0, null );
//				//teddyITA.shareVertices( null );
//			}
		} catch( java.io.FileNotFoundException fnfe ) {
			fnfe.printStackTrace();
		} catch( java.io.IOException ioe ) {
			ioe.printStackTrace();
		}

        Appearance yellowAppearance = new Appearance();
        yellowAppearance.setAmbientColor( Color.YELLOW );
        yellowAppearance.setDiffuseColor( Color.YELLOW );

        Appearance greenAppearance = new Appearance();
        greenAppearance.setAmbientColor( Color.GREEN );
        greenAppearance.setDiffuseColor( Color.GREEN );

        final int SIZE = 8;
        for( int i=0; i<SIZE; i++ ) {
            for( int j=0; j<SIZE; j++ ) {
	            Transformable teddy = new Transformable();
	            teddy.setParent( scene );
	            Visual visual = new Visual();
	            visual.setFrontFacingAppearance( yellowAppearance );
	            visual.setParent( teddy );
	            visual.setGeometry( teddyITA );
	    		//teddy obj's seem to come in face down.
	    		teddy.translate( new javax.vecmath.Vector3d( j*2-SIZE, 0, i*2-SIZE ), null );
	    		teddy.rotate( edu.cmu.cs.stage3.math.Vector3.X_AXIS, -Math.PI/2.0, null );
	    	    new YThread( teddy ).start();
	    	    new YawThread( teddy ).start();
	        }
        }
        
//        DoTogether(
//                RunMethod( bunny, "move", new Object[] { up, 1 } )
//        )
        
		Transformable cameraVehicle = new Transformable();
        cameraVehicle.setParent( scene );
		cameraVehicle.setPosition( new edu.cmu.cs.stage3.math.Vector3( 10,10,20 ), scene );
		cameraVehicle.pointAt( scene, edu.cmu.cs.stage3.math.Vector3.ZERO, edu.cmu.cs.stage3.math.Vector3.Y_AXIS, scene );
        SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
        camera.setParent( cameraVehicle );

        return camera;
    }

    public void keyPressed( java.awt.event.KeyEvent keyEvent ) {
    }
    public void keyReleased( java.awt.event.KeyEvent keyEvent ) {
    }
    public void keyTyped( java.awt.event.KeyEvent keyEvent ) {
    }

	private Visual pressVisual = null;
	private Color pressColor = null;
    public void mousePressed( java.awt.event.MouseEvent mouseEvent ) {
//		edu.cmu.cs.stage3.alice.scenegraph.renderer.PickInfo pickInfo = m_renderTarget.pick( mouseEvent.getX(), mouseEvent.getY(), edu.cmu.cs.stage3.alice.scenegraph.renderer.PickStyle.USE_GEOMETRY );
//		pressVisual = null;
//		pressColor = null;
//		if( pickInfo!=null ) {
//			Visual[] visuals = pickInfo.getVisuals();
//			if( visuals!=null && visuals.length>0 ) {
//				if( mouseEvent.isControlDown() ) {
//					pressVisual = visuals[0];
//					pressColor = pressVisual.getDiffuseColor();
//					pressVisual.setDiffuseColor( Color.RED );
//					pressVisual.setAmbientColor( Color.RED );
//					//pressVisual.setShadingStyle( ShadingStyle.NONE );
//				} else {
//					m_renderTarget.setAutomaticOnscreenUpdateIsEnabled( false );
//					Color originalColor = visuals[0].getDiffuseColor();
//					visuals[0].setDiffuseColor( Color.BLUE );
//					m_offscreenRenderTarget.render();
//					m_imageView.setImage( m_offscreenRenderTarget.getOffscreenImage() );
//					visuals[0].setDiffuseColor( originalColor );
//					m_renderTarget.setAutomaticOnscreenUpdateIsEnabled( true );
//				}
//			}
//		}		   
    }
    public void mouseReleased( java.awt.event.MouseEvent mouseEvent ) {
//		if( pressVisual!=null && pressColor!=null ) {
//			pressVisual.setDiffuseColor( pressColor );
//			pressVisual.setAmbientColor( pressColor );
//			//pressVisual.setShadingStyle( ShadingStyle.SMOOTH );
//		}
    }

	
	public void mouseClicked( java.awt.event.MouseEvent mouseEvent ) {
    }
    public void mouseEntered( java.awt.event.MouseEvent mouseEvent ) {
    }
    public void mouseExited( java.awt.event.MouseEvent mouseEvent ) {
    }

    public void mouseDragged( java.awt.event.MouseEvent mouseEvent ) {
    }
    public void mouseMoved( java.awt.event.MouseEvent mouseEvent ) {
    }


	public void windowActivated( java.awt.event.WindowEvent windowEvent ) {
	}
	public void windowClosed( java.awt.event.WindowEvent windowEvent ) {
	}
	public void windowClosing( java.awt.event.WindowEvent windowEvent ) {
		System.exit( 0 );
	}
	public void windowDeactivated( java.awt.event.WindowEvent windowEvent ) {
	}
	public void windowDeiconified( java.awt.event.WindowEvent windowEvent ) {
	}
	public void windowIconified( java.awt.event.WindowEvent windowEvent ) {
	}
	public void windowOpened( java.awt.event.WindowEvent windowEvent ) {
	}

	private IndexedTriangleArray loadIndexedTriangleArray( String dirname ) {
        IndexedTriangleArray ita = new IndexedTriangleArray();
        try {
            java.io.InputStream is;
            is = new java.io.FileInputStream( new java.io.File( dirname+"/vertices.bin" ) );
            ita.loadVertices( is );
            is.close();
            is = new java.io.FileInputStream( new java.io.File( dirname+"/indices.bin" ) );
            ita.loadIndices( is );
            is.close();
        } catch( java.io.FileNotFoundException fnfe ) {
            fnfe.printStackTrace();
        } catch( java.io.IOException ioe ) {
            ioe.printStackTrace();
        }
        return ita;
    }
    private TextureMap loadTextureMap( String pathname ) {
		java.awt.Toolkit toolkit = getToolkit();
		java.awt.Image image = toolkit.getImage( pathname );
		while( image.getWidth( this )==-1 && image.getWidth( this )==-1 ) {
			Thread.yield();
		}
        TextureMap textureMap = new TextureMap();
        textureMap.setImage( image );
        return textureMap;
    }

    public static void main( String[] args ) {
        new ThreadTest();
	}
}
