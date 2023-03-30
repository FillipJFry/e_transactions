package com.goit.fry.transactions.ex;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface IQueryResult {

	void addRecord(ResultSet rs) throws SQLException;
}
