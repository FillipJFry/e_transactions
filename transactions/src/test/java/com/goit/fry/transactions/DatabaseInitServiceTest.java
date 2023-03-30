package com.goit.fry.jdbc;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseInitServiceTest {

	@Test
	void mainDoesntThrow() {

		assertDoesNotThrow(() -> DatabaseInitService.main(new String[] { }));
	}

	@Test
	void tableWorkerCreatedProperly() {

		checkTable("worker");
	}

	@Test
	void tableClientCreatedProperly() {

		checkTable("client");
	}

	@Test
	void tableProjectCreatedProperly() {

		checkTable("project");
	}

	@Test
	void tableProjectWorkerCreatedProperly() {

		checkTable("project_worker");
	}

	private static void checkTable(String table_name) {

		DatabaseInitService.main(new String[] { });

		try (Connection conn = Database.getInstance().getConnection()) {
			table_name = table_name.toUpperCase();

			DatabaseMetaData metaData = conn.getMetaData();
			ResultSet tables = metaData.getTables(null, null, table_name, null);

			boolean tableExists = tables.next();
			assertTrue (tableExists);
			if (tableExists)
				assertEquals(table_name, tables.getString("TABLE_NAME"));
		}
		catch (SQLException e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}