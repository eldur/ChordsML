package info.chordsml;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;

public class ChordsMl {

	private static final String schemaRef = "https://raw.github.com/eldur/ChordsML/master/src/main/resources/ChordsML100";
	private static final File SONGML_XSD;
	private static final Logger log = LoggerFactory.getLogger(ChordsMl.class);

	static {
		try {
			SONGML_XSD = File.createTempFile(ChordsMl.class.getSimpleName() + "_", ".xsd");
			SONGML_XSD.deleteOnExit();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	public static final URL getSchemaRef() {
		try {
			return new URL(schemaRef);
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	public static URI getSchemaUri() {
		try {
			return getSchema().toURI();
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	public static URL getSchema() {
		if (!SONGML_XSD.exists() || SONGML_XSD.length() < 5) {

			try {
				File folder = SONGML_XSD.getParentFile();
				if (!folder.exists() || !folder.isDirectory())
					folder.mkdirs();
				if (!SONGML_XSD.exists())
					SONGML_XSD.createNewFile();
				String remoteFile;
				try {
					remoteFile = Resources.toString(getSchemaRef(), Charsets.UTF_8);

				} catch (IOException e) {
					log.warn("fallback: use local definiton", e);
					remoteFile = Files.toString(
							new File(Resources.getResource("ChordsML100").toURI()),
							Charsets.UTF_8);
				}
				Files.write(remoteFile, SONGML_XSD, Charsets.UTF_8);

			} catch (Exception e) {
				throw new IllegalStateException(e);
			}
		}
		try {
			return SONGML_XSD.toURI().toURL();
		} catch (MalformedURLException e) {
			throw new IllegalStateException(e);
		}
	}

	public static void writeGenerated(Map<File, Object> generate) {
		try {
			for (Entry<File, Object> entry : generate.entrySet()) {
				File to = entry.getKey();
				to.getParentFile().mkdirs();
				Object input = entry.getValue();
				if (input instanceof File) {
					Files.copy((File) input, to);
				} else if (input instanceof CharSequence) {
					CharSequence chars = (CharSequence) input;
					Files.write(chars, to, Charsets.UTF_8);
				} else {
					throw new UnsupportedOperationException("input of type " //
							+ input.getClass().getCanonicalName() //
							+ " is not allowed.");
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}

	}
}
