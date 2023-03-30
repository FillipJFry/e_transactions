package com.goit.fry.jdbc.ex;

import com.goit.fry.jdbc.MaxProjectsClient;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MaxProjectsClientResult extends QueryResultBase<MaxProjectsClient> {

	@Override
	public void addRecord(ResultSet rs) throws SQLException {

		result.add(new MaxProjectsClient(rs.getString(1),
												rs.getInt(2)));
	}
}
