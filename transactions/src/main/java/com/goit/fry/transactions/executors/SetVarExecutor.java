package com.goit.fry.transactions.executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SetVarExecutor extends ScalarExecutor{

	private static final Logger logger = LogManager.getRootLogger();
	private static final HashMap<String, String> vars = new HashMap<>();
	private static final Pattern patternVarName = Pattern.compile("SET (@[a-zA-Z_]+) = \\(([^)]+)\\)");

	public static String getVariable(String name) {

		return vars.get(name);
	}

	@Override
	public void execute(String command) throws SQLException {

		Matcher m = patternVarName.matcher(command);
		if (!m.find())
			throw new SQLException("wrong variable name: " + command);

		super.execute(command);

		String value;
		try (Statement stmt = createStmt()) {
			String query = m.group(2);
			ResultSet rs = stmt.executeQuery(query);
			if(!rs.next())
				throw new SQLException("query result is empty: " + query);

			value = rs.getString(1);
		}
		assert value != null;

		String varName = m.group(1);
		vars.put(varName, value);
		logger.info("the var " + varName + " = " + value);
	}
}
