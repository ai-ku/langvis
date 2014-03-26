package ai.ku.astar;

import java.awt.Point;
import java.util.ArrayList;

public class AStar {
		
	private ArrayList<ShortestPathStep>openSteps = null;
	private ArrayList<ShortestPathStep>closedSteps = null;
	private ArrayList<ShortestPathStep>shortestPath = null;
	
	private int[][] map = null;
	
	public AStar( int[][] map ) {
		this.map = map;
	}
	
	private boolean isValidAt(Point tileCoord) {
		if( tileCoord.x < 0 || tileCoord.y < 0 || tileCoord.x >= map.length || tileCoord.y >= map[0].length )
			return false;
		else
			return true;
	}

	private boolean isBlockedAt(Point tileCoord) {
		if( map[tileCoord.x][tileCoord.y] == 1 )
			return true;
		else
			return false;
	}
	
	private ArrayList<Point> getAdjacentTilesForPoint(Point tileCoord) {
		
		ArrayList<Point> tmp = new ArrayList<Point>();
		
		boolean t = false;
		boolean l = false;
		boolean b = false;
		boolean r = false;
		Point p = null;
		
		// Top
		p = new Point(tileCoord.x,tileCoord.y - 1);
		if( isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
			t = true;
		}
		
		// Left
		p = new Point(tileCoord.x - 1,tileCoord.y);
		if( isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
			l = true;
		}
		
		// Bottom 
		p = new Point(tileCoord.x,tileCoord.y + 1);
		if( isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
			b = true;
		}
		
		// Right
		p = new Point(tileCoord.x + 1,tileCoord.y);
		if( isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
			r = true;
		}
		/*
		// Top left 
		p = new Point(tileCoord.x - 1,tileCoord.y - 1);
		if( t && l && isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
		}
		
		// Bottom left 
		p = new Point(tileCoord.x - 1,tileCoord.y + 1);
		if( b && l && isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
		}
		
		// Top right
		p = new Point(tileCoord.x + 1,tileCoord.y - 1);
		if( t && r && isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
		}
		
		// Bottom right 
		p = new Point(tileCoord.x + 1,tileCoord.y + 1);
		if( b && r && isValidAt(p) && !isBlockedAt(p) ) {
			tmp.add(p);
		}
		*/
		return tmp;
	}
	
	private int getCostBetweenSteps(ShortestPathStep from, ShortestPathStep to) {
		return ((from.getPosition().x != to.getPosition().x) && (from.getPosition().y != to.getPosition().y)) ? 14 : 10; 
	}
	
	private int getHScoreBetweenCoords(Point from,Point to) {
		return Math.abs( (to.x - from.x) ) + Math.abs( (to.y - from.y) );
	}
	
	public ArrayList<ShortestPathStep> getShortestPath() {
		return this.shortestPath;
	}
	
	private void constructPathFromStep(ShortestPathStep step) {
		shortestPath = new ArrayList<ShortestPathStep>();
		do {
			if( step.getParent() != null ) {
				shortestPath.add(0, step);
			}
			step = step.getParent();
		} while(step != null);
	}

	public void calculatePath(Point start, Point end) {
		
		if( start.equals(end) ) {
			log("Start and end points are equal.");
			return;
		}
		
		openSteps = new ArrayList<ShortestPathStep>();
		closedSteps = new ArrayList<ShortestPathStep>();
		shortestPath = null;
		
		insert(openSteps,new ShortestPathStep(start));
		
		do {
			
			ShortestPathStep currentStep = openSteps.get(0); // get step with lowest F score
			
			closedSteps.add(currentStep); // add it to closed steps
			
			openSteps.remove(currentStep); // remove it from open steps
			
			if( currentStep.getPosition().equals(end) ) {
				log("Ended at target\n");
				constructPathFromStep(currentStep); // construct path
				// printArray(map, start, end);
				openSteps = null;
				closedSteps = null;
				break;
			} 
			
			ArrayList<Point> adjSteps = getAdjacentTilesForPoint(currentStep.getPosition());
			
			for (Point point : adjSteps) {
				
				ShortestPathStep step = new ShortestPathStep(point);
				
				if( closedSteps.contains(step) ) {
					continue;
				}
				
				int moveCost = getCostBetweenSteps(currentStep,step);
				
				int index = openSteps.indexOf(step);
				
				// Not found
				if( index == -1 ) {
					step.setParent(currentStep);
					step.setGScore(currentStep.getGScore() + moveCost);
					step.setHScore(getHScoreBetweenCoords(step.getPosition(),end));
					insert(openSteps,step);
				} else {
					step = openSteps.get(index);
					if( (currentStep.getGScore() + moveCost) < step.getGScore() ) {
						step.setGScore( currentStep.getGScore() + moveCost );
						openSteps.remove(index);
						insert(openSteps,step);
					}
				}
			}

		}while( openSteps.size() > 0 );
	}
	
	private void insert(ArrayList<ShortestPathStep>openSteps,ShortestPathStep step) {
		
		int stepFScore = step.getFScore();
		int size = openSteps.size();
		int i = 0;
		for( ; i < size; i++ ) {
			if( stepFScore <= openSteps.get(i).getFScore() ) {
				break;
			}
		}
		openSteps.add(i, step);
	}

	private boolean isOnShortestPath(Point p) {
		
		if( shortestPath == null )
			return false;
		
		for ( ShortestPathStep s : shortestPath)
			if( s.getPosition().equals(p) )
				return true;
		
		return false;
	}

	
	private void printArray(int[][] map, Point start, Point end) {
		Point p = new Point();
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				p.setLocation(i, j);
				if (start.equals(p))
					log("s ");
				else if (end.equals(p))
				 	log("e ");
				else if( isOnShortestPath(p)  )
					log("x ");
				else
					log("" + map[i][j] + " ");
			}
			log("\n");
		}
		log("\n");
	}

	private void log(String s) {
		System.out.print(s);
	}

}
