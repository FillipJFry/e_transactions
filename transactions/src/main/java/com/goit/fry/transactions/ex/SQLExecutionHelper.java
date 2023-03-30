package com.goit.fry.jdbc.ex;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.goit.fry.jdbc.Database;
import org.apache.logging.log4j.Logger;

public class SQLExecutionHelper {

	private static final int MAX_LEN = 50;
	private final SimpleSQLFileParser parser;
	private final Logger logger;

	public SQLExecutionHelper(Logger logger) {

		parser = new SimpleSQLFileParser();
		this.logger = logger;
	}

	public void addDDLPatterns() {

		parser.addPattern("CREATE TABLE ");
		parser.addPattern("DROP TABLE ");
		parser.addPattern("ALTER TABLE ");
	}

	public void addDMLUpdatePatterns() {

		parser.addPattern("DELETE FROM ");
		parser.addPattern("INSERT INTO ");
	}

	public void addDMLSelectPattern() {

		parser.addPattern("SELECT ");
	}

	public boolean addPattern(String pattern_str) {

		return parser.addPattern(pattern_str);
	}

	public String loadCommand(String sqlFilePath) throws Exception {

		logger.info("parsing the file '" + sqlFilePath + "'");
		try (BufferedReader in = getReaderFromResource(sqlFilePath)) {

			String cmd = parser.findNext(in);
			if (cmd != null) {
				logger.info("the command extracted: " + cmd);
				return cmd;
			}
		}
		throw new Exception("the sql-file " + sqlFilePath + " is empty");
	}

	public List<String> loadMultipleCommands(String sqlFilePath) throws Exception {

		logger.info("parsing the file '" + sqlFilePath + "'");
		ArrayList<String> commands = new ArrayList<>();

		try (BufferedReader in = getReaderFromResource(sqlFilePath)) {
			String cmd;
			while ((cmd = parser.findNext(in)) != null) {
				commands.add(cmd);
				logger.info("adding a sql-command: " + cmd);
			}
		}
		logger.info(sqlFilePath + " - parsing finished");

		return commands;
	}

	public void executeCommand(String command, IQueryResult queryResult) throws SQLException {

		assert command != null;
		try (Connection conn = Database.getInstance().getConnection()) {
			try (Statement stmt = conn.createStatement()) {

				ResultSet rs = stmt.executeQuery(command);
				int records = 0;
				while (rs.next()) {
					queryResult.addRecord(rs);
					records++;
				}

				logger.info("the command " +
						command.substring(0, Integer.min(MAX_LEN, command.length())) +
						"... returned " + records + " record(s)");
			}
		}
	}

	public void executeMultipleCommands(List<String> commands) throws SQLException {

		logger.info("executing the commands");
		try (Connection conn = Database.getInstance().getConnection()) {
			try (Statement stmt = conn.createStatement()) {
				for (String command : commands) {
					int r = stmt.executeUpdate(command);
					logger.info("the command " +
							command.substring(0, Integer.min(MAX_LEN, command.length())) +
							"... returned " + r);
				}
			}
		}
	}

	private BufferedReader getReaderFromResource(String path) {

		return new BufferedReader(
				new InputStreamReader(
						SQLExecutionHelper.class.getResourceAsStream("/" + path)));
	}
}
