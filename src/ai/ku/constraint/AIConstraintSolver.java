package ai.ku.constraint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

import EDU.Washington.grad.gjb.cassowary.CL;
import EDU.Washington.grad.gjb.cassowary.ClLinearEquation;
import EDU.Washington.grad.gjb.cassowary.ClLinearExpression;
import EDU.Washington.grad.gjb.cassowary.ClLinearInequality;
import EDU.Washington.grad.gjb.cassowary.ClPoint;
import EDU.Washington.grad.gjb.cassowary.ClSimplexSolver;
import EDU.Washington.grad.gjb.cassowary.ClVariable;
import EDU.Washington.grad.gjb.cassowary.ExCLError;
import EDU.Washington.grad.gjb.cassowary.ExCLInternalError;
import EDU.Washington.grad.gjb.cassowary.ExCLNonlinearExpression;
import EDU.Washington.grad.gjb.cassowary.ExCLRequiredFailure;
import ai.ku.model.AIModel;
import ai.ku.nlp.AIPreposition.Preposition;

public class AIConstraintSolver {
	ClSimplexSolver solver;
	ArrayList<AIConstraintRectangle> objects;
	HashSet<ClLinearEquation> equationList;
	HashSet<ClLinearInequality> inequalityList;
	HashMap<ClPoint, Double> stayPoints;
	HashSet<ClVariable> stayVariables;
	Random randomizer;
	
	public AIConstraintSolver()
	{
		solver = new ClSimplexSolver();
		objects = new ArrayList<AIConstraintRectangle>();
		equationList = new HashSet<ClLinearEquation>();
		inequalityList = new HashSet<ClLinearInequality>();
		stayPoints = new HashMap<ClPoint, Double>();
		stayVariables = new HashSet<ClVariable>();
		solver.setAutosolve(false);
		randomizer = new Random();
	}
	
	public ClSimplexSolver getSolver() { return this.solver; }
	public ArrayList<AIConstraintRectangle> getObjectList() { return this.objects; }
	
	public void addObject(AIModel model)
	{
		AIConstraintRectangle rect = model.getRectangle(); 
		if(!objects.contains(rect))
		{
			objects.add(rect);
			stayPoints.put(rect.getCenter(), 1.0);
			stayVariables.add(rect.width());
			stayVariables.add(rect.depth());
		}
	}
	
