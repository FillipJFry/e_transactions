package com.goit.fry.transactions.binders;

import com.goit.fry.transactions.basic.IRecordBinder;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProjWorkerNameBinder implements IRecordBinder {

	@Override
	public void bindRecord(String[] record, PreparedStatement stmt) throws Exception {

		if (record.length != 2)
			throw new SQLException("the fields count for the ProjWorkerNameBinder is wrong: " + record.length);

		stmt.setInt(1, Integer.parseInt(record[0]));
		stmt.setString(2, record[1]);
	}
}
