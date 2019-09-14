package de.bright_side.beam;

import java.util.concurrent.ThreadLocalRandom;

/**
 * 
 * @author Philip Heyse
 *
 */
class DefaultRandomNumberGenerator implements RandomNumberGenerator{
	
	@Override
	public byte[] getRandomBytes(int length) {
		byte[] result = new byte[length];
		ThreadLocalRandom.current().nextBytes(result);
		return result;
	}
	
}
