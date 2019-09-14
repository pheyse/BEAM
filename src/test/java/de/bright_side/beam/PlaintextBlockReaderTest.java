package de.bright_side.beam;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.Test;

public class PlaintextBlockReaderTest {

	@Test
	public void readNextBlock_fullBlock() throws Exception {
		InputStream input = new ByteArrayInputStream(TestUtil.byteArray(0, 10, 20, 30, 40, 50, 60));
		PlaintextBlockReader reader = new PlaintextBlockReader(input, 5, new FakeRandomNumberGenerator(TestUtil.byteArray(90, 91, 92, 93)));
		
		Block result = reader.readNextBlock();
		
		assertEquals(5, result.getLength());
		assertEquals(TestUtil.bytesToString(0, 10, 20, 30, 40), TestUtil.toString(result.getData()));
	}
	
	@Test
	public void readNextBlock_lastBlock() throws Exception {
		InputStream input = new ByteArrayInputStream(TestUtil.byteArray(0, 10, 20, 30, 40));
		PlaintextBlockReader reader = new PlaintextBlockReader(input, 5, new FakeRandomNumberGenerator(TestUtil.byteArray(90, 91, 92, 93)));
		
		Block result = reader.readNextBlock();
		
		assertEquals(5, result.getLength());
		assertEquals(TestUtil.bytesToString(0, 10, 20, 30, 40), TestUtil.toString(result.getData()));
	}
	
	@Test
	public void readNextBlock_noDataInInputStream() throws Exception {
		InputStream input = new ByteArrayInputStream(TestUtil.byteArray());
		PlaintextBlockReader reader = new PlaintextBlockReader(input, 5, new FakeRandomNumberGenerator(TestUtil.byteArray(90, 91, 92, 93, 94)));
		
		Block result = reader.readNextBlock();
		
		assertNull(result);
	}
	
	@Test
	public void readNextBlock_blockNotFull() throws Exception {
		InputStream input = new ByteArrayInputStream(TestUtil.byteArray(0, 10));
		PlaintextBlockReader reader = new PlaintextBlockReader(input, 5, new FakeRandomNumberGenerator(TestUtil.byteArray(90, 91, 92, 93, 94)));
		
		Block result = reader.readNextBlock();
		assertEquals(2, result.getLength());
		assertEquals(TestUtil.bytesToString(0, 10, 90, 91, 92), TestUtil.toString(result.getData()));
	}
	
}
