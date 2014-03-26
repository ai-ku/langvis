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

package edu.cmu.cs.stage3.alice.authoringtool.editors.soundeditor;

/**
 * @author Ben Buchwald, Dennis Cosgrove
 */

public class SoundEditor extends javax.swing.JPanel implements edu.cmu.cs.stage3.alice.authoringtool.Editor {
	class SoundMarkersPanel extends javax.swing.JPanel {
		private javax.swing.JLabel m_label;
		private javax.swing.JLabel m_pad;
		private javax.swing.JButton m_dropMarkerButton;
		private java.awt.GridBagLayout m_gridbagLayout;
		private java.util.HashMap m_soundMarkersGuiCache = new java.util.HashMap();

		public SoundMarkersPanel() {
			m_label = new javax.swing.JLabel( "Markers:" );
			m_dropMarkerButton = new javax.swing.JButton( "Drop Marker" );
			m_dropMarkerButton.addActionListener( new java.awt.event.ActionListener() {
				public void actionPerformed( java.awt.event.ActionEvent ev ) {
					SoundMarkersPanel.this.onDropMarker();
				}
			} );
			m_pad = new javax.swing.JLabel();
			m_gridbagLayout = new java.awt.GridBagLayout();
			setLayout( m_gridbagLayout );
			refreshGUI();
		}
		private void onDropMarker() {
			SoundEditor.this.onDropMarker();
			refreshGUI();
		}
		public void refreshGUI() {
			removeAll();
			if( SoundEditor.this.m_sound != null ) {
				java.awt.GridBagConstraints gbc = new java.awt.GridBagConstraints();
				gbc.fill = java.awt.GridBagConstraints.NONE;
				gbc.anchor = java.awt.GridBagConstraints.NORTHWEST;
				gbc.gridwidth = java.awt.GridBagConstraints.REMAINDER;
				gbc.weightx = 1.0;
				add( m_label, gbc );
				for( int i = 0; i < SoundEditor.this.m_sound.markers.size(); i++ ) {
					edu.cmu.cs.stage3.alice.core.media.SoundMarker marker = (edu.cmu.cs.stage3.alice.core.media.SoundMarker)SoundEditor.this.m_sound.markers.get( i );
					javax.swing.JComponent gui = (javax.swing.JComponent)m_soundMarkersGuiCache.get( marker );
					if( gui == null ) {
						gui = edu.cmu.cs.stage3.alice.authoringtool.util.GUIFactory.getGUI( marker );
						m_soundMarkersGuiCache.put( marker, gui );
					}
					add( gui, gbc );
				}
				add( m_dropMarkerButton, gbc );
				gbc.weighty = 1.0;
				add( m_pad, gbc );
			}
			revalidate();
			repaint();
		}
	}

	class SoundView extends javax.swing.JComponent {
		private double m_currentTime;
		private int[] m_samples;
		private java.awt.Image m_backgroundImage = null;
		private int m_backgroundImageWidth;
		private int m_backgroundImageHeight;

		public double getCurrentTime() {
			return m_currentTime;
		}
		public void setCurrentTime( double currentTime ) {
			int width = getWidth();
			int prevX = mapPixelX( m_currentTime, width );
			int currX = mapPixelX( currentTime, width );
			m_currentTime = currentTime;
			if( prevX != currX ) {
				repaint();
			}
		}

		public void setData( byte[] data ) {
			m_samples = new int[ data.length/2 ];
			int j = 0;
			for( int i=0; i<m_samples.length; i++ ) {
				int lowByte  = data[ j++ ];
				int highByte = data[ j++ ];
				m_samples[ i ] = lowByte | ( highByte<<8 );
				if( ( m_samples[ i ] & 0x8000 ) != 0 ) {
					m_samples[ i ] |= 0xFFFF0000;
				}
								  
			}
			repaint();
		}
		
		private void setPixelX( int x ) {
			double duration = SoundEditor.this.getDuration();
			if( Double.isNaN( duration ) ) {
				setCurrentTime( 0 );
			} else {
				setCurrentTime( (x*duration)/getWidth() );
			}
		}

