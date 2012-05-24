package info.chordsml.terminal;

import info.chordsml.ChordsMl;
import info.chordsml.ISongTransformator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

public class Terminal {

	public static String DEFAULT_DATA_FOLDER = "data";
	public static String DEFAULT_STYLE_FOLDER = "style";
	public static String DEFAULT_OUTPUT_FOLDER = "tex";

	@SuppressWarnings("unused")
	private static final Logger log = LoggerFactory.getLogger(Terminal.class);

	@Inject
	@Nullable
	private static RemoteSongDownload updater;

	@Inject
	private static ISongTransformator songTransformer;

	public static void execCommandArgs(String[] args) {
		JSAP jsap = new JSAP();
		final String update = "up";
		final String help = "help";
		final String prozess = "tex";
		final String outout = "out";
		final String src = "src";

		try {

			Switch swHelp = new Switch(help);
			swHelp.setShortFlag('h');
			swHelp.setLongFlag("help");
			swHelp.setHelp("Show this.");

			jsap.registerParameter(swHelp);

			Switch swUpdate = new Switch(update);
			swUpdate.setShortFlag('u');
			swUpdate.setLongFlag("update");
			swUpdate.setHelp("Updates from Remote");

			jsap.registerParameter(swUpdate);

			Switch swTex = new Switch(prozess);
			swTex.setShortFlag('t');
			swTex.setLongFlag("transform");
			swTex.setHelp("starts transform to tex");

			jsap.registerParameter(swTex);

			FlaggedOption optOut = new FlaggedOption(outout);
			optOut.setStringParser(JSAP.STRING_PARSER);
			optOut.setDefault(DEFAULT_OUTPUT_FOLDER + "/Songbook.tex");
			optOut.setRequired(false);
			optOut.setShortFlag('o');
			optOut.setLongFlag("output");
			optOut.setHelp("Filename for output");

			jsap.registerParameter(optOut);

			FlaggedOption optSrc = new FlaggedOption(src);
			optSrc.setStringParser(JSAP.STRING_PARSER);
			optSrc.setDefault(DEFAULT_DATA_FOLDER);
			optSrc.setRequired(false);
			optSrc.setShortFlag('s');
			optSrc.setLongFlag("src");
			optSrc.setHelp("input folder");

			jsap.registerParameter(optSrc);

			if (false) {
				final String style = "style";
				FlaggedOption optStyle = new FlaggedOption(style);
				optStyle.setStringParser(JSAP.STRING_PARSER);
				optStyle.setDefault(getDefault());
				optStyle.setRequired(false);
				optStyle.setShortFlag('y');
				optStyle.setLongFlag("style");

				jsap.registerParameter(optStyle);
			}

		} catch (JSAPException e) {
			throw new IllegalStateException(e);
		}
		JSAPResult config = jsap.parse(args);
		if (config.getBoolean("help") || !config.success()
				|| !config.getBoolean(prozess)) {
			// TODO jar name/ package
			System.out.println("Usage: java -jar " + "??????.jar " + jsap.getUsage());

			System.out.println();
			System.out.println(jsap.getHelp());
			System.out.println();
		}

		if (config.getBoolean(update)) {
			updater.update();
		}
		if (config.getBoolean(prozess)) {
			File sourceDir = new File(config.getString(src));
			if (!sourceDir.isDirectory()) {
				System.out.println("No songs. Please update; "
						+ sourceDir.getAbsolutePath());
			} else {
				processSongs(config.getString(src), config.getString(outout));
			}
		}
	}

	private static String getStyles() {
		throw new UnsupportedOperationException();
	}

	private static String getDefault() {
		throw new UnsupportedOperationException();
	}

	public static void processSongs(String dataFolder, String out) {

		File outfile = new File(out);

		File songdata = new File(dataFolder);
		File[] files = songdata.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				if (pathname.getName().endsWith(".xml"))
					return true;
				return false;
			}
		});

		for (File file : files) {
			try {
				songTransformer.addSong(Files.toString(file, Charsets.UTF_8));
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}

		Map<File, Object> generate = songTransformer.generate(outfile);
		ChordsMl.writeGenerated(generate);
		System.out.println("to create pdf type: cd "
				+ outfile.getParentFile().getAbsolutePath() + ";" + " pdflatex "
				+ outfile.getName());
	}

}
