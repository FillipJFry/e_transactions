package com.goit.fry.jdbc;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseQueryServiceTest {

	private DatabaseQueryService srv;

	@BeforeAll
	static void initDb() {

		DatabaseInitService.main(new String[] { });
		DatabasePopulateService.main(new String[] { });
		Database.reset();
	}

	@BeforeEach
	void init() {

		srv = new DatabaseQueryService();
	}

	@Test
	void findLongestProject() {

		List<LongestProject> longestProj = srv.findLongestProject();
		assertEquals(1, longestProj.size());
		assertEquals("Next generation text editor", longestProj.get(0).getProjectName());
		assertEquals(99, longestProj.get(0).getDurationInMonth());
	}

	@Test
	void findMaxProjectsClient() {

		List<MaxProjectsClient> maxProjClient = srv.findMaxProjectsClient();
		assertEquals(2, maxProjClient.size());
		assertEquals("Microsoft", maxProjClient.get(0).getName());
		assertEquals(3, maxProjClient.get(0).getProjectsCount());
		assertEquals("SpaceX", maxProjClient.get(1).getName());
		assertEquals(3, maxProjClient.get(1).getProjectsCount());
	}

	@Test
	void findMaxSalaryWorker() {

		List<MaxSalaryWorker> maxSalaryWorker = srv.findMaxSalaryWorker();
		assertEquals(1, maxSalaryWorker.size());
		assertEquals("Teamlead", maxSalaryWorker.get(0).getName());
		assertEquals(new BigDecimal("99999.99"), maxSalaryWorker.get(0).getSalary());
	}

	@Test
	void findYoungestEldestWorkers() {

		List<YoungestEldestWorkers> youngestEldest = srv.findYoungestEldestWorkers();
		Calendar birthday = new GregorianCalendar(2001, Calendar.APRIL, 19);

		assertEquals(4, youngestEldest.size());
		assertEquals("Alice", youngestEldest.get(0).getName());
		assertEquals(birthday, youngestEldest.get(0).getBirthday());
		assertEquals("Carol", youngestEldest.get(1).getName());
		assertEquals(birthday, youngestEldest.get(1).getBirthday());
		assertEquals("Eve", youngestEldest.get(2).getName());
		assertEquals(birthday, youngestEldest.get(2).getBirthday());

		birthday.set(1901, Calendar.JANUARY, 1);
	}

	@Test
	void printProjectPrices() {

		ProjectPrices[] etalon = {
					new ProjectPrices("assault drone", new BigDecimal("18720012.48")),
					new ProjectPrices("Starlink satelites firmware", new BigDecimal("18599997.21")),
					new ProjectPrices("Starship flight emulation system", new BigDecimal("18599997.21")),
					new ProjectPrices("Next generation text editor",new BigDecimal("8415014.85")),
					new ProjectPrices("bing search engine", new BigDecimal("4844000.40")),
					new ProjectPrices("Falcon 9 avionics", new BigDecimal("4599999.08")),
					new ProjectPrices("Windows 12", new BigDecimal("1650003.84")),
					new ProjectPrices("Glaciers orbital observation",new BigDecimal("659999.88")),
					new ProjectPrices("Minesweeper 3D", new BigDecimal("275000.64")),
					new ProjectPrices("Strong XOR cryptography", new BigDecimal("1200.18")),
				};

		List<ProjectPrices> projPrices = srv.printProjectPrices();
		assertEquals(10, projPrices.size());

		for (int i = 0; i < etalon.length; i++) {
			assertEquals(etalon[i].getName(), projPrices.get(i).getName());
			assertEquals(etalon[i].getPrice(), projPrices.get(i).getPrice());
		}
	}
}