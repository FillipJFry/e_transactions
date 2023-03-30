package com.goit.fry.transactions.ex;

import com.goit.fry.transactions.ProjectPrices;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectPricesResult extends QueryResultBase<ProjectPrices> {

	@Override
	public void addRecord(ResultSet rs) throws SQLException {

		result.add(new ProjectPrices(rs.getString(1),
									rs.getBigDecimal(2)));
	}
}
