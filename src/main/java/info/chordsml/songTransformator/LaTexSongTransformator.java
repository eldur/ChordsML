package info.chordsml.songTransformator;

import info.chordsml.IFilenameGenerator;
import info.chordsml.ISongTransformator;
import info.chordsml.LaTexStyle;
import info.chordsml.ITransformator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;

public class LaTexSongTransformator implements ISongTransformator {

	private final List<String> songRawTexts;

	private final IFilenameGenerator nameGenerator;

	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory
			.getLogger(LaTexSongTransformator.class);

	private final LaTexStyle style;

	private final String songSubFolderName = "songs";

	@Inject
	public LaTexSongTransformator(LaTexStyle style, IFilenameGenerator nameGenerator) {
		this.style = style;
		this.nameGenerator = nameGenerator;
		songRawTexts = Lists.newArrayList();

	}

	/**
	 * {@inheritDoc}
	 */
	public void addSong(String song) {

		songRawTexts.add(song);
	}

	private static String readFromFile(File f) {
		try {
			return Files.toString(f, Charsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Map<File, Object> generate(File outFile) {
		Map<File, Object> outputMap = Maps.newHashMap();
		StringBuilder result = new StringBuilder();

		File parentFile = outFile.getParentFile();
		Iterable<File> misc = style.getMisc();

		for (File f : misc) {
			outputMap.put(new File(parentFile, f.getName()), f);
		}
		File texHeader = style.getTexHeader();
		if (texHeader != null) {
			result.append(readFromFile(texHeader));
		}

		Map<String, String> filenameSongMap = Maps.newHashMap();
		ITransformator repl = style.getSongTransformator();
		for (String rawText : songRawTexts) {

			String filename = nameGenerator.generate(rawText);
			if (repl != null) {
				Locale locale = Locale.getDefault(); // TODO get song locale or overide
																							// by style
				rawText = repl.replace(rawText, locale);
			}
			filenameSongMap.put(filename, rawText);
		}

		for (Entry<String, String> entry : filenameSongMap.entrySet()) {

			File to = new File(outFile.getParent() + "/" + songSubFolderName + "/"
					+ entry.getKey());

			outputMap.put(to, entry.getValue());
			result.append("\\input{" + songSubFolderName + "/" + entry.getKey()
					+ "}\n");

		}
		File texFooter = style.getTexFooter();
		if (texFooter != null) {
			result.append(readFromFile(texFooter));
		}
		outputMap.put(outFile, result.toString());

		return outputMap;
	}

}
