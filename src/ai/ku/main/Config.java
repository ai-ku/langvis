package ai.ku.main;

public class Config {

	/*
	 * 
	 * Fill in absolute paths for
	 * - 3D model gallery
	 * - Model - Synset map
	 * - WordNet 3.0 dict folder path
	 * 
	 * e.g. ~/Desktop/research/resources/mapping/
	 * e.g. ~/Desktop/research/resources/gallery/
	 * e.g. ~/Desktop/research/resources/WordNet-3.0/dict/
	 */

	private static final String BASE_PATH = "/Users/emre/Desktop/research/resources/"; // Path of langvis resources
	
	public static final String MODEL_SYNSET_MAP_PATH = BASE_PATH + "mapping/"; // Synset2Model.xml file path
	public static final String GALLERY_PATH = BASE_PATH + "gallery/"; // 3D model gallery path
	public static final String WORDNET_PATH = BASE_PATH + "WordNet-3.0/dict/"; // WordNet 3.0 dict folder path
}
