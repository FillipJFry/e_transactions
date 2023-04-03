package com.goit.fry.transactions;

import com.goit.fry.transactions.binders.ClientsBinder;
import com.goit.fry.transactions.binders.ProjWorkerNameBinder;
import com.goit.fry.transactions.binders.ProjectsBinder;
import com.goit.fry.transactions.binders.WorkersBinder;
import com.goit.fry.transactions.ex.SQLExecutionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabasePopulateService {

	private static final Logger logger = LogManager.getRootLogger();

	public static void main(String[] args) {

		SQLExecutionHelper sqlExecutionHelper = new SQLExecutionHelper(logger);
		sqlExecutionHelper.addDMLUpdatePatterns();
		sqlExecutionHelper.addPatternWithDefExecutor("CREATE UNIQUE INDEX ");
		sqlExecutionHelper.addPatternWithDefExecutor("CREATE TEMPORARY TABLE ");
		sqlExecutionHelper.addPatternWithDefExecutor("DROP TABLE ");
		sqlExecutionHelper.addPatternWithDefExecutor("DROP INDEX ");
		sqlExecutionHelper.addTransactionalPattern();
		sqlExecutionHelper.addParametrizedInsertPattern();
		sqlExecutionHelper.addSetVarPattern();

		sqlExecutionHelper.addRecordBinder("worker", new WorkersBinder());
		sqlExecutionHelper.addRecordBinder("client", new ClientsBinder());
		sqlExecutionHelper.addRecordBinder("project", new ProjectsBinder());
		sqlExecutionHelper.addRecordBinder("proj_worker_name", new ProjWorkerNameBinder());

		try {
			sqlExecutionHelper.loadAndExecute("sql/populate_db.sql");

		} catch (Exception e) {

			logger.error(e);
		}
	}
}
