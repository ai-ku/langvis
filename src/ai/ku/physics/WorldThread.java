package ai.ku.physics;

import javax.vecmath.Vector3f;

import ai.ku.util.Logger;
import ai.ku.util.Sleeper;

import com.bulletphysics.collision.broadphase.AxisSweep3;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;

public class WorldThread extends Thread{

	private static DefaultCollisionConfiguration collisionConfiguration;
	private static CollisionDispatcher dispatcher;
	private static DiscreteDynamicsWorld world;
	private static Clock clock;

	private static boolean simOn = true;

	private long timeNow;
	private long timeLast;
	private long targetMillis = 1000/30;
	private long stepTime;
	private static float PPS = 0f;

	public WorldThread(){
		world = getWorld();
	}

	public static DiscreteDynamicsWorld getWorld() {
		
		if( world == null )
		{
			collisionConfiguration = new DefaultCollisionConfiguration();
			dispatcher = new CollisionDispatcher(collisionConfiguration);
			Vector3f worldAabbMin = new Vector3f(-10000,-10000,-10000);
			Vector3f worldAabbMax = new Vector3f(10000,10000,10000);
			AxisSweep3 overlappingPairCache = new AxisSweep3(worldAabbMin, worldAabbMax);
			SequentialImpulseConstraintSolver solver = new SequentialImpulseConstraintSolver();

			world = new DiscreteDynamicsWorld(dispatcher, overlappingPairCache, solver, collisionConfiguration);
			world.setGravity(new Vector3f(0,-10,0));
			world.getDispatchInfo().allowedCcdPenetration = 0f;
			
			clock = new Clock();
		}
		
		return world;
	}
	
	public void run() {

		while(true){

			while(simOn){

				timeLast = System.currentTimeMillis();

				// for( int i = 0; i < 10; i++ ) world.step();

				float ms = clock.getTimeMicroseconds();
				clock.reset();
				world.stepSimulation(ms / 1000000f);
				Logger.log("stepping");
				
				timeNow = System.currentTimeMillis();

				stepTime = timeNow-timeLast;

				if( stepTime < targetMillis )
				{ Sleeper.sleep(targetMillis-stepTime); }
				
				setPPS();
			}
		}
	}

	public static float getPPS(){	
		return PPS;
	}

	private void setPPS(){	
		PPS = 1000f/stepTime;
	}

	public void stopSim(){
		simOn = false;
	}

	public void startSim(){
		simOn = true;
	}
	
	public void reset(){
		world = null;
	}
	public static void removeAllBodies(){
		/*
		while(world.getBodies().size()>1){
			world.remove(world.getBodies().get(1));
		}
		*/
	}

}
