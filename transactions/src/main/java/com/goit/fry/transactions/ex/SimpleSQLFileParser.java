package com.goit.fry.transactions.ex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleSQLFileParser {

	private static final Logger logger = LogManager.getRootLogger();
	private final HashSet<String> patternHashes;
	private final ArrayList<Pattern> patterns;
	Pattern commentBegin, commentEnd, sqlCmdEol;

	public SimpleSQLFileParser() {

		patternHashes = new HashSet<>();
		patterns = new ArrayList<>();

		commentBegin = Pattern.compile("/\\*");
		commentEnd = Pattern.compile("\\*/");
		sqlCmdEol = Pattern.compile("[^;]*;");
	}

	public boolean addPattern(String patternStr) {

		boolean isNew = patternHashes.add(patternStr);
		if (isNew) patterns.add(Pattern.compile(patternStr));
		else
			logger.warn("the pattern has already been added: " + patternStr);

		return isNew;
	}

	public String findNext(BufferedReader reader) throws Exception {

		Map.Entry<String, Pattern> line_n_pattern = findLineMatchingPattern(reader);
		if (line_n_pattern == null) return null;
		String line = line_n_pattern.getKey();
		Pattern matchedPattern = line_n_pattern.getValue();

		StringBuilder sqlCommand = new StringBuilder();
		sqlCommand.append(line);
		addSpaceIfNecessary(sqlCommand, line);

		Matcher mEol = sqlCmdEol.matcher(line);
		while (!mEol.find() &&	(line = reader.readLine()) != null) {

			for (Pattern pattern : patterns) {
				if (pattern == matchedPattern) continue;
				Matcher m = pattern.matcher(line);
				if (m.find())
					throw new Exception("wrong SQL-command: " +
							sqlCommand + line + ". Probably semicolon is missing");
			}

			line = line.trim();
			sqlCommand.append(line);
			addSpaceIfNecessary(sqlCommand, line);
			mEol.reset(line);
		}
		if (line == null)
			throw new Exception("no semicolon at the end: " + sqlCommand);

		return sqlCommand.toString();
	}

	private Map.Entry<String, Pattern> findLineMatchingPattern(BufferedReader reader) throws IOException {

		boolean insideComment = false;
		String line = null;
		Matcher mCommBegin = commentBegin.matcher("");
		Matcher mCommEnd = commentEnd.matcher("");

		while ((line = reader.readLine()) != null) {

			if (!insideComment) {
				mCommBegin.reset(line);
				insideComment = mCommBegin.find();
			}
			for (int i = 0; !insideComment && i < patterns.size(); i++) {
				Matcher m = patterns.get(i).matcher(line);
				if (m.find())
					return new AbstractMap.SimpleEntry<>(line, patterns.get(i));
			}
			if (insideComment) {
				mCommEnd.reset(line);
				insideComment = !mCommEnd.find();
			}
		}

		return null;
	}

	private void addSpaceIfNecessary(StringBuilder sqlCommand, String line) {

		if (line.length() == 0) return;

		char line_end = line.charAt(line.length() - 1);
		if (line_end != ';' && line_end != ' ')
			sqlCommand.append(' ');
	}
}
