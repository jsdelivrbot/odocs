package com.pchudzik.docs.utils.builder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by pawel on 08.02.15.
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ObjectBuilder<B, T> {
	private List<BuildOperation<T>> buildOperations = new LinkedList<>();
	private List<BuildValidator<T>> buildValidators = new LinkedList<>();

	public T build() {
		final T object = createObject();

		buildOperations.forEach(builder -> builder.apply(object));
		buildValidators.forEach(validator -> validator.validate(object));

		postConstruct(object);

		return object;
	}

	@SuppressWarnings("unchecked")
	protected B addOperation(BuildOperation<T> operation) {
		buildOperations.add(operation);
		return (B) this;
	}

	@SuppressWarnings("unchecked")
	protected B addValidator(BuildValidator<T> validator) {
		buildValidators.add(validator);
		return (B) this;
	}

	protected abstract T createObject();

	protected void postConstruct(T object) {}

	@FunctionalInterface
	protected interface BuildOperation<T> {
		void apply(T object);
	}

	@FunctionalInterface
	protected interface BuildValidator<T> {
		void validate(T object);
	}
}
