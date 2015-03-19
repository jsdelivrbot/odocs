package com.pchudzik.docs.utils.json;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Created by pawel on 18.03.15.
 */
@Getter
@EqualsAndHashCode @ToString
public class SimpleValue<T> {
	final T value;

	public SimpleValue(T value) {
		this.value = value;
	}
}
