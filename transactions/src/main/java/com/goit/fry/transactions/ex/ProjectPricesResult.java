package com.goit.fry.jdbc.ex;

import com.goit.fry.jdbc.ProjectPrices;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProjectPricesResult extends QueryResultBase<ProjectPrices> {

	@Override
	public void addRecord(ResultSet rs) throws SQLException {

		result.add(new ProjectPrices(rs.getString(1),
									rs.getBigDecimal(2)));
	}
}