		public void paint( java.awt.Graphics g ) {
			java.awt.Dimension size = getSize();
			if( m_samples != null ) {
				if( m_backgroundImage == null || size.width != m_backgroundImageWidth || size.height != m_backgroundImageHeight ) {
					m_backgroundImageWidth = size.width;
					m_backgroundImageHeight = size.height;
					m_backgroundImage = createImage( m_backgroundImageWidth, m_backgroundImageHeight );

					java.awt.Graphics backgroundGraphics = m_backgroundImage.getGraphics();
					backgroundGraphics.setColor( java.awt.Color.white );
					backgroundGraphics.fillRect( 0, 0, m_backgroundImageWidth, m_backgroundImageHeight );
					backgroundGraphics.setColor( java.awt.Color.blue );

					final int CENTER_Y = m_backgroundImageHeight/2;
					double factorI = m_samples.length/(double)m_backgroundImageWidth;
					double factorY = CENTER_Y/(double)(1<<15);
					int minI = 0;
					for( int x=0; x<size.width; x++ ) {
						int maxI = (int)((x+1)*factorI);
						if( minI < maxI ) {
							int minSample = Integer.MAX_VALUE;
							int maxSample = Integer.MIN_VALUE;
							for( int i=minI; i<maxI; i++ ) {
								int sample = m_samples[ i ];
								minSample = Math.min( minSample, sample );
								maxSample = Math.max( maxSample, sample );
							}
							backgroundGraphics.drawLine( x, CENTER_Y+(int)(minSample*factorY), x, CENTER_Y+(int)(maxSample*factorY) );
						}
						minI = maxI;
					}
					backgroundGraphics.dispose();
				}
				g.drawImage( m_backgroundImage, 0, 0, this );
			}
			edu.cmu.cs.stage3.alice.core.media.SoundMarker[] soundMarkers = SoundEditor.this.getSoundMarkers();
			if( soundMarkers != null ) {
				g.setColor( java.awt.Color.black );
				for( int i=0; i<soundMarkers.length; i++ ) {
					drawLineAtTime( g, soundMarkers[ i ].time.doubleValue(), size );
				}
			}
			g.setColor( java.awt.Color.red );
			drawLineAtTime( g, m_currentTime, size );

		}
		private int mapPixelX( double t, int width ) {
			if( Double.isNaN( m_currentTime ) ) {
				return -1;
			} else {
				double duration = SoundEditor.this.getDuration();
				if( Double.isNaN( duration ) ) {
					return -1;
				} else {
					return (int)( ( t / duration ) * width ); 
				}
			}
		}
		private void drawLineAtTime( java.awt.Graphics g, double t, java.awt.Dimension size ) {
			int x = mapPixelX( t, size.width );
			g.drawLine( x, 0, x, size.height );
		}
	}

	public static String viewerName = "Sound Editor";
    
    private edu.cmu.cs.stage3.alice.core.Sound m_sound;
	private edu.cmu.cs.stage3.media.Player m_player;
    
	private SoundView m_soundView;

	private javax.swing.JButton m_playButton;
	private javax.swing.JButton m_pauseButton;
	private javax.swing.JButton m_stopButton;
	private javax.swing.JButton m_prevMarkerButton;

	private SoundMarkersPanel m_markersPanel;

	private edu.cmu.cs.stage3.media.event.PlayerListener m_playerListener = new edu.cmu.cs.stage3.media.event.PlayerListener() {
		public void stateChanged( edu.cmu.cs.stage3.media.event.PlayerEvent e ) {
		}
		public void endReached( edu.cmu.cs.stage3.media.event.PlayerEvent e ) {
			SoundEditor.this.onStop();
		}
	};

	private edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener m_markersListener = new edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyListener() {
		public void objectArrayPropertyChanging( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
		}
		public void objectArrayPropertyChanged( edu.cmu.cs.stage3.alice.core.event.ObjectArrayPropertyEvent e ) {
			SoundEditor.this.m_soundView.repaint();
			SoundEditor.this.m_markersPanel.refreshGUI();
		}
	};

