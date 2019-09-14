package de.bright_side.beam;

import java.io.IOException;
import java.io.OutputStream;

class BeamEncryptedOutputStream extends OutputStream{
	private OutputStream innerOutputStream;
	
	private BlockEncryptor encryptor;
	private RandomNumberGenerator randomNumberGenerator;
	
	/** array that acts as a buffer until the block is full or the stream is closed. The array always stays the same and the bytes get overwritten*/
	private byte[] blockBytes;
	
	/** indicates the position where the next bytes should be written to in the blockBytes array*/
	private int blockPos = 0;
	
	/** the length of the blocks as indicated by the provided Beam instance*/
	private int blockLength = 0;

	private BeamProgressListener listener;
	private long plainBytesProcessed = 0;
	private long encryptedBytesProcessed = 0;

	protected BeamEncryptedOutputStream(Beam beam, OutputStream innerOutputStream, BeamProgressListener listener) {
		this.innerOutputStream = innerOutputStream;
		this.listener = listener;
		blockLength = beam.getBlockLength();
		blockBytes = new byte[blockLength];
		encryptor = beam.createBlockEncryptor();
		randomNumberGenerator = beam.getRandomNumberGenerator();
	}
	
	@Override
	public void write(int b) throws IOException {
		byte[] data = new byte[1];
		data[0] = (byte)b;
		write(data[0]);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		byte[] bytesToWrite = new byte[len];
		System.arraycopy(b, off, bytesToWrite, 0, len);
		write(bytesToWrite);
	}
	
	@Override
	public void write(byte[] input) throws IOException {
		int posInInput = 0;
		int remainingInputLength = input.length;

		while (remainingInputLength > 0) {
			int lengthThatFitInBlock = blockLength - blockPos;
			
			int lengthToWrite = 0;
			if (lengthThatFitInBlock >= remainingInputLength) {
				//: it fits so write all remaining bytes from input
				lengthToWrite = remainingInputLength;
			} else {
				//: it doesn't fit so write as many bytes as fit into the current block
				lengthToWrite = lengthThatFitInBlock;
			}

			System.arraycopy(input, posInInput, blockBytes, blockPos, lengthToWrite);
			posInInput += lengthToWrite;
			blockPos += lengthToWrite;
			remainingInputLength -= lengthToWrite;

			//: write the block to inner output stream once it is full
			if (blockPos >= blockLength) {
				writeBlock();
				blockPos = 0;
			}
		}
	}


	@Override
	public void flush() throws IOException {
	}
	
	@Override
	public void close() throws IOException {
		writeBlock();
		innerOutputStream.close();
		if (listener != null) {
			listener.finishedSuccessfully(plainBytesProcessed, encryptedBytesProcessed);
		}
	}

	private void writeBlock() throws IOException {
		if (blockPos == 0) {
			return;
		}

		//: create a block with the correct length where the rest of the bytes is filled with random values
		byte[] blockWithCorrectLength = BeamUtil.createDataBlockWithCorrectLength(blockBytes, blockPos, blockLength, randomNumberGenerator);
		byte[] encryptedBytes = encryptor.process(blockWithCorrectLength);
		
		innerOutputStream.write(BeamUtil.get2BytesFromPosInt(blockPos));
		innerOutputStream.write(encryptedBytes);
		
		plainBytesProcessed += blockPos;
		encryptedBytesProcessed += blockLength + Beam.LENGTH_OF_BLOCK_SIZE_BYTES;

		if (listener != null) {
			listener.bytesProcessed(plainBytesProcessed, encryptedBytesProcessed);
		}
		
		blockPos = 0;
	}

}
