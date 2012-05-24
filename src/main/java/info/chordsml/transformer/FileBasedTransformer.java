package info.chordsml.transformer;

import info.chordsml.Transformator;

import java.io.File;
import java.io.FileInputStream;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.regex.PatternSyntaxException;

import com.google.common.io.Resources;

public class FileBasedTransformer implements Transformator {

	private final Properties p = new Properties();

	public FileBasedTransformer(File propertieXmlFile) {
		load(propertieXmlFile);
	}

	public void load(File properties) {
		try {
			p.loadFromXML(new FileInputStream(properties));
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	public final String replace(String text, Locale locale) {

		Enumeration<?> keys = p.keys();
		while (keys.hasMoreElements()) {
			Object k = keys.nextElement();
			String[] rp = p.get(k).toString().split(":::");

			String search = rp[0].substring(1, rp[0].length() - 1);
			String replace = "";
			try {
				replace = rp[1].substring(1, rp[1].length() - 1);
			} catch (ArrayIndexOutOfBoundsException e) {
				replace = "";
			}

			try {
				text = text.replaceAll(search, replace);
			} catch (PatternSyntaxException e) {
				throw e;
			}
		}
		return text;
	}

	public static File getDefaultFile() {
		File propertieXmlFile;
		try {
			propertieXmlFile = new File(Resources.getResource(
					"info/chordsml/transformer/simpleReplace.xml").toURI());
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
		return propertieXmlFile;
	}

}
