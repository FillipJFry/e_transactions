package com.goit.fry.transactions;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Database {

	private static final Logger logger = LogManager.getRootLogger();
	private static Database instance = null;
	private Connection conn;

	private Database() throws SQLException {

		conn = DriverManager.getConnection("jdbc:h2:./megasoft");
		logger.info("connected to the database megasoft");
	}

	public static Database getInstance() throws SQLException {

		if (instance == null)
			instance = new Database();
		return instance;
	}

	public Connection getConnection() {

		try {
			if (conn.isClosed()) {
				conn = DriverManager.getConnection("jdbc:h2:./megasoft");
				logger.info("reconnected to the database megasoft");
			}
		} catch (SQLException e) {

			logger.error(e);
		}
		return conn;
	}

	/**
	 * only for tests
	 */
	static void reset() {

		try {
			if (instance != null && !instance.conn.isClosed())
				instance.conn.close();
		}
		catch (SQLException e) {
			logger.error(e);
		}
		instance = null;
	}
}
