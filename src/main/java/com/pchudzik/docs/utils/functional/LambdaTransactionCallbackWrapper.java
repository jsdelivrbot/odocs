package com.pchudzik.docs.utils.functional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;

/**
 * Created by pawel on 08.03.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LambdaTransactionCallbackWrapper {
	public static TransactionCallbackWithoutResult wrapTransactionCallback(Runnable callback) {
		return new TransactionCallbackWithoutResult() {
			@Override
			protected void doInTransactionWithoutResult(TransactionStatus status) {
				callback.run();
			}
		};
	}
}
