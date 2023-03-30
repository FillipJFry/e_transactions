package com.goit.fry.transactions.ex;

import com.goit.fry.transactions.MaxSalaryWorker;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MaxSalaryWorkerResult extends QueryResultBase<MaxSalaryWorker> {

	@Override
	public void addRecord(ResultSet rs) throws SQLException {

		result.add(new MaxSalaryWorker(rs.getString(1),
										rs.getBigDecimal(2)));
	}
}
