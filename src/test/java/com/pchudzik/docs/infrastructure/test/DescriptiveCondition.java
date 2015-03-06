package com.pchudzik.docs.infrastructure.test;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;

import java.util.function.Function;
import java.util.function.Predicate;

/**
* Created by pawel on 08.02.15.
*/
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DescriptiveCondition<T> extends Condition<T> {
	private Predicate<T> predicate;
	private Function<T, String> toString;

	private T actual;

	public static <T> DescriptiveConditionBuilder<T> builderFor(Class<T> clazz) {
		return new DescriptiveConditionBuilder<>();
	}

	@Override
	public boolean matches(T value) {
		this.actual = value;
		return predicate.test(value);
	}

	@Override
	public String toString() {
		return toString.apply(actual);
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class DescriptiveConditionBuilder<T> extends ObjectBuilder<DescriptiveConditionBuilder, DescriptiveCondition<T>> {
		public DescriptiveConditionBuilder<T> predicate(Predicate<T> predicate) {
			return addOperation(condition -> condition.predicate = predicate);
		}

		public DescriptiveConditionBuilder<T> toString(Function<T, String> toString) {
			return addOperation(condition -> condition.toString = toString);
		}

		@Override
		protected DescriptiveCondition<T> createObject() {
			return new DescriptiveCondition<>();
		}
	}
}
