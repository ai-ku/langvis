package ai.ku.model;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector3f;

import ai.ku.animation.MoveAnimation;
import ai.ku.astar.AStar;
import ai.ku.astar.ShortestPathStep;
import ai.ku.drawing.AIPoint;
import ai.ku.drawing.AIRect;
import ai.ku.drawing.AISize;
import ai.ku.math.AIVector;
import ai.ku.nlp.AIAdjective;
import ai.ku.nlp.AINoun;
import ai.ku.nlp.AIPOS;
import ai.ku.nlp.AIPreposition;
import ai.ku.nlp.AIPreposition.Preposition;
import ai.ku.nlp.Parser;
import ai.ku.nlp.WordNetFinder;
import ai.ku.reasoning.Oracle;
import ai.ku.resource.SynsetRepository;
import ai.ku.util.GeomUtils;
import ai.ku.util.Logger;
import ai.ku.util.Randomizer;
import ai.ku.util.Scale;

import com.bulletphysics.BulletStats;
import com.bulletphysics.collision.broadphase.BroadphaseInterface;
import com.bulletphysics.collision.broadphase.DbvtBroadphase;
import com.bulletphysics.collision.dispatch.CollisionDispatcher;
import com.bulletphysics.collision.dispatch.CollisionObject;
import com.bulletphysics.collision.dispatch.DefaultCollisionConfiguration;
import com.bulletphysics.collision.shapes.BoxShape;
import com.bulletphysics.collision.shapes.CollisionShape;
import com.bulletphysics.dynamics.DiscreteDynamicsWorld;
import com.bulletphysics.dynamics.RigidBody;
import com.bulletphysics.dynamics.RigidBodyConstructionInfo;
import com.bulletphysics.dynamics.constraintsolver.ConstraintSolver;
import com.bulletphysics.dynamics.constraintsolver.SequentialImpulseConstraintSolver;
import com.bulletphysics.linearmath.Clock;
import com.bulletphysics.linearmath.DefaultMotionState;
import com.bulletphysics.linearmath.Transform;
import com.bulletphysics.util.ObjectArrayList;

import edu.cmu.cs.stage3.alice.authoringtool.AuthoringTool;
import edu.cmu.cs.stage3.alice.authoringtool.WorldFrame;
import edu.cmu.cs.stage3.alice.authoringtool.util.ElementPopupUtilities.DeleteRunnable;
import edu.cmu.cs.stage3.alice.core.Direction;
import edu.cmu.cs.stage3.alice.core.Model;
import edu.cmu.cs.stage3.alice.core.World;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationEvent;
import edu.cmu.cs.stage3.alice.scenegraph.event.AbsoluteTransformationListener;
import edu.cmu.cs.stage3.math.Box;
import edu.cmu.cs.stage3.math.Vector3;
import edu.stanford.nlp.util.StringUtils;
// import ai.ku.nlp.WordNetFinder;

public class ModelExtractor implements AbsoluteTransformationListener {

	// private POSTagger tagger;
	private AuthoringTool authoringTool;
	private WorldFrame frame;

	public ModelExtractor(AuthoringTool authoringTool, WorldFrame frame){
		WordNetFinder.init();
		SynsetRepository.init();
		Parser.init();
		// tagger = new POSTagger();
		this.authoringTool = authoringTool;
		this.frame = frame;
	}

	public void checkWNFirst(AINoun noun) {
		ArrayList<AIAdjective> mods = noun.getModifiers();
		for(int i=0;i<mods.size();i++) 
		{
			ArrayList<AIAdjective> adjs = new ArrayList<AIAdjective>();
			for( int j = i; j < mods.size(); j++ )
				adjs.add(mods.get(j));

			String value = "";
			for( int j = 0; j < adjs.size(); j++ )
			{
				value += adjs.get(j).value();  
				if( j != adjs.size()-1 ) value += " ";
			}
			value += " "+noun.getStem();
			Logger.log(value);
			if( WordNetFinder.getInstance().hasNounPhrase(value) )
			{
				noun.setValue(value);
				for(int j=0;j<adjs.size();j++)
					mods.remove(adjs.get(j));
				Logger.log("included");		
			}
		}
	}

	public void startQuery() {
		String question = JOptionPane.showInputDialog(null, "Ask me:", "JLangVis", JOptionPane.QUESTION_MESSAGE).toLowerCase();

		// StringTokenizer tokenizer = new StringTokenizer(question);

		if( question == null || question.equals("") )
			return;

		HashMap<Integer,AIPOS> posIndexMap = new HashMap<Integer,AIPOS>();

		for( AIPOS pos : posModelMap.keySet() ) {
			// Logger.log(""+pos.value()+" "+posModelMap.get(pos).getModel().getRepr());
			int index = question.indexOf(pos.value());
			if( index >= 0 ) {
				posIndexMap.put(index, pos);
				// Logger.log(""+ index + " : "+pos.value());
			}
		}

		ArrayList<Integer> idxs = new ArrayList<Integer>();
		for( Integer i:posIndexMap.keySet() )
			idxs.add(i);
		Collections.sort(idxs);

		String answer = "I do not know.";

		if( idxs.size() == 1 )
		{
			AIPOS posA = posIndexMap.get(idxs.get(0));
			Model A = posModelMap.get(posA).getModel();

			if( question.contains("where") )
			{
				answer = StringUtils.capitalize(posA.value())+ " is ";
				for( AIPOS posB : posModelMap.keySet() )
				{
					Model B = posModelMap.get(posB).getModel();
					if( B.equals(A) )
						continue;
					else
					{
						answer += ( A.isInFrontOf(B) ) ? "in front of the "+posB.value()+", " : "";
						answer += ( A.isBehind(B) ) ? "behind the "+posB.value()+", " : "";
						answer += ( A.isLeftOf(B) ) ? "on the left side of the "+posB.value()+", " : "";
						answer += ( A.isRightOf(B) ) ? "on the right side of the "+posB.value()+", " : "";
						answer += ( A.isAbove(B) ) ? "above the "+posB.value()+", " : "";
						answer += ( A.isBelow(B) ) ? "below the "+posB.value()+", " : "";
						answer += ( Oracle.isInside(A, B) ) ? "in the "+posB.value()+", " : "";
					}
				}
				if( !answer.equals(posA.value()+ " is ") )
				{
					answer = answer.substring(0, answer.length()-2);
					answer += ".";
				}
			}
		}
		else if( idxs.size() == 2 )
		{ 
			AIPOS posA = posIndexMap.get(idxs.get(0));
			AIPOS posB = posIndexMap.get(idxs.get(1));
			Model A = posModelMap.get(posA).getModel();
			Model B = posModelMap.get(posB).getModel();
			Logger.log("Checking A:"+A.getRepr()+", B:"+B.getRepr());

			if( question.contains("in front of") )
				answer = ( A.isInFrontOf(B) ) ? "Yes" : "No";
			else if( question.contains("behind") )
				answer = ( A.isBehind(B) ) ? "Yes" : "No";
			else if( question.contains("left of") )
				answer = ( A.isLeftOf(B) ) ? "Yes" : "No";
			else if( question.contains("right of") )
				answer = ( A.isRightOf(B) ) ? "Yes" : "No";
			else if( question.contains("above") )
				answer = ( A.isAbove(B) ) ? "Yes" : "No";
			else if( question.contains("below") )
				answer = ( A.isBelow(B) ) ? "Yes" : "No";
			else if( question.contains("see") ) {
				answer = ( Oracle.canSee(A, B, models) ) ? "Yes" : "No";
			}
			else if( question.contains("intersect") )
				answer = ( Oracle.intersects(A, B) ) ? "Yes" : "No";
			else if( question.contains("inside") || question.contains("in") )
				answer = ( Oracle.isInside(A, B) ) ? "Yes" : "No";

			Logger.log("Answer: "+answer);
		}
		else if( idxs.size() == 3 )
		{
			AIPOS posA = posIndexMap.get(idxs.get(0));
			AIPOS posB = posIndexMap.get(idxs.get(1));
			AIPOS posC = posIndexMap.get(idxs.get(2));
			Model A = posModelMap.get(posA).getModel();
			Model B = posModelMap.get(posB).getModel();
			Model C = posModelMap.get(posC).getModel();

			if( question.contains("between") )
				answer = (A.isBetween(B, C) ) ? "Yes" : "No";
		}

		JOptionPane.showMessageDialog(null, answer);

	}

