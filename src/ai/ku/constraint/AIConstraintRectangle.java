package ai.ku.constraint;

import java.util.ArrayList;
import java.util.HashSet;

import EDU.Washington.grad.gjb.cassowary.CL;
import EDU.Washington.grad.gjb.cassowary.ClLinearEquation;
import EDU.Washington.grad.gjb.cassowary.ClLinearExpression;
import EDU.Washington.grad.gjb.cassowary.ClLinearInequality;
import EDU.Washington.grad.gjb.cassowary.ClPoint;
import EDU.Washington.grad.gjb.cassowary.ClVariable;
import EDU.Washington.grad.gjb.cassowary.ExCLInternalError;
import EDU.Washington.grad.gjb.cassowary.ExCLNonlinearExpression;

public class AIConstraintRectangle {
	ClPoint center;
	ClVariable width;
	ClVariable depth;
	ArrayList<AIConstraintRectangle> insideList;
	HashSet<ClLinearEquation> equationList;
	HashSet<ClLinearInequality> inequalityList;
	
	public AIConstraintRectangle(double centerX, double centerY, double width, double depth)
	{
		center = new ClPoint(centerX, centerY);
		this.depth = new ClVariable(depth);
		this.width = new ClVariable(width);
		insideList = new ArrayList<AIConstraintRectangle>();
		equationList = new HashSet<ClLinearEquation>();
		inequalityList = new HashSet<ClLinearInequality>();
		
		try {
			ClLinearExpression cle = new ClLinearExpression(this.width());
			inequalityList.add(new ClLinearInequality(cle, CL.GEQ, new ClLinearExpression(width)));
			
			cle = new ClLinearExpression(this.depth());
			inequalityList.add(new ClLinearInequality(cle, CL.GEQ, new ClLinearExpression(depth)));
			
			cle.multiplyMe(width / depth);
			equationList.add(new ClLinearEquation(cle, new ClLinearExpression(this.width())));
			
		} catch (ExCLInternalError e) {
			e.printStackTrace();
		}
	}
	
	public ClPoint getCenter() { return center; }
	public ClVariable centerX() { return center.X(); }
	public double getXDoubleForm() { return center.Xvalue(); }
	public ClVariable centerY() { return center.Y(); }
	public double getYDoubleForm() { return center.Yvalue(); }
	public ClVariable depth() { return depth; }
	public double getDepthDoubleForm() { return depth.value(); }
	public ClVariable width() { return width; }
	public double getWidthDoubleForm() { return width.value(); }
	public void setWidth(double width){ this.width.change_value(width); }
	public void setDepth(double depth){ this.depth.change_value(depth); }
	
	public HashSet<ClLinearEquation> getEquationList()
	{
		return equationList;
	}
	
	public HashSet<ClLinearInequality> getInequalityList()
	{
		return inequalityList;
	}
	
	public void addInside(AIConstraintRectangle newObject) throws ExCLInternalError, ExCLNonlinearExpression
	{
		if(insideList.size() != 0)
		{
			ClLinearExpression cleForXLess = new ClLinearExpression(newObject.centerX());
			cleForXLess.addExpression(new ClLinearExpression(newObject.width()).divide(2));
			ClLinearExpression cleForXGreat = new ClLinearExpression(newObject.centerX());
			cleForXGreat.addExpression(new ClLinearExpression(newObject.width()).multiplyMe(-1).divide(2));
			
			ClLinearExpression cleForYLess = new ClLinearExpression(newObject.centerY());
			cleForXLess.addExpression(new ClLinearExpression(newObject.depth()).divide(2));
			ClLinearExpression cleForYGreat = new ClLinearExpression(newObject.centerY());
			cleForXGreat.addExpression(new ClLinearExpression(newObject.depth()).multiplyMe(-1).divide(2));
			
			for(AIConstraintRectangle r : insideList)
			{
				ClLinearExpression forXLess = new ClLinearExpression(r.centerX());
				forXLess.addExpression(new ClLinearExpression(r.width).multiplyMe(-1).divide(2));
				ClLinearExpression forXGreat = new ClLinearExpression(r.centerX());
				forXGreat.addExpression(new ClLinearExpression(r.width).divide(2));
				
				ClLinearExpression forYLess = new ClLinearExpression(r.centerY());
				forYLess.addExpression(new ClLinearExpression(r.depth).multiplyMe(-1).divide(2));
				ClLinearExpression forYGreat = new ClLinearExpression(r.centerY());
				forYGreat.addExpression(new ClLinearExpression(r.depth).divide(2));
				
				inequalityList.add(new ClLinearInequality(cleForXLess, CL.LEQ, forXLess));
				inequalityList.add(new ClLinearInequality(cleForXGreat, CL.GEQ, forXGreat));
				inequalityList.add(new ClLinearInequality(cleForYLess, CL.LEQ, forYLess));
				inequalityList.add(new ClLinearInequality(cleForYGreat, CL.GEQ, forYGreat));
				
			}
		}
		
		ClLinearExpression cleForWidthLess = new ClLinearExpression(newObject.centerX());
		cleForWidthLess.addExpression(new ClLinearExpression(newObject.width()).divide(2).multiplyMe(-1));
		ClLinearExpression forWidthLess = new ClLinearExpression(this.centerX());
		forWidthLess.addExpression(new ClLinearExpression(this.width()).divide(2).multiplyMe(-1));
		
		ClLinearExpression cleForWidthGreat = new ClLinearExpression(newObject.centerX());
		cleForWidthGreat.addExpression(new ClLinearExpression(newObject.width()).divide(2));
		ClLinearExpression forWidthGreat = new ClLinearExpression(this.centerX());
		forWidthGreat.addExpression(new ClLinearExpression(this.width()).divide(2));
		
		ClLinearExpression cleForDepthLess = new ClLinearExpression(newObject.centerY());
		cleForDepthLess.addExpression(new ClLinearExpression(newObject.depth()).divide(2).multiplyMe(-1));
		ClLinearExpression forDepthLess = new ClLinearExpression(this.centerY());
		forDepthLess.addExpression(new ClLinearExpression(this.depth()).divide(2).multiplyMe(-1));
		
		ClLinearExpression cleForDepthGreat = new ClLinearExpression(newObject.centerY());
		cleForDepthGreat.addExpression(new ClLinearExpression(newObject.depth()).divide(2));
		ClLinearExpression forDepthGreat = new ClLinearExpression(this.centerY());
		forDepthGreat.addExpression(new ClLinearExpression(this.depth()).divide(2));
		
		inequalityList.add(new ClLinearInequality(forWidthLess, CL.LEQ, cleForWidthLess));
		inequalityList.add(new ClLinearInequality(cleForWidthGreat, CL.LEQ, forWidthGreat));
		
		inequalityList.add(new ClLinearInequality(forDepthLess, CL.LEQ, cleForDepthLess));
		inequalityList.add(new ClLinearInequality(cleForDepthGreat, CL.LEQ, forDepthGreat));
		
		insideList.add(newObject);
		
	}

}
