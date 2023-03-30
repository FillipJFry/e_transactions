package com.goit.fry.transactions.ex;

import java.util.ArrayList;
import java.util.List;

public abstract class QueryResultBase<TRecord> implements IQueryResult{

	protected final List<TRecord> result = new ArrayList<>();

	public List<TRecord> getResult() {

		return result;
	}
}
