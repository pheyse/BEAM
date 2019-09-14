package de.bright_side.beam;

import java.io.IOException;
import java.io.InputStream;

class BeamDecryptedInputStream extends InputStream{
	private static final int END_OF_STREAM = -1;
	
	private InputStream innerInputStream;
	private BlockDecryptor decryptor;
	private EncryptedBlockReader blockReader;
	
	/** array that acts as a buffer until the block is read or the stream is closed. The array always stays the same and the bytes get overwritten*/
	private byte[] blockBytes;

	/** indicated how much data is in the block. This may be a different value than block length if e.g. the last block in a file is not full. 
	 * A value of END_OF_STREAM_REACHED indicates that the innerInputStream contains no more data*/
	private int lengthOfDataInBlock = 0;
	
	/** indicates the position where the next bytes should be passed on in the read methods*/
	private int blockPos = 0;
	
	/** the length of the blocks as indicated by the provided Beam instance*/
	private int blockLength = 0;

	private BeamProgressListener listener;
	private long plainBytesProcessed = 0;
	private long encryptedBytesProcessed = 0;
	
	protected BeamDecryptedInputStream(Beam beam, InputStream innerInputStream, BeamProgressListener listener) {
		this.innerInputStream = innerInputStream;
		this.listener = listener;
		blockLength = beam.getBlockLength();
		decryptor = beam.createBlockDecryptor();
		blockReader = new EncryptedBlockReader(innerInputStream, blockLength);
	}

	@Override
	public boolean markSupported() {
		return false;
	}
	
	@Override
	public synchronized void mark(int readlimit) {
		//: do nothing as mark is not supported
	}
	
	@Override
	public synchronized void reset() throws IOException {
		//: do nothing as mark is not supported
	}

	@Override
	public int read(byte[] output) throws IOException {
		return read(output, output.length);
	}

	/**
	 * 
	 * @param output may be null for skipping only
	 * @param lengthToRead the length to be either skipped (if output is null) or written to output
	 * @return read length
	 * @throws IOException
	 */
	private int read(byte[] output, long lengthToRead) throws IOException {
		int remainingLenghInBlock = lengthOfDataInBlock - blockPos; 
		//: buffer empty?
		if (remainingLenghInBlock == 0) {
			readNextBlock();
		}
		//: end of input stream reached?
		if (lengthOfDataInBlock == END_OF_STREAM) {
			return END_OF_STREAM;
		}
		if (lengthToRead == 0) {
			return 0;
		}

		int writtenLength = 0;
		long requestedLength = lengthToRead;
		long remainingBytesForOutput = requestedLength;
		
		while (remainingBytesForOutput > 0) {
			
			int bytesToWrite;
			//: enough bytes in buffer to write entire output?
			if (remainingLenghInBlock > remainingBytesForOutput) {
				bytesToWrite = (int)remainingBytesForOutput; //: cast ok as bytesToWrite is an int and larger than the long value of remainingBytesForOutput 
			} else {
				bytesToWrite = remainingLenghInBlock;
			}
			//: not just skipping? Then copy
			if (output != null) {
				System.arraycopy(blockBytes, blockPos, output, writtenLength, bytesToWrite);
			}
			blockPos += bytesToWrite;
			writtenLength += bytesToWrite;
			remainingBytesForOutput -= bytesToWrite;
			remainingLenghInBlock = lengthOfDataInBlock - blockPos;
			plainBytesProcessed += bytesToWrite;
			
			if (remainingLenghInBlock <= 0) {
				readNextBlock();
			}
			if (lengthOfDataInBlock == END_OF_STREAM) {
				if (listener != null) {
					listener.bytesProcessed(plainBytesProcessed, encryptedBytesProcessed);
				}
				return writtenLength;
			}
			remainingLenghInBlock = lengthOfDataInBlock - blockPos;
			
		}

		
		if (listener != null) {
			listener.bytesProcessed(plainBytesProcessed, encryptedBytesProcessed);
		}

//		plainBytesProcessed += blockPos;
//		encryptedBytesProcessed += blockLength + Beam.LENGTH_OF_BLOCK_SIZE_BYTES;
//		private long plainBytesProcessed = 0;
//		private long encryptedBytesProcessed = 0;

		
		return writtenLength;
	}

	private void readNextBlock() throws IOException {
		//: end of input stream was reached before?
		if (lengthOfDataInBlock == END_OF_STREAM) {
			return;
		}
		
		Block block;
		try {
			block = blockReader.readNextBlock();
		} catch (Exception e) {
			throw new IOException(e);
		}
		blockPos = 0;
		
		//: end of input stream reached now?
		if (block == null) {
			lengthOfDataInBlock = END_OF_STREAM;
			return;
		}
		lengthOfDataInBlock = block.getLength();
		blockBytes = decryptor.process(block);
		encryptedBytesProcessed += blockLength;
	}


	@Override
	public int read() throws IOException {
		byte[] result = new byte[1];
		int amountRead = read(result);
		if (amountRead == END_OF_STREAM) {
			return END_OF_STREAM;
		}
		return result[0];
	}
	
	@Override
	public int read(byte[] output, int off, int len) throws IOException {
		byte[] result = new byte[len];
		int amountRead = read(result);
		if (amountRead == END_OF_STREAM) {
			return END_OF_STREAM;
		}

		System.arraycopy(result, 0, output, off, amountRead);
		return amountRead;
	}

	@Override
	public void close() throws IOException {
		innerInputStream.close();
		if (listener != null) {
			listener.finishedSuccessfully(plainBytesProcessed, encryptedBytesProcessed);
		}
	}
	
	@Override
	public long skip(long n) throws IOException {
		if (n < 0) {
			return 0;
		}
		return read(null, n);
	}

	@Override
	public int available() throws IOException {
		int remainingLenghInBlock = lengthOfDataInBlock - blockPos; 
		//: buffer empty?
		if (remainingLenghInBlock == 0) {
			readNextBlock();
		}
		//: end of input stream reached?
		if (lengthOfDataInBlock == END_OF_STREAM) {
			return 0;
		}

		return remainingLenghInBlock;
	}

}
