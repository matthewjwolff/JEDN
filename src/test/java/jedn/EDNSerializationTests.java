package jedn;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import io.wolff.jedn.EDN;

public class EDNSerializationTests {
	
	private static void assertEDN(String expected, Object actual) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			EDN.serializeEDN(bos, actual);
			assertEquals(expected, bos.toString());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Test
	public void testBuiltins() {
		assertEDN("nil", null);
		assertEDN("12", 12);
		assertEDN("12.5", 12.5);
		assertEDN("-12", -12);
		assertEDN("-12.5", -12.5);
		assertEDN("\\f", 'f');
		assertEDN("\\newline", '\n');
		assertEDN("\\space", ' ');
		assertEDN("\\return", '\r');
		assertEDN("\\tab", '\t');
	}

}
