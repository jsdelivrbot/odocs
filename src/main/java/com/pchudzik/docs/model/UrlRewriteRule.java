package com.pchudzik.docs.model;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;

/**
 * Created by pawel on 25.02.15.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Entity
public class UrlRewriteRule extends BaseEntity {
	String regexp;
	String replacement;

	public UrlRewriteRule(UrlRewriteRule other) {
		update(other);
	}

	public static UrlRewriteRuleBuilder builder() {
		return new UrlRewriteRuleBuilder();
	}

	public void update(UrlRewriteRule other) {
		this.regexp = other.getRegexp();
		this.replacement = other.replacement;
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class UrlRewriteRuleBuilder extends ObjectBuilder<UrlRewriteRuleBuilder, UrlRewriteRule> {
		public UrlRewriteRuleBuilder regexp(String regexp) {
			return addOperation(rule -> rule.regexp = regexp);
		}

		public UrlRewriteRuleBuilder replacement(String replacement) {
			return addOperation(rule -> rule.replacement = replacement);
		}

		@Override
		protected UrlRewriteRule createObject() {
			return new UrlRewriteRule();
		}
	}
}