	public void extractModels(String text) {

		if( Parser.getInstance() == null )
			Logger.log("null parser");
		else
			Logger.log("parser valid");

		/*
		// creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution 
	    Properties props = new Properties();
	    props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
	    StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

	    // read some text in the text variable
	    // String text = "Alice is in the room. She is sitting on the sofa. She was looking pale."; // Add your text here!

	    // create an empty Annotation just with the given text
	    Annotation document = new Annotation(text);

	    // run all Annotators on this text
	    pipeline.annotate(document);

	    // these are all the sentences in this document
	    // a CoreMap is essentially a Map that uses class objects as keys and has values with custom types
	    List<CoreMap> sentences = document.get(SentencesAnnotation.class);

	    for(CoreMap sentence: sentences) {
	      // traversing the words in the current sentence
	      // a CoreLabel is a CoreMap with additional token-specific methods
	      for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
	        // this is the text of the token
	        String word = token.get(TextAnnotation.class);
	        // this is the POS tag of the token
	        String pos = token.get(PartOfSpeechAnnotation.class);
	        // this is the NER label of the token
	        String ne = token.get(NamedEntityTagAnnotation.class);

	      }

	      // this is the parse tree of the current sentence
	      Tree tree = sentence.get(TreeAnnotation.class);

	      // this is the Stanford dependency graph of the current sentence
	      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
	    }

	    // This is the coreference link graph
	    // Each chain stores a set of mentions that link to each other,
	    // along with a method for getting the most representative mention
	    // Both sentence and token offsets start at 1!
	    Map<Integer, CorefChain> graph = document.get(CorefChainAnnotation.class);

	    for( Integer i : graph.keySet() ) {
	    	CorefChain cc = graph.get(i);
	    	Logger.log(cc.getRepresentativeMention().gender.toString());
	    	Logger.log("Printing mentions...");
	    	List<CorefMention> cms = cc.getCorefMentions();
	    	for( CorefMention cm : cms ) {
	    		Logger.log("Gender: "+cm.gender.toString());
	    		Logger.log("Mention ID: "+cm.mentionID);
	    		Logger.log("Mention Type: "+cm.mentionType);
	    		Logger.log("Mention Span:"+cm.mentionSpan);
	    	}
	    }

	    text = text.replace("alice", "girl");
		 */

		WordNetFinder wordNet = WordNetFinder.getInstance();
		ArrayList<AINoun> nouns = Parser.getInstance().parse(text.toLowerCase());
		for( AINoun noun : nouns ){
			int count = Parser.getInstance().parseNumber(noun.getCount());
			checkWNFirst(noun);

			AIModel aiModel = wordModelMap.get(noun.value());
			if( aiModel == null || !noun.getDeterminer().equals("the") ) {
				for(int i=0;i<count;i++) {
					Model mod = this.retrieveModel(noun.value(), noun.tag());
					if( mod != null ) {

						AIModel model = new AIModel(mod);
						model.setGround(this.getGround());
						// model.setGround( new AIModel( (Model)mod.getWorld().getChildAt(5) ) );

						// Model box = this.createAndAddBox();
						// model.setBox(box);

						/*
						for( int k = 0; k < mod.getWorld().getChildCount(); k++ ) {
						Logger.log(""+mod.getWorld().getChildAt(k).getRepr());
						}
						 */
						
						for( AIAdjective adj : noun.getModifiers() ) {
							// String id = WordNetFinder.getInstance().getSynsetID(adj.value(), adj.tag());
							ArrayList<String> ids = wordNet.getSynsetIDs(adj.value(), adj.tag());
							for( String id : ids ) {
								String property = wordNet.getSynsetProperty(id); 
								if(property!=null) Logger.log("Property: " + property);
								
								String modifier = SynsetRepository.getInstance().getModifier(id);
								Logger.log("modifier:"+modifier);
								if( modifier != null )
								{
									Logger.log("resizing "+model.getModel().getRepr());
									ModelModifier.getInstance().modify(model.getModel(), new Scale(modifier));
								}
							}

							/*
							String attribute = WordNetFinder.getInstance().getSynsetProperty(id);
							if( attribute != null ) {
								Logger.log("Attribute:"+attribute);
								String function = SynsetRepository.getInstance().getFunction(attribute);
								Logger.log("function:"+function);
								if( function != null )
								{
									Logger.log("resizing "+model.getRepr());
									ModelModifier.getInstance().modify(model, function);
								}
							}
							 */	
						}

						ModelPositioner.getInstance().placeOnGround(model);
						// AIModelPositionerWithSolver.getInstance().placeOnGround(model);

						posModelMap.put(noun, model);
						wordModelMap.put(noun.value(), model);
						models.add(model);

						// BubbleManager bm = new BubbleManager();
						// bm.setBubbles(new Bubble());

						// model.setupBox();
						// model.getModel().getAGoodLookAtRightNow(mod);
					}
					else {
						Logger.log("Model is null");
					}
				}
			}
			else if( noun.getDeterminer().equals("the") ) {
				Logger.log("using the old model");

				AIModel model = wordModelMap.get(noun.value());
				for( AIAdjective adj : noun.getModifiers() ) {
					// String id = WordNetFinder.getInstance().getSynsetID(adj.value(), adj.tag());
					ArrayList<String> ids = wordNet.getSynsetIDs(adj.value(), adj.tag());
					for( String id : ids ) {
						String modifier = SynsetRepository.getInstance().getModifier(id);
						Logger.log("modifier:"+modifier);
						if( modifier != null )
						{
							Logger.log("resizing "+model.getModel().getRepr());
							Scale scale = new Scale(modifier);
							ModelModifier.getInstance().modify(model.getModel(), model.getScale().inverse());
							ModelModifier.getInstance().modify(model.getModel(), scale);
							model.setScale(scale);
						}
					}
				}
			}
		}

		// HashMap<AIPOS,AIModel> posModelMap = new HashMap<AIPOS,AIModel>();
		for( AIPOS pos : posModelMap.keySet() ) {
			Logger.log(""+pos.value()+" "+posModelMap.get(pos).getModel().getRepr());
		}

		ArrayList<AIPreposition> preps = Parser.getInstance().getPreps();
		for( AIPreposition prep : preps ) {
			AIPOS Apos = prep.getSource();
			AIPOS Bpos = prep.getTarget();
			// AIModel A = posModelMap.get(Apos);
			// AIModel B = posModelMap.get(Bpos);
			AIModel A = wordModelMap.get(Apos.value());
			AIModel B = wordModelMap.get(Bpos.value());
			Preposition p = prep.getRelation();

			if( A == null )
				Logger.log("A is null! Value: "+Apos.value());
			if( B == null )
				Logger.log("B is null! Value: "+Bpos.value());

			if( A != null && B != null ) {				
				System.out.println(""+A.getModel().getRepr()+" "+prep.toString()+" "+B.getModel().getRepr());
				ModelPositioner.getInstance().place(A, B, p);
				// AIModelPositionerWithSolver.getInstance().place(A, B, p);
				// A.setupBox();
				// B.setupBox();
			}
		}
		/*
		boolean printPositive = true;
		boolean printNegative = false;

		for( int i = 0; i < models.size(); i++ )
		{
			for( int j = i+1; j < models.size(); j++ )
			{
				// if( i == j ) continue;

				// Logger.log("Checking i"+i+", j"+j);
				Model A = models.get(i).getModel();
				Model B = models.get(j).getModel();

				// check in_front_of relation
				if(A.isInFrontOf(B) && printPositive)
					Logger.log(""+A.getRepr()+" is in front of "+B.getRepr());
				else if( printNegative )
					Logger.log(""+A.getRepr()+" is not in front of "+B.getRepr());
				if(B.isInFrontOf(A) && printPositive)
					Logger.log(""+B.getRepr()+" is in front of "+A.getRepr());
				else if( printNegative )
					Logger.log(""+B.getRepr()+" is not in front of "+A.getRepr());

				// check behind relation
				if(A.isBehind(B) && printPositive)
					Logger.log(""+A.getRepr()+" is behind "+B.getRepr());
				else if( printNegative ) 
					Logger.log(""+A.getRepr()+" is not behind "+B.getRepr());
				if(B.isBehind(A) && printPositive)
					Logger.log(""+B.getRepr()+" is behind "+A.getRepr());
				else if( printNegative )
					Logger.log(""+B.getRepr()+" is not behind "+A.getRepr());

				// check left_of relation
				if(A.isLeftOf(B) && printPositive)
					Logger.log(""+A.getRepr()+" is on the left of "+B.getRepr());
				else if( printNegative )
					Logger.log(""+A.getRepr()+" is not on the left of "+B.getRepr());
				if(B.isLeftOf(A) && printPositive)
					Logger.log(""+B.getRepr()+" is on the left of "+A.getRepr());
				else if( printNegative )
					Logger.log(""+B.getRepr()+" is not on the left of "+A.getRepr());

				// check right_of relation
				if(A.isRightOf(B) && printPositive)
					Logger.log(""+A.getRepr()+" is on the right of "+B.getRepr());
				else if( printNegative )
					Logger.log(""+A.getRepr()+" is not on the right of "+B.getRepr());
				if(B.isRightOf(A) && printPositive)
					Logger.log(""+B.getRepr()+" is on the right of "+A.getRepr());
				else if( printNegative )
					Logger.log(""+B.getRepr()+" is not on the right of "+A.getRepr());

				// check above relation
				if(A.isAbove(B) && printPositive)
					Logger.log(""+A.getRepr()+" is above "+B.getRepr());
				else if( printNegative )
					Logger.log(""+A.getRepr()+" is not above "+B.getRepr());
				if(B.isAbove(A) && printPositive)
					Logger.log(""+B.getRepr()+" is above "+A.getRepr());
				else if( printNegative )
					Logger.log(""+B.getRepr()+" is not above "+A.getRepr());

				// check below relation
				if(A.isBelow(B) && printPositive)
					Logger.log(""+A.getRepr()+" is below "+B.getRepr());
				else if( printNegative )
					Logger.log(""+A.getRepr()+" is not below "+B.getRepr());
				if(B.isBelow(A) && printPositive)
					Logger.log(""+B.getRepr()+" is below "+A.getRepr());
				else if( printNegative )
					Logger.log(""+B.getRepr()+" is not below "+A.getRepr());
			}
		}
		 */

		/*
		AITokenizer tokenizer = new AITokenizer(sentence);
		ArrayList<String> list = tokenizer.getTokens();

		for( int i = 0; i < list.size(); i++ )
		{
			String replace = list.get(i);
			if( replace.contains(" ") )
			{
				this.retrieveModel(replace, "NN");
				sentence = sentence.replace( replace, "");
			}	
		}
		 */

		/*
		 * This was already commented out.
		ArrayList<String> list = WordNetFinder.getInstance().getNounPhrases( sentence );
		for(int i = 0; i < list.size(); i++ )
		{
			String replace = list.get(i);
			this.retrieveModel(replace, "NN");
			sentence = sentence.replace( replace, "");
		}
		Logger.log(list);
		 */

		/*
		Logger.log( tagger.tagString( sentence ) );
		ArrayList<TaggedWord> tagged = tagger.getTagged();
		// tagger.removeDeterminers(tagged); // remove determiners

		for( int i = 0; i < tagged.size(); i++ )
		{
			TaggedWord tw = tagged.get(i);
			String tag = tw.tag();
			String word = tw.word().toLowerCase();

			if( tag.contains("NN") )
				this.retrieveModel(word, tag);
		}
		 */

		// this.placeModels();
		// for(Model model:models) model.addAbsoluteTransformationListener(this);

		AIModel biggest = this.getBiggestModel();
		if( biggest != null ) {
			Timer timer = new Timer();
			timer.schedule( new PositionCameraTask( biggest.getModel(), this.getCamera() ), 2000 );
		}
		// AIModelPositionerWithSolver.getInstance().solveConstraintAndUpdatePosition(models);
		Logger.log("Done.");
	}

