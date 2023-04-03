package com.goit.fry.transactions.executors;

import com.goit.fry.transactions.basic.ISQLExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class ScalarExecutor implements ISQLExecutor {

	private static final Logger logger = LogManager.getRootLogger();
	private Connection conn;

	@Override
	public void initConnection(Connection conn) {

		this.conn = conn;
	}

	protected Statement createStmt() throws SQLException {

		assert conn != null;
		return conn.createStatement();
	}

	@Override
	public void execute(String command) throws SQLException {

		assert conn != null;
		try (Statement stmt = conn.createStatement()) {
			int r = stmt.executeUpdate(command);
			logger.info("the command " + command + " returned " + r);
		}
	}
}
