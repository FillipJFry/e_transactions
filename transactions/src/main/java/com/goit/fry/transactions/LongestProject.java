package com.goit.fry.jdbc;

public class LongestProject {
	private final String name;
	private final int durationInMonth;

	public LongestProject(String name, int durationInMonth) {

		this.name = name;
		this.durationInMonth = durationInMonth;
	}

	public String getProjectName() {

		return name;
	}

	public int getDurationInMonth() {

		return durationInMonth;
	}
}
