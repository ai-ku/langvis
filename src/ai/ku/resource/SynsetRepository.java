package ai.ku.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import ai.ku.main.Config;
import ai.ku.util.Randomizer;

public class SynsetRepository {

	// This is the path where 'Synset2Model.xml' file is located
	// private static final String repositoryPath = "/Users/emre/Desktop/research/marks_story/Dictionary/";
	// private static final String galleryPath = "/Users/emre/Desktop/research/marks_story/gallery/";
	// private static final String galleryPath = "http://www.alice.org/gallery/";
	// private static final String galleryPath = "http://www.ku.edu.tr/sites/ai.ku.edu.tr/files/gallery/";
	
	private Hashtable<String, ArrayList<String>> synsetModelMap;
	private Hashtable<String, String> synsetFunctionMap;
	private Hashtable<String, String> synsetModifierMap;
	private static SynsetRepository repo = null;

	// Singleton
	public static SynsetRepository getInstance(){
		if( repo == null )
			repo = new SynsetRepository();
		return repo;
	}

	public static void init(){
		SynsetRepository.getInstance();
	}

	private SynsetRepository(){

		String fileName = Config.MODEL_SYNSET_MAP_PATH + "Synset2Object.xml";
		File file = new File( fileName );

		// Read 'Synset2Model.xml' file
		SynsetRepositoryReader reader = new SynsetRepositoryReader( file );

		// Store SynsetIDs and ModelMetaDataFileNames in a HashTable to reach quickly
		synsetModelMap = reader.getElements( "Synset" );
		
		synsetFunctionMap = new Hashtable<String,String>();
		synsetFunctionMap.put("SID-05098942-N", "makeBig");
		synsetFunctionMap.put("SID-05002352-N", "makeTall");
				
		synsetModifierMap = new Hashtable<String,String>();
		synsetModifierMap.put("SID-01383582-A", "[3.0,3.0,3.0]"); // astronomical
		synsetModifierMap.put("SID-01385773-A", "[2.5,2.5,2.5]"); // giant
		synsetModifierMap.put("SID-01387319-A", "[1.5,1.5,1.5]"); // huge
		synsetModifierMap.put("SID-01382086-A", "[1.2,1.2,1.2]"); // big
		synsetModifierMap.put("SID-02295998-A", "[1.0,1.0,1.0]"); // standard
		synsetModifierMap.put("SID-01391351-A", "[0.8,0.8,0.8]"); // small
		synsetModifierMap.put("SID-01392249-A", "[0.5,0.5,0.5]"); // tiny
		synsetModifierMap.put("SID-01393483-A", "[0.25,0.25,0.25]"); // infinitesimal
		
		synsetModifierMap.put("SID-02385102-A", "[1.0,1.1,1.0]"); // tall
		synsetModifierMap.put("SID-02386612-A", "[1.0,0.9,1.0]"); // short
		
		synsetModifierMap.put("SID-00986027-A", "[1.3,1.0,1.3]"); // fat
		synsetModifierMap.put("SID-00988232-A", "[0.8,1.0,0.8]"); // thin
	}

	public void reset()
	{ repo = null; }

	public ArrayList<String> getAvailableModels(HashSet<String> csids){

		ArrayList<String> availables = new ArrayList<String>();

		for( Iterator<String> iter = csids.iterator(); iter.hasNext(); )
		{
			String candidate = iter.next();
			if( containsModel( candidate ) )
				availables.add( candidate );
		}

		return availables;
	}
	
	public ArrayList<String> getAvailableModels(String sid){

		ArrayList<String> availables = new ArrayList<String>();
		if( containsModel( sid ) )
			availables.add( sid );

		return availables;
	}
	
	/*
	public String getFunction(String id){
		return synsetFunctionMap.get(id);
	}
	*/
	public String getModifier(String id){
		return synsetModifierMap.get(id);
	}
	
	public boolean containsModel( String id )
	{ return synsetModelMap.containsKey( id ); }
	
	public boolean containsFunction( String id )
	{ return synsetFunctionMap.containsKey( id ); }

	public boolean containsModifier( String id )
	{ return synsetModifierMap.containsKey( id ); }
	
	public String getModelFilePath(String id){
		ArrayList<String> files = synsetModelMap.get( id );

		// Get random model for the synset
		int randomIndex = Randomizer.randomInRange( files.size() );
		String file = files.get( randomIndex );

		// Full path of model file for given synset id
		String fileName = Config.GALLERY_PATH + file;
		
		return fileName;
	}
	
	public String getModelMetaFile( String id ){

		ArrayList<String> files = synsetModelMap.get( id );

		// Get random model for the synset
		int randomIndex = Randomizer.randomInRange( files.size() );
		String file = files.get( randomIndex );

		// Full path of model file for given synset id
		String fileName = Config.MODEL_SYNSET_MAP_PATH + file;
		
		return fileName;
	}

	private class SynsetRepositoryReader {

		private Document doc = null;

		public SynsetRepositoryReader(File file){		
			try 
			{
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				doc = builder.parse( file );
			}
			catch (Exception e) 
			{ e.printStackTrace(); }
		}

		public Hashtable< String, ArrayList<String> > getElements(String tag){
			Hashtable< String, ArrayList<String> > table = new Hashtable< String, ArrayList<String> >();
			NodeList nodes = doc.getElementsByTagName( tag );
			System.out.println( "Total model count: " + nodes.getLength() );
			for( int i = 0; i < nodes.getLength(); i++  )
			{
				Element element = (Element) nodes.item( i );
				String key = element.getAttribute( "id" );				

				// Add model alternatives to list
				NodeList childNodes = element.getElementsByTagName("Model");
				ArrayList<String> paths = new ArrayList<String>(); 
				for( int j = 0; j < childNodes.getLength(); j++ )
				{
					Element child = (Element) childNodes.item( j );
					String file = child.getAttribute( "path" );
					paths.add( file );
				}
				table.put( key, paths );
			}
			return table;
		}	
	}
}
