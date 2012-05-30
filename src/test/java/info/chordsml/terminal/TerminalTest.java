package info.chordsml.terminal;

import info.chordsml.ChordsMlTest;
import info.chordsml.XmlNameGenerator;
import info.chordsml.IFilenameGenerator;
import info.chordsml.ISongTransformator;
import info.chordsml.LaTexStyle;
import info.chordsml.songTransformator.SongTransformatorTestUtils;
import info.chordsml.songTransformator.LaTexSongTransformator;

import java.io.File;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Provider;
import com.google.inject.util.Providers;

public class TerminalTest {

	static {
		Guice.createInjector(new AbstractModule() {

			@Override
			protected void configure() {
				bind(LaTexStyle.class).toInstance(
						SongTransformatorTestUtils.newXmlMockStyle());

				Provider<RemoteSongDownload> provider = Providers.of(null);
				bind(RemoteSongDownload.class).toProvider(provider);

				bind(ISongTransformator.class).to(LaTexSongTransformator.class);

				bind(IFilenameGenerator.class).to(XmlNameGenerator.class);
				requestStaticInjection(Terminal.class);
			}

		});
	}

	@Test
	public void test() {
		File terminalOut = new File(ChordsMlTest.TESTFOLDER, "terminal.tex");
		String[] args = { "-t", "-s", "src/test/resources", "-o",
				terminalOut.getAbsolutePath() };
		Terminal.execCommandArgs(args);

	}

	public static void main(String[] args) {
		Terminal.execCommandArgs(args);
	}
}
