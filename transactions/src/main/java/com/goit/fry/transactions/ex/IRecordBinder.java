package com.goit.fry.transactions.ex;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface IRecordBinder {

	void bindRecord(String[] record, PreparedStatement stmt) throws Exception;
}
