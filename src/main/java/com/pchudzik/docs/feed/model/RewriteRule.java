package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.model.UrlRewriteRule;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by pawel on 15.03.15.
 */
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RewriteRule {
	private String regexp;
	private String replacement;

	public static RewriteRuleBuilder builder() {
		return new RewriteRuleBuilder();
	}

	public UrlRewriteRule asUrlRewriteRule() {
		return UrlRewriteRule.builder()
				.regexp(regexp)
				.replacement(replacement)
				.build();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class  RewriteRuleBuilder extends ObjectBuilder<RewriteRuleBuilder, RewriteRule> {
		public RewriteRuleBuilder regexp(String regexp) {
			return addOperation(rule -> rule.regexp = regexp);
		}

		public RewriteRuleBuilder replacement(String replacement) {
			return addOperation(rule -> rule.replacement = replacement);
		}

		@Override
		protected RewriteRule createObject() {
			return new RewriteRule();
		}
	}
}
