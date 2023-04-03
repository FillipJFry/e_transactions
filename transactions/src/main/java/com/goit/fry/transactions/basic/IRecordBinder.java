package com.goit.fry.transactions.basic;

import java.sql.PreparedStatement;

public interface IRecordBinder {

	void bindRecord(String[] record, PreparedStatement stmt) throws Exception;
}
