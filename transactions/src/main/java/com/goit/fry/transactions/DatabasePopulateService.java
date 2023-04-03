package com.goit.fry.transactions;

import com.goit.fry.transactions.basic.*;
import com.goit.fry.transactions.binders.*;
import com.goit.fry.transactions.executors.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabasePopulateService {

	private static final Logger logger = LogManager.getRootLogger();

	public static void main(String[] args) {

		ScalarExecutor scalarExecutor = new ScalarExecutor();
		ParametrizedInsertExecutor paramInsertExecutor = new ParametrizedInsertExecutor();
		SetVarExecutor setVarExecutor = new SetVarExecutor();

		paramInsertExecutor.addTableRecordBinder("worker", new WorkersBinder());
		paramInsertExecutor.addTableRecordBinder("client", new ClientsBinder());
		paramInsertExecutor.addTableRecordBinder("project", new ProjectsBinder());
		paramInsertExecutor.addTableRecordBinder("proj_worker_name", new ProjWorkerNameBinder());

		SQLExecutionHelper sqlExecutionHelper = new SQLExecutionHelper(logger);
		sqlExecutionHelper.addDMLUpdatePatterns(scalarExecutor);
		sqlExecutionHelper.addPattern("CREATE UNIQUE INDEX ", scalarExecutor);
		sqlExecutionHelper.addPattern("CREATE TEMPORARY TABLE ", scalarExecutor);
		sqlExecutionHelper.addPattern("DROP TABLE ", scalarExecutor);
		sqlExecutionHelper.addPattern("DROP INDEX ", scalarExecutor);
		sqlExecutionHelper.addTransactionalPattern();

		sqlExecutionHelper.addPattern(ParametrizedInsertExecutor.strPattern,
						"(INSERT INTO)|(DELETE FROM )|(CREATE )|(BEGIN)",
									paramInsertExecutor);
		sqlExecutionHelper.addPattern("SET @[a-zA-Z_]+ = ",
						"(INSERT INTO)|(DELETE FROM )|(CREATE )|(BEGIN)",
									setVarExecutor);
		try {
			sqlExecutionHelper.loadAndExecute("sql/populate_db.sql");

		} catch (Exception e) {

			logger.error(e);
		}
	}
}
