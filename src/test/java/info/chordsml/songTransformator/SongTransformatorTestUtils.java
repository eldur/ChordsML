package info.chordsml.songTransformator;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import info.chordsml.ChordsMlTest;
import info.chordsml.LaTexStyle;
import info.chordsml.ITransformator;
import info.chordsml.TransformatorChain;
import info.chordsml.transformer.FileBasedTransformer;
import info.chordsml.transformer.TexQuoteTransformer;
import info.chordsml.transformer.XslTransformer;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class SongTransformatorTestUtils {

	static void execLatex(File out2, String expected) {
		assertTrue(out2.exists());
		Runtime rt = Runtime.getRuntime();
		try {
			Process process = rt.exec(new String[] { "pdflatex", out2.getName() },
					new String[] {}, out2.getParentFile());
			new TimeKiller(process, 10000).start();
			process.waitFor();

			System.err.println(CharStreams.toString(new InputStreamReader(process
					.getErrorStream())));
			String stdOut = CharStreams.toString(new InputStreamReader(process
					.getInputStream()));
			System.out.println(stdOut);
			assertTrue(stdOut.contains(expected));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		} finally {
		}
	}

	private static class TimeKiller extends Thread {

		private final Process process;
		private final int millis;

		public TimeKiller(Process process, int millis) {
			this.process = process;
			this.millis = millis;
		}

		@Override
		public void run() {
			try {
				synchronized (this) {

					wait(millis);
				}
			} catch (InterruptedException e) {
				interrupt();
			}
			process.destroy();
		}

	}

	public static LaTexStyle newMockStyle(ITransformator transformator) {
		LaTexStyle style = mock(LaTexStyle.class);
		File texHeader = getFileFromCP("style/header.tex");
		texHeader.getClass();
		when(style.getTexHeader()).thenReturn(texHeader);

		File texFooter = getFileFromCP("style/footer.tex");
		texFooter.getClass();
		when(style.getTexFooter()).thenReturn(texFooter);

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

		when(style.getSongTransformator()).thenReturn(transformator);
		return style;
	}

	public static LaTexStyle newXmlMockStyle() {
		ITransformator transformatorChain = new TransformatorChain(
				new XslTransformer(getFileFromCP("style/test.xslt")),
				new FileBasedTransformer(FileBasedTransformer.getDefaultFile()),
				new TexQuoteTransformer());
		return newMockStyle(transformatorChain);
	}

	static File getFileFromCP(String path) {
		try {
			return new File(Resources.getResource(path).toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	static String readFromFile(File f) {
		try {
			return Files.toString(f, Charsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}
}
