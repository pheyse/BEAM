package de.bright_side.beam;

/**
 * 
 * @author Philip Heyse
 *
 */
public class FakeRandomNumberGenerator implements RandomNumberGenerator{
	private byte[] fakeRandomNumbers;
	private int pos = 0;
	
	public FakeRandomNumberGenerator(byte[] fakeRandomNumbers) {
		this.fakeRandomNumbers = fakeRandomNumbers;
	}
	
	public FakeRandomNumberGenerator() {
		this(null);
	}
	
	@Override
	public byte[] getRandomBytes(int length) {
		byte[] result = new byte[length];
		
		//: only use 0 as random numbers?
		if (fakeRandomNumbers == null) {
			for (int i = 0; i < length; i++) {
				result[i] = 0;
			}
			return null;
		}
		
		//: use provided byte array
		for (int i = 0; i < length; i++) {
			result[i] = fakeRandomNumbers[pos];
			pos ++;
			if (pos >= fakeRandomNumbers.length) {
				pos = 0;
			}
		}
		return result;

	}

}