	private javax.swing.Timer m_timer = new javax.swing.Timer( 100, new java.awt.event.ActionListener(){
        public void actionPerformed( java.awt.event.ActionEvent e ) {
        	SoundEditor.this.onTimerUpdate();
        }
    } );

    public SoundEditor() {
		m_soundView = new SoundView();
		m_soundView.addMouseListener( new java.awt.event.MouseAdapter() {
			public void mousePressed( java.awt.event.MouseEvent e ) {
				m_soundView.setPixelX( e.getX() );
			}
			public void mouseReleased( java.awt.event.MouseEvent e ) {
				m_soundView.setPixelX( e.getX() );
				if( m_player != null ) {
					m_player.setCurrentTime( m_soundView.getCurrentTime() );
				}
			}
		} );
		m_soundView.addMouseMotionListener( new java.awt.event.MouseMotionAdapter() {
			public void mouseDragged( java.awt.event.MouseEvent e ) {
				m_soundView.setPixelX( e.getX() );
			}
		} );

		m_playButton = new javax.swing.JButton( "Play" );
		m_playButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onPlay();
			}
		} );

		m_pauseButton = new javax.swing.JButton( "Pause" );
		m_pauseButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onPause();
			}
		} );

		m_stopButton = new javax.swing.JButton( "Stop" );
		m_stopButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onStop();
			}
		} );

		m_prevMarkerButton = new javax.swing.JButton( "<< Skip to Previous Marker" );
		m_prevMarkerButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed( java.awt.event.ActionEvent e ) {
				onPrevMarker();
			}
		} );

		javax.swing.JPanel buttonPanel = new javax.swing.JPanel();
		buttonPanel.setLayout( new java.awt.FlowLayout() );
		buttonPanel.add( m_playButton );
		buttonPanel.add( m_pauseButton );
		buttonPanel.add( m_stopButton );
		buttonPanel.add( m_prevMarkerButton );

		javax.swing.JPanel soundPanel = new javax.swing.JPanel();
		soundPanel.setLayout( new java.awt.BorderLayout() );
		soundPanel.add( m_soundView, java.awt.BorderLayout.CENTER );		
		soundPanel.add( buttonPanel, java.awt.BorderLayout.SOUTH );		

		m_markersPanel = new SoundMarkersPanel();

		javax.swing.JSplitPane splitPane = new javax.swing.JSplitPane( javax.swing.JSplitPane.HORIZONTAL_SPLIT, soundPanel, m_markersPanel );
		splitPane.setResizeWeight( 0.75 );

		setLayout( new java.awt.GridLayout( 1, 1 ) );
		add( splitPane );
    }


    public javax.swing.JComponent getJComponent() {
        return this;
    }

	public Object getObject() {
		return m_sound;
	}

   	public void setObject( edu.cmu.cs.stage3.alice.core.Sound sound ) {
		m_timer.stop();
		m_playButton.setEnabled( true );
		m_pauseButton.setEnabled( false );
		m_stopButton.setEnabled( false );

		//m_timeSlider.setValue( 0 );
		m_soundView.setCurrentTime( 0 );

		if( m_sound != sound ) {
			if( m_player != null ) {
				m_player.stop();
				m_player.removePlayerListener( m_playerListener );
				m_player.setIsAvailable( true );
				m_player = null;
			}
			if( m_sound != null ) {
				m_sound.markers.removeObjectArrayPropertyListener( m_markersListener );		
			}

			m_sound = sound;

			if( m_sound != null ) {
				edu.cmu.cs.stage3.media.DataSource dataSourceValue = sound.dataSource.getDataSourceValue();
				dataSourceValue.waitForRealizedPlayerCount( 1, 0 );
				m_player = dataSourceValue.acquirePlayer();
				m_player.addPlayerListener( m_playerListener );
				if( dataSourceValue instanceof edu.cmu.cs.stage3.media.jmfmedia.DataSource ) {
					javax.media.protocol.DataSource jmfDataSource = ((edu.cmu.cs.stage3.media.jmfmedia.DataSource)dataSourceValue).getJMFDataSource();
					try {
						javax.media.Processor jmfProcessor = javax.media.Manager.createProcessor( jmfDataSource );
					
						final Object configureLock = new Object();
						synchronized( configureLock ) {
							javax.media.ControllerListener configureControllerListener = new javax.media.ControllerListener() {
								public void controllerUpdate( javax.media.ControllerEvent e ) {
									if( e instanceof javax.media.TransitionEvent ) {
										javax.media.TransitionEvent te = (javax.media.TransitionEvent)e;
										if( te.getCurrentState() == javax.media.Processor.Configured ) {
											synchronized( configureLock ) {
												configureLock.notify();
											}
										}
									}
								}
							};
							jmfProcessor.addControllerListener( configureControllerListener );

							jmfProcessor.configure();
							try {
								configureLock.wait();
							} catch( InterruptedException ie ) {
								ie.printStackTrace();
							}
							jmfProcessor.removeControllerListener( configureControllerListener );
						}
						jmfProcessor.setContentDescriptor( null );

						final edu.cmu.cs.stage3.alice.authoringtool.util.CaptureRenderer jmfRenderer = new edu.cmu.cs.stage3.alice.authoringtool.util.CaptureRenderer();
						jmfProcessor.getTrackControls()[ 0 ].setRenderer( jmfRenderer );

						final Object endLock = new Object();
						synchronized( endLock ) {
							javax.media.ControllerListener endControllerListener = new javax.media.ControllerListener() {
								public void controllerUpdate( javax.media.ControllerEvent e ) {
									if( e instanceof javax.media.EndOfMediaEvent ) {
										synchronized( endLock ) {
											endLock.notify();
										}
									}
								}
							};
							jmfProcessor.addControllerListener( endControllerListener );
							jmfProcessor.start();
							try {
								endLock.wait();
							} catch( InterruptedException ie ) {
								ie.printStackTrace();
							}
							jmfProcessor.removeControllerListener( endControllerListener );
						}
						jmfProcessor.stop();
						jmfRenderer.stop();
						jmfRenderer.close();
						synchronized( jmfRenderer ) {
							byte[] data = new byte[ jmfRenderer.getDataLength() ];
							jmfRenderer.getData( data, 0, data.length );
							m_soundView.setData( data );
						}
					} catch( Throwable t ) {
						t.printStackTrace();
					}
				}
				m_sound.markers.addObjectArrayPropertyListener( m_markersListener );		
			}
		}

		m_markersPanel.refreshGUI();
    }

    public void worldStarting( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {
    	onStop();
    }

    public void setAuthoringTool( edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool at ) {}

    public void worldStopped( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void stateChanging( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoading( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStopping( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldPausing( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaving( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void stateChanged( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldUnLoaded( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldStarted( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
    public void worldPaused( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}
	public void worldSaved( edu.cmu.cs.stage3.alice.authoringtool.event.AuthoringToolStateChangedEvent ev ) {}


	private double getDuration() {
		if( m_sound != null ) {
			return m_sound.dataSource.getDataSourceValue().getDuration( edu.cmu.cs.stage3.media.DataSource.USE_HINT_IF_NECESSARY );
		} else {
			return Double.NaN;
		}
	}
	private edu.cmu.cs.stage3.alice.core.media.SoundMarker[] getSoundMarkers() {
		if( m_sound != null ) {
			return (edu.cmu.cs.stage3.alice.core.media.SoundMarker[])m_sound.markers.getElementArrayValue();
		} else {
			return null;	
		}
	}

	private void onTimerUpdate() {
		if( m_player != null ) {
			m_soundView.setCurrentTime( m_player.getCurrentTime() );
		}
	}
		
	private void onDropMarker() {
		if( m_sound != null ) {
			double t = m_soundView.getCurrentTime();
			edu.cmu.cs.stage3.alice.core.media.SoundMarker[] markers = getSoundMarkers();
			if( markers != null ) {
				for( int i=0; i<markers.length; i++ ) {
					if( markers[ i ].time.doubleValue()==t ) {
						int result = edu.cmu.cs.stage3.swing.DialogManager.showConfirmDialog( "There is already a marker at this location.  Would you like another?", "Identical Marker Detected", javax.swing.JOptionPane.YES_NO_CANCEL_OPTION );
						if( result == javax.swing.JOptionPane.YES_OPTION ) {
							break;
						} else {
							return;
						}
					}
				}
			}
			edu.cmu.cs.stage3.alice.core.media.SoundMarker marker = new edu.cmu.cs.stage3.alice.core.media.SoundMarker();
			marker.time.set( new Double( t ) );
			marker.name.set( generateMarkerName( t ) );
			marker.setParent( m_sound );
			m_sound.markers.add( marker ); 
		}
	}
	private String generateMarkerName( double t ) {
		int minutes = ((int)t)/60; 
		int seconds = ((int)t)%60; 
		int milliseconds = (int)((t-(int)t)*1000); 
		StringBuffer sb = new StringBuffer();
		sb.append( "marker at " );
		sb.append( minutes );
		sb.append( "min " );
		sb.append( seconds );
		sb.append( "sec " );
		sb.append( milliseconds );
		sb.append( "msec" );
		return edu.cmu.cs.stage3.alice.authoringtool.AuthoringToolResources.getNameForNewChild( sb.toString(), m_sound );  
	}

	private void onPlay() {
		if( m_player != null ) {
			m_player.start();
		}
		
		m_timer.start();
		m_playButton.setEnabled( false );
		m_pauseButton.setEnabled( true );
		m_stopButton.setEnabled( true );
	}

	private void onPause() {
		if( m_player != null ) {
			m_player.stop();
		}
		
		m_timer.stop();

		m_playButton.setEnabled( true );
		m_pauseButton.setEnabled( false );
		m_stopButton.setEnabled( false );
	}

	private void onStop() {
		if( m_player != null ) {
			m_player.stop();
			m_player.setCurrentTime( 0 );
			m_soundView.setCurrentTime( 0 );
		}
		m_timer.stop();

		m_playButton.setEnabled( true );
		m_pauseButton.setEnabled( false );
		m_stopButton.setEnabled( false );
	}

	void onPrevMarker() {
//		  SoundMarker mark = sound.previousMarker(m_lock.getPlayer().getMediaTime().getSeconds());
//		  double newTime;
//		  if (mark==null)
//			  newTime = 0;
//		  else
//			  newTime = mark.getTime();
//
//		  m_lock.getPlayer().setMediaTime(new Time(newTime));
//		  timeSlider.setValue((int)(newTime*1000));
//		  Spectrum.curTimeInMillis = timeSlider.getValue();
//		  Spectrum.repaint();
	}

//	void onPrevMarker() {
////		  SoundMarker mark = sound.previousMarker(m_lock.getPlayer().getMediaTime().getSeconds());
////		  double newTime;
////		  if (mark==null)
////			  newTime = 0;
////		  else
////			  newTime = mark.getTime();
////
////		  m_lock.getPlayer().setMediaTime(new Time(newTime));
////		  timeSlider.setValue((int)(newTime*1000));
////		  Spectrum.curTimeInMillis = timeSlider.getValue();
////		  Spectrum.repaint();
//	}
//	public SoundMarker previousMarker(double time) {
//		SoundMarker[] oldarry=getMarkers();
//		if (oldarry.length>0) {
//			if (time<=oldarry[0].getTime())
//				return null;
//		} else
//			return null;
//		for (int i=1; i<oldarry.length; i++) {
//			if (oldarry[i].getTime()>=time)
//				return oldarry[i-1];
//		}
//		return oldarry[oldarry.length-1];
//	}

}