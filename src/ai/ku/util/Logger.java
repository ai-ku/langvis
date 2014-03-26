package ai.ku.util;

public class Logger {

	public static void print(java.util.HashSet<?> set){
		// for( Iterator<?> iter = set.iterator(); iter.hasNext(); ) log( iter.next() );
		for(Object o:set) log(o);
	}
	
	public static void print(java.util.ArrayList<?> list){
		// for( Iterator<?> iter = list.iterator(); iter.hasNext(); ) log( iter.next() );
		for(Object o:list) log(o);
	}
	
	public static void log(Object object)
	{ System.out.println(object); }	
	
}
