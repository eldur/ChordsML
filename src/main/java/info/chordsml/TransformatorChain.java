package info.chordsml;

import java.util.Locale;

import com.google.common.collect.Lists;

public class TransformatorChain implements ITransformator {

	private final Iterable<ITransformator> replacers;

	public TransformatorChain(ITransformator... replacers) {
		this(Lists.newArrayList(replacers));
	}

	public TransformatorChain(Iterable<ITransformator> replacers) {
		this.replacers = replacers;
	}

	public String replace(String text, Locale locale) {
		for (ITransformator replacer : replacers) {
			text = replacer.replace(text, locale);
		}
		return text;
	}

}
