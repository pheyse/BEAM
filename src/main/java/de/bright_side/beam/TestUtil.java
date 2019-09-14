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
class TestUtil {
	public static byte[] byteArray(byte...data) {
		return data;
	}

	public static byte[] byteArray(int ...data) {
		byte[] result = new byte[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = (byte) data[i];
		}
		return result;
	}

	public static byte[] byteArrayFromRange(int start, int end) {
		byte[] result = new byte[end - start + 1];
		for (int i = start; i <= end; i++) {
			result[i-start] = (byte) i;
		}
		return result;
	}
	
	public static String bytesToString(int ...data) {
		return toString(byteArray(data));
	}

	public static String toString(byte[] data) {
		StringBuilder result = new StringBuilder();
		for (byte i: data) {
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append("" + i);
		}
		return result.toString();
	}

	public static <K> String toString(List<K> items) {
		StringBuilder result = new StringBuilder();
		for (K i: items) {
			if (result.length() > 0) {
				result.append(", ");
			}
			result.append("" + i);
		}
		return result.toString();
	}
	
	public static List<Integer> toIntList(int ... items){
		List<Integer> result = new ArrayList<Integer>();
		for (int i: items) {
			result.add(i);
		}
		return result;
	}

	public static String intsToString(int ... items) {
		return toString(toIntList(items));
	}

	public static SortedMap<Integer, Integer> intMap(int ...data) {
		SortedMap<Integer, Integer> result = new TreeMap<Integer, Integer>();
		for (int i = 0; i < data.length; i += 2) {
			result.put(data[i], data[i + 1]);
		}
		return result;
	}

}
