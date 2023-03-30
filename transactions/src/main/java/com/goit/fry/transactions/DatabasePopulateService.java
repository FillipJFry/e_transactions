package com.goit.fry.jdbc;

import java.util.List;

import com.goit.fry.jdbc.ex.SQLExecutionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabasePopulateService {

	private static final Logger logger = LogManager.getRootLogger();

	public static void main(String[] args) {

		SQLExecutionHelper sqlExecutionHelper = new SQLExecutionHelper(logger);
		sqlExecutionHelper.addDMLUpdatePatterns();
		sqlExecutionHelper.addPattern("SET @[a-zA-Z]* = ");
		sqlExecutionHelper.addPattern("CREATE UNIQUE INDEX ");
		sqlExecutionHelper.addPattern("CREATE TEMPORARY TABLE ");
		sqlExecutionHelper.addPattern("DROP TABLE ");
		sqlExecutionHelper.addPattern("DROP INDEX ");

		try {
			List<String> commands = sqlExecutionHelper.loadMultipleCommands("sql/populate_db.sql");
			sqlExecutionHelper.executeMultipleCommands(commands);
		} catch (Exception e) {

			logger.error(e);
		}
	}
}
