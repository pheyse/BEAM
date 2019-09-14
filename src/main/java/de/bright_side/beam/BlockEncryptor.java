package de.bright_side.beam;

/**
 * 
 * @author Philip Heyse
 *
 */
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;

class BlockEncryptor {
	private byte[] key;
	private Map<Integer, Integer> posSwitchMap;
	private int blockLength;
	private int digitSum;

	protected BlockEncryptor(byte[] key, SortedMap<Integer, Integer> posSwitchMap, int digitSum, int blockLength) {
		this.key = key;
		this.posSwitchMap = posSwitchMap;
		this.digitSum = digitSum;
		this.blockLength = blockLength;
	}

	public byte[] process(byte[] block) {
		byte[] result = new byte[blockLength];
		int keyPos = digitSum % key.length;
		for (Entry<Integer, Integer> i: posSwitchMap.entrySet()) {
			result[i.getKey()] = (byte)(block[i.getValue()] + key[keyPos]);
//			System.out.println("value = " + i.getValue() + ", key = " + i.getKey());
			keyPos ++;
			if (keyPos >= key.length) {
				keyPos = 0;
			}
		}
		
		return result;
	}

}
