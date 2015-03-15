package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;

import java.util.Objects;

/**
 * Created by pawel on 15.03.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHeaderAssertHelper {
	public static Condition<? super HttpHeader> name(String name) {
		return DescriptiveCondition.builderFor(HttpHeader.class)
				.predicate(header -> Objects.equals(header.getName(), name))
				.toString(header -> String.format("%n" +
						"  name equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", name, header.getName()))
				.build();
	}

	public static Condition<? super HttpHeader> value(String value) {
		return DescriptiveCondition.builderFor(HttpHeader.class)
				.predicate(header -> Objects.equals(header.getValue(), value))
				.toString(header -> String.format("%n" +
						"  value equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", value, header.getName()))
				.build();
	}
}
