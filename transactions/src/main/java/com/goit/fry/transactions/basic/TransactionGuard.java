package com.goit.fry.transactions.basic;

public final class TransactionGuard implements AutoCloseable {

	private final TransactionExecutor executor;

	public TransactionGuard(TransactionExecutor executor) {

		this.executor = executor;
	}

	@Override
	public void close() throws Exception {

		if (executor != null) {
			try {
				if (executor.transactionNotCommited())
					executor.rollback();
			}
			finally {
				executor.reset();
			}
		}
	}
}
