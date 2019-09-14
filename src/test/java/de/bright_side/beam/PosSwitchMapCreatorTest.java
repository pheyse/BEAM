package de.bright_side.beam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class PosSwitchMapCreatorTest {
	private static final boolean ENABLE_LOGGING = false;
	
	@Test
	public void createList_startWith1() {
		List<Integer> result = new PosSwitchMapCreator().createList(1, 10);
		assertEquals(TestUtil.intsToString(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), TestUtil.toString(result));
	}

	@Test
	public void createList_startWith0() {
		List<Integer> result = new PosSwitchMapCreator().createList(0, 6);
		assertEquals(TestUtil.intsToString(0, 1, 2, 3, 4, 5), TestUtil.toString(result));
	}
	
	@Test
	public void create_normal() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		final int blockLength = 10;
		Map<Integer, Integer> result = new PosSwitchMapCreator().create(key, blockLength);
		
		log("create_normal: result = " + result);
		
		assertEquals(blockLength, result.size());
		for (int i = 0; i < blockLength; i++) {
			assertTrue(result.containsKey(Integer.valueOf(i)));
			assertTrue(result.containsValue(Integer.valueOf(i)));
		}
	}
	
	@Test
	public void create_negativeValues() {
		byte[] key = TestUtil.byteArray(-5, -99, -12, -3);
		final int blockLength = 10;
		Map<Integer, Integer> result = new PosSwitchMapCreator().create(key, blockLength);
		
		log("create_negativeValues: result = " + result);
		
		assertEquals(blockLength, result.size());
		for (int i = 0; i < blockLength; i++) {
			assertTrue(result.containsKey(Integer.valueOf(i)));
			assertTrue(result.containsValue(Integer.valueOf(i)));
		}
	}
	
	@Test
	public void create_allByteValues() {
		int length = -Byte.MIN_VALUE + Byte.MAX_VALUE + 1; 
		log("create_allByteValues: length = " + length);
		byte[] key = new byte[length];
		
		for (int i = 0; i < length; i++) {
			key[i] = (byte)i;
		}
		
		final int blockLength = length;
		Map<Integer, Integer> result = new PosSwitchMapCreator().create(key, blockLength);
		
		log("create_negativeValues: result = " + result);
		
		assertEquals(blockLength, result.size());
		for (int i = 0; i < blockLength; i++) {
			assertTrue(result.containsKey(Integer.valueOf(i)));
			assertTrue(result.containsValue(Integer.valueOf(i)));
		}
	}
	
	private void log(String message) {
		if (ENABLE_LOGGING) {
			System.out.println("PosSwitchMapCreatorTest> " + message);
		}
	}

}
