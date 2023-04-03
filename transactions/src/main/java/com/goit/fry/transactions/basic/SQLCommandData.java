package com.goit.fry.transactions.basic;

import java.util.regex.Pattern;

public class SQLCommandData {

	public final String command;
	public final Pattern pattern;
	public final ISQLExecutor executor;

	public SQLCommandData(String command, Pattern pattern, ISQLExecutor executor) {

		this.command = command;
		this.pattern = pattern;
		this.executor = executor;
	}
}
