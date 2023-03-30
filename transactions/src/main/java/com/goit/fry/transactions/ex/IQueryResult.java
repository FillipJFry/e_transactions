package com.goit.fry.jdbc.ex;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IQueryResult {

	void addRecord(ResultSet rs) throws SQLException;
}