	public void getAGoodLookAtScene() {

		AIModel biggest = this.getBiggestModel();
		if( biggest != null ) {
			// Model first = (Model) authoringTool.getWorld().getChildAt(6);
			Model camera = this.getCamera();
			camera.getAGoodLookAtRightNow( biggest.getModel() );
		}
		// this.initWorld();
		// viewAll();
	}

	public Box getAllBoundingBox() {
		Box bm = new Box();
		for( AIModel m : models )
			bm.union( m.getBoundingBox() );
		return bm;
	}

	public void viewAll() {

		Model box = this.createAndAddBox();

		Box bm = new Box();
		for( AIModel m : models )
			bm.union( m.getBoundingBox() );

		Box bb = box.getBoundingBox( box.getWorld() );
		ModelModifier.getInstance().modify(box, new Scale(
				bm.getWidth()/bb.getWidth(),
				bm.getHeight()/bb.getHeight(),
				bm.getDepth()/bb.getDepth()
				));
		box.setPositionRightNow( bm.getCenter() );

		Timer timer = new Timer();
		timer.schedule( new PositionCameraTask(box,getCamera()), 3000 );
		timer.scheduleAtFixedRate( new PrintNameTask(), 0, 2000 );

		// (new DeleteRunnable( box, authoringTool )).run();
	}

