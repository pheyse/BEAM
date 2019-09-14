package de.bright_side.beam;

import java.io.ByteArrayOutputStream;
/**
 * 
 * @author Philip Heyse
 *
 */
import java.io.InputStream;

class BeamUtil {
	private static final String STRING_ENCODING = "UTF-8";
	private static final char[] hexCode = "0123456789ABCDEF".toCharArray();
	
	public static byte[] stringToByteArray(String string) throws Exception {
		return string.getBytes(STRING_ENCODING);
	}
	
	public static String byteArrayToString(byte[] data) throws Exception {
		return new String(data, STRING_ENCODING);
	}
	
	public static byte[] readExactAmountOfBytes(InputStream inputStream, int length) throws Exception{
		byte[] data = new byte[length];
		int readLength = 0;
		int readLengthTotal = inputStream.read(data, 0, length);
		if (readLengthTotal < 0) {
			throw new Exception("Cannot read any bytes read from input stream (input stream type: " + inputStream.getClass().getName() + ")");
		}
		if (readLengthTotal >= length) {
			return data;
		}
		while (readLengthTotal < length){
			int numberOfBytesToRead = length - readLengthTotal;
			try{
				readLength = inputStream.read(data, readLengthTotal, numberOfBytesToRead);
			} catch (Exception e){
				throw new Exception("Could not read " + numberOfBytesToRead + " byte(s). buffer = " + data.length + " bytes. readLengthTotal = " + readLengthTotal, e);
			}
			if (readLength > 0) {
				readLengthTotal += readLength;
			}
			if (readLengthTotal >= length) {
				return data;
			}

			if (readLength <= 0) {
				throw new Exception("Could not read as much data as requested,"
						+ " becuase the end of the file was reached. (Wanted size: "
						+ length + ", read size total: " + readLengthTotal + ", read size in step: " + readLength + ")");
			}
		}
		return data;
	}
	
	public static byte[] readMaximumAmountOfBytes(InputStream inputStream, int length) throws Exception{
		byte[] data = new byte[length];
		int readLength = 0;
		int readLengthTotal = inputStream.read(data, 0, length);
		if (readLengthTotal < 0) {
			return new byte[0];
		}
		if (readLengthTotal >= length) {
			return data;
		}
		while (readLengthTotal < length){
			int numberOfBytesToRead = length - readLengthTotal;
			try{
				readLength = inputStream.read(data, readLengthTotal, numberOfBytesToRead);
			} catch (Exception e){
				throw new Exception("Could not read " + numberOfBytesToRead + " byte(s). buffer = " + data.length + " bytes. readLengthTotal = " + readLengthTotal, e);
			}
			if (readLength > 0) {
				readLengthTotal += readLength;
			}
			if (readLengthTotal >= length) {
				return data;
			}

			if (readLength <= 0) {
				byte[] result = new byte[readLengthTotal];
				System.arraycopy(data, 0, result, 0, readLengthTotal);
				return result;
			}
		}
		return data;
	}

	public static byte[] readAllBytes(InputStream inputStream) throws Exception {
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			byte[] buffer = new byte[10240];
			int len;
			while ((len = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, len);
			}
			return outputStream.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			inputStream.close();
		}
	}

	public static byte[] get2BytesFromPosInt(int value) {
		byte[] result = new byte[2];
		result[0] = (byte)((value / 256) + Byte.MIN_VALUE);
		result[1] = (byte)((value % 256) + Byte.MIN_VALUE);
		return result;
	}

	public static int getPosIntFrom2Bytes(byte byte1, byte byte2) {
		return ((byte1 - Byte.MIN_VALUE) * 256) + (byte2 - Byte.MIN_VALUE);
	}
	
	private static int hexToBin(char character) {
		if ('0' <= character && character <= '9') {
			return character - '0';
		}
		if ('A' <= character && character <= 'F') {
			return character - 'A' + 10;
		}
		if ('a' <= character && character <= 'f') {
			return character - 'a' + 10;
		}
		return -1;
	}
	
	public static byte[] hexStringToByteArray(String hexString) {
		final int length = hexString.length();

		if (length % 2 != 0) {
			throw new IllegalArgumentException("hexBinary needs to be even-length: " + hexString);
		}

		byte[] result = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			int high = hexToBin(hexString.charAt(i));
			int low = hexToBin(hexString.charAt(i + 1));
			if (high == -1 || low == -1) {
				throw new IllegalArgumentException("contains illegal character: " + hexString);
			}

			result[i / 2] = (byte) (high * 16 + low);
		}

		return result;
	}
	
    public static String byteArrayToHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

	public static int getDigitSum(byte[] password) {
		int result = 0;
		for (byte i: password) {
			result += i;
		}
		return result;
	}		

	/**
	 * if the provided data is shorted than the block length, return an array with the data and the rest filled with random values
	 * @param data
	 * @return
	 */
	protected static byte[] createDataBlockWithCorrectLength(byte[] data, int lengthOfData, int blockLength, RandomNumberGenerator randomNumberGenerator) {
		if (lengthOfData == blockLength) {
			return data;
		}
		byte[] dataWithCorrectLength = new byte[blockLength];
		byte[] randomBytes = randomNumberGenerator.getRandomBytes(blockLength - lengthOfData);
		System.arraycopy(data, 0, dataWithCorrectLength, 0, lengthOfData);
		System.arraycopy(randomBytes, 0, dataWithCorrectLength, lengthOfData, randomBytes.length);
		return dataWithCorrectLength;
	}

}
