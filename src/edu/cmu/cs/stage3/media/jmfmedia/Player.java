package edu.cmu.cs.stage3.media.jmfmedia;

public class Player extends edu.cmu.cs.stage3.media.AbstractPlayer {
	private javax.media.Player m_jmfPlayer;
	private javax.media.Time m_pendingMediaTime;
	private Float m_pendingVolumeLevel;
	private Float m_pendingRate;
	public Player( DataSource dataSource ) {
		super( dataSource );
		try {
			m_jmfPlayer = javax.media.Manager.createPlayer( dataSource.getJMFDataSource() );
			m_jmfPlayer.addControllerListener( new javax.media.ControllerListener() {
				public void controllerUpdate( javax.media.ControllerEvent e ) {
					if( e instanceof javax.media.TransitionEvent ) {
						javax.media.TransitionEvent te = (javax.media.TransitionEvent)e;
						switch( te.getCurrentState() ) {
						case javax.media.Controller.Realized:
							if( m_pendingMediaTime != null ) {
								m_jmfPlayer.setMediaTime( m_pendingMediaTime );
								m_pendingMediaTime = null;
							}
							if( m_pendingVolumeLevel != null ) {
								updateVolumeLevel( m_pendingVolumeLevel.floatValue() );
								m_pendingVolumeLevel = null;
							}
							if( m_pendingRate != null ) {
								updateRate( m_pendingRate.floatValue() );
								m_pendingRate = null;
							}
							break;
						}
						fireStateChanged();
					}
					if( e instanceof javax.media.EndOfMediaEvent ) {
						fireEndReached();
					}
					if( e instanceof javax.media.DurationUpdateEvent ) {
						fireDurationUpdated();
					}
				}
			} );
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}

//	public double waitForDuration( final long timeout ) {
//		double duration = getDuration();
//		if( timeout != 0 ) {
//			if( Double.isNaN( duration ) ) {
//				final Object lock = new Object();
//				synchronized( lock ) {
//					javax.media.ControllerListener controllerListener = new javax.media.ControllerListener() {
//						public void controllerUpdate( javax.media.ControllerEvent e ) {
//							if( e instanceof javax.media.DurationUpdateEvent ) {
//								synchronized( lock ) {
//									lock.notify();
//								}
//							}
//						}
//					};
//
//					m_jmfPlayer.addControllerListener( controllerListener );
//					if( timeout != Long.MAX_VALUE ) {
//						new Thread() {
//							public void run() {
//								try {
//									sleep( timeout );
//								} catch( InterruptedException ie ) {
//									ie.printStackTrace();
//								} finally {
//									synchronized( lock ) {
//										lock.notify();
//									}
//								}
//							}
//						}.start();
//					}
//					try {
//						lock.wait();
//					} catch( InterruptedException ie ) {
//						ie.printStackTrace();
//					}
//					m_jmfPlayer.removeControllerListener( controllerListener );
//				}
//			}
//			duration = getDuration();
//		}
//		return duration;
//	}

	public int getState() {
		return m_jmfPlayer.getState();
//		int jmfState = m_jmfPlayer.getState();
//		switch( jmfState ) {
//		case javax.media.Controller.Unrealized:
//			return STATE_UNREALIZED;
//		case javax.media.Controller.Prefetching:
//			return STATE_PREFETCHING;
//		case javax.media.Controller.Prefetched:
//			return STATE_PREFETCHED;
//		case javax.media.Controller.Realizing:
//			return STATE_REALIZING;
//		case javax.media.Controller.Realized:
//			return STATE_REALIZED;
//		case javax.media.Controller.Started:
//			return STATE_STARTED;
//		default:
//			//todo: throw Error?
//			return STATE_UNREALIZED;
//		}
	}
	
	public double getDuration() {
		javax.media.Time t = m_jmfPlayer.getDuration();
		if( t != null ) {
			long nsec = t.getNanoseconds();
			if( nsec == Long.MAX_VALUE-1 ) {
				return Double.NaN;
			} else if( nsec == Long.MAX_VALUE-1 ) {
				return Double.POSITIVE_INFINITY;
			} else {
				return nsec*1.0E-9;
			}
		} else {
			return Double.NaN;
		}
	}
	public double getCurrentTime() {
		javax.media.Time t;
		if( m_pendingMediaTime != null ) {
			t = m_pendingMediaTime;
		} else {
			t = m_jmfPlayer.getMediaTime();
		}
		if( t != null ) {
			long nsec = t.getNanoseconds();
			if( nsec == Long.MAX_VALUE-1 ) {
				return Double.NaN;
			} else if( nsec == Long.MAX_VALUE-1 ) {
				return Double.POSITIVE_INFINITY;
			} else {
				return nsec*1.0E-9;
			}
		} else {
			return Double.NaN;
		}
	}
	private boolean isAtLeastRealized() {
		int state = getState();
		return state != STATE_UNREALIZED && state != STATE_REALIZING;
	}
	public void setCurrentTime( double currentTime ) {
		javax.media.Time t = new javax.media.Time( currentTime );
		if( isAtLeastRealized() ) {
			m_jmfPlayer.setMediaTime( t );
		} else {
			m_pendingMediaTime = t;
		}
	}

	private void updateVolumeLevel( float volumeLevel ) {
		javax.media.GainControl gainConrol = m_jmfPlayer.getGainControl();
		if( gainConrol != null ) {
			gainConrol.setLevel( volumeLevel );
		} else {
			//todo: throw exception?
		}
	}
	public void setVolumeLevel( float volumeLevel ) {
		if( isAtLeastRealized() ) {
			updateVolumeLevel( volumeLevel );
		} else {
			m_pendingVolumeLevel = new Float( volumeLevel );
		}
	}

	private void updateRate( float rate ) {
		float actualRate = m_jmfPlayer.setRate( rate );
		if( actualRate != rate ) {
			//todo: throw exception?
		}
	}
	
	public void setRate( float rate ) {
		if( isAtLeastRealized() ) {
			updateRate( rate );
		} else {
			m_pendingRate = new Float( rate );
		}
	}

	public void realize() {
		try {
			m_jmfPlayer.realize();
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}
	public void prefetch() {
		try {
			m_jmfPlayer.prefetch();
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}
	public void start() {
		try {
			m_jmfPlayer.start();
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}
	public void stop() {
		try {
			if( m_jmfPlayer.getState()==javax.media.Player.Started ) {
				m_jmfPlayer.stop();
			}
		} catch( Throwable t ) {
			t.printStackTrace();
		}
	}
} 
