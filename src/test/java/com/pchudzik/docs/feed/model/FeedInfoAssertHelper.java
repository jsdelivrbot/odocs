package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedInfoAssertHelper {
	public static Condition<? super FeedInfo> name(String name) {
		return DescriptiveCondition.builderFor(FeedInfo.class)
				.predicate(info -> Objects.equals(info.getName(), name))
				.toString(info -> String.format("%n" +
						"  name equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", name, info.getName()))
				.build();
	}

	public static Condition<? super FeedInfo> additionalInfoHtml(String additionalInfo) {
		return DescriptiveCondition.builderFor(FeedInfo.class)
				.predicate(info -> Objects.equals(info.getAdditionalInfoHtml(), additionalInfo))
				.toString(info -> String.format("%n" +
						"  additionalInfoHtml equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", additionalInfo, info.getAdditionalInfoHtml()))
				.build();
	}

	public static Condition<? super FeedInfo> url(String url) {
		return DescriptiveCondition.builderFor(FeedInfo.class)
				.predicate(info -> Objects.equals(info.getFeedFile(), url))
				.toString(info -> String.format("%n" +
						"  feedFile equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", url, info.getFeedFile()))
				.build();
	}
}