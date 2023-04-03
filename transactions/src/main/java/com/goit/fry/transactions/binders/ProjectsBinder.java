package com.goit.fry.transactions.binders;

import com.goit.fry.transactions.ex.IRecordBinder;
import com.goit.fry.transactions.ex.SetVarExecutor;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ProjectsBinder implements IRecordBinder {

	static DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void bindRecord(String[] record, PreparedStatement stmt) throws Exception {

		if (record.length != 5)
			throw new SQLException("the fields count for the ProjectsBinder is wrong: " + record.length);

		String proj_id = SetVarExecutor.getVariable(record[2]);
		if (proj_id == null)
			throw new SQLException("no such var: " + record[2]);

		stmt.setInt(1, Integer.parseInt(record[0]));
		stmt.setString(2, record[1]);
		stmt.setInt(3, Integer.parseInt(proj_id));
		stmt.setDate(4, new Date(dtFormat.parse(record[3]).getTime()));
		stmt.setDate(5, new Date(dtFormat.parse(record[4]).getTime()));
	}
}