	public void addObjectRightSideOfAnotherOne(AIModel newObject, AIModel target)
	{
		newObject.getRelationList().add(new AIRelation(newObject, target, Preposition.NEAR));
		target.getRelationList().add(new AIRelation(target, newObject, Preposition.NEAR));
		
		AIConstraintRectangle rectForNewObject = newObject.getRectangle();
		AIConstraintRectangle rectForTarget = target.getRectangle();
		AIConstraintRules rulesForNewObject = newObject.getRules();
		AIConstraintRules rulesForTarget = target.getRules();
		
		try {
			if(!rulesForNewObject.ignoreX && !rulesForTarget.ignoreX)
			{	
				ClLinearExpression cleNew;
				ClLinearExpression cleTarget;
				
				if(rulesForNewObject.isXDynamic)
					cleNew = new ClLinearExpression(rectForNewObject.centerX());
				else
					cleNew = new ClLinearExpression(rectForNewObject.getXDoubleForm());
				if(rulesForTarget.isXDynamic)
					cleTarget = new ClLinearExpression(rectForTarget.centerX());
				else
					cleTarget = new ClLinearExpression(rectForTarget.getXDoubleForm());
				if(!rulesForNewObject.ignoreX && !rulesForTarget.ignoreX)
				{
					if(rulesForNewObject.isWidthDynamic)
						cleNew.addExpression(new ClLinearExpression(rectForNewObject.width()).divide(2).multiplyMe(-1));
					else
						cleNew.addExpression(new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2).multiplyMe(-1));
					if(rulesForTarget.isWidthDynamic)
						cleTarget.addExpression(new ClLinearExpression(rectForTarget.width()).multiplyMe(0.75));
					else
						cleTarget.addExpression(new ClLinearExpression(rectForTarget.getWidthDoubleForm()).multiplyMe(0.75));
					if(!(!rulesForNewObject.isWidthDynamic && !rulesForTarget.isWidthDynamic && rulesForNewObject.ignoreX && rulesForTarget.ignoreX))
						this.inequalityList.add(new ClLinearInequality(cleTarget, CL.LEQ,cleNew ));
				}
			}
			if(!rulesForNewObject.ignoreY && !rulesForTarget.ignoreY)
			{
				if(rulesForNewObject.isYDynamic && rulesForTarget.isYDynamic)
					this.equationList.add(new ClLinearEquation(new ClLinearExpression(rectForNewObject.centerY()),new ClLinearExpression(rectForTarget.centerY())));
				else if(!rulesForNewObject.isYDynamic && rulesForTarget.isYDynamic)
					this.equationList.add(new ClLinearEquation(new ClLinearExpression(rectForNewObject.getYDoubleForm()),new ClLinearExpression(rectForTarget.centerY())));
				else if(rulesForNewObject.isYDynamic && !rulesForTarget.isYDynamic)
					this.equationList.add(new ClLinearEquation(new ClLinearExpression(rectForNewObject.centerY()),new ClLinearExpression(rectForTarget.getYDoubleForm())));	
			}
		} catch (ExCLNonlinearExpression e) {
			e.printStackTrace();
		} catch (ExCLInternalError e) {
			e.printStackTrace();
		}
		if(!objects.contains(rectForNewObject))
		{	
			objects.add(rectForNewObject);
			stayPoints.put(rectForNewObject.getCenter(), 1.0);
			stayVariables.add(rectForNewObject.depth());
			stayVariables.add(rectForNewObject.width());
		}	
	}
	
	public void addObjectLeftSideOfAnotherOne(AIModel newObject, AIModel target)
	{
		newObject.getRelationList().add(new AIRelation(newObject, target, Preposition.NEAR));
		target.getRelationList().add(new AIRelation(target, newObject, Preposition.NEAR));
		
		AIConstraintRectangle rectForNewObject = newObject.getRectangle();
		AIConstraintRectangle rectForTarget = target.getRectangle();
		AIConstraintRules rulesForNewObject = newObject.getRules();
		AIConstraintRules rulesForTarget = target.getRules();
		try {
			if(!rulesForNewObject.ignoreX && !rulesForTarget.ignoreX)
			{	
				ClLinearExpression cleNew;
				ClLinearExpression cleTarget;
				
				if(rulesForNewObject.isXDynamic)
					cleNew = new ClLinearExpression(rectForNewObject.centerX());
				else
					cleNew = new ClLinearExpression(rectForNewObject.getXDoubleForm());
				if(rulesForTarget.isXDynamic)
					cleTarget = new ClLinearExpression(rectForTarget.centerX());
				else
					cleTarget = new ClLinearExpression(rectForTarget.getXDoubleForm());
				if(!rulesForNewObject.ignoreX && !rulesForTarget.ignoreX)
				{
					if(rulesForNewObject.isWidthDynamic)
						cleNew.addExpression(new ClLinearExpression(rectForNewObject.width()).divide(2));
					else
						cleNew.addExpression(new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2));
					if(rulesForTarget.isWidthDynamic)
						cleTarget.addExpression(new ClLinearExpression(rectForTarget.width()).multiplyMe(-0.75));
					else
						cleTarget.addExpression(new ClLinearExpression(rectForTarget.getWidthDoubleForm()).multiplyMe(-0.75));
					if(!(!rulesForNewObject.isWidthDynamic && !rulesForTarget.isWidthDynamic && rulesForNewObject.ignoreX && rulesForTarget.ignoreX))
						this.inequalityList.add(new ClLinearInequality(cleNew, CL.LEQ,cleTarget ));
				}
			}
			if(!rulesForNewObject.ignoreY && !rulesForTarget.ignoreY)
			{
				if(rulesForNewObject.isYDynamic && rulesForTarget.isYDynamic)
					this.equationList.add(new ClLinearEquation(new ClLinearExpression(rectForNewObject.centerY()),new ClLinearExpression(rectForTarget.centerY())));
				else if(!rulesForNewObject.isYDynamic && rulesForTarget.isYDynamic)
					this.equationList.add(new ClLinearEquation(new ClLinearExpression(rectForNewObject.getYDoubleForm()),new ClLinearExpression(rectForTarget.centerY())));
				else if(rulesForNewObject.isYDynamic && !rulesForTarget.isYDynamic)
					this.equationList.add(new ClLinearEquation(new ClLinearExpression(rectForNewObject.centerY()),new ClLinearExpression(rectForTarget.getYDoubleForm())));	
			}
		} catch (ExCLNonlinearExpression e) {
			e.printStackTrace();
		} catch (ExCLInternalError e) {
			e.printStackTrace();
		}
		
		if(!objects.contains(rectForNewObject))
		{	
			objects.add(rectForNewObject);
			stayPoints.put(rectForNewObject.getCenter(), 1.0);
			stayVariables.add(rectForNewObject.depth());
			stayVariables.add(rectForNewObject.width());
		}	
	}
	
	public void addObjectInFrontOfAnotherOne(AIModel newObject, AIModel target)
	{
		try {
			
			AIConstraintRectangle rectForNewObject = newObject.getRectangle();
			AIConstraintRectangle rectForTarget = target.getRectangle();
			AIConstraintRules rulesForNewObject = newObject.getRules();
			AIConstraintRules rulesForTarget = target.getRules();
			
			for(Iterator<AIRelation> it = target.getRelationList().iterator(); it.hasNext();)
			{
				AIRelation rel = it.next();
				AIModel sourceForRelation = rel.getSource();//sourceForRelation and target are same model.
				AIModel targetForRelation = rel.getTarget();//targetForRelation is another model.
				Preposition type = rel.getType();
				AIConstraintRules rulesForTargetRelation = targetForRelation.getRules();
				
				if(type == Preposition.IN_FRONT_OF)
				{
					AIConstraintRules backTrace1 = rulesForNewObject;
					AIConstraintRules backTrace2 = rulesForTargetRelation;
					rulesForNewObject.ignoreX = false;
					rulesForNewObject.isXDynamic = true;
					rulesForNewObject.isYDynamic = true;
					rulesForNewObject.isWidthDynamic = false;
					rulesForNewObject.isDepthDynamic = false;
					rulesForTargetRelation.ignoreX = false;
					rulesForTargetRelation.isXDynamic = true;
					rulesForTargetRelation.isYDynamic = true;
					rulesForTargetRelation.isWidthDynamic = false;
					rulesForTargetRelation.isDepthDynamic = false;
					
					if(targetForRelation.getRectangle().getXDoubleForm() > sourceForRelation.getRectangle().getXDoubleForm())
						this.addObjectLeftSideOfAnotherOne(newObject, targetForRelation);
					else
						this.addObjectRightSideOfAnotherOne(newObject, targetForRelation);
					rulesForNewObject = backTrace1;
					rulesForTargetRelation = backTrace2;
				}
				else if(type == Preposition.IN || type == Preposition.ON)
					this.addObjectOnORInToAnotherOne(newObject, targetForRelation, type, true);
			}
			
			target.getRelationList().add(new AIRelation(target, newObject, Preposition.IN_FRONT_OF));
			newObject.getRelationList().add(new AIRelation(newObject, target, Preposition.BEHIND));
			
			ClLinearExpression cleXGreat;
			ClLinearExpression cleXLess;
			
			if(!rulesForNewObject.ignoreX && !rulesForTarget.ignoreX)
			{
				if(rulesForNewObject.isXDynamic)
				{
					cleXGreat = new ClLinearExpression(rectForNewObject.centerX());
					cleXLess = new ClLinearExpression(rectForNewObject.centerX());
				}
				else
				{
					cleXGreat = new ClLinearExpression(rectForNewObject.getXDoubleForm());
					cleXLess = new ClLinearExpression(rectForNewObject.getXDoubleForm());
				}
				
				if(rulesForNewObject.isWidthDynamic)
				{
					cleXGreat.addExpression(new ClLinearExpression(rectForNewObject.width()).divide(2));
					cleXLess.minus(new ClLinearExpression(rectForNewObject.width()).divide(2));
				}
				else
				{
					cleXGreat.addExpression(new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2));
					cleXLess.minus(new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2));
				}

				ClLinearExpression cleTargetGreat;
				ClLinearExpression cleTargetLess;
				
				if(rulesForTarget.isXDynamic)
				{
					cleTargetGreat = new ClLinearExpression(rectForTarget.centerX());
					cleTargetLess = new ClLinearExpression(rectForTarget.centerX());
				}
				else
				{
					cleTargetGreat = new ClLinearExpression(rectForTarget.getXDoubleForm());
					cleTargetLess = new ClLinearExpression(rectForTarget.getXDoubleForm());
				}
					
				if(rulesForTarget.isWidthDynamic)
				{
					cleTargetGreat.addExpression(new ClLinearExpression(rectForTarget.width()).divide(2));
					cleTargetLess.minus(new ClLinearExpression(rectForTarget.width()).divide(2));
				}
				else
				{
					cleTargetGreat.addExpression(new ClLinearExpression(rectForTarget.getWidthDoubleForm()).divide(2));
					cleTargetLess.minus(new ClLinearExpression(rectForTarget.getWidthDoubleForm()).divide(2));
				}
				if(!(!rulesForNewObject.isWidthDynamic && !rulesForTarget.isXDynamic))
				{
					this.inequalityList.add(new ClLinearInequality(cleXGreat, CL.LEQ, cleTargetGreat));
					this.inequalityList.add(new ClLinearInequality(cleTargetLess, CL.LEQ, cleXLess));
				}
			}
			
			if(!rulesForNewObject.ignoreY && !rulesForTarget.ignoreY)
			{
				ClLinearExpression cleForNewObject;
				
				if(rulesForNewObject.isYDynamic)
					cleForNewObject = new ClLinearExpression(rectForNewObject.centerY());
				else
					cleForNewObject = new ClLinearExpression(rectForNewObject.getYDoubleForm());
				if(rulesForNewObject.isDepthDynamic)
					cleForNewObject.addExpression(new ClLinearExpression(rectForNewObject.depth()).divide(2).multiplyMe(-1));
				else
					cleForNewObject.addExpression(new ClLinearExpression(rectForNewObject.getDepthDoubleForm()).divide(2).multiplyMe(-1));
				
				ClLinearExpression cleForTarget;
				
				if(rulesForTarget.isYDynamic)
					cleForTarget = new ClLinearExpression(rectForTarget.centerY());
				else
					cleForTarget = new ClLinearExpression(rectForTarget.getYDoubleForm());
				if(rulesForTarget.isDepthDynamic)
					cleForTarget.addExpression(new ClLinearExpression(rectForTarget.depth()).multiplyMe(0.75));//0.5 merkezden kenara çeyrek de uzaklýk belirlensin
				else
					cleForTarget.addExpression(new ClLinearExpression(rectForTarget.getDepthDoubleForm()).multiplyMe(0.75));
				if(!(!rulesForNewObject.isWidthDynamic && !rulesForTarget.isWidthDynamic))
					this.inequalityList.add(new ClLinearInequality(cleForNewObject, CL.GEQ, cleForTarget));
			}
			
			if(!objects.contains(rectForNewObject))
			{	
				objects.add(rectForNewObject);
				stayPoints.put(rectForNewObject.getCenter(), 10.0);
				stayVariables.add(rectForNewObject.depth());
				stayVariables.add(rectForNewObject.width());
			}			
		} catch (ExCLNonlinearExpression e) {
			e.printStackTrace();
		} catch (ExCLInternalError e) {
			e.printStackTrace();
		}
		
	}
	
	public void addObjectBehindAnotherOne(AIModel newObject, AIModel target)
	{
		try {
			
			AIConstraintRectangle rectForNewObject = newObject.getRectangle();
			AIConstraintRectangle rectForTarget = target.getRectangle();
			AIConstraintRules rulesForNewObject = newObject.getRules();
			AIConstraintRules rulesForTarget = target.getRules();
			
			for(Iterator<AIRelation> it = target.getRelationList().iterator(); it.hasNext();)
			{
				AIRelation rel = it.next();
				AIModel sourceForRelation = rel.getSource();//sourceForRelation and target are same model.
				AIModel targetForRelation = rel.getTarget();//targetForRelation is another model.
				Preposition type = rel.getType();
				AIConstraintRules rulesForTargetRelation = targetForRelation.getRules();
				
				if(type == Preposition.BEHIND)
				{
					AIConstraintRules backTrace1 = rulesForNewObject;
					AIConstraintRules backTrace2 = rulesForTargetRelation;
					rulesForNewObject.ignoreX = false;
					rulesForNewObject.isXDynamic = true;
					rulesForNewObject.isYDynamic = true;
					rulesForNewObject.isWidthDynamic = false;
					rulesForNewObject.isDepthDynamic = false;
					rulesForTargetRelation.ignoreX = false;
					rulesForTargetRelation.isXDynamic = true;
					rulesForTargetRelation.isYDynamic = true;
					rulesForTargetRelation.isWidthDynamic = false;
					rulesForTargetRelation.isDepthDynamic = false;
					
					if(targetForRelation.getRectangle().getXDoubleForm() > sourceForRelation.getRectangle().getXDoubleForm())
						this.addObjectLeftSideOfAnotherOne(newObject, targetForRelation);
					else
						this.addObjectRightSideOfAnotherOne(newObject, targetForRelation);
					rulesForNewObject = backTrace1;
					rulesForTargetRelation = backTrace2;
				}
				else if(type == Preposition.IN || type == Preposition.ON)
					this.addObjectOnORInToAnotherOne(newObject, targetForRelation, type, true);
			}
			
			target.getRelationList().add(new AIRelation(target, newObject, Preposition.BEHIND));
			newObject.getRelationList().add(new AIRelation(newObject, target, Preposition.IN_FRONT_OF));
			
			ClLinearExpression cleXGreat;
			ClLinearExpression cleXLess;
			
			if(!rulesForNewObject.ignoreX && !rulesForTarget.ignoreX)
			{
				if(rulesForNewObject.isXDynamic)
				{
					cleXGreat = new ClLinearExpression(rectForNewObject.centerX());
					cleXLess = new ClLinearExpression(rectForNewObject.centerX());
				}
				else
				{
					cleXGreat = new ClLinearExpression(rectForNewObject.getXDoubleForm());
					cleXLess = new ClLinearExpression(rectForNewObject.getXDoubleForm());
				}
				
				if(rulesForNewObject.isWidthDynamic)
				{
					cleXGreat.addExpression(new ClLinearExpression(rectForNewObject.width()).divide(2));
					cleXLess.minus(new ClLinearExpression(rectForNewObject.width()).divide(2));
				}
				else
				{
					cleXGreat.addExpression(new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2));
					cleXLess.minus(new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2));
				}

				ClLinearExpression cleTargetGreat;
				ClLinearExpression cleTargetLess;
				
				if(rulesForTarget.isXDynamic)
				{
					cleTargetGreat = new ClLinearExpression(rectForTarget.centerX());
					cleTargetLess = new ClLinearExpression(rectForTarget.centerX());
				}
				else
				{
					cleTargetGreat = new ClLinearExpression(rectForTarget.getXDoubleForm());
					cleTargetLess = new ClLinearExpression(rectForTarget.getXDoubleForm());
				}
					
				if(rulesForTarget.isWidthDynamic)
				{
					cleTargetGreat.addExpression(new ClLinearExpression(rectForTarget.width()).divide(2));
					cleTargetLess.minus(new ClLinearExpression(rectForTarget.width()).divide(2));
				}
				else
				{
					cleTargetGreat.addExpression(new ClLinearExpression(rectForTarget.getWidthDoubleForm()).divide(2));
					cleTargetLess.minus(new ClLinearExpression(rectForTarget.getWidthDoubleForm()).divide(2));
				}
				if(!(!rulesForNewObject.isWidthDynamic && !rulesForTarget.isXDynamic))
				{
					this.inequalityList.add(new ClLinearInequality(cleXGreat, CL.LEQ, cleTargetGreat));
					this.inequalityList.add(new ClLinearInequality(cleTargetLess, CL.LEQ, cleXLess));
				}
			}
			
			if(!rulesForNewObject.ignoreY && !rulesForTarget.ignoreY)
			{
				ClLinearExpression cleForNewObject;
				
				if(rulesForNewObject.isYDynamic)
					cleForNewObject = new ClLinearExpression(rectForNewObject.centerY());
				else
					cleForNewObject = new ClLinearExpression(rectForNewObject.getYDoubleForm());
				if(rulesForNewObject.isDepthDynamic)
					cleForNewObject.addExpression(new ClLinearExpression(rectForNewObject.depth()).divide(2));
				else
					cleForNewObject.addExpression(new ClLinearExpression(rectForNewObject.getDepthDoubleForm()).divide(2));
				
				ClLinearExpression cleForTarget;
				
				if(rulesForTarget.isYDynamic)
					cleForTarget = new ClLinearExpression(rectForTarget.centerY());
				else
					cleForTarget = new ClLinearExpression(rectForTarget.getYDoubleForm());
				if(rulesForTarget.isDepthDynamic)
					cleForTarget.addExpression(new ClLinearExpression(rectForTarget.depth()).multiplyMe(-0.75));//0.5 merkezden kenara çeyrek de uzaklýk belirlensin
				else
					cleForTarget.addExpression(new ClLinearExpression(rectForTarget.getDepthDoubleForm()).multiplyMe(-0.75));
				if(!(!rulesForNewObject.isWidthDynamic && !rulesForTarget.isWidthDynamic))
					this.inequalityList.add(new ClLinearInequality(cleForNewObject, CL.LEQ, cleForTarget));
			}
			
			if(!objects.contains(rectForNewObject))
			{	
				objects.add(rectForNewObject);
				stayPoints.put(rectForNewObject.getCenter(), 10.0);
				stayVariables.add(rectForNewObject.depth());
				stayVariables.add(rectForNewObject.width());
			}			
		} catch (ExCLNonlinearExpression e) {
			e.printStackTrace();
		} catch (ExCLInternalError e) {
			e.printStackTrace();
		}
	}
	
	public void addObjectNearAnotherOne(AIModel newObject, AIModel target)
	{
		try {
			boolean coin = this.randomizer.nextBoolean();
			for(AIRelation rel : target.getRelationList())
			{
				AIModel targetForRelation = rel.getTarget();//targetForRelation is another model.
				Preposition type = rel.getType();
				
				if(type == Preposition.NEAR)
				{
					if(coin)
						this.addObjectLeftSideOfAnotherOne(newObject, targetForRelation);
					else
						this.addObjectRightSideOfAnotherOne(newObject, targetForRelation);
				}
			}
			
			target.getRelationList().add(new AIRelation(target, newObject, Preposition.NEAR));
			newObject.getRelationList().add(new AIRelation(newObject, target, Preposition.NEAR));
			
			coin = this.randomizer.nextBoolean();
			if(coin)
				this.addObjectLeftSideOfAnotherOne(newObject, target);
			else
				this.addObjectRightSideOfAnotherOne(newObject, target);
			
			AIConstraintRectangle rectForNewObject = newObject.getRectangle();
			if(!objects.contains(rectForNewObject))
			{	
				objects.add(rectForNewObject);
				stayPoints.put(rectForNewObject.getCenter(), 10.0);
				stayVariables.add(rectForNewObject.depth());
				stayVariables.add(rectForNewObject.width());
			}			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void addObjectOnORInToAnotherOne(AIModel newObject, AIModel target, Preposition prep, boolean passRelation)
	{
		try {
			AIConstraintRectangle rectForNewObject = newObject.getRectangle();
			AIConstraintRectangle rectForTarget = target.getRectangle();
			AIConstraintRules rulesForNewObject = newObject.getRules();
			AIConstraintRules rulesForTarget = target.getRules();
			
			boolean coin = this.randomizer.nextBoolean();
			if(!passRelation)
			{
				for(Iterator<AIRelation> it = target.getRelationList().iterator(); it.hasNext();)
				{
					AIRelation rel = it.next();
					AIModel targetForRelation = rel.getTarget();
					Preposition type = rel.getType();
					AIConstraintRules rulesForTargetRelation = targetForRelation.getRules();
				
					AIConstraintRules backTrace1 = rulesForNewObject;
					AIConstraintRules backTrace2 = rulesForTargetRelation;
					rulesForNewObject.ignoreX = false;
					rulesForNewObject.isXDynamic = true;
					rulesForNewObject.isYDynamic = true;
					rulesForNewObject.isWidthDynamic = false;
					rulesForNewObject.isDepthDynamic = false;
					rulesForTargetRelation.ignoreX = false;
					rulesForTargetRelation.isXDynamic = true;
					rulesForTargetRelation.isYDynamic = true;
					rulesForTargetRelation.isWidthDynamic = false;
					rulesForTargetRelation.isDepthDynamic = false;
				
					if(type == prep)
					{
						if(coin)
							this.addObjectLeftSideOfAnotherOne(newObject, targetForRelation);
						else
							this.addObjectRightSideOfAnotherOne(newObject, targetForRelation);
					}
				
					rulesForNewObject = backTrace1;
					rulesForTargetRelation = backTrace2;
				}
			}
			
			target.getRelationList().add(new AIRelation(target, newObject, prep));
			newObject.getRelationList().add(new AIRelation(newObject, target, prep));
			
			if(!rulesForNewObject.ignoreX && ! rulesForTarget.ignoreX)
			{
				ClLinearExpression cleX;
				ClLinearExpression cleWidth;
				ClLinearExpression cleTargetX;
				ClLinearExpression cleTargetWidth;
				
				if(rulesForNewObject.isXDynamic)
					cleX = new ClLinearExpression(rectForNewObject.centerX());
				else
					cleX = new ClLinearExpression(rectForNewObject.getXDoubleForm());
				if(rulesForNewObject.isWidthDynamic)
					cleWidth = new ClLinearExpression(rectForNewObject.width()).divide(2);
				else
					cleWidth = new ClLinearExpression(rectForNewObject.getWidthDoubleForm()).divide(2);
				if(rulesForTarget.isXDynamic)
					cleTargetX = new ClLinearExpression(rectForTarget.centerX());
				else
					cleTargetX = new ClLinearExpression(rectForTarget.getXDoubleForm());
				if(rulesForTarget.isWidthDynamic)
					cleTargetWidth = new ClLinearExpression(rectForTarget.width()).divide(2);
				else
					cleTargetWidth = new ClLinearExpression(rectForTarget.getWidthDoubleForm()).divide(2);
				if(rulesForNewObject.isXDynamic || rulesForNewObject.isWidthDynamic || rulesForTarget.isXDynamic || rulesForTarget.isWidthDynamic)
				{
					this.inequalityList.add(new ClLinearInequality(CL.Plus(cleX, cleWidth), CL.LEQ, CL.Plus(cleTargetX, cleTargetWidth)));
					this.inequalityList.add(new ClLinearInequality(CL.Minus(cleX, cleWidth), CL.GEQ, CL.Minus(cleTargetX, cleTargetWidth)));
					System.out.println("EQ: " + new ClLinearInequality(CL.Minus(cleX, cleWidth), CL.GEQ, CL.Minus(cleTargetX, cleTargetWidth)).toString());
				}
			}
			
			if(!rulesForNewObject.ignoreY && ! rulesForTarget.ignoreY)
			{
				ClLinearExpression cleY;
				ClLinearExpression cleDepth;
				ClLinearExpression cleTargetY;
				ClLinearExpression cleTargetDepth;
				
				if(rulesForNewObject.isYDynamic)
					cleY = new ClLinearExpression(rectForNewObject.centerY());
				else
					cleY = new ClLinearExpression(rectForNewObject.getYDoubleForm());
				if(rulesForNewObject.isDepthDynamic)
					cleDepth = new ClLinearExpression(rectForNewObject.depth()).divide(2);
				else
					cleDepth = new ClLinearExpression(rectForNewObject.getDepthDoubleForm()).divide(2);
				if(rulesForTarget.isYDynamic)
					cleTargetY = new ClLinearExpression(rectForTarget.centerY());
				else
					cleTargetY = new ClLinearExpression(rectForTarget.getYDoubleForm());
				if(rulesForTarget.isWidthDynamic)
					cleTargetDepth = new ClLinearExpression(rectForTarget.depth()).divide(2);
				else
					cleTargetDepth = new ClLinearExpression(rectForTarget.getDepthDoubleForm()).divide(2);
				if(rulesForNewObject.isYDynamic || rulesForNewObject.isDepthDynamic || rulesForTarget.isYDynamic || rulesForTarget.isDepthDynamic)
				{
					this.inequalityList.add(new ClLinearInequality(CL.Plus(cleY, cleDepth), CL.LEQ, CL.Plus(cleTargetY, cleTargetDepth)));
					this.inequalityList.add(new ClLinearInequality(CL.Minus(cleY, cleDepth), CL.GEQ, CL.Minus(cleTargetY, cleTargetDepth)));
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void addAllConstraint()
	{
		try
		{
			for(AIConstraintRectangle object : objects)
			{
				for(ClLinearEquation leq : object.getEquationList())
					solver.addConstraint(leq);
				for(ClLinearInequality liq : object.getInequalityList())
					solver.addConstraint(liq);
			}
		
			for(ClLinearEquation leq : this.equationList)
				solver.addConstraint(leq);
			for(ClLinearInequality liq : this.inequalityList)
				solver.addConstraint(liq);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void resetSolver()
	{
		this.solver = new ClSimplexSolver();
	}
	
	public void addAllStay()
	{
		for(ClVariable cv : this.stayVariables)
		{
			try {
				solver.addStay(cv);
			} catch (ExCLRequiredFailure e) {
				e.printStackTrace();
			} catch (ExCLInternalError e) {
				e.printStackTrace();
			}
		}
		
		for(ClPoint cp : this.stayPoints.keySet())
		{
			try {
				solver.addPointStay(cp, this.stayPoints.get(cp));
			} catch (ExCLRequiredFailure e) {
				e.printStackTrace();
			} catch (ExCLInternalError e) {
				e.printStackTrace();
			}
		}	
	}
	
	public void addInequality(ClLinearInequality liq)
	{
		inequalityList.add(liq);
	}
	
	public void addEquation(ClLinearEquation leq)
	{
		equationList.add(leq);
	}
	
	public void solveForNewValue(ClVariable variable, double newValue) throws ExCLError
	{
		solver.addEditVar(variable);
		solver.beginEdit();
		solver.suggestValue(variable, newValue);
		solver.solve();
		solver.endEdit();
	}
	
	public void solveForNewValues(ArrayList<ClVariable> variables, ArrayList<Double> values) throws ExCLError
	{
		for(ClVariable v : variables)
			solver.addEditVar(v);
		solver.beginEdit();
		for(int i = 0; i < variables.size(); i++)
			solver.suggestValue(variables.get(i), values.get(i));
		solver.solve();
		solver.endEdit();
	}
}
