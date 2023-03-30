package com.goit.fry.transactions;

import java.math.BigDecimal;

public class MaxSalaryWorker {
	private final String name;
	private final BigDecimal salary;

	public MaxSalaryWorker(String name, BigDecimal salary) {

		this.name = name;
		this.salary = salary;
	}

	public String getName() {

		return name;
	}

	public BigDecimal getSalary() {

		return salary;
	}
}
