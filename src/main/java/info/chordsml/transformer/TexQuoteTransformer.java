package info.chordsml.transformer;

import info.chordsml.QuoteException;
import info.chordsml.ITransformator;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

public class TexQuoteTransformer implements ITransformator {

	private static final String quoteReplaceExpr = "\"[a-zA-Z\\s\\n\\\\\\[\\\\\\]\\W]+\"";

	@SuppressWarnings("unused")
	private static Logger log = LoggerFactory
			.getLogger(TexQuoteTransformer.class);

	private static final Map<Locale, QuoteSigns> localeQuotes;

	static {
		Map<Locale, QuoteSigns> tmp = Maps.newHashMap();
		tmp.put(Locale.GERMANY, new QuoteSigns("\\\\glqq ", "\\\\grqq~ "));

		localeQuotes = Collections.unmodifiableMap(tmp);
	}

	private static final Pattern pattern = Pattern.compile("([ ]*)\"([ ]*)",
			Pattern.MULTILINE);

	public String replace(final String s, Locale locale) {

		String buff = s;
		buff = removeRepeatParBreaks(s);

		String beginQuote;
		String endQuote;
		QuoteSigns qSigns = localeQuotes.get(locale);
		if (qSigns != null) {
			beginQuote = qSigns.getBegin();
			endQuote = qSigns.getEnd();
		} else {
			// define in tex
			beginQuote = "\\\\olqq ";
			endQuote = "\\\\orqq ";
		}
		buff = replaceRegExpres(quoteReplaceExpr, beginQuote, endQuote, buff);
		return buff;
	}

	private String removeRepeatParBreaks(final String s) {
		return s.replaceAll("\n\\\\end\\{RepeatPar\\}", "\\\\end\\{RepeatPar\\}");
	}

	private String replaceRegExpres(String expression, String beginQuote,
			String endQuote, String text) {

		Matcher matcher = pattern.matcher(text);

		StringBuffer sBuilder = new StringBuffer();
		int foundQuoteCount = 0;
		while (matcher.find()) {
			foundQuoteCount++;
			if (foundQuoteCount % 2 == 0) {
				checkResult(matcher, 1);
				matcher.appendReplacement(sBuilder, endQuote);
			} else {
				checkResult(matcher, 2);
				matcher.appendReplacement(sBuilder, matcher.group(1) + beginQuote);
			}
		}
		matcher.appendTail(sBuilder);
		if (foundQuoteCount % 2 != 0) {
			throw new QuoteException("Odd number of quotes");
		}

		String text1 = sBuilder.toString();
		text1 = text1.replaceAll("[ ]{2,}", " ");

		text1 = text1.replaceAll("^\"", beginQuote).replaceAll("\"$", endQuote);

		text1 = text1.replaceAll("\\\\par\\n\\\\end\\{RepeatPar\\}",
				"\\\\end{RepeatPar}\\\\par\n");

		return text1;

	}

	private void checkResult(Matcher matcher, int grp) {
		if (matcher.group(grp).length() > 0) {
			throw new QuoteException(matcher.group());
		}
	}

	private static class QuoteSigns {
		private final String begin;
		private final String end;

		public QuoteSigns(String begin, String end) {
			this.begin = begin;
			this.end = end;

		}

		public String getBegin() {
			return begin;
		}

		public String getEnd() {
			return end;
		}

	}

}
