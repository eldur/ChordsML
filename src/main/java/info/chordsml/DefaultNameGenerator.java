package info.chordsml;

import org.jdom.Document;

public class DefaultNameGenerator implements INameGenerator {

	public String generate(Document document) {
		return document.getRootElement().getAttribute("title").getValue()
				.replaceAll("[\\s]+", " ").replaceAll("\\s", "_")
				+ ".tex";
	}

}
