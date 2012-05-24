package info.chordsml;

import java.io.File;
import java.util.Map;

public interface ISongTransformator {

	/**
	 * @param chordsMltext
	 *          data as raw xml, not as filename
	 */
	void addSong(String chordsMltext);

	/**
	 * 
	 * @param outfile
	 * @return destination file -> source
	 * 
	 */
	Map<File, Object> generate(File outfile);

}
