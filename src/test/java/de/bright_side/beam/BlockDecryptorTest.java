package de.bright_side.beam;

import static org.junit.Assert.assertEquals;

import java.util.SortedMap;

import org.junit.Test;

public class BlockDecryptorTest {

	@Test
	public void process_fullBlock() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 3, 1, 0, 2, 4, 3, 1, 4, 5, 5, 2);
		int digitSum = 2;
		int blockLength = posSwitchMap.size();
		byte[] plainBlock = TestUtil.byteArray(0, 10, 20, 30, 40, 50);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		BlockDecryptor blockDecryptor = new BlockDecryptor(key, posSwitchMap, digitSum, blockLength);
		byte[] encryptedBytes = blockEncryptor.process(plainBlock);

		Block block = new Block();
		block.setData(encryptedBytes);
		block.setLength(blockLength);
		byte[] decryptedBytes = blockDecryptor.process(block);
		
		assertEquals(TestUtil.toString(plainBlock), TestUtil.toString(decryptedBytes));
	}

	@Test
	public void process_blockWithPadding() {
		byte[] key = TestUtil.byteArray(0, 1, 2, 3);
		SortedMap<Integer, Integer> posSwitchMap = TestUtil.intMap(0, 3, 1, 0, 2, 4, 3, 1, 4, 5, 5, 2);
		int digitSum = 2;
		int blockLength = posSwitchMap.size();
		byte[] plainBlock = TestUtil.byteArray(0, 10, 20, 30);
		byte[] plainBlockWithPadding = TestUtil.byteArray(0, 10, 20, 30, 99, 99);
		BlockEncryptor blockEncryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		BlockDecryptor blockDecryptor = new BlockDecryptor(key, posSwitchMap, digitSum, blockLength);
		byte[] encryptedBytes = blockEncryptor.process(plainBlockWithPadding);
		
		Block block = new Block();
		block.setData(encryptedBytes);
		block.setLength(plainBlock.length);
		byte[] decryptedBytes = blockDecryptor.process(block);
		
		assertEquals(TestUtil.toString(plainBlock), TestUtil.toString(decryptedBytes));
	}
}
