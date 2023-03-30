package com.goit.fry.transactions;

import java.util.Calendar;

public class YoungestEldestWorkers {

	private final String ageType;
	private final String name;
	private final Calendar birthday;

	public YoungestEldestWorkers(String ageType, String name, Calendar birthday) {

		this.ageType = ageType;
		this.name = name;
		this.birthday = birthday;
	}

	public String getAgeType() {

		return ageType;
	}

	public String getName() {

		return name;
	}

	public Calendar getBirthday() {

		return birthday;
	}
}
