package com.goit.fry.jdbc;

import java.util.List;

import com.goit.fry.jdbc.ex.SQLExecutionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseInitService {

	private static final Logger logger = LogManager.getRootLogger();

	public static void main(String[] args) {

		SQLExecutionHelper sqlExecutionHelper = new SQLExecutionHelper(logger);
		sqlExecutionHelper.addDDLPatterns();

		try {
			List<String> ddlCommands = sqlExecutionHelper.loadMultipleCommands("sql/init_db.sql");
			sqlExecutionHelper.executeMultipleCommands(ddlCommands);
		} catch (Exception e) {

			logger.error(e);
		}
	}
}
