package com.pchudzik.docs.model;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import org.assertj.core.api.Condition;

import java.util.Objects;

public class UrlRewriteRuleAsserthelper extends BaseEntityAssertHelper {
	public static Condition<? super UrlRewriteRule> regexp(String regexp) {
		return DescriptiveCondition.builderFor(UrlRewriteRule.class)
				.predicate(entity -> Objects.equals(entity.getRegexp(), regexp))
				.toString(entity -> String.format("%n" +
						"  regexp equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", regexp, entity.getRegexp()))
				.build();
	}

	public static Condition<? super UrlRewriteRule> replacement(String replacement) {
		return DescriptiveCondition.builderFor(UrlRewriteRule.class)
				.predicate(entity -> Objects.equals(entity.getReplacement(), replacement))
				.toString(entity -> String.format("%n" +
						"  replacement equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", replacement, entity.getReplacement()))
				.build();
	}
}