	class PrintNameTask extends TimerTask {
		public void run() {
			Logger.log("emre");
		}
	}

	class PositionCameraTask extends TimerTask {

		private Model model, camera;

		public PositionCameraTask(Model m, Model cam) { 
			this.model = m; 
			this.camera = cam;
		}

		public void run() {
			camera.getAGoodLookAtRightNow(model);
		}
	}

	private Model retrieveModel(String word, String tag){
		Model model = null;
		// Logger.log("Word:"+word+", Tag:"+tag);

		boolean fetchFirst = false;

		String sid = WordNetFinder.getInstance().getSynsetID(word, tag);
		boolean included = SynsetRepository.getInstance().containsModel(sid);
		if( included && fetchFirst )
			return this.createAndAddWithID(sid);
		else
		{
			HashSet<String> asids = WordNetFinder.getInstance().getAvailableSynsetIDs(word, tag); // Logger.print( asids );

			// Logger.log("Asids.size: "+asids.size());

			ArrayList<String> availables = SynsetRepository.getInstance().getAvailableModels(asids); // Logger.print( availables );
			if( !availables.isEmpty() )	
			{
				int randomIndex = Randomizer.randomInRange( availables.size() );
				String synsetID = availables.get( randomIndex );
				model = this.createAndAddWithID( synsetID );
			}
			else
			{
				String message = "I cannot find an accurate model for \""+word+"\".\nDo you want me to search for something similar?";
				int answer = JOptionPane.showConfirmDialog(null, message, "JLangVis", JOptionPane.YES_NO_OPTION);
				if( answer == JOptionPane.YES_OPTION ) {
					ArrayList<String> list;
					ArrayList<String> availables2 = new ArrayList<String>();
					HashSet<String> asids2 = new HashSet<String>();
					while( availables2.isEmpty() ) {
						Logger.log("Checking parent.");
						list = new ArrayList<String>();

						if( asids2.isEmpty() )
							list.add(sid);
						else
							for( String s : asids2 ) 
								list.add(s);

						asids2 = WordNetFinder.getInstance().getCousins(list, null);
						availables2 = SynsetRepository.getInstance().getAvailableModels(asids2);
						if( !availables2.isEmpty() )	
						{
							int randomIndex = Randomizer.randomInRange( availables2.size() );
							String synsetID = availables2.get( randomIndex );
							model = this.createAndAddWithID( synsetID );
						}
						Logger.log("Checked parent.");
					}
				}
				else if( answer == JOptionPane.NO_OPTION ) {
					System.out.printf( "\"%s\" is not found in SynsetRepository.\n", word );	
				}

			}
		}
		return model;
	}

	// ArrayList<Model> models = new ArrayList<Model>();
	ArrayList<AIModel> models = new ArrayList<AIModel>();

	HashMap<String,AIModel> wordModelMap = new HashMap<String,AIModel>(); 
	HashMap<AIPOS,AIModel> posModelMap = new HashMap<AIPOS,AIModel>(); 

	public Model createAndAddBox() {
		File file = new File( "/Users/emre/Desktop/research/marks_story/gallery/Shapes/Box.a2c" ); // System.out.println( file.toString() );
		Model box = (Model) authoringTool.loadAndAddCharacter( file );
		// box.setPropertyNamed("fillingStyle", FillingStyle.WIREFRAME);
		Box bb = box.getBoundingBox( box );
		box.moveRightNow( Direction.UP, bb.getHeight(), box );
		// box.turnRightNow( Direction.BACKWARD, Math.PI/2, box );
		return box;
	}

	private ObjectArrayList<CollisionShape> collisionShapes = new ObjectArrayList<CollisionShape>();
	private BroadphaseInterface broadphase;
	private CollisionDispatcher dispatcher;
	private ConstraintSolver solver;
	private DefaultCollisionConfiguration collisionConfiguration;
	private DiscreteDynamicsWorld dynamicsWorld;

