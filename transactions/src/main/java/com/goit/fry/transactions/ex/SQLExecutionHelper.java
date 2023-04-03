package com.goit.fry.transactions.ex;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.goit.fry.transactions.Database;
import org.apache.logging.log4j.Logger;

public class SQLExecutionHelper {

	private static final int MAX_LEN = 50;
	private final SimpleSQLFileParser parser;
	private ScalarExecutor scalarExecutor;
	private ParametrizedInsertExecutor paramInsertExecutor;
	private TransactionExecutor transactionExecutor;
	private SetVarExecutor setVarExecutor;
	private final Logger logger;

	public SQLExecutionHelper(Logger logger) {

		parser = new SimpleSQLFileParser();
		this.logger = logger;
	}

	public void addDDLPatterns() {

		parser.addPattern("CREATE TABLE ", "(DROP)|(ALTER)", null);
		parser.addPattern("DROP TABLE ", "(CREATE)|(ALTER)", null);
		parser.addPattern("ALTER TABLE ", "(CREATE)|(DROP)", null);
	}

	public void addDMLUpdatePatterns() {

		addPatternWithDefExecutor("DELETE FROM ", "(INSERT INTO )|(SELECT)");
		addPatternWithDefExecutor("INSERT INTO ([a-z_]+) *$", "(DELETE FROM )");
	}

	public void addDMLSelectPattern() {

		parser.addPattern("SELECT ");
	}

	public boolean addPattern(String pattern_str) {

		return parser.addPattern(pattern_str);
	}

	public boolean addPattern(String pattern_str, String patternExclude) {

		return parser.addPattern(pattern_str, patternExclude, null);
	}

	public void addPatternWithDefExecutor(String pattern_str, String patternExclude) {

		if (scalarExecutor == null)
			scalarExecutor = new ScalarExecutor();
		parser.addPattern(pattern_str, patternExclude, scalarExecutor);
	}

	public void addPatternWithDefExecutor(String pattern_str) {

		addPatternWithDefExecutor(pattern_str, null);
	}

	public void addParametrizedInsertPattern() {

		if (paramInsertExecutor == null)
			paramInsertExecutor = new ParametrizedInsertExecutor();

		parser.addPattern(ParametrizedInsertExecutor.str_pattern,
				"(INSERT INTO)|(DELETE FROM )|(CREATE )|(BEGIN)",
							paramInsertExecutor);
	}

	public void addRecordBinder(String table_name, IRecordBinder binder) {

		if (paramInsertExecutor == null)
			paramInsertExecutor = new ParametrizedInsertExecutor();

		paramInsertExecutor.addTableRecordBinder(table_name, binder);
	}

	public void addTransactionalPattern() {

		if (transactionExecutor == null)
			transactionExecutor = new TransactionExecutor();

		parser.addPattern("(BEGIN TRANSACTION;)|(COMMIT;)", null,
									transactionExecutor);
	}

	public void addSetVarPattern() {

		if (setVarExecutor == null)
			setVarExecutor = new SetVarExecutor();

		parser.addPattern("SET @[a-zA-Z_]+ = ",
				"(INSERT INTO)|(DELETE FROM )|(CREATE )|(BEGIN)",
							setVarExecutor);
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
				initExecutors(conn);

				try (TransactionGuard guard = new TransactionGuard(transactionExecutor)) {
					SQLCommandData cmd_data;
					while ((cmd_data = parser.findNextEx(in)) != null) {
						assert cmd_data.executor != null;
						cmd_data.executor.execute(cmd_data.command);
					}
				}
			}
		}
		logger.info(sqlFilePath + " - execution is finished");
	}

	void initExecutors(Connection conn) {

		scalarExecutor.initConnection(conn);
		paramInsertExecutor.initConnection(conn);
		transactionExecutor.initConnection(conn);
		setVarExecutor.initConnection(conn);
	}

	private BufferedReader getReaderFromResource(String path) {

		InputStream r = SQLExecutionHelper.class.getResourceAsStream("/" + path);
		if (r == null)
			throw new NullPointerException("No such resource: " + path);

		return new BufferedReader(new InputStreamReader(r));
	}
}
