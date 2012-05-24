package info.chordsml.transformer;

import info.chordsml.ChordsMl;
import info.chordsml.Transformator;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.parsers.SAXParser;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.Text;
import org.jdom.input.SAXBuilder;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;

public class XslTransformer implements Transformator {

	private final SAXBuilder builder;
	private final File[] xslts;
	private static XslTransformer replacer = new XslTransformer();

	public XslTransformer(File... xslts) {
		this.xslts = xslts;
		builder = new SAXBuilder(SAXParser.class.getCanonicalName(), true);
		builder.setProperty(
				"http://apache.org/xml/properties/schema/external-schemaLocation",
				ChordsMl.getSchemaRef() + " " + ChordsMl.getSchema().toString());
		builder
				.setFeature("http://apache.org/xml/features/validation/schema", true);
	}

	public static Document makeDocument(String xmlString) {
		return replacer.getDocument(xmlString);
	}

	public String replace(String song, Locale locale) {
		org.jdom.Document doc = getDocument(song);
		String songAfterXslt = song;
		for (File xsltFile : xslts) {
			songAfterXslt = transform(doc, xsltFile);
		}
		return songAfterXslt;
	}

	private synchronized org.jdom.Document getDocument(String song) {
		StringReader is = new StringReader(song);

		org.jdom.Document doc = new Document();

		try {
			doc = builder.build(is);
			doc.getClass();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (JDOMException e) {
			throw new IllegalStateException("Problem with content "
					+ String.format("\"%.12s...\"", song), e);
		}
		return doc;
	}

	private String transform(Document doc, File stylesheet) {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();

			Transformer transformer = factory.newTransformer(new StreamSource(
					stylesheet));

			JDOMSource in = new JDOMSource(doc);
			JDOMResult out = new JDOMResult();
			transformer.transform(in, out);

			Text result = (org.jdom.Text) out.getResult().get(0);

			return result.getText();
		} catch (TransformerException e) {
			throw new IllegalStateException("XSLT Transformation failed", e);
		}
	}

}
