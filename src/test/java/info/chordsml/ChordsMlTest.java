package info.chordsml;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class ChordsMlTest {

	public static final File TESTFOLDER = new File("target/livetst");
	
	@Test
	public void test() throws URISyntaxException, IOException {
		URL schema = ChordsMl.getSchema();
		schema.getClass();
		String readLines = "";
		String localXsd = "local";

		localXsd = Files.toString(new File(Resources.getResource("ChordsML100")
				.toURI()), Charsets.UTF_8);
		readLines = Files.toString(new File(ChordsMl.getSchemaUri()),
				Charsets.UTF_8);

		assertEquals(localXsd, readLines);

	}
}
