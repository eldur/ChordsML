package info.chordsml.terminal;

import info.chordsml.ChordsMlTest;
import info.chordsml.DefaultNameGenerator;
import info.chordsml.INameGenerator;
import info.chordsml.ISongTransformator;
import info.chordsml.LaTexStyle;
import info.chordsml.songTransformator.XmlSongTransformator;
import info.chordsml.songTransformator.XmlSongTransformatorTest;

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
						XmlSongTransformatorTest.newMockStyle());

				Provider<RemoteSongDownload> provider = Providers.of(null);
				bind(RemoteSongDownload.class).toProvider(provider);

				bind(ISongTransformator.class).to(XmlSongTransformator.class);

				bind(INameGenerator.class).to(DefaultNameGenerator.class);
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
