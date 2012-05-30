package info.chordsml.transformer;

import static org.junit.Assert.assertEquals;
import info.chordsml.ITransformator;

import java.util.Locale;

import org.junit.Test;

public class FileBasedTransformerTest {

	private final Locale locale = new Locale("es", "ES");

	@Test
	public void testSimple() {

		// TODO check properties content; looks strange
		// TODO try to remove properties
		ITransformator t = new FileBasedTransformer(
				FileBasedTransformer.getDefaultFile());
		assertEquals("\\ldots", t.replace("...", locale));
		assertEquals("", t.replace("\t", locale));
	}
}
