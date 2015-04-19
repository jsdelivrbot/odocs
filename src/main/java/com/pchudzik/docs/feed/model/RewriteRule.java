package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.model.UrlRewriteRule;
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

	public RewriteRule(String regexp, String replacement) {
		this.regexp = regexp;
		this.replacement = replacement;
	}

	public UrlRewriteRule asUrlRewriteRule() {
		return UrlRewriteRule.builder()
				.regexp(regexp)
				.replacement(replacement)
				.build();
	}
}
