package com.goit.fry.transactions.results;

import com.goit.fry.transactions.LongestProject;
import com.goit.fry.transactions.ex.QueryResultBase;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class LongestProjectResult extends QueryResultBase<LongestProject> {

	@Override
	public void addRecord(ResultSet rs) throws SQLException {

		result.add(new LongestProject(rs.getString(1),
										rs.getInt(2)));
	}
}
