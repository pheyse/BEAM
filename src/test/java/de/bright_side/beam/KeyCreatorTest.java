package de.bright_side.beam;

import static org.junit.Assert.*;

import org.junit.Test;

public class KeyCreatorTest {
	private static final boolean ENABLE_LOGGING = false;

	@Test
	public void everySecondByte_oddLength() {
		byte[] inputBytes = TestUtil.byteArray(1, 2, 3, 4, 5, 6, 7);
		
		byte[] oddBytes = new KeyCreator().everySecondByte(inputBytes, 0);
		byte[] evenBytes = new KeyCreator().everySecondByte(inputBytes, 1);
		
		assertEquals(TestUtil.bytesToString(1, 3, 5, 7), TestUtil.toString(oddBytes));
		assertEquals(TestUtil.bytesToString(2, 4, 6), TestUtil.toString(evenBytes));
	}

	@Test
	public void everySecondByte_evenLength() {
		byte[] inputBytes = TestUtil.byteArray(1, 2, 3, 4, 5, 6, 7, 8);
		
		byte[] oddBytes = new KeyCreator().everySecondByte(inputBytes, 0);
		byte[] evenBytes = new KeyCreator().everySecondByte(inputBytes, 1);
		
		assertEquals(TestUtil.bytesToString(1, 3, 5, 7), TestUtil.toString(oddBytes));
		assertEquals(TestUtil.bytesToString(2, 4, 6, 8), TestUtil.toString(evenBytes));
	}
	
	@Test
	public void generateKeyOfEvenAndOddBytes_normal() {
		byte[] password = TestUtil.byteArray(1, 2, 3, 4, 5, 6, 7);
		byte[] result = new KeyCreator().generateKeyOfEvenAndOddBytes(password);
		
		int evenBytesLenght = 4;
		int oddBytesLenght = 3;
		int iterationsOfEvenAndOddBytes = 2;
		
		log("generateKeyOfEvenAndOddBytes_normal. result: " + TestUtil.toString(result));
		assertEquals(evenBytesLenght * oddBytesLenght * iterationsOfEvenAndOddBytes, result.length);
	}

	@Test
	public void applyPassword_normal() {
		byte[] password = TestUtil.byteArray(1, 2, 3, 4, 5, 6, 7);
		byte[] simpleKey = new KeyCreator().generateKeyOfEvenAndOddBytes(password);
		byte[] result = new KeyCreator().applyPassword(simpleKey, password);
		
		int evenBytesLenght = 4;
		int oddBytesLenght = 3;
		int iterationsOfEvenAndOddBytes = 2;
		int iterationsOfApplyPassword = 2;
		
		log("applyPassword_normal. result: " + TestUtil.toString(result));
		assertEquals(evenBytesLenght * oddBytesLenght * iterationsOfEvenAndOddBytes * password.length * iterationsOfApplyPassword, result.length);
	}
	
	@Test
	public void applyPassword_negativeValues() {
		byte[] password = TestUtil.byteArray(-1, -2, -3, -4, -5, -6, -7);
		byte[] simpleKey = new KeyCreator().generateKeyOfEvenAndOddBytes(password);
		byte[] result = new KeyCreator().applyPassword(simpleKey, password);
		
		int evenBytesLenght = 4;
		int oddBytesLenght = 3;
		int iterationsOfEvenAndOddBytes = 2;
		int iterationsOfApplyPassword = 2;
		
		log("applyPassword_negativeValues. result: " + TestUtil.toString(result));
		assertEquals(evenBytesLenght * oddBytesLenght * iterationsOfEvenAndOddBytes * password.length * iterationsOfApplyPassword, result.length);
	}
	
	private void log(String message) {
		if (ENABLE_LOGGING) {
			System.out.println("KeyCreatorTest> " + message);
		}
	}
}
