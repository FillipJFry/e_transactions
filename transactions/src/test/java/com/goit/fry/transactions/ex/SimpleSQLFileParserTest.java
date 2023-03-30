package com.goit.fry.transactions.ex;

import java.io.BufferedReader;
import java.io.StringReader;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SimpleSQLFileParserTest {

	@Test
	void noPatternDoubles() {

		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE "));
		assertFalse(parser.addPattern("CREATE TABLE "));
	}

	@Test
	void parseCorrectFile_1cmd() {

		String query = "CREATE TABLE t1(\nid INT PRIMARY KEY);";
		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE "));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			query = query.replace("\n", " ");
			assertEquals(query, parser.findNext(in));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void parseCorrectFile_oneline_cmd() {

		String query = "CREATE TABLE t1(id INT PRIMARY KEY);";
		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE "));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			assertEquals(query, parser.findNext(in));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void parseCorrectFile_2cmd() {

		String query = "CREATE TABLE t1(\n" +
				"id INT PRIMARY KEY);\n" +
				"\n" +
				"SELECT * FROM t1;\n" +
				"INSERT INTO TABLE t1 VALUES\n" +
				"(1), (2);";
		String[] cmds = {"CREATE TABLE t1( id INT PRIMARY KEY);",
						"INSERT INTO TABLE t1 VALUES (1), (2);"};

		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE "));
		assertTrue(parser.addPattern("INSERT INTO "));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			String cmd;
			int i = 0;
			while ((cmd = parser.findNext(in)) != null) {
				assertEquals(cmds[i++], cmd);
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void parseCorrectFile_with_comments() {

		String query = "/* CREATE TABLE */\n" +
				"CREATE TABLE t1(\n" +
				"id INT PRIMARY KEY);\n" +
				"/*\n" +
				"SELECT * FROM\n" +
				"*/\n" +
				"SELECT * FROM t1;\n" +
				"INSERT INTO TABLE t1 VALUES\n" +
				"(1), (2);";
		String[] cmds = {"CREATE TABLE t1( id INT PRIMARY KEY);",
				"INSERT INTO TABLE t1 VALUES (1), (2);"};

		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE "));
		assertTrue(parser.addPattern("INSERT INTO "));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			String cmd;
			int i = 0;
			while ((cmd = parser.findNext(in)) != null) {
				assertEquals(cmds[i++], cmd);
			}
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void parseIncorrectFile() {

		String query = "CREATE TABLE t1(\nid INT PRIMARY KEY)";
		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE "));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			assertThrows(Exception.class, () -> parser.findNext(in));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void parseIncorrectFile_1line() {

		String query = "CREATE TABLE t1(DROP TABLE , SELECT );";
		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE ", "(DROP TABLE )|(SELECT)", null));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			assertThrows(Exception.class, () -> parser.findNext(in));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}

	@Test
	void parseIncorrectFile_2lines() {

		String query = "CREATE TABLE t1(\nid INT PRIMARY KEY)\n" +
						"DROP TABLE t1;";
		SimpleSQLFileParser parser = new SimpleSQLFileParser();
		assertTrue(parser.addPattern("CREATE TABLE ", "DROP TABLE ", null));
		assertTrue(parser.addPattern("DROP TABLE "));

		try (BufferedReader in = new BufferedReader(new StringReader(query))) {

			assertThrows(Exception.class, () -> parser.findNext(in));
		}
		catch (Exception e) {

			assertNull(e);
			System.err.println(e.getMessage());
		}
	}
}