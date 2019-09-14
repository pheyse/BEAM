package de.bright_side.beam;

import java.io.InputStream;

/**
 * 
 * @author Philip Heyse
 *
 */
class PlaintextBlockReader {
	private InputStream input;
	private int blockLength;
	private RandomNumberGenerator randomNumberGenerator;

	protected PlaintextBlockReader(InputStream input, int blockLength, RandomNumberGenerator randomNumberGenerator) {
		this.input = input;
		this.blockLength = blockLength;
		this.randomNumberGenerator = randomNumberGenerator;
	}

	public Block readNextBlock() throws Exception {
		Block result = new Block();
		byte[] data = BeamUtil.readMaximumAmountOfBytes(input, blockLength);
		//: end of file?
		if ((data == null) || (data.length == 0)) {
			return null;
		}
		result.setLength(data.length);
		
		//: read full block?
		if (data.length == blockLength) {
			result.setData(data);
			return result;
		}
		
		//: fill rest of block with random numbers
		byte[] dataWithCorrectLength = BeamUtil.createDataBlockWithCorrectLength(data, data.length, blockLength, randomNumberGenerator);
		result.setData(dataWithCorrectLength);
		
		return result;
	}


}
