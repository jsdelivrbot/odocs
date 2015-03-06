package com.pchudzik.docs.utils.functional;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Created by pawel on 18.02.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionWrapper {
	@FunctionalInterface
	public interface ThrowableSupplier<T> {
		T get() throws Exception;
	}

	@FunctionalInterface
	public interface ThrowablePredicate<T> {
		boolean test(T t) throws Exception;
	}

	@FunctionalInterface
	public interface ThrowableFunction<T, R> {
		R apply(T t) throws Exception;
	}

	public static <T, R> Function<T, R> wrapFunction(ThrowableFunction<T, R> fn) {
		return (val) -> {
			try {
				return fn.apply(val);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}

	public static <T> Predicate<T> wrapPredicate(ThrowablePredicate<T> predicate) {
		return value -> {
			try {
				return predicate.test(value);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}

	public static <T> Supplier<T> wrapSupplier(ThrowableSupplier<T> supplier) {
		return () -> {
			try {
				return supplier.get();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		};
	}
}