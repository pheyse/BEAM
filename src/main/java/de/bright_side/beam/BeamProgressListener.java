package de.bright_side.beam;

import java.io.IOException;

/**
 * 
 * @author Philip Heyse
 *
 */
public interface BeamProgressListener {
	void bytesProcessed(long amountPlain, long amountEncrypted);
	void finishedSuccessfully(long amountPlain, long amountEncrypted) throws IOException;
}