	public void initWorld() {

		// collision configuration contains default setup for memory, collision setup
		collisionConfiguration = new DefaultCollisionConfiguration();
		// use the default collision dispatcher. For parallel processing you can use a diffent dispatcher (see Extras/BulletMultiThreaded)
		dispatcher = new CollisionDispatcher(collisionConfiguration);
		broadphase = new DbvtBroadphase();
		// the default constraint solver. For parallel processing you can use a different solver (see Extras/BulletMultiThreaded)
		SequentialImpulseConstraintSolver sol = new SequentialImpulseConstraintSolver();
		solver = sol;

		dynamicsWorld = new DiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
		dynamicsWorld.setGravity(new Vector3f(0f, -10f, 0f));

		// Added by emre
		Model ground = this.getGround().getModel();
		// ground.setTransformationRightNow( GeomUtils.aliceTransformIdentity(), getWorld());
		// ground.setOrientationRightNow(getWorld());
		Box bb = ground.getBoundingBox( getWorld() ); 
		Vector3d bo = bb.getCenter();
		Vector3f bs = new Vector3f( (float)bb.getWidth()/2, (float)bb.getHeight()/2, (float)bb.getDepth()/2 );

		// Vector3d boxOriginShifted = new Vector3d( bo.x, bo.y - 0.5, bo.z );

		Logger.log(""+bs);
		Logger.log(""+AIVector.toVector3f(bo));

		// create a few basic rigid bodies
		CollisionShape groundShape = new BoxShape( bs );
		// CollisionShape groundShape = new StaticPlaneShape(new Vector3f(0, 1, 0), 0);

		collisionShapes.add(groundShape);

		Transform groundTransform = new Transform();
		groundTransform.setIdentity();
		// groundTransform.origin.set( AIVector.toVector3f(bo) );
		// groundTransform.set( GeomUtils.transformAliceToBullet( ground.getTransformation(getWorld()) ) );

		// Transform t = GeomUtils.transformAliceToBullet( getWorld().getSceneGraphReferenceFrame().getAbsoluteTransformation() );
		// Transform groundTr = GeomUtils.transformAliceToBullet( ground.getTransformation( getWorld() ) );
		// t.mul(groundTr);

		Transform t = GeomUtils.transformAliceToBullet(ground.getTransformation(getWorld()));
		groundTransform.set( t );

		{
			float mass = 0f;

			// rigidbody is dynamic if and only if mass is non zero, otherwise static
			boolean isDynamic = (mass != 0f);

			Vector3f localInertia = new Vector3f(0, 0, 0);
			if (isDynamic) {
				groundShape.calculateLocalInertia(mass, localInertia);
			}

			// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
			DefaultMotionState myMotionState = new DefaultMotionState(groundTransform);
			RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, groundShape, localInertia);
			RigidBody body = new RigidBody(rbInfo);

			// add the body to the dynamics world
			dynamicsWorld.addRigidBody(body);
		}

		//  {
		//  addBox();
		//  }

		// edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.RenderContext rc = ((edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.OnscreenRenderTarget)authoringTool.getJAliceFrame().getSceneEditor().getCameraViewPanel().getRenderTarget()).getContext();
		// dynamicsWorld.setDebugDrawer(new GLDebugDrawer((IGL) rc.gl));
		clientResetScene();
	}

	public void addBox() {

		simulating = false;

		Model box = this.createAndAddBox();
		box.syncLocalTransformationPropertyToSceneGraph();
		// box.setTransformationRightNow( GeomUtils.aliceTransformIdentity(), getWorld());
		// box.setOrientationRightNow(getWorld());
		// box.turnRightNow( Direction.UP, Math.PI/4, getWorld() );
		// box.rotateRightNow( Vector3.Z_AXIS, Math.PI/6, getWorld());
		Box bbox = box.getBoundingBox( getWorld() );
		Vector3d boxOrigin = bbox.getCenter();
		Vector3f boxSize = new Vector3f( (float)bbox.getWidth()/2, (float)bbox.getHeight()/2, (float)bbox.getDepth()/2 );
		Logger.log(""+boxSize);
		Logger.log(""+AIVector.toVector3f(boxOrigin));

		// create a few dynamic rigidbodies
		// Re-using the same collision is better for memory usage and performance

		CollisionShape colShape = new BoxShape( boxSize );
		//CollisionShape colShape = new SphereShape(1f);
		collisionShapes.add(colShape);

		// Create Dynamic Objects
		float mass = 1f;

		// rigidbody is dynamic if and only if mass is non zero, otherwise static
		boolean isDynamic = (mass != 0f);

		Vector3f localInertia = new Vector3f(0, 0, 0);
		if (isDynamic) {
			colShape.calculateLocalInertia(mass, localInertia);
		}

		Transform startTransform = new Transform();
		startTransform.setIdentity();

		// Transform t = GeomUtils.transformAliceToBullet( getWorld().getSceneGraphReferenceFrame().getAbsoluteTransformation() );
		// Transform boxTr = GeomUtils.transformAliceToBullet( box.getTransformation( getWorld() ) );
		// t.mul(boxTr);
		Transform t = GeomUtils.transformAliceToBullet(box.getTransformation(getWorld()));

		// startTransform.set( GeomUtils.transformAliceToBullet( box.getTransformation(getWorld()) ) );
		// startTransform.origin.set( AIVector.toVector3f(boxOrigin) );

		startTransform.set( t );

		// using motionstate is recommended, it provides interpolation capabilities, and only synchronizes 'active' objects
		DefaultMotionState myMotionState = new DefaultMotionState(startTransform);
		RigidBodyConstructionInfo rbInfo = new RigidBodyConstructionInfo(mass, myMotionState, colShape, localInertia);
		RigidBody body = new RigidBody(rbInfo);
		body.setUserPointer(box);
		body.setActivationState(RigidBody.ISLAND_SLEEPING);

		dynamicsWorld.addRigidBody(body);
		// body.setActivationState(RigidBody.ISLAND_SLEEPING);

		clientResetScene();

		simulating = true;
	}

	public void clientResetScene() {
		//#ifdef SHOW_NUM_DEEP_PENETRATIONS
		BulletStats.gNumDeepPenetrationChecks = 0;
		BulletStats.gNumGjkChecks = 0;
		//#endif //SHOW_NUM_DEEP_PENETRATIONS

		int numObjects = 0;
		if (dynamicsWorld != null) {
			dynamicsWorld.stepSimulation( 1f / 60f, 0 );
			numObjects = dynamicsWorld.getNumCollisionObjects();
		}

		for (int i = 0; i < numObjects; i++) {
			CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
			RigidBody body = RigidBody.upcast(colObj);
			if (body != null) {
				if (body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					// myMotionState.graphicsWorldTrans.set(myMotionState.startWorldTrans);
					// myMotionState.graphicsWorldTrans.set( GeomUtils.transformAliceToBullet( Matrix44.invert( getCamera().getTransformation() ) ) );

					// Transform t = GeomUtils.transformAliceToBullet( getWorld().getSceneGraphScene().getTransformation( getWorld().getSceneGraphScene() ) );
					// myMotionState.startWorldTrans.mul(t);
					myMotionState.graphicsWorldTrans.set(myMotionState.startWorldTrans);
					// myMotionState.graphicsWorldTrans.set(t);
					colObj.setWorldTransform(myMotionState.graphicsWorldTrans);
					colObj.setInterpolationWorldTransform(myMotionState.startWorldTrans);
					colObj.activate();
				}
				// removed cached contact points
				dynamicsWorld.getBroadphase().getOverlappingPairCache().cleanProxyFromPairs(colObj.getBroadphaseHandle(), dynamicsWorld.getDispatcher());

				body = RigidBody.upcast(colObj);
				if (body != null && !body.isStaticObject()) {
					RigidBody.upcast(colObj).setLinearVelocity(new Vector3f(0f, 0f, 0f));
					RigidBody.upcast(colObj).setAngularVelocity(new Vector3f(0f, 0f, 0f));
				}
			}
		}
	}

	protected Clock clock = new Clock();

	public float getDeltaTimeMicroseconds() {
		float dt = clock.getTimeMicroseconds();
		clock.reset();
		return dt;
	}

	boolean simulating = false;

	class UpdateWorldTask extends TimerTask {
		public void run() {

			if( simulating ) {
				float ms = getDeltaTimeMicroseconds();
				// step the simulation
				if (dynamicsWorld != null)
					dynamicsWorld.stepSimulation(ms / 1000000f);
				else
					Logger.log("null world!");
				renderme();
				refresh();
			}
		}
	}

	Timer timer = new Timer();
	public void startWorld() {
		timer.scheduleAtFixedRate( new UpdateWorldTask(), 0, (long) (1000.0f/60.0f) );
	}

	private final Transform m = new Transform();
	public void renderme() {
		if (dynamicsWorld != null) {
			int numObjects = dynamicsWorld.getNumCollisionObjects();
			for (int i = 0; i < numObjects; i++) {
				CollisionObject colObj = dynamicsWorld.getCollisionObjectArray().getQuick(i);
				RigidBody body = RigidBody.upcast(colObj);

				if (body != null && body.getMotionState() != null) {
					DefaultMotionState myMotionState = (DefaultMotionState) body.getMotionState();
					m.set(myMotionState.graphicsWorldTrans);

					if( body.getUserPointer() instanceof Model ) {
						// Logger.log("update");
						Model box = (Model) body.getUserPointer();

						// float halfH = (float)box.getBoundingBox(box.getWorld()).getHeight()/2;						

						// Vector3f o = m.origin;
						// Vector3d newPos = new Vector3d( o.x, o.y, o.z );
						// Logger.log(""+newPos);
						// box.setPositionRightNow( newPos, box.getWorld() );
						// box.setTransformationRightNow(null, box);

						// box.setTransformationRightNow( GeomUtils.transformBulletToAlice(m), getWorld());
						// box.setAbsoluteTransformationRightNow( GeomUtils.transformBulletToAlice(m) );
						// box.setLocalTransformationRightNow( GeomUtils.transformBulletToAlice(m) );

						// box.setTransformationRightNow( GeomUtils.transformBulletToAlice(m), getWorld() );
						box.setAbsoluteTransformationRightNow(GeomUtils.transformBulletToAlice(m));

						// Matrix3f r = m.basis;
						// box.setOrientationRightNow( AIMatrix.toMatrix3d(r) );
						// box.setTransformationRightNow( m, getWorld());
					}

				}
				else {
					Logger.log("update");
					colObj.getWorldTransform(m);
				}
			}
		}
	}


	public void refresh() {
		authoringTool.getJAliceFrame().getSceneEditor().refresh();
		// edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.RenderContext rc = 
		// ((edu.cmu.cs.stage3.alice.scenegraph.renderer.joglrenderer.OnscreenRenderTarget)authoringTool.getJAliceFrame().getSceneEditor().getCameraViewPanel().getRenderTarget()).getContext();
		// dynamicsWorld.setDebugDrawer(new GLDebugDrawer(rc.gl));
		// cameraViewPanel.renderTarget.getAWTComponent().repaint();
	}

	public Model createAndAddWithID(String id){

		String filename = SynsetRepository.getInstance().getModelFilePath( id ); 
		File file = new File( filename ); // System.out.println( file.toString() );
		Model model = (Model) authoringTool.loadAndAddCharacter( file );

		/*
		URL url = null;
		try { url = new URL( filename ); }
		catch (MalformedURLException e) { e.printStackTrace(); }
		Model model = (Model) authoringTool.loadAndAddCharacter(url);
		 */

		// authoringTool.getWorld().addCollisionManagementFor(model);
		// model.addAbsoluteTransformationListener(this);
		// authoringTool.getWorld().removeChild(model);
		// authoringTool.getWorld().sandboxes.remove(model);

		// models.add(new AIModel(model));

		// nameModelMap.put(model.getRepr(), model);
		model.setPropertyNamed( "isBoundingBoxShowing", Boolean.TRUE );
		if( model.isBoundingBoxShowing.booleanValue() ) {
			Logger.log("yes showing.");
		}
		// model.setSizeRightNow( model.getWidth()*2, model.getHeight()*2, model.getDepth()*2 );
		return model;
	}

	public AIModel getBiggestModel() {
		Collections.sort( models, Collections.reverseOrder() );
		AIModel biggest = ( models.size() > 0 ) ? models.get(0) : null;
		return biggest;
	}

	public AIModel getSmallestModel() { 
		Collections.sort( models );
		AIModel smallest = ( models.size() > 0 ) ? models.get(0) : null;
		return smallest;
	}

	public AIRect boundingRect() {
		
		AIModel2D ground2D = new AIModel2D(ground);
		AIPoint o = ground2D.topLeft();
		
		ArrayList<Integer> topLeftX = new ArrayList();
		ArrayList<Integer> topLeftY = new ArrayList();
		ArrayList<Integer> topRightX = new ArrayList();
		ArrayList<Integer> topRightY = new ArrayList();
		ArrayList<Integer> bottomLeftX = new ArrayList();
		ArrayList<Integer> bottomLeftY = new ArrayList();
		ArrayList<Integer> bottomRightX = new ArrayList();
		ArrayList<Integer> bottomRightY = new ArrayList();
		
		for( AIModel model : models ) {
			AIModel2D model2D = new AIModel2D(model);
			model2D.setOrigin(o);
			
			AIPoint topLeft = model2D.topLeft();
			AIPoint topRight = model2D.topRight();
			AIPoint bottomLeft = model2D.bottomLeft();
			AIPoint bottomRight = model2D.bottomRight();
			
			topLeftX.add((int)topLeft.x);
			topLeftY.add((int)topLeft.y);
			
			topRightX.add((int)topRight.x);
			topRightY.add((int)topRight.y);
			
			
			
		}
		
		return null;
	}
	
	public AISize boundingSize(AIModel model) {
		double w = model.width();
		double h = model.depth();
		Logger.log(String.format("Dimensions for Model: %s, Width: %.2f, Height: %.2f",model.name(),w,h));
		w = Math.max(w, h);
		h = w;
		Logger.log(String.format("Tile size for Model: %s, Width: %.2f, Height: %.2f",model.name(),w,h));
		return new AISize(w,h);
	}

	public AISize tileSizeForMap() {
		AIModel smallest = this.getSmallestModel();
		return this.boundingSize(smallest);
	}

	public static void log(String s) {
		System.out.print(s);
	}

	public void printArray(int[][] map) {
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				log("" + map[i][j] + " ");
			}
			log("\n");
		}
		log("\n");
	}

	public void writeArray(int[][] map) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("map.txt")));
		} catch (FileNotFoundException e) { e.printStackTrace(); }
		
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				try { writer.append( String.format("%d ", map[i][j]) ); } 
				catch (IOException e) { e.printStackTrace(); }
			}
			try { writer.append( String.format("\n") ); } 
			catch (IOException e) { e.printStackTrace(); }
		}
		try { writer.close(); } 
		catch (IOException e) { e.printStackTrace(); }
	}
	
	public AIModel modelNamed(String name) {
		for( AIModel model : models )
			if( model.name().equals(name) )
				return model;
		
		return null;
	}

	public void makeMap() {

		AIModel ground = this.getGround();
		double w = ground.width();
		double h = ground.depth();

		AISize tileSize = this.tileSizeForMap();
		int x = (int) Math.ceil( w / tileSize.width );
		int y = (int) Math.ceil( h / tileSize.height );

		Logger.log(String.format("Dimensions for Model: %s, Width: %.2f, Height: %.2f",ground.name(),w,h));
		Logger.log(String.format("Tile size for Model: %s, X: %d, Y: %d",ground.name(),x,y));

		int map[][] = new int[x][y];
		// int map[][] = new int[10][10];

		AIModel2D ground2D = new AIModel2D(ground);
		ground2D.printInfo();
		AIPoint o = ground2D.topLeft();

		// AIModel smallest = this.getSmallestModel();
		AIModel modelA = wordModelMap.get("penguin");
		AIModel modelB = wordModelMap.get("dog");
		
		AIModel2D modelA2D = new AIModel2D(modelA);
		modelA2D.setOrigin(o);
		AIModel2D modelB2D = new AIModel2D(modelB);
		modelB2D.setOrigin(o);
		
		for( AIModel model : models ) {
			
			if( model.equals(modelA) || model.equals(modelB) ) {
				Logger.log("model found: "+model.name());
				continue;
			} 
			
			AIModel2D model2D = new AIModel2D(model);
			model2D.setOrigin(o);
			model2D.printInfo();
			
			AIRect r = model2D.tileRectForSize(tileSize);
			// AIRect r = new AIRect( 3, 2, 4, 5 );
			Logger.log(""+r);

			int sx = (int) r.origin.x;
			int ex = sx + ((int)r.size.width);
			int sy = (int) r.origin.y;
			int ey = sy + ((int)r.size.height);

			for ( int i = sx; i < ex; i++ )
				for ( int j = sy; j < ey; j++ )
					map[j][i] = 1;

		}
		
		AIPoint s = modelA2D.tileRectForSize(tileSize).origin;
		AIPoint e = modelB2D.tileRectForSize(tileSize).origin;
		
		AStar astar = new AStar(map);
		astar.calculatePath(s.toPoint(),e.toPoint());
		
		ArrayList<ShortestPathStep> path = astar.getShortestPath();
		
		MoveAnimation moveAnim = new MoveAnimation(modelA, path, tileSize, authoringTool);
		moveAnim.run();
		
		/*
		Logger.log("\nStart: "+s+"\n");
		for ( ShortestPathStep step : path ) {
			Point pos = step.getPosition();
			Logger.log( String.format("Step: [x:%d y:%d]",pos.x,pos.y) );
		}
		Logger.log("\nEnd: "+e+"\n");
		// this.printArray(map);
		// this.writeArray(map);
		
		// return map; 
		
		Point prev = s.toPoint();
		
		// timer.schedule( new PositionCameraTask(box,getCamera()), 3000 );
		
		for ( ShortestPathStep step : path ) {
			
			Point pos = step.getPosition();
			Point diff = new Point( pos.x - prev.x, pos.y - prev.y );
			Logger.log("Diff: "+diff.toString());
			// AIPoint tp = new AIPoint( o.x + (pos.x * tileSize.width), o.y + (pos.y * tileSize.height ) );
			// Vector3 v = new Vector3(tp.x, modelA.position().y ,tp.y);
			// modelA.setPosition(v);
			
			double amount = tileSize.width;
			Direction direction = Direction.FORWARD;
			
			if( diff.y == -1 )
				direction = Direction.BACKWARD;
			// modelA.getModel().moveRightNow(Direction.BACKWARD, tileSize.width );
			
			if( diff.y == 1 )
				direction = Direction.FORWARD;
			// modelA.getModel().moveRightNow(Direction.FORWARD, tileSize.width );
			
			if( diff.x == -1 )
				direction = Direction.RIGHT;
			// modelA.getModel().moveRightNow(Direction.LEFT, tileSize.width );
			
			if( diff.x == 1 )
				direction = Direction.LEFT;
			// modelA.getModel().moveRightNow(Direction.RIGHT, tileSize.width );
			
			modelA.getModel().moveRightNow( direction, amount );
			
			this.frame.getSceneEditor().refresh();
			this.frame.getSceneEditor().repaint();
			this.frame.getSceneEditor().getRenderPanel().repaint();
			
			try { Thread.sleep(100); } 
			catch (InterruptedException e1) { e1.printStackTrace(); }
			
			// edu.cmu.cs.stage3.alice.core.response.Wait wait = new edu.cmu.cs.stage3.alice.core.response.Wait();
			// wait.duration.set( new Double( .2 ) );
			// authoringTool.performOneShot(wait, wait, null);
			
			prev = pos;
		}
		*/
	}

	public void calculatePath(Vector3 start, Vector3 end) {

		// Point s = new Point((int)start.x,(int)start.z);
		// Point e = new Point((int)end.x,(int)end.z);

	}

	public void showBirdsEyeView() {
		
		this.makeMap();


		// Logger.log("showing birds eye");
		// AIDrawFrame frame = new AIDrawFrame();
		// frame.setVisible(true);
		/*
		AIRect r = new AIRect(100,100);
		r.setColor(Color.blue);
		frame.addDrawable(r);
		 */
		/*
		AIRect gr = new AIRect(512,512);
		gr.setCenter(new AIPoint(256,256));
		gr.setColor(Color.green.darker());
		frame.addDrawable(gr);

		for (AIModel model : this.models) {
			// Box b = this.getAllBoundingBox();
			// AIRect r = new AIRect(2*b.getWidth(),2*b.getHeight());
			// r.setPosition(new Point((int)b.getCenter().x,(int)b.getCenter().z));
			// AIRect rect = model.getDrawRectWithReferenceRect(r);
			// AIRect rect = model.getDrawRectWithReferenceBox(this.getAllBoundingBox());
			AIRect rect = model.getDrawRect();
			// rect.setPosition(new Point(256,256));
			rect.setColor(Color.red);
			Logger.log(rect.toString());
			frame.addDrawable(rect);
		}
		frame.getDrawPanel().refresh();
		*/
	}

	public void deleteModels(){
		// for( Model model : models ) model.removeAbsoluteTransformationListener(this);
		for( AIModel model : models ) {
			new DeleteRunnable( model.getModel(), authoringTool ).run();
		}

		while( !models.isEmpty() ) 
			models.remove(0);
		models =  new ArrayList<AIModel>();
		// nameModelMap = new HashMap<String,Model>();
		Logger.log("Models size:"+models.size());

		posModelMap = new HashMap<AIPOS,AIModel>();
		wordModelMap = new HashMap<String,AIModel>(); 

		// Timer timer = new Timer();
		// timer.scheduleAtFixedRate( new PrintNameTask(), 0, 2000 );
	}

	boolean isCollision(edu.cmu.cs.stage3.math.Sphere sphereA, edu.cmu.cs.stage3.math.Sphere sphereB ){
		Vector3d relPos = Vector3.subtract(sphereA.getCenter(), sphereB.getCenter());
		double dist = Vector3.dotProduct(relPos, relPos);
		double minDist = sphereA.getRadius() + sphereB.getRadius();
		return ( dist <= (minDist * minDist) );
	}

	boolean isCollision( edu.cmu.cs.stage3.math.Box boxA, edu.cmu.cs.stage3.math.Box boxB ){
		Vector3d lowerA = boxA.getMinimum(); Vector3d upperA = boxA.getMaximum();
		Vector3d lowerB = boxB.getMinimum(); Vector3d upperB = boxB.getMaximum();
		return ( upperA.x > lowerB.x && upperB.x > lowerA.x && upperA.y > lowerB.y && upperB.y > lowerA.y && upperA.z > lowerB.z && upperB.z > lowerA.z ); // both boxes are axis aligned
	}

	public World getWorld() {
		return authoringTool.getWorld();
	}

	public Model getCamera() {
		return (Model)authoringTool.getWorld().getChildAt(2);
	}

	private AIModel ground;
	public AIModel getGround() {
		if( ground == null ) {
			ground = new AIModel( (Model)authoringTool.getWorld().getChildAt(5) );
			ground.getModel().resizeRightNow(0.0625);
		}
		return ground;
	}
	/*
	public Model getAliceGround() {
		return (Model)authoringTool.getWorld().getChildAt(5);
	}
	 */

	/*
	public void placeModels() {

		for( Model modelS : models )
		{
			for( Model modelT : models )
			{
				if( !modelT.equals(modelS) )
				{
					edu.cmu.cs.stage3.math.Box boxA = modelS.getBoundingBox( modelS.getWorld() );
					edu.cmu.cs.stage3.math.Box boxB = modelT.getBoundingBox( modelT.getWorld() );

					int count = 0;
					while( this.isCollision( boxA, boxB ) )
					{
						// Logger.log("Collision: "+modelS.getRepr()+" and "+modelT.getRepr()+"in place");	
						if( boxA.volume() > boxB.volume() )
							modelS.moveRightNow( Direction.BACKWARD, boxB.getDepth() );
						else
							modelT.moveRightNow( Direction.BACKWARD, boxA.getDepth() );

						boxA = modelS.getBoundingBox( modelS.getWorld() );
						boxB = modelT.getBoundingBox( modelT.getWorld() );

						if( count++ == 10 ) break; // Logger.log("Replacing...");
					}	
				}
			}
		}
	}
	public void absoluteTransformationChanged(AbsoluteTransformationEvent event) {

		Transformable t = (Transformable)event.getSource();
		edu.cmu.cs.stage3.alice.core.Model modelS = (edu.cmu.cs.stage3.alice.core.Model) t.getBonus();

		for( Model modelT : models )
		{
			if( !modelT.equals(modelS) )
			{
				edu.cmu.cs.stage3.math.Box boxA = modelS.getBoundingBox( modelS.getWorld() );
				edu.cmu.cs.stage3.math.Box boxB = modelT.getBoundingBox( modelT.getWorld() );
				if( this.isCollision( boxA, boxB ) ) 
				{
					Logger.log("Collision: "+modelS.getRepr()+" and "+modelT.getRepr());	
					if( boxA.volume() > boxB.volume() )
						modelS.moveRightNow( Direction.BACKWARD, boxB.getDepth() );
					else
						modelT.moveRightNow( Direction.BACKWARD, boxA.getDepth() );

					boxA = modelS.getBoundingBox( modelS.getWorld() );
					boxB = modelT.getBoundingBox( modelT.getWorld() );

					Logger.log("Replacing...");
				}
			}
		}
	}
	 */

	@Override
	public void absoluteTransformationChanged(
			AbsoluteTransformationEvent absoluteTransformationEvent) {
		// TODO Auto-generated method stub

	}

}
