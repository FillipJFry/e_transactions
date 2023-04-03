package com.goit.fry.transactions.binders;

import com.goit.fry.transactions.basic.IRecordBinder;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class WorkersBinder implements IRecordBinder {

	static DateFormat dtFormat = new SimpleDateFormat("yyyy-MM-dd");

	@Override
	public void bindRecord(String[] record, PreparedStatement stmt) throws Exception {

		if (record.length != 4)
			throw new SQLException("the fields count for the WorkersBinder is wrong: " + record.length);

		stmt.setString(1, record[0]);
		stmt.setDate(2, new Date(dtFormat.parse(record[1]).getTime()));
		stmt.setString(3, record[2]);
		stmt.setBigDecimal(4, new BigDecimal(record[3]));
	}
}
