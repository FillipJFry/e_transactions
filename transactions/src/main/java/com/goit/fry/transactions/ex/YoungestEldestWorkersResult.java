package com.goit.fry.jdbc.ex;

import com.goit.fry.jdbc.YoungestEldestWorkers;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class YoungestEldestWorkersResult extends QueryResultBase<YoungestEldestWorkers> {

	@Override
	public void addRecord(ResultSet rs) throws SQLException {

		Date dt = rs.getDate(3);
		Calendar birthday = new GregorianCalendar();
		birthday.setTime(dt);
		result.add(new YoungestEldestWorkers(rs.getString(1),
											rs.getString(2), birthday));
	}
}