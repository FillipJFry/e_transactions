package com.goit.fry.transactions.executors;

import com.goit.fry.transactions.basic.IRecordBinder;
import com.goit.fry.transactions.basic.ISQLExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParametrizedInsertExecutor implements ISQLExecutor {

	public static final String strPattern = "INSERT INTO ([a-z_]+)(\\(([a-z_]+,)*[^)]+\\)){0,1} VALUES *";
	private static final Logger logger = LogManager.getRootLogger();
	private Connection conn;
	private static final Pattern patternHead, patternRecords, patternValues;
	private final HashMap<String, IRecordBinder> tableRecBinders = new HashMap<>();

	static {
		patternHead = Pattern.compile(strPattern);
		patternRecords = Pattern.compile("\\(([^)]+)\\)");
		patternValues = Pattern.compile("(@|\\w|\\p{InCyrillic})[^',)]*");
	}

	public void addTableRecordBinder(String table_name, IRecordBinder record_binder) {

		tableRecBinders.put(table_name, record_binder);
	}

	@Override
	public void initConnection(Connection conn) {

		assert !tableRecBinders.isEmpty();
		this.conn = conn;
	}

	@Override
	public void execute(String command) throws Exception {

		Matcher m = patternHead.matcher(command);
		if(m.find()) {

			String table_name = m.group(1);
			IRecordBinder record_binder = tableRecBinders.get(table_name);
			if (record_binder == null) {
				logger.error("a binder is not found for the table: " + table_name);
				return;
			}

			insertRecords(m.group(0), command, record_binder);
		}
		else
			throw new SQLException("the command is invalid: " + command);
	}

	private void insertRecords(String head, String command,
							   IRecordBinder recordBinder) throws Exception {

		StringBuilder cmdToPrepare = new StringBuilder();
		cmdToPrepare.append(head);

		Matcher mRec = patternRecords.matcher(command);
		int i = head.length();
		if (mRec.find(i)) {
			String record = mRec.group();
			try(PreparedStatement stmt = prepareStatement(cmdToPrepare, record)) {

				insertRecord(record, recordBinder, stmt, cmdToPrepare);
				i = mRec.end();
				while (mRec.find(i)) {
					record = mRec.group();
					insertRecord(record, recordBinder, stmt, cmdToPrepare);
					i = mRec.end();
				}
			}
		}
		else
			throw new SQLException("the command is invalid: " + command);
	}

	private void insertRecord(String record, IRecordBinder record_binder,
							  PreparedStatement stmt, StringBuilder cmd) throws Exception {

		Matcher m = patternValues.matcher(record);
		int i = 0, j = 0;
		while (m.find(i)) {
			i = m.end();
			j++;
		}
		String[] values = new String[j];

		m.reset(record);
		i = 0; j = 0;
		while (m.find(i)) {
			values[j++] = record.substring(m.start(), m.end());
			i = m.end();
		}

		record_binder.bindRecord(values, stmt);
		int r = stmt.executeUpdate();
		logger.info("command: " + cmd +
					"; record: " + record + "; returned - " + r);
	}

	private PreparedStatement prepareStatement(StringBuilder cmdToPrepare,
											   String record) throws SQLException {

		int values_count = (int)record.chars().filter(ch -> ch == ',').count();

		cmdToPrepare.append("(?");
		while (values_count > 0) {
			cmdToPrepare.append(",?");
			values_count--;
		}
		cmdToPrepare.append(')');

		return conn.prepareStatement(cmdToPrepare.toString());
	}
}
