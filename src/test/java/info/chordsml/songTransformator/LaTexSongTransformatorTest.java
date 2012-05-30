package info.chordsml.songTransformator;

import static info.chordsml.songTransformator.SongTransformatorTestUtils.execLatex;
import static info.chordsml.songTransformator.SongTransformatorTestUtils.getFileFromCP;
import static info.chordsml.songTransformator.SongTransformatorTestUtils.newXmlMockStyle;
import static info.chordsml.songTransformator.SongTransformatorTestUtils.readFromFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import info.chordsml.ChordsMl;
import info.chordsml.ChordsMlTest;
import info.chordsml.XmlNameGenerator;
import info.chordsml.ISongTransformator;
import info.chordsml.LaTexStyle;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

public class LaTexSongTransformatorTest {

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

		sti = new LaTexSongTransformator(style, new XmlNameGenerator());
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
		LaTexStyle style = newXmlMockStyle();
		sti = new LaTexSongTransformator(style, new XmlNameGenerator());
		sti.addSong(readFromFile(getFileFromCP("validTestSong.xml")));
		ChordsMl.writeGenerated(sti.generate(out));

		execLatex(out, "Output written on out.pdf");

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

}
