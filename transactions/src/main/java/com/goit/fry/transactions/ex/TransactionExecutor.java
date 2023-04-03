package com.goit.fry.transactions.ex;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionExecutor implements ISQLExecutor {

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

			conn.setAutoCommit(false);
			started = true;
		}
		else {
			conn.commit();
			started = false;
		}
	}

	public void rollback() throws SQLException {

		assert started;
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
