package info.chordsml;

import info.chordsml.transformer.XslTransformer;

import org.jdom.Document;

public class XmlNameGenerator implements IFilenameGenerator {

	public String generate(String xml) {
		Document document = XslTransformer.makeDocument(xml);
		return document.getRootElement().getAttribute("title").getValue()
				.replaceAll("[\\s]+", " ").replaceAll("\\s", "_")
				+ ".tex";
	}
}
