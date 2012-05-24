package info.chordsml;

import java.util.Locale;

import com.google.common.collect.Lists;

public class TransformatorChain implements Transformator {

	private final Iterable<Transformator> replacers;

	public TransformatorChain(Transformator... replacers) {
		this(Lists.newArrayList(replacers));
	}

	public TransformatorChain(Iterable<Transformator> replacers) {
		this.replacers = replacers;
	}

	public String replace(String text, Locale locale) {
		for (Transformator replacer : replacers) {
			text = replacer.replace(text, locale);
		}
		return text;
	}

}
