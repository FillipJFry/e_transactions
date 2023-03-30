package com.goit.fry.transactions;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DatabasePopulateServiceTest {

	@BeforeAll
	static void initDb() {

		DatabaseInitService.main(new String[] { });
		Database.reset();
	}

	@Test
	void mainDoesntThrow() {

		assertDoesNotThrow(() -> DatabasePopulateService.main(new String[] { }));
	}
}