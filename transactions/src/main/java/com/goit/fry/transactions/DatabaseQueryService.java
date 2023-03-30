package com.goit.fry.transactions;

import java.util.List;

import com.goit.fry.transactions.ex.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DatabaseQueryService {

	private static final Logger logger = LogManager.getRootLogger();
	private final SQLExecutionHelper sqlExecutionHelper;

	public DatabaseQueryService() {

		sqlExecutionHelper = new SQLExecutionHelper(logger);
		sqlExecutionHelper.addDMLSelectPattern();
	}

	private void executeAndGetResult(String funcName, String sqlFilePath,
									 IQueryResult resultContainer) {

		try {
			String query = sqlExecutionHelper.loadCommand(sqlFilePath);
			sqlExecutionHelper.executeCommand(query, resultContainer);
		}
		catch (Exception e) {

			logger.error(funcName + " failed: " + e);
		}
	}

	public List<LongestProject> findLongestProject() {

		logger.info("executing findLongestProject()");
		LongestProjectResult resultContainer = new LongestProjectResult();
		executeAndGetResult("findLongestProject()",
							"sql/find_longest_project.sql", resultContainer);

		return resultContainer.getResult();
	}

	public List<MaxProjectsClient> findMaxProjectsClient() {

		logger.info("executing findMaxProjectsClient()");
		MaxProjectsClientResult resultContainer = new MaxProjectsClientResult();
		executeAndGetResult("findMaxProjectsClient()",
							"sql/find_max_projects_client.sql", resultContainer);

		return resultContainer.getResult();
	}

	public List<MaxSalaryWorker> findMaxSalaryWorker() {

		logger.info("executing findMaxSalaryWorker()");
		MaxSalaryWorkerResult resultContainer = new MaxSalaryWorkerResult();
		executeAndGetResult("findMaxSalaryWorker()",
							"sql/find_max_salary_worker.sql", resultContainer);

		return resultContainer.getResult();
	}

	public List<YoungestEldestWorkers> findYoungestEldestWorkers() {

		logger.info("executing findYoungestEldestWorkers()");
		YoungestEldestWorkersResult resultContainer = new YoungestEldestWorkersResult();
		executeAndGetResult("findYoungestEldestWorkers()",
							"sql/find_youngest_eldest_workers.sql", resultContainer);

		return resultContainer.getResult();
	}

	public List<ProjectPrices> printProjectPrices() {

		logger.info("executing printProjectPrices()");
		ProjectPricesResult resultContainer = new ProjectPricesResult();
		executeAndGetResult("printProjectPrices()",
							"sql/print_project_prices.sql", resultContainer);

		return resultContainer.getResult();
	}
}
