package com.goit.fry.transactions.ex;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParametrizedInsertExecutor implements ISQLExecutor {

	public static final String str_pattern = "INSERT INTO ([a-z_]+)(\\(([a-z_]+,)*[^)]+\\)){0,1} VALUES *";
	private static final Logger logger = LogManager.getRootLogger();
	private Connection conn;
	private static final Pattern pattern_header, pattern_records, pattern_values;
	private final HashMap<String, IRecordBinder> table_record_binders = new HashMap<>();

	static {
		pattern_header = Pattern.compile(str_pattern);
		pattern_records = Pattern.compile("\\(([^)]+)\\)");
		pattern_values = Pattern.compile("(@|\\w|\\p{InCyrillic})[^',)]*");
	}

	public void addTableRecordBinder(String table_name, IRecordBinder record_binder) {

		table_record_binders.put(table_name, record_binder);
	}

	@Override
	public void initConnection(Connection conn) {

		assert !table_record_binders.isEmpty();
		this.conn = conn;
	}

	@Override
	public void execute(String command) throws Exception {

		Matcher m_begin = pattern_header.matcher(command);
		if(m_begin.find()) {

			String table_name = m_begin.group(1);
			IRecordBinder record_binder = table_record_binders.get(table_name);
			if (record_binder == null) {
				logger.error("a binder is not found for the table: " + table_name);
				return;
			}

			prepareAndExecute(m_begin.group(0), command, record_binder);
		}
		else
			throw new SQLException("the command is invalid: " + command);
	}

	private void prepareAndExecute(String header, String command,
								   IRecordBinder record_binder) throws Exception {

		StringBuilder cmd_to_prepare = new StringBuilder();
		cmd_to_prepare.append(header);

		Matcher m_rec = pattern_records.matcher(command);
		int i = header.length();
		if (m_rec.find(i)) {
			String record = m_rec.group();
			try(PreparedStatement stmt = prepareStatement(cmd_to_prepare, record)) {

				insertRecord(record, record_binder, stmt, cmd_to_prepare);
				i = m_rec.end();
				while (m_rec.find(i)) {
					record = m_rec.group();
					insertRecord(record, record_binder, stmt, cmd_to_prepare);
					i = m_rec.end();
				}
			}
		}
		else
			throw new SQLException("the command is invalid: " + command);
	}

	private void insertRecord(String record, IRecordBinder record_binder,
							  PreparedStatement stmt, StringBuilder cmd) throws Exception {

		Matcher m = pattern_values.matcher(record);
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

	private PreparedStatement prepareStatement(StringBuilder cmd_to_prepare,
											   String record) throws SQLException {

		int values_count = (int)record.chars().filter(ch -> ch == ',').count();

		cmd_to_prepare.append("(?");
		while (values_count > 0) {
			cmd_to_prepare.append(",?");
			values_count--;
		}
		cmd_to_prepare.append(')');

		return conn.prepareStatement(cmd_to_prepare.toString());
	}
}
