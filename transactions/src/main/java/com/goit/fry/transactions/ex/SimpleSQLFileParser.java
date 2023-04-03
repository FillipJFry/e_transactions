package com.goit.fry.transactions.ex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleSQLFileParser {

	private static final Logger logger = LogManager.getRootLogger();
	private final HashMap<String, PatternItem> patterns;
	Pattern commentBegin, commentEnd, sqlCmdEol;

	public SimpleSQLFileParser() {

		patterns = new HashMap<>();

		commentBegin = Pattern.compile("/\\*");
		commentEnd = Pattern.compile("\\*/");
		sqlCmdEol = Pattern.compile("[^;]*;");
	}

	public boolean addPattern(String patternStr, String patternExclude, ISQLExecutor executor) {

		assert patternStr != null;

		PatternItem prev = patterns.putIfAbsent(patternStr,
				new PatternItem(Pattern.compile(patternStr),
								patternExclude != null ?
										Pattern.compile(patternExclude) : null, executor));
		if (prev != null)
			logger.warn("the pattern has already been added: " + patternStr);

		return prev == null;
	}

	public boolean addPattern(String patternStr) {

		return addPattern(patternStr, null, null);
	}

	public SQLCommandData findNextEx(BufferedReader reader) throws Exception {

		Map.Entry<String, PatternItem> data = findLineMatchingPattern(reader);
		if (data == null) return null;
		String line = data.getKey();
		Pattern pattern_exclude =  data.getValue().pattern_exclude;

		StringBuilder sqlCommand = new StringBuilder();
		sqlCommand.append(line);
		addSpaceIfNecessary(sqlCommand, line);

		Matcher mEol = sqlCmdEol.matcher(line);
		Matcher mExcl = pattern_exclude != null ? pattern_exclude.matcher(line) : null;
		int row = 0;
		boolean exclFound;
		while (!(exclFound = mExcl != null && mExcl.find() &&
							(row > 0 || mExcl.start() > 0)) &&
				!mEol.find() && (line = reader.readLine()) != null) {

			line = line.trim();
			sqlCommand.append(line);
			addSpaceIfNecessary(sqlCommand, line);

			mEol.reset(line);
			if (mExcl != null) mExcl.reset(line);
			row++;
		}
		if (line == null)
			throw new Exception("no semicolon at the end: " + sqlCommand);
		if (exclFound)
				throw new Exception("wrong SQL-command: " +
					sqlCommand + line + ". Probably semicolon is missing");

		return new SQLCommandData(sqlCommand.toString(), data.getValue().pattern,
								data.getValue().executor);
	}

	public String findNext(BufferedReader reader) throws Exception {

		SQLCommandData c = findNextEx(reader);
		return c != null ? c.command : null;
	}

	private Map.Entry<String, PatternItem> findLineMatchingPattern(BufferedReader reader) throws IOException {

		boolean insideComment = false;
		String line;
		Matcher mCommBegin = commentBegin.matcher("");
		Matcher mCommEnd = commentEnd.matcher("");

		while ((line = reader.readLine()) != null) {

			if (!insideComment) {
				mCommBegin.reset(line);
				insideComment = mCommBegin.find();
			}
			Iterator<PatternItem> p_item = patterns.values().iterator();
			while (!insideComment && p_item.hasNext()) {

				PatternItem item = p_item.next();
				Matcher m = item.pattern.matcher(line);
				if (m.find())
					return new AbstractMap.SimpleEntry<>(line, item);
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

	private static final class PatternItem {

		final Pattern pattern;
		Pattern pattern_exclude;
		ISQLExecutor executor;

		PatternItem(Pattern pattern, Pattern pattern_exclude, ISQLExecutor executor) {

			this.pattern = pattern;
			this.pattern_exclude = pattern_exclude;
			this.executor = executor;
		}
	}
}
