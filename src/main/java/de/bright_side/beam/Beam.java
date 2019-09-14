package de.bright_side.beam;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.SortedMap;

/**
 * 
 * @author Philip Heyse
 *
 */
public class Beam {
	protected static final int DEFAULT_BLOCK_LENGTH = 1024;
	protected static final int VALUES_IN_BYTE = -Byte.MIN_VALUE + Byte.MAX_VALUE + 1;
	protected static final int LENGTH_OF_BLOCK_SIZE_BYTES = 2;
	private static final int MINIMUM_BLOCK_LENGTH = 32;
	private static final int MAXIMUM_BLOCK_LENGTH = VALUES_IN_BYTE * VALUES_IN_BYTE; //: so the block length can be stored in 2 bytes
	private static final int MINIMUM_PASSWORD_LENGTH = 8;
	
	
	private byte[] password;
	private int blockLength;
	private byte[] key = null;
	private int digitSum = 0;
	private SortedMap<Integer, Integer> posSwitchMap = null;
	private RandomNumberGenerator randomNumberGenerator = new DefaultRandomNumberGenerator();

	/**
	 * initialized the Beam class with the given password and the default block length (Beam.DEFAULT_BLOCK_LENGTH)
	 * @param password length must be at least MINIMUM_PASSWORD_LENGTH bytes in UTF-8
	 * @throws Exception if an error occurs
	 */
	public Beam(String password) throws Exception {
		this(password, DEFAULT_BLOCK_LENGTH);
	}

	/**
	 * initialized the Beam class with the given password and the default block length (Beam.DEFAULT_BLOCK_LENGTH)
	 * @param password length must be at least MINIMUM_PASSWORD_LENGTH
	 * @throws Exception if an error occurs
	 */
	public Beam(byte[] password) throws Exception {
		this(password, DEFAULT_BLOCK_LENGTH);
	}
	
	/**
	 * initialized the Beam class with the given password and given block length. The block length must be at least Beam.MINIMUM_BLOCK_LENGTH and may not exceed Beam.MAXIMUM_BLOCK_LENGTH.
	 * @param password length must be at least MINIMUM_PASSWORD_LENGTH bytes in UTF-8
	 * @param blockLength length of the blocks
	 * @throws Exception if an error occurs
	 */
	public Beam(String password, int blockLength) throws Exception {
		this(BeamUtil.stringToByteArray(password), blockLength);
	}

	/**
	 * initialized the Beam class with the given password and given block length. The block length must be at least Beam.MINIMUM_BLOCK_LENGTH and may not exceed Beam.MAXIMUM_BLOCK_LENGTH.
	 * @param password length must be at least MINIMUM_PASSWORD_LENGTH
	 * @param blockLength length of the blocks
	 * @throws Exception if an error occurs
	 */
	public Beam(byte[] password, int blockLength) throws Exception {
		if (password.length < MINIMUM_PASSWORD_LENGTH) {
			throw new Exception("Password length must be at least " + MINIMUM_PASSWORD_LENGTH + " but was " + password.length);
		}
		if (blockLength < MINIMUM_BLOCK_LENGTH) {
			throw new Exception("Block length must be at least " + MINIMUM_BLOCK_LENGTH + " but was " + blockLength);
		}
		if (blockLength > MAXIMUM_BLOCK_LENGTH) {
			throw new Exception("Block length must be less or equal to " + MAXIMUM_BLOCK_LENGTH + " but was " + blockLength);
		}
		this.password = password;
		this.blockLength = blockLength;
	}

	/**
	 * Optional method to provide a custom random number generator (e.g. for testing)
	 * @param randomNumberGenerator must implement RandomNumberGenerator interface
	 */
	public void setRandomNumberGenerator(RandomNumberGenerator randomNumberGenerator) {
		this.randomNumberGenerator = randomNumberGenerator;
	}
	
	protected RandomNumberGenerator getRandomNumberGenerator() {
		return randomNumberGenerator;
	}

	
	/**
     * It is optional to call this method. The method gets executed automatically when needed.
	 * Initializes the encryption/decryption. This may take some time (a few milliseconds).
	 * 
	 */
	public void init() {
		if (key != null) {
			//: init has been called already
			return;
		}
		key = new KeyCreator().create(password);
		posSwitchMap = new PosSwitchMapCreator().create(key, blockLength);
		digitSum = BeamUtil.getDigitSum(password);
	}

	/**
	 * encrypts the given string and returns the encrypted bytes as a hex-string
	 * @param input string to encrypt
	 * @return encrypted string as hex-string
	 * @throws Exception if an error occurs
	 */
	public String encrypt(String input) throws Exception {
		byte[] inputBytes = BeamUtil.stringToByteArray(input);
		byte[] outputBytes = encrypt(inputBytes);
		return BeamUtil.byteArrayToHexString(outputBytes);
	}

	/**
	 * encrypts the given hex-string and returns the plain-text string
	 * @param input string to decrypt as hex-string
	 * @return decrypted string
	 * @throws Exception if an error occurs
	 */
	public String decrypt(String input) throws Exception {
		byte[] inputBytes = BeamUtil.hexStringToByteArray(input);
		byte[] outputBytes = decrypt(inputBytes);
		return BeamUtil.byteArrayToString(outputBytes);
	}

