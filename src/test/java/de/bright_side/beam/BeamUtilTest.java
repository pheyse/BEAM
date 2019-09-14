package de.bright_side.beam;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BeamUtilTest{
	private static final int VALUES_IN_BYTE = -Byte.MIN_VALUE + Byte.MAX_VALUE + 1;
	private static final boolean ENABLE_LOGGING = false;
	
	@Test
	public void getBytesFromPositiveIntAndGetPositiveIntegerFromBytes_allValues() {
		int max = VALUES_IN_BYTE * VALUES_IN_BYTE;
		for (int i = 0; i < max; i++) {
			byte[] bytes = BeamUtil.get2BytesFromPosInt(i);
			int convertedBack = BeamUtil.getPosIntFrom2Bytes(bytes[0], bytes[1]);
			assertEquals(i, convertedBack);
		}
	}
	
	@Test
	public void byteArrayToHexStringAndHexStringToByteArray_allValues() {
		for (int i = 0; i < VALUES_IN_BYTE; i++) {
			byte[] bytes = TestUtil.byteArray(-7, i, 99, Byte.MAX_VALUE, Byte.MIN_VALUE);
			String hexString = BeamUtil.byteArrayToHexString(bytes);
			byte[] convertedBack = BeamUtil.hexStringToByteArray(hexString);
			log("hex test: bytes = " + TestUtil.toString(bytes) + ". hexString = >>" + hexString + "<<, conv-back = " + TestUtil.toString(convertedBack));
			assertEquals(TestUtil.toString(bytes), TestUtil.toString(convertedBack));
		}
	}
	
	private void log(String message) {
		if (ENABLE_LOGGING) {
			System.out.println("BeamUtilTest> " + message);
		}
	}

}
