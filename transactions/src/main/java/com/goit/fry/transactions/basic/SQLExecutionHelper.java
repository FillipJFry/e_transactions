package com.goit.fry.transactions.basic;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.goit.fry.transactions.Database;
import org.apache.logging.log4j.Logger;

public class SQLExecutionHelper {

	private static final int MAX_LEN = 50;
	private final SimpleSQLFileParser parser;
	private final HashSet<ISQLExecutor> executors;
	private TransactionExecutor transactionExecutor;
	private final Logger logger;

	public SQLExecutionHelper(Logger logger) {

		parser = new SimpleSQLFileParser();
		executors = new HashSet<>();
		this.logger = logger;
	}

	public void addDDLPatterns() {

		parser.addPattern("CREATE TABLE ", "(DROP)|(ALTER)", null);
		parser.addPattern("DROP TABLE ", "(CREATE)|(ALTER)", null);
		parser.addPattern("ALTER TABLE ", "(CREATE)|(DROP)", null);
	}

	public void addDMLUpdatePatterns(ISQLExecutor executor) {

		parser.addPattern("DELETE FROM ", "(INSERT INTO )|(SELECT)", executor);
		parser.addPattern("INSERT INTO ([a-z_]+) *$", "(DELETE FROM )", executor);
		executors.add(executor);
	}

	public void addDMLSelectPattern() {

		parser.addPattern("SELECT ");
	}

	public void addPattern(String pattern, String patternExclude, ISQLExecutor executor) {

		parser.addPattern(pattern, patternExclude, executor);
		executors.add(executor);
	}

	public void addPattern(String pattern, ISQLExecutor executor) {

		parser.addPattern(pattern, null, executor);
		executors.add(executor);
	}

	public void addTransactionalPattern() {

		if (transactionExecutor == null) {
			transactionExecutor = new TransactionExecutor();
			executors.add(transactionExecutor);
		}

		parser.addPattern("(BEGIN TRANSACTION;)|(COMMIT;)", null,
									transactionExecutor);
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

	public void loadAndExecute(String sqlFilePath) throws Exception {

		logger.info("loading and executing commands from the file: " + sqlFilePath);
		try (BufferedReader in = getReaderFromResource(sqlFilePath)) {

			try (Connection conn = Database.getInstance().getConnection()) {
				for (ISQLExecutor executor : executors)
					executor.initConnection(conn);

				try (TransactionGuard guard = new TransactionGuard(transactionExecutor)) {
					SQLCommandData cmdData;
					while ((cmdData = parser.findNextEx(in)) != null) {
						assert cmdData.executor != null;
						cmdData.executor.execute(cmdData.command);
					}
				}
			}
		}
		logger.info(sqlFilePath + " - execution is finished");
	}

	private BufferedReader getReaderFromResource(String path) {

		InputStream r = SQLExecutionHelper.class.getResourceAsStream("/" + path);
		if (r == null)
			throw new NullPointerException("No such resource: " + path);

		return new BufferedReader(new InputStreamReader(r));
	}
}
