package de.bright_side.beam;

/**
 * 
 * @author Philip Heyse
 *
 */
class KeyCreator {

	public byte[] create(byte[] password) {
		byte[] evenAndOddBytesKey = generateKeyOfEvenAndOddBytes(password);
		byte[] result = applyPassword(evenAndOddBytesKey, password);
		
		return result;
	}

	protected byte[] applyPassword(byte[] simpleKey, byte[] password) {
		byte[] result = new byte[simpleKey.length * password.length * 2];
		int resultPos = 0;
		
		//: add forward
		for (byte a : simpleKey) {
			for (byte p : password) {
				result[resultPos] = (byte)(a + p);
				resultPos ++;
			}
		}

		//: add backwards
		for (byte a : simpleKey) {
			for (int passwordPos = password.length - 1; passwordPos >= 0; passwordPos --) {
				result[resultPos] = (byte)(a + password[passwordPos]);
				resultPos ++;
			}
		}
		
		return result;
	}

	protected byte[] generateKeyOfEvenAndOddBytes(byte[] password) {
		byte[] keyA = everySecondByte(password, 0);
		byte[] keyB = everySecondByte(password, 1);
		byte[] result = new byte[keyA.length * keyB.length * 2];
		
		int resultPos = 0;
		
		//: add
		for (byte a : keyA) {
			for (byte b : keyB) {
				result[resultPos] = (byte)(a + b);
				resultPos ++;
			}
		}
		//: subtract
		for (byte a : keyA) {
			for (byte b : keyB) {
				result[resultPos] = (byte)(a - b);
				resultPos ++;
			}
		}
		return result;
	}

	protected byte[] everySecondByte(byte[] input, int offset) {
		int length = input.length;
		byte[] result = new byte[(input.length - offset + 1) / 2];
		
		int resultPos = 0;
		for (int inputPos = offset; inputPos < length; inputPos += 2) {
			result[resultPos] = input[inputPos];
			resultPos ++;
		}
		return result;
	}
}
