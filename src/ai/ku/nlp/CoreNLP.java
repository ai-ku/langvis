package ai.ku.nlp;

import java.util.List;
import java.util.Properties;

import ai.ku.util.Logger;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.NamedEntityTagAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;
import edu.stanford.nlp.trees.TreeCoreAnnotations.TreeAnnotation;
import edu.stanford.nlp.trees.semgraph.SemanticGraph;
import edu.stanford.nlp.trees.semgraph.SemanticGraphCoreAnnotations.CollapsedCCProcessedDependenciesAnnotation;
import edu.stanford.nlp.util.CoreMap;

public class CoreNLP {

	private static CoreNLP corenlp = null;
	private Annotation document = null;
	private StanfordCoreNLP pipeline = null;
	
	// Singleton
	public static CoreNLP getInstance(){
		if( corenlp == null )
			corenlp = new CoreNLP();
		return corenlp;
	}

	public static void init(){
		CoreNLP.getInstance();
	}

	private CoreNLP(){
		 Properties props = new Properties();
		 props.put("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		 pipeline = new StanfordCoreNLP(props);
	}
	
	public void process(String text) {
		
		// create an empty Annotation just with the given text
	    document = new Annotation(text);
	    
	    // run all Annotators on this text
	    pipeline.annotate(document);
	    
	    List<CoreMap> sentences = document.get( SentencesAnnotation.class );
	    
	    for( CoreMap sentence : sentences ) {
	    	
	    	Tree tree = sentence.get( TreeAnnotation.class );
	    	
	    	TreebankLanguagePack tlp = new PennTreebankLanguagePack();
			GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
			GrammaticalStructure gs = gsf.newGrammaticalStructure( tree );
			List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();
			
			// System.out.println(tdl);
			// System.out.println();
			TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
		    tp.printTree( tree );
		    
			AISentence lastSentence = new AISentence();
			lastSentence.construct( tdl );
			
	    }
	    
	    
	    
	    /*
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
		        Logger.log(""+token.value()+", NamedEntity : "+ne);
		        Logger.log(""+token.value()+", TextAnnotation : "+word);
		        Logger.log(""+token.value()+", POS : "+pos);
		      }

		      // this is the parse tree of the current sentence
		      Tree tree = sentence.get(TreeAnnotation.class);

		      // this is the Stanford dependency graph of the current sentence
		      SemanticGraph dependencies = sentence.get(CollapsedCCProcessedDependenciesAnnotation.class);
		}
	    */
	}
	
}
