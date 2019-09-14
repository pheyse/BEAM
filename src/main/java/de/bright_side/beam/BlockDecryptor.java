package de.bright_side.beam;

/**
 * 
 * @author Philip Heyse
 *
 */
import java.util.Map;
import java.util.Map.Entry;

class BlockDecryptor {
	private byte[] key;
	private Map<Integer, Integer> posSwitchMap;
	private int blockLength;
	private int digitSum;

	public BlockDecryptor(byte[] key, Map<Integer, Integer> posSwitchMap, int digitSum, int blockLength) {
		this.key = key;
		this.posSwitchMap = posSwitchMap;
		this.digitSum = digitSum;
		this.blockLength = blockLength;
	}

	public byte[] process(Block block) {
		byte[] blockData = block.getData();
		byte[] dataIncludingPadding = new byte[blockLength];
		int keyPos = digitSum % key.length;
		for (Entry<Integer, Integer> i: posSwitchMap.entrySet()) {
			dataIncludingPadding[i.getValue()] = (byte)(blockData[i.getKey()] - key[keyPos]);
			keyPos ++;
			if (keyPos >= key.length) {
				keyPos = 0;
			}
		}
		
		//: there is no padding with random data because the length of the data is the block length?
		if (block.getLength() ==  blockLength) {
			return dataIncludingPadding;
		}

		//: remove the padding random data at the end
		byte[] result = new byte[block.getLength()];
		System.arraycopy(dataIncludingPadding, 0, result, 0, block.getLength());
		return result;
	}

}
