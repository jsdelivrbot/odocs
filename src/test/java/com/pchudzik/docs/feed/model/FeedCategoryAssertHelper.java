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
public class FeedCategoryAssertHelper {
	public static Condition<? super FeedCategory> name(String name) {
		return DescriptiveCondition.builderFor(FeedCategory.class)
					.predicate(category -> Objects.equals(category.getName(), name))
					.toString(category -> String.format("%n" +
							"  name equal to:%n" +
							"    <%s>%n" +
							"  but was%n" +
							"    <%s>%n", name, category.getName()))
					.build();
	}
}
