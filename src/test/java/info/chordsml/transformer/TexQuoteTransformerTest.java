package info.chordsml.transformer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import info.chordsml.QuoteException;
import info.chordsml.Transformator;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;

public class TexQuoteTransformerTest {

	private Transformator r;
	private final Locale locale = Locale.CHINA;

	@Before
	public void setUp() {
		r = new TexQuoteTransformer();
	}

	/**
	 * without quotes
	 */
	@Test
	public void replaceNothing() {
		String a = "89 sjd";
		// ,.:;-_+#''()[]
		assertEquals("89 sjd", r.replace(a, locale));

	}

	/**
	 * quotes without end space
	 */
	@Test
	public void replaceQuotes() {
		String a = "di \"wilde\"";

		assertEquals("di \\olqq wilde\\orqq ", r.replace(a, locale));

	}

	/**
	 * common
	 */
	@Test
	public void replaceQuotesVariant() {
		String a = "asdfsdf di \"wilde\" asdfjhkwe";

		assertEquals("asdfsdf di \\olqq wilde\\orqq asdfjhkwe",
				r.replace(a, locale));
	}

	/**
	 * quotes with newline
	 */
	@Test
	public void replaceQuotesWithNewline() {
		String a = "asdfsdf di \"wil\nde\" asdfjhkwe";

		assertEquals("asdfsdf di \\olqq wil\nde\\orqq asdfjhkwe",
				r.replace(a, locale));
	}

	/**
	 * two quote blocks
	 * 
	 */
	@Test
	public void replaceQuotesMulti() {
		String a = "a\"wil\"s \"sdf\" sd";

		assertEquals("a\\olqq wil\\orqq s \\olqq sdf\\orqq sd",
				r.replace(a, locale));
	}

	/**
	 * with comma
	 */
	@Test
	public void replaceQuotesWithComma() {
		String a = "a\"a , s\"s";

		assertEquals("a\\olqq a , s\\orqq s", r.replace(a, locale));
	}

	/**
	 * with comma and spaces
	 */
	@Test
	public void replaceQuotesWithCommaVariant() {
		String a = "a \"a , s\" s";

		assertEquals("a \\olqq a , s\\orqq s", r.replace(a, locale));
	}

	/**
	 * with special chars
	 */
	@Test
	public void replaceSpecialChars() {
		String a = "a \"a ,\t.:;-_+#''()[] s\" s";

		assertEquals("a \\olqq a ,\t.:;-_+#''()[] s\\orqq s", r.replace(a, locale));
	}

	/**
	 * comma, space, newline
	 */
	@Test
	public void replaceQuotesMultiline() {
		String a = "a \"a ,\n s\" s";

		assertEquals("a \\olqq a ,\n s\\orqq s", r.replace(a, locale));
	}

	/**
	 * with umlaut
	 */
	@Test
	public void replaceUmlaute() {
		String a = "a \"a ,öäüÖÄÜß s\" s";

		assertEquals("a \\glqq a ,öäüÖÄÜß s\\grqq~ s", r.replace(a, Locale.GERMANY));
	}

	/**
	 * two quote signs without space TODO maybe invalid
	 */
	@Test
	public void replaceMultiQuote() {
		String a = "\"\"";

		assertEquals("\\olqq \\orqq ", r.replace(a, locale));
	}

	/**
	 * single quote
	 */
	@Test(expected = QuoteException.class)
	public void replaceSingleQuote() {
		String a = "\"";

		assertEquals("\\olqq ", r.replace(a, locale));
	}

	/**
	 * three quotes
	 */
	@Test(expected = QuoteException.class)
	public void replaceOddNummerOfQuotes() {
		String a = "\"Hallo\" du peter \" ws";

		assertEquals("\\olqq Hallo\\orqq a peter \\olqq ws", r.replace(a, locale));
	}

	/**
	 * three quotes with newline
	 */
	@Test(expected = QuoteException.class)
	public void replaceOddNumbersOfQuotesVari() {
		String a = "\"Hallo\" a \npeter \"ws";

		assertEquals("\\olqq Hallo\\orqq a \npeter \\olqq ws", r.replace(a, locale));
	}

	/**
	 * three quotes without space
	 */
	@Test(expected = QuoteException.class)
	public void replaceThreeQuotes() {
		String a = "\"\"\"";

		assertEquals("\\olqq \\orqq \\olqq ", r.replace(a, locale));
	}

	/**
	 * space around quotes
	 */
	public void relplaceQuotesWithWhitespace() {
		String a = "space around \" quotes \" , => not good";
		try {
			r.replace(a, locale);
			fail("no exception occured, but have to");
		} catch (QuoteException e) {
			assertEquals("no", e.getLocalizedMessage().toString());
		}
	}

	/**
	 * comma visibilty in tex, after quote
	 * 
	 */
	@Test
	public void replaceCommaVisibility() {
		String a = "ne \"h\", nur";

		assertEquals("ne \\olqq h\\orqq , nur", r.replace(a, locale));
	}

	/**
	 * test tab replacement
	 */
	@Test
	public void replaceTab() {
		String a = " \tW\"h\", nur";

		assertEquals(" \tW\\olqq h\\orqq , nur", r.replace(a, locale));
	}

}
