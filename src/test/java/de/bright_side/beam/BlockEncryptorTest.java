package de.bright_side.beam;

import static org.junit.Assert.assertEquals;

import java.util.SortedMap;

import org.junit.Test;

public class BlockEncryptorTest {
	@Test
	public void process_switchMapWithoutSwitch() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3, 4, 5);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		int digitSum = 0;
		int blockLength = posSwitchMap.size();
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(0 + 0, 10 + 1, 20 + 2, 30 + 3, 40 + 4, 50 + 5);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	@Test
	public void process_switchMapWithoutSwitchDigitSum1() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3, 4, 5);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		int digitSum = 1;
		int blockLength = posSwitchMap.size();
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(0 + 1, 10 + 2, 20 + 3, 30 + 4, 40 + 5, 50 + 0);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	@Test
	public void process_switchMapWithoutSwitchKeyShort() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5);
		int digitSum = 0;
		int blockLength = posSwitchMap.size();
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(0 + 0, 10 + 1, 20 + 2, 30 + 3, 40 + 0, 50 + 1);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	@Test
	public void process_switchMapShift1() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3, 4, 5);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0);
		int digitSum = 0;
		int blockLength = posSwitchMap.size();
		
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(10 + 0, 20 + 1, 30 + 2, 40 + 3, 50 + 4, 0 + 5);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	@Test
	public void process_switchMapShift1KeyShort() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0);
		int digitSum = 0;
		int blockLength = posSwitchMap.size();
		
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(10 + 0, 20 + 1, 30 + 2, 40 + 3, 50 + 0, 0 + 1);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	@Test
	public void process_switchMapShift1KeyShortDigitSum2() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 0);
		int digitSum = 2;
		int blockLength = posSwitchMap.size();
		
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(10 + 2, 20 + 3, 30 + 0, 40 + 1, 50 + 2, 0 + 3);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	@Test
	public void process_differentOrderKeyShort() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 3, 1, 0, 2, 4, 3, 1, 4, 5, 5, 2);
		int digitSum = 2;
		int blockLength = posSwitchMap.size();
		
		byte[] block = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		byte[] expectedResult = TestUtil.byteArray(30 + 2, 0 + 3, 40 + 0, 10 + 1, 50 + 2, 20 + 3);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		
		byte[] result = blockEncryptor.process(block);
		
		assertEquals(TestUtil.toString(expectedResult), TestUtil.toString(result));
	}
	
	
}
