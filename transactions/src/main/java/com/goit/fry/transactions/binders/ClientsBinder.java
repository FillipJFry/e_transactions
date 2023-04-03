package com.goit.fry.transactions.binders;

import com.goit.fry.transactions.basic.IRecordBinder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ClientsBinder implements IRecordBinder {

	@Override
	public void bindRecord(String[] record, PreparedStatement stmt) throws Exception {

		if (record.length != 1)
			throw new SQLException("the fields count for the ClientsBinder is wrong: " + record.length);

		stmt.setString(1, record[0]);
	}
}
