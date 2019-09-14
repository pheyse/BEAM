package de.bright_side.beam;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 
 * @author Philip Heyse
 *
 */
class PosSwitchMapCreator {
	protected SortedMap<Integer, Integer> create(byte[] key, int blockLength) {
		SortedMap<Integer, Integer> result = new TreeMap<>();
		
		List<Integer> remainingDestPositions = createList(0, blockLength);
		int keyPos = 0;
		for (int origPos = 0; origPos < blockLength; origPos ++) {
			int index = (key[keyPos] - Byte.MIN_VALUE) % remainingDestPositions.size();
			int destPos = remainingDestPositions.remove(index);
			result.put(origPos, destPos);
			keyPos ++;
			if (keyPos >= key.length) {
				keyPos = 0;
			}
		}
		return result;
	}

	protected List<Integer> createList(int start, int length) {
		List<Integer> result = new ArrayList<>();
		int limit = start + length;
		for (int i = start; i < limit; i++) {
			result.add(i);
		}
		return result;
	}
}
