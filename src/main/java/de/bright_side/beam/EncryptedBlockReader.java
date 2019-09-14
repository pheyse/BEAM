package de.bright_side.beam;

import java.io.InputStream;

/**
 * 
 * @author Philip Heyse
 *
 */
class EncryptedBlockReader {
	private InputStream input;
	private int blockLength;

	public EncryptedBlockReader(InputStream input, int blockLength) {
		this.input = input;
		this.blockLength = blockLength;
	}

	public Block readNextBlock() throws Exception {
		return readNextBlock(false);
	}
	
	public Block readNextBlock(boolean skipDataOnly) throws Exception {
		Block result = new Block();
		byte[] lengthBytes = BeamUtil.readMaximumAmountOfBytes(input, 2);
		
		//: end of file?
		if ((lengthBytes == null) || (lengthBytes.length == 0)) {
			return null;
		}
		
		if (lengthBytes.length != 2) {
			throw new Exception("Could not read 2 length bytes but only " + lengthBytes.length + ". File or stream is corrupt");
		}
		
		int dataLength = BeamUtil.getPosIntFrom2Bytes(lengthBytes[0], lengthBytes[1]);
		if (dataLength < 0) {
			throw new Exception("Wrong data length: " + dataLength);
		}
		if (dataLength > blockLength) {
			throw new Exception("Data length " + dataLength + " exceeds the block length " + blockLength);
		}
		result.setLength(dataLength);

		if (skipDataOnly) {
			long skipped = input.skip(blockLength);
			if (skipped < blockLength) {
				//: skip did not work, then just read
				BeamUtil.readExactAmountOfBytes(input, (int)(blockLength - skipped));
			}
			return result;
		}
		
		byte[] data = BeamUtil.readExactAmountOfBytes(input, blockLength);
		if (data.length != blockLength) {
			throw new Exception("Expected data of length " + blockLength + " but could only read " + data.length + " bytes");
		}
		
		result.setData(data);
		return result;
	}

}
