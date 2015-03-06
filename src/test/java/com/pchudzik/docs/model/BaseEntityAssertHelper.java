package com.pchudzik.docs.model;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseEntityAssertHelper {
	public static Condition<? super BaseEntity> id(String id) {
		return DescriptiveCondition.builderFor(BaseEntity.class)
				.predicate(entity -> Objects.equals(entity.getId(), id))
				.toString(entity -> String.format("%n" +
						"  id equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", id, entity.getId()))
				.build();
	}
}