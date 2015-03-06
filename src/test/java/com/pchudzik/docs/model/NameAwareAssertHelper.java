package com.pchudzik.docs.model;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import org.assertj.core.api.Condition;

import java.util.Objects;

public class NameAwareAssertHelper {

	public static Condition<? super NameAware> name(String name) {
		return DescriptiveCondition.builderFor(NameAware.class)
				.predicate(nameAware -> Objects.equals(nameAware.getName(), name))
				.toString(nameAware -> String.format("%n" +
						"  name equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", name, nameAware.getName()))
				.build();
	}
}