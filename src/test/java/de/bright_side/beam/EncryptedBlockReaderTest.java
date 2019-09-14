package de.bright_side.beam;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class EncryptedBlockReaderTest {

	@Test
	public void readNextBlock_fullBlock() throws Exception {
		byte[] lengthBytes = BeamUtil.get2BytesFromPosInt(5);
		InputStream input = new ByteArrayInputStream(TestUtil.byteArray(lengthBytes[0], lengthBytes[1], 0, 10, 20, 30, 40, 50, 60));
		
		EncryptedBlockReader reader = new EncryptedBlockReader(input, 5);
		
		Block result = reader.readNextBlock();
		
		assertEquals(5, result.getLength());
		assertEquals(TestUtil.bytesToString(0, 10, 20, 30, 40), TestUtil.toString(result.getData()));
	}

	@Test
	public void readNextBlock_notFullBlock() throws Exception {
		byte[] lengthBytes = BeamUtil.get2BytesFromPosInt(3);
		InputStream input = new ByteArrayInputStream(TestUtil.byteArray(lengthBytes[0], lengthBytes[1], 0, 10, 20, 30, 40, 50, 60));
		
		EncryptedBlockReader reader = new EncryptedBlockReader(input, 5);
		
		Block result = reader.readNextBlock();
		
		assertEquals(3, result.getLength());
		assertEquals(TestUtil.bytesToString(0, 10, 20, 30, 40), TestUtil.toString(result.getData()));
	}
	
}
