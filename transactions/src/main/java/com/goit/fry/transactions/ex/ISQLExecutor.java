package com.goit.fry.transactions.ex;

import java.sql.Connection;
import java.sql.SQLException;

public interface ISQLExecutor {

	void initConnection(Connection conn);

	void execute(String command) throws Exception;
}
