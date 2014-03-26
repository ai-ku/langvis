package ai.ku.nlp;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import ai.ku.util.Logger;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.objectbank.TokenizerFactory;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.trees.GrammaticalStructure;
import edu.stanford.nlp.trees.GrammaticalStructureFactory;
import edu.stanford.nlp.trees.PennTreebankLanguagePack;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreePrint;
import edu.stanford.nlp.trees.TreebankLanguagePack;
import edu.stanford.nlp.trees.TypedDependency;

public class Parser {

	private String trainedModelPath = "grammar/englishPCFG.ser.gz";
	private static Parser parser = null;
	private AISentence lastSentence = null;
	private LexicalizedParser lp = null;

	// Singleton
	public static Parser getInstance(){
		if( parser == null )
			parser = new Parser();
		return parser;
	}

	public static void init(){
		Parser.getInstance();
	}

	private Parser(){
		lp = new LexicalizedParser(trainedModelPath);
		if( lp == null )
			Logger.log("lex parser null");
		else
			Logger.log("lex parser valid");
	}
	
	public int parseNumber(String number) {
		
		if(number == null) return 1;

		int num = 1;
		try { 
			num = Integer.parseInt(number);
		} 
		catch( Exception e ) { 
			
			if( number.equals("one") )
				num = 1;
			else if( number.equals("two") )
				num = 2;
			else if( number.equals("three") )
				num = 3;
			else if( number.equals("four") )
				num = 4;
			else if( number.equals("five") )
				num = 5;
			else if( number.equals("six") )
				num = 6;
			else if( number.equals("seven") )
				num = 7;
			else if( number.equals("eight") )
				num = 8;
			else if( number.equals("nine") )
				num = 9;
			else if( number.equals("ten") )
				num = 10;
		}
		return num;
	}
	
	public ArrayList<AINoun> parse(String text) {

		/*
	    // This option shows parsing a list of correctly tokenized words
	    String[] sent = { "A", "young", "girl", "stood", "next", "to", "the", "car", "." };
	    List<CoreLabel> rawWords = new ArrayList<CoreLabel>();
	    for (String word : sent) {
	      CoreLabel l = new CoreLabel();
	      l.setWord(word);
	      rawWords.add(l);
	    }
	    Tree parse = lp.apply(rawWords);
	    parse.pennPrint();
	    System.out.println();
		*/

		// This option shows loading and using an explicit tokenizer
		System.out.println(text);
		TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer.factory(new CoreLabelTokenFactory(), "");
		List<CoreLabel> rawWords2 = tokenizerFactory.getTokenizer(new StringReader(text)).tokenize();
		Tree parse = lp.apply(rawWords2);

		TreebankLanguagePack tlp = new PennTreebankLanguagePack();
		GrammaticalStructureFactory gsf = tlp.grammaticalStructureFactory();
		GrammaticalStructure gs = gsf.newGrammaticalStructure(parse);
		List<TypedDependency> tdl = gs.typedDependenciesCCprocessed();

		// System.out.println(tdl);
		// System.out.println();
		TreePrint tp = new TreePrint("penn,typedDependenciesCollapsed");
	    tp.printTree(parse);

		lastSentence = new AISentence();
		lastSentence.construct(tdl);
		return lastSentence.getNouns();
	}
	
	public ArrayList<AIPreposition> getPreps(){
		return lastSentence.getPreps();
	}
}
