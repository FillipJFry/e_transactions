package com.goit.fry.transactions.basic;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionExecutor implements ISQLExecutor {

	private static final Logger logger = LogManager.getRootLogger();
	private boolean started;
	private Connection conn;

	public TransactionExecutor() {

		started = false;
	}

	@Override
	public void initConnection(Connection conn) {

		this.conn = conn;
	}

	@Override
	public void execute(String command) throws SQLException {

		assert (command.equals("BEGIN TRANSACTION;") ||
				command.equals("COMMIT;"));

		if (command.equals("BEGIN TRANSACTION;")) {
			if (started)
				throw new SQLException("a transaction cannot be nested");
			logger.info("a new transaction started");

			conn.setAutoCommit(false);
			started = true;
		}
		else {
			conn.commit();
			logger.info("the transaction is commited");
			started = false;
		}
	}

	public void rollback() throws SQLException {

		assert started;
		logger.info("rolling back the transaction");
		conn.rollback();
		started = false;
	}

	public boolean transactionNotCommited() {

		return started;
	}

	public void reset() throws SQLException {

		started = false;
		conn.setAutoCommit(true);
	}
}
