package ai.ku.util;

import java.util.Random;

public class Randomizer {

	public static int randomInRange(int range){
		Random random = new Random( System.currentTimeMillis() );
		int randomNumber = random.nextInt( range );
		return randomNumber;
	}

	public static boolean randomBoolean() {
		Random random = new Random( System.currentTimeMillis() );
		boolean randomBool = random.nextBoolean();
		return randomBool;
	}
	
}
