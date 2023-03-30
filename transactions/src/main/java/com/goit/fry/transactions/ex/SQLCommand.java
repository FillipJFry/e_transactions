package com.goit.fry.transactions.ex;

import java.util.regex.Pattern;

public class SQLCommand {

	public final String command;
	public final Pattern pattern;
	public final IAction action;

	public SQLCommand(String command, Pattern pattern, IAction action) {

		this.command = command;
		this.pattern = pattern;
		this.action = action;
	}
}