	/**
	 * encrypts the provided byte array
	 * @param input byte array to be encrypted
	 * @return encrypted byte array
	 * @throws Exception if an error occurs
	 */
	public byte[] encrypt(byte[] input) throws Exception {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
		encrypt(inputStream, result, null);
		return result.toByteArray();
	}
	
	/**
	 * decrypts the provided byte array
	 * @param input byte array to decrypt
	 * @return decrypted byte array
	 * @throws Exception if an error occurs
	 */
	public byte[] decrypt(byte[] input) throws Exception {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(input);
		decrypt(inputStream, result, null);
		return result.toByteArray();
	}
	
	/**
	 * encrypts all bytes available in input and writes the result to output 
	 * @param input input stream to encrypt
	 * @param output output stream to write to
	 * @param listener may be null
	 * @throws Exception if an error occurs
	 */
	public void encrypt(InputStream input, OutputStream output, BeamProgressListener listener) throws Exception{
		init();
		long plainBytesProcessed = 0;
		long encryptedBytesProcessed = 0;
		PlaintextBlockReader reader = new PlaintextBlockReader(input, blockLength, randomNumberGenerator);
		BlockEncryptor encryptor = new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
		Block block = reader.readNextBlock();
		while (block != null) {
			byte[] encryptedBlock = encryptor.process(block.getData());
			output.write(BeamUtil.get2BytesFromPosInt(block.getLength()));
			output.write(encryptedBlock);
			if (listener != null) {
				plainBytesProcessed += block.getLength();
				encryptedBytesProcessed += blockLength;
				listener.bytesProcessed(plainBytesProcessed, encryptedBytesProcessed);
			}
			block = reader.readNextBlock();
		}
		if (listener != null) {
			listener.finishedSuccessfully(plainBytesProcessed, encryptedBytesProcessed);
		}
	}

	/**
	 * decrypts all bytes available in input and writes the result to output 
	 * @param input input stream to decrypt
	 * @param output output stream to write to
	 * @param listener may be null
	 * @throws Exception if an error occurs
	 */
	public void decrypt(InputStream input, OutputStream output, BeamProgressListener listener) throws Exception {
		init();
		long plainBytesProcessed = 0;
		long encryptedBytesProcessed = 0;
		EncryptedBlockReader reader = new EncryptedBlockReader(input, blockLength);
		BlockDecryptor decryptor = createBlockDecryptor();
		Block block = reader.readNextBlock();
		while (block != null) {
			byte[] decryptedData = decryptor.process(block);
			output.write(decryptedData);
			if (listener != null) {
				plainBytesProcessed += block.getLength();
				encryptedBytesProcessed += blockLength;
				listener.bytesProcessed(plainBytesProcessed, encryptedBytesProcessed);
			}
			block = reader.readNextBlock();
		}

		if (listener != null) {
			listener.finishedSuccessfully(plainBytesProcessed, encryptedBytesProcessed);
		}
	}

	/**
	 * @param innerOutputStream the output stream where the encrypted data is written to
	 * @param listener may be null
	 * @return an output stream to which all data is written in an encrypted form
	 */
	public OutputStream getEncryptedOutputStream(OutputStream innerOutputStream, BeamProgressListener listener) {
		return new BeamEncryptedOutputStream(this, innerOutputStream, listener);
	}
	
	/**
	 * 
	 * @param innerInputStream the input stream to the encrypted data
	 * @param listener may be null
	 * @return an input stream that provides all data in a decrypted form
	 */
	public InputStream getDecryptedInputStream(InputStream innerInputStream, BeamProgressListener listener) {
		return new BeamDecryptedInputStream(this, innerInputStream, listener);
	}

	protected BlockDecryptor createBlockDecryptor() {
		init();
		return new BlockDecryptor(key, posSwitchMap, digitSum, blockLength);
	}
	
	protected BlockEncryptor createBlockEncryptor() {
		init();
		return new BlockEncryptor(key, posSwitchMap, digitSum, blockLength);
	}

	
	/**
	 * returns the length of the input stream after it would have been decrypted 
	 * @param input input stream to read from 
	 * @param listener may be null
	 * @return the length of the decrypted data
	 * @throws Exception if an error occurs
	 */
	public long getDecryptedLength(InputStream input, BeamProgressListener listener) throws Exception {
		long result = 0;
		long encryptedBytesProcessed = 0;
		init();
		EncryptedBlockReader reader = new EncryptedBlockReader(input, blockLength);
		Block block = reader.readNextBlock(true);
		
		while (block != null) {
			result += block.getLength();
			encryptedBytesProcessed += blockLength;
			if (listener != null) {
				listener.bytesProcessed(result, encryptedBytesProcessed);
			}
			block = reader.readNextBlock(true);
		}
		
		if (listener != null) {
			listener.finishedSuccessfully(result, encryptedBytesProcessed);
		}

		
		return result;
	}
	
	protected int getBlockLength() {
		return blockLength;
	}
	

}
