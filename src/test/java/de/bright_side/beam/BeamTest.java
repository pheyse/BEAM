package de.bright_side.beam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.Test;

public class BeamTest{
	private static final int DATA_IN_BLOCK_SIZE_LENGTH = 2;
	private static final int HEX_CHARS_PER_BYTE = 2;
	private static final boolean ENABLE_LOGGING = true;
	
	@Test
	public void encryptAndDecryptBytes_simple() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		log("encryptAndDecryptBytes_simple: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(Beam.DEFAULT_BLOCK_LENGTH + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptBytes_partOfBlock() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		log("encryptAndDecryptBytes_smallBlockThatFits: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(blockLength + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptBytes_multipleBlocks() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 128);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainBytes.length / ((double)blockLength))), encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptBytes_exactly1Block() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 31);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptBytes_empty() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = new byte[0];
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertEquals(0, decryptedBytes.length);
		assertEquals(0, encryptedBytes.length);
	}

	@Test
	public void encryptAndDecryptStrings_simple() throws Exception {
		String password = "thePassword";
		String plainText = "This is the plain text.";
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);
		
		String encryptedString = encryptionBeam.encrypt(plainText);
		String decryptedString = decryptionBeam.decrypt(encryptedString);
		
		log("encryptAndDecryptStrings_simple: encryptedString = >>" + encryptedString + "<<");
		
		assertFalse(plainText.equals(encryptedString));
		assertEquals(plainText, decryptedString);
		assertEquals((Beam.DEFAULT_BLOCK_LENGTH + DATA_IN_BLOCK_SIZE_LENGTH) * HEX_CHARS_PER_BYTE, encryptedString.length());
	}
	
	@Test
	public void encryptAndDecryptStrings_partOfBlock() throws Exception {
		String password = "thePassword";
		String plainText = "This is the plain text.";
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		String encryptedString = encryptionBeam.encrypt(plainText);
		String decryptedString = decryptionBeam.decrypt(encryptedString);
		
		log("encryptAndDecryptStrings_partOfBlock: encryptedString = " + encryptedString);

		assertFalse(plainText.equals(encryptedString));
		assertEquals(plainText, decryptedString);
		assertEquals((blockLength + DATA_IN_BLOCK_SIZE_LENGTH) * HEX_CHARS_PER_BYTE, encryptedString.length());
	}
	
	@Test
	public void encryptAndDecryptStrings_multipleBlocks() throws Exception {
		String password = "thePassword";
		String plainText = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut";
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		String encryptedString = encryptionBeam.encrypt(plainText);
		String decryptedString = decryptionBeam.decrypt(encryptedString);

		assertFalse(plainText.equals(encryptedString));
		assertEquals(plainText, decryptedString);
		assertEquals(0, (blockLength + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainText.length() / ((double)blockLength)))) * HEX_CHARS_PER_BYTE, encryptedString.length());
	}
	
	@Test
	public void encryptAndDecryptStrings_exactly1Block() throws Exception {
		String password = "thePassword";
		String plainText = "Lorem ipsum dolor sit amet, cons";
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		String encryptedString = encryptionBeam.encrypt(plainText);
		String decryptedString = decryptionBeam.decrypt(encryptedString);

		assertFalse(plainText.equals(encryptedString));
		assertEquals(plainText, decryptedString);
		assertEquals((blockLength + DATA_IN_BLOCK_SIZE_LENGTH) * HEX_CHARS_PER_BYTE, encryptedString.length());
	}
	
	@Test
	public void encryptAndDecryptStrings_empty() throws Exception {
		String password = "thePassword";
		String plainText = "";
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		String encryptedString = encryptionBeam.encrypt(plainText);
		String decryptedString = decryptionBeam.decrypt(encryptedString);
		
		assertEquals(0, encryptedString.length());
		assertEquals(0, decryptedString.length());
	}

	@Test
	public void encryptAndDecryptStreams_simple() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);

		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream decryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		decryptionBeam.decrypt(new ByteArrayInputStream(encryptedBytes), decryptionOutputStream, null);
		byte[] decryptedBytes = decryptionOutputStream.toByteArray();
		
		log("encryptAndDecryptStreams_simple: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(Beam.DEFAULT_BLOCK_LENGTH + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptStreams_partOfBlock() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream decryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		decryptionBeam.decrypt(new ByteArrayInputStream(encryptedBytes), decryptionOutputStream, null);
		byte[] decryptedBytes = decryptionOutputStream.toByteArray();
		
		log("encryptAndDecryptStreams_partOfBlock: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(blockLength + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptStreams_multipleBlocks() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 128);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream decryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		decryptionBeam.decrypt(new ByteArrayInputStream(encryptedBytes), decryptionOutputStream, null);
		byte[] decryptedBytes = decryptionOutputStream.toByteArray();
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainBytes.length / ((double)blockLength))), encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptStreams_exactly1Block() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 31);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream decryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		decryptionBeam.decrypt(new ByteArrayInputStream(encryptedBytes), decryptionOutputStream, null);
		byte[] decryptedBytes = decryptionOutputStream.toByteArray();
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptAndDecryptStreams_empty() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = new byte[0];
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		ByteArrayOutputStream decryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		decryptionBeam.decrypt(new ByteArrayInputStream(encryptedBytes), decryptionOutputStream, null);
		byte[] decryptedBytes = decryptionOutputStream.toByteArray();
		
		assertEquals(0, decryptedBytes.length);
		assertEquals(0, encryptedBytes.length);
	}


	@Test
	public void getDecryptedLength_simple() throws Exception{
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);

		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		long decryptedLength = decryptionBeam.getDecryptedLength(new ByteArrayInputStream(encryptedBytes), null);
		
		assertEquals(plainBytes.length, decryptedLength);
	}
	
	@Test
	public void getDecryptedLength_partOfBlock() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		long decryptedLength = decryptionBeam.getDecryptedLength(new ByteArrayInputStream(encryptedBytes), null);
		
		assertEquals(plainBytes.length, decryptedLength);
	}
	
	@Test
	public void getDecryptedLength_multipleBlocks() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 128);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		long decryptedLength = decryptionBeam.getDecryptedLength(new ByteArrayInputStream(encryptedBytes), null);
		
		assertEquals(plainBytes.length, decryptedLength);
	}
	
	@Test
	public void getDecryptedLength_exactly1Block() throws Exception {
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 31);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		long decryptedLength = decryptionBeam.getDecryptedLength(new ByteArrayInputStream(encryptedBytes), null);
		
		assertEquals(plainBytes.length, decryptedLength);
	}

	@Test
	public void getDecryptedLength_empty() throws Exception{
		byte[] password = "thePassword".getBytes("UTF-8");
		byte[] plainBytes = new byte[0];
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);
		
		ByteArrayOutputStream encryptionOutputStream = new ByteArrayOutputStream();
		encryptionBeam.encrypt(new ByteArrayInputStream(plainBytes), encryptionOutputStream, null);
		byte[] encryptedBytes = encryptionOutputStream.toByteArray();
		long decryptedLength = decryptionBeam.getDecryptedLength(new ByteArrayInputStream(encryptedBytes), null);
		
		assertEquals(plainBytes.length, decryptedLength);
	}

