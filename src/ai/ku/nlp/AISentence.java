package ai.ku.nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.stanford.nlp.trees.TreeGraphNode;
import edu.stanford.nlp.trees.TypedDependency;

public class AISentence {

	private HashMap<Integer,AIPOS> posMap = new HashMap<Integer,AIPOS>();
	private HashSet<Integer> keyset = new HashSet<Integer>();
	private ArrayList<AINoun> nouns = new ArrayList<AINoun>();
	private ArrayList<AIPreposition> preps = new ArrayList<AIPreposition>();
	
	public AISentence() {
		WordNetFinder.init();
	}
	
	public void construct(List<TypedDependency> tdl) {

		int nsubjIndex = -1;
		
		for ( TypedDependency td : tdl ) {
			
			int dpi = td.dep().label().index();
			int gvi = td.gov().label().index();
			
			AIPOS dp = posMap.get(dpi);
			if( dp == null ) {
				dp = createPOS(td.dep());
				posMap.put(dpi, dp);
				keyset.add(dpi);
			}
			
			AIPOS gv = posMap.get(gvi);
			if( gv == null ) {
				gv = createPOS(td.gov());
				posMap.put(gvi, gv);
				keyset.add(gvi);
			}
						
			String rl = td.reln().getShortName();
			// if( rl.equals("null") ) rl = td.reln().getShortName();
			
			if( rl.equals("amod") ) {
				if( gv instanceof AINoun ){
					AINoun gov = (AINoun) gv;
					if( dp instanceof AIAdjective )
						gov.addModifier((AIAdjective)dp);
					else
					{ System.out.println(""+dp.value()+" is not an adjective.");}
				}
				else 
				{ System.out.println(""+gv.value()+" is not a noun.");}
			}
			else if( rl.equals("num") ) {
				if( gv instanceof AINoun ){
					AINoun gov = (AINoun) gv;
					gov.setCount(dp.value());
				}
				else 
				{ System.out.println(""+gv.value()+" is not a noun.");}
			}
			else if( rl.equals("det") ) {
				if( gv instanceof AINoun ){
					AINoun gov = (AINoun) gv;
					gov.setCount("one");
					gov.setDeterminer(dp.value());
				}
				else 
				{ System.out.println(""+gv.value()+" is not a noun.");}
			}
			else if( rl.equals("nsubj") ) {
				nsubjIndex = dp.index();
				System.out.println("NSJIndex: "+nsubjIndex);
				// change gv with dp
				if( dp instanceof AINoun ){
					AINoun dep = (AINoun) dp;
					if( gv instanceof AIAdjective )
						dep.addModifier((AIAdjective)gv);
					else
					{ System.out.println(""+gv.value()+" is not an adjective.");}
				}
				else 
				{ System.out.println(""+dp.value()+" is not a noun.");}
			}
			/*
			else if( rl.contains("prep") ) {
				System.out.println(""+td.reln().getSpecific()+" is a prep, dv: "+dp.value()+", "+gv.value());
			}
			*/
			// System.out.println("("+dp.value()+"-"+dp.index()+"-"+dp.tag()+", "+gv.value()+"-"+gv.index()+"-"+gv.tag()+") : "+rl+" ");
			
			// String rl = td.reln().getSpecific();
			/*
			TreeGraphNode dp = td.dep();
			TreeGraphNode gv = td.gov();
			String rl = td.reln().getShortName();
			String dep = dp.value();
			String gov = gv.value();
			String dtag = dp.label().tag();
			String gtag = gv.label().tag();
			int dpi = td.dep().label().index();
			int gvi = td.gov().label().index();
			System.out.println("("+dep+"-"+dpi+"-"+dtag+", "+gov+"-"+gvi+"-"+gtag+") : "+rl+" ");
			*/
		}
		
		for ( TypedDependency td : tdl ) {
			int dpi = td.dep().label().index();
			// int gvi = td.gov().label().index();
			AIPOS dp = posMap.get(dpi);			
			// AIPOS gv = posMap.get(gvi);
			AIPOS sub = posMap.get(nsubjIndex);
			String rl = td.reln().getShortName();
			if( rl.equals("prep") ) {
				String rln = td.reln().getSpecific();
				System.out.println(""+sub.value()+"-"+rln+"-"+dp.value());
				AIPreposition prep = new AIPreposition(sub, dp, rln);
				preps.add(prep);
			}
		}
		
		ArrayList<Integer> keylist = new ArrayList<Integer>();
		for( Integer i : keyset ) keylist.add(i);
		Collections.sort(keylist);
		
		for( Integer i : keylist ) {
			AIPOS pos = posMap.get(i);
			if( pos instanceof AINoun ) {
				System.out.println();
				System.out.println(pos.toString());
				nouns.add((AINoun) pos);
			}
		}
	}
	
	public ArrayList<AINoun> getNouns(){
		return nouns;
	}
	
	public ArrayList<AIPreposition> getPreps(){
		return preps;
	}
	
	public AIPOS createPOS(TreeGraphNode node) {
		AIPOS pos = null;
		
		String value = node.value();
		String tag = node.label().tag();
		int index = node.label().index();
		
		if( tag == null ) tag = "null";
		if( value == null ) value = "null";
		
		if( tag.equals("NN") ) {
			pos = new AINoun(value,tag,index);
			((AINoun)pos).setStem(WordNetFinder.getInstance().stem(pos.value(), "NN"));
		}
		else if( tag.equals("NNS") ) {
			pos = new AINoun(value,tag,index);
			((AINoun)pos).setStem(WordNetFinder.getInstance().stem(pos.value(), "NN"));
		}
		else if( tag.equals("JJ") )
			pos = new AIAdjective(value,tag,index);
		else if( tag.equals("VBD") )
			pos = new AIVerb(value,tag,index);
		else{
			// System.out.println("POS not implemented for tag:"+tag);
			pos = new AIPOS(value,tag,index);
		}
			
		return pos;
	}
}
