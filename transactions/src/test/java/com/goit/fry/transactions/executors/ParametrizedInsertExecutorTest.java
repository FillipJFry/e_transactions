package com.goit.fry.transactions.executors;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.mockito.Mockito.*;
import com.goit.fry.transactions.basic.IRecordBinder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParametrizedInsertExecutorTest {

	@Test
	void execute() {

		try(Connection conn = mock(Connection.class)) {
			PreparedStatement stmt = mock(PreparedStatement.class);
			IRecordBinder binder = mock();
			when(conn.prepareStatement(anyString())).thenReturn(stmt);
			when(stmt.executeUpdate()).thenReturn(1);

			ParametrizedInsertExecutor executor = new ParametrizedInsertExecutor();
			executor.addTableRecordBinder("client", binder);
			executor.initConnection(conn);
			executor.execute("INSERT INTO client(name) VALUES('Simon'), ('Microsoft')");

			verify(conn).prepareStatement("INSERT INTO client(name) VALUES(?)");
			verify(binder).bindRecord(new String[]{"Simon"}, stmt);
			verify(binder).bindRecord(new String[]{"Microsoft"}, stmt);
		}
		catch (Exception e) {

			System.err.println(e.getMessage());
		}
	}
}