/* =============================================================================================================== */
	
	@Test
	public void encryptedOutputStream_simple() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		OutputStream stream = encryptionBeam.getEncryptedOutputStream(encryptedOutputStream, null);
		stream.write(plainBytes);
		stream.close();
		byte[] encryptedBytes = encryptedOutputStream.toByteArray(); 
		log("encryptedOutputStream_simple: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(Beam.DEFAULT_BLOCK_LENGTH + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
	}
	
	@Test
	public void encryptedOutputStream_partOfBlock() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		OutputStream stream = encryptionBeam.getEncryptedOutputStream(encryptedOutputStream, null);
		stream.write(plainBytes);
		stream.close();
		byte[] encryptedBytes = encryptedOutputStream.toByteArray(); 
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		log("encryptedOutputStream_partOfBlock: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(blockLength + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptedOutputStream_multipleBlocks() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 128);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		OutputStream stream = encryptionBeam.getEncryptedOutputStream(encryptedOutputStream, null);
		stream.write(plainBytes);
		stream.close();
		byte[] encryptedBytes = encryptedOutputStream.toByteArray(); 
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainBytes.length / ((double)blockLength))), encryptedBytes.length);
	}
	
	@Test
	public void encryptedOutputStream_multipleBlocksMultipleWrites() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 128);
		byte[] plainBytesFirstHalf = TestUtil.byteArrayFromRange(0, 64);
		byte[] plainBytesSecondHalf = TestUtil.byteArrayFromRange(65, 128);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		OutputStream outputStream = encryptionBeam.getEncryptedOutputStream(encryptedOutputStream, null);
		outputStream.write(plainBytesFirstHalf);
		outputStream.write(plainBytesSecondHalf);
		outputStream.close();
		byte[] encryptedBytes = encryptedOutputStream.toByteArray(); 
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainBytes.length / ((double)blockLength))), encryptedBytes.length);
	}
	
	@Test
	public void encryptedOutputStream_exactly1Block() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 31);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		OutputStream stream = encryptionBeam.getEncryptedOutputStream(encryptedOutputStream, null);
		stream.write(plainBytes);
		stream.close();
		byte[] encryptedBytes = encryptedOutputStream.toByteArray(); 
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void encryptedOutputStream_empty() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = new byte[0];
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		ByteArrayOutputStream encryptedOutputStream = new ByteArrayOutputStream();
		OutputStream stream = encryptionBeam.getEncryptedOutputStream(encryptedOutputStream, null);
		stream.write(plainBytes);
		stream.close();
		byte[] encryptedBytes = encryptedOutputStream.toByteArray(); 
		byte[] decryptedBytes = decryptionBeam.decrypt(encryptedBytes);
		
		assertEquals(0, decryptedBytes.length);
		assertEquals(0, encryptedBytes.length);
	}
	
	/* =============================================================================================================== */
	
	@Test
	public void decryptedInputStream_simple() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		Beam encryptionBeam = new Beam(password);
		Beam decryptionBeam = new Beam(password);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(encryptedBytes);
		InputStream stream = decryptionBeam.getDecryptedInputStream(decryptedInputStream, null);
		byte[] decryptedBytes = BeamUtil.readAllBytes(stream);
		
		log("decryptedInputStream_simple: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(Beam.DEFAULT_BLOCK_LENGTH + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
	}
	
	@Test
	public void decryptedInputStream_partOfBlock() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArray(10, 20, 30, 40, 50, 99, -77);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(encryptedBytes);
		InputStream stream = decryptionBeam.getDecryptedInputStream(decryptedInputStream, null);
		byte[] decryptedBytes = BeamUtil.readAllBytes(stream);
		
		log("encryptedOutputStream_partOfBlock: encryptedBytes = " + TestUtil.toString(encryptedBytes));
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(blockLength + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void decryptedInputStream_multipleBlocks() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 128);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(encryptedBytes);
		InputStream stream = decryptionBeam.getDecryptedInputStream(decryptedInputStream, null);
		byte[] decryptedBytes = BeamUtil.readAllBytes(stream);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainBytes.length / ((double)blockLength))), encryptedBytes.length);
	}
	
	@Test
	public void decryptedInputStream_multipleBlocksMultipleReads() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 127);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(encryptedBytes);
		InputStream stream = decryptionBeam.getDecryptedInputStream(decryptedInputStream, null);
		byte[] decryptedBytesPart1 = BeamUtil.readExactAmountOfBytes(stream, 64);
		byte[] decryptedBytesPart2 = BeamUtil.readExactAmountOfBytes(stream, 64);
		byte[] decryptedBytes = new byte[decryptedBytesPart1.length + decryptedBytesPart2.length];
		System.arraycopy(decryptedBytesPart1, 0, decryptedBytes, 0, decryptedBytesPart1.length);
		System.arraycopy(decryptedBytesPart2, 0, decryptedBytes, decryptedBytesPart1.length, decryptedBytesPart2.length);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + (DATA_IN_BLOCK_SIZE_LENGTH * Math.ceil(plainBytes.length / ((double)blockLength))), encryptedBytes.length);
	}
	
	@Test
	public void decryptedInputStream_exactly1Block() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = TestUtil.byteArrayFromRange(0, 31);
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(encryptedBytes);
		InputStream stream = decryptionBeam.getDecryptedInputStream(decryptedInputStream, null);
		byte[] decryptedBytes = BeamUtil.readAllBytes(stream);
		
		assertFalse(TestUtil.toString(plainBytes).equals(TestUtil.toString(encryptedBytes)));
		assertEquals(TestUtil.toString(plainBytes), TestUtil.toString(decryptedBytes));
		assertEquals(0, plainBytes.length + DATA_IN_BLOCK_SIZE_LENGTH, encryptedBytes.length);
	}
	
	@Test
	public void decryptedInputStream_empty() throws Exception {
		String password = "thePassword";
		byte[] plainBytes = new byte[0];
		int blockLength = 32;
		Beam encryptionBeam = new Beam(password, blockLength);
		Beam decryptionBeam = new Beam(password, blockLength);
		
		byte[] encryptedBytes = encryptionBeam.encrypt(plainBytes);
		ByteArrayInputStream decryptedInputStream = new ByteArrayInputStream(encryptedBytes);
		InputStream stream = decryptionBeam.getDecryptedInputStream(decryptedInputStream, null);
		byte[] decryptedBytes = BeamUtil.readAllBytes(stream);
		
		assertEquals(0, decryptedBytes.length);
		assertEquals(0, encryptedBytes.length);
	}


	@Test
	public void encrypt_simpleString() throws Exception {
		Beam beam = new Beam("myPassword");
		String encrypted = beam.encrypt("my text");
		
		log("encrypt_simple: encrypted = >>" + encrypted + "<<");
	}

	private void log(String message) {
		if (ENABLE_LOGGING) {
			System.out.println("BeamTest> " + message);
		}
	}
	
}
