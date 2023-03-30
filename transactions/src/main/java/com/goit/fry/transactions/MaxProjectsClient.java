package com.goit.fry.transactions;

public class MaxProjectsClient {
	private final String name;
	private final int projectCount;

	public MaxProjectsClient(String name, int projectCount) {

		this.name = name;
		this.projectCount = projectCount;
	}

	public String getName() {

		return name;
	}

	public int getProjectsCount() {

		return projectCount;
	}
}