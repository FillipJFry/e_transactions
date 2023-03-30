package com.goit.fry.jdbc;

import java.math.BigDecimal;

public class ProjectPrices {
	private final String name;
	private final BigDecimal price;

	public ProjectPrices(String name, BigDecimal price) {

		this.name = name;
		this.price = price;
	}

	public String getName() {

		return name;
	}

	public BigDecimal getPrice() {

		return price;
	}
}
