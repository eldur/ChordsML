package info.chordsml.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import info.chordsml.ChordsMl;
import info.chordsml.ChordsMlTest;
import info.chordsml.DefaultNameGenerator;
import info.chordsml.ISongTransformator;
import info.chordsml.LaTexStyle;
import info.chordsml.TransformatorChain;
import info.chordsml.transformer.FileBasedTransformer;
import info.chordsml.transformer.TexQuoteTransformer;
import info.chordsml.transformer.XslTransformer;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class XmlSongTransformatorTest {

	private static String xml1 = "";
	private static String xml1fail = "";
	private static String xmlDec = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	private ISongTransformator sti;

	@BeforeClass
	public static void setUpBeforeClass() {
		xml1 = "<chordsML:song title=\"text\" composer=\"text\" "
				+ "licence=\"text\" "
				+ "xsi:schemaLocation=\"https://raw.github.com/eldur/ChordsML/master/src/main/resources/ChordsML100 songml.xsd\""
				+ " xmlns:chordsML=\"https://raw.github.com/eldur/ChordsML/master/src/main/resources/ChordsML100\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<meta>	<index type=\"begin\">String</index> </meta> <text>"
				+ "<verse type=\"chorus\">text</verse></text></chordsML:song>";

		xml1fail = "<chordsML:song title=\"text\" composer=\"text\" "
				+ "licence=\"text\" "
				+ "xsi:schemaLocation=\"https://raw.github.com/eldur/ChordsML/master/src/main/resources/ChordsML100 songml.xsd\""
				+ " xmlns:chordsML=\"https://raw.github.com/eldur/ChordsML/master/src/main/resources/ChordsML100\""
				+ " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">"
				+ "<meta>	<index type=\"begin\">String</index> </meta> <text>"
				+ "<verse type=\"chorrus\">text</verse></text></chordsML:song>";
	}

	private File out;

	@Before
	public void setUp() {
		out = new File(ChordsMlTest.TESTFOLDER, "out.tex");
		out.delete();
		LaTexStyle style = mock(LaTexStyle.class);

		sti = new XmlSongTransformator(style, new DefaultNameGenerator());
	}

	@Test
	public void addSongFail1() {
		sti.addSong("sdfj");
		try {
			sti.generate(out);
			fail();
		} catch (IllegalStateException e) {
			assertEquals("Problem with content \"sdfj...\"", e.getMessage());
		}
	}

	@Test(expected = IllegalStateException.class)
	public void addSongFail2() {
		sti.addSong(xml1fail);
		sti.generate(out);
	}

	@Test
	public void testCompleteGenerate() {
		LaTexStyle style = mock(LaTexStyle.class);
		File texHeader = getFileFromCP("style/header.tex");
		texHeader.getClass();
		when(style.getTexHeader()).thenReturn(texHeader);

		File texFooter = getFileFromCP("style/footer.tex");
		texFooter.getClass();
		when(style.getTexFooter()).thenReturn(texFooter);
		String x = readFromFile(getFileFromCP("validTestSong.xml"));

		String songStyUrl = "http://prdownloads.sourceforge.net/songs/songs.sty?download";
		File songStyFile = new File(ChordsMlTest.TESTFOLDER, "songs.sty");
		try {
			String songSty = Resources.toString(new URL(songStyUrl), Charsets.UTF_8);
			Files.write(songSty, songStyFile, Charsets.UTF_8);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
		when(style.getSongTransformator()).thenReturn(
				new TransformatorChain(new XslTransformer(
						getFileFromCP("style/test.xslt")), new FileBasedTransformer(
						FileBasedTransformer.getDefaultFile()), new TexQuoteTransformer()));
		sti = new XmlSongTransformator(style, new DefaultNameGenerator());
		sti.addSong(x);
		ChordsMl.writeGenerated(sti.generate(out));
		// TODO incomplete
		System.out.println("break here, and execute for e.g. pdflatex");
	}

	private File getFileFromCP(String path) {
		try {
			return new File(Resources.getResource(path).toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	@Test
	public void addSongValid2() {
		sti.addSong(xml1);
		sti.generate(out);
	}

	@Test
	public void addSongValid3() {
		sti.addSong(xmlDec + xml1);
		Map<File, Object> generate = sti.generate(out);
		List<File> generatedFileList = Lists.newArrayList(generate.keySet());
		Collections.sort(generatedFileList);
		List<File> expected = ImmutableList.of(new File(ChordsMlTest.TESTFOLDER,
				"out.tex"), new File(ChordsMlTest.TESTFOLDER, "songs/text.tex"));
		assertEquals(expected, generatedFileList);
	}

	@Test
	public void addSongValid4() {

		String com = "<!--XML-commentar-->";
		sti.addSong(xmlDec + com + xml1);
		sti.generate(out);
	}

	private static String readFromFile(File f) {
		try {
			return Files.toString(f, Charsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}
}
