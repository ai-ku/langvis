package ai.ku.resource;

import java.io.File;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class DictionaryReader {

	private Document doc = null;
	
	public DictionaryReader(File file){		
		try 
		{
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			doc = builder.parse(file);
		}
		catch (Exception e) 
		{ e.printStackTrace(); }
	}
	
	public Hashtable<String, String> getElements(String tag){
		
		Hashtable<String, String> elements = new Hashtable<String, String>();
		NodeList nodes = doc.getElementsByTagName( tag );
		
		for( int i = 0; i < nodes.getLength(); i++  )
		{
			Element element = (Element) nodes.item( i );
			String key = element.getAttribute( "name" );
			String value = element.getAttribute( "path" );
			elements.put( key, value );
		}		
		
		return elements;
	}	
}
