package com.goit.fry.transactions.basic;

import java.sql.Connection;

public interface ISQLExecutor {

	void initConnection(Connection conn);

	void execute(String command) throws Exception;
}
