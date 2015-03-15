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
public class RewriteRuleAssertHelper {
	public static Condition<? super RewriteRule> regexp(String regexp) {
		return DescriptiveCondition.builderFor(RewriteRule.class)
				.predicate(rule -> Objects.equals(rule.getRegexp(), regexp))
				.toString(rule -> String.format("%n" +
						"  regexp equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", regexp, rule.getRegexp()))
				.build();
	}

	public static Condition<? super RewriteRule> replacement(String replacement) {
		return DescriptiveCondition.builderFor(RewriteRule.class)
				.predicate(rule -> Objects.equals(rule.getReplacement(), replacement))
				.toString(rule -> String.format("%n" +
						"  replacement equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", replacement, rule.getReplacement()))
				.build();
	}
}
