package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.assertj.core.api.Condition;
import org.joda.time.DateTime;

import java.util.Objects;

/**
 * Created by pawel on 15.03.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedAssertHelper {
	public static Condition<? super Feed> name(String name) {
		return DescriptiveCondition.builderFor(Feed.class)
				.predicate(feed -> Objects.equals(feed.getName(), name))
				.toString(feed -> String.format("%n" +
						"  name equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", name, feed.getName()))
				.build();
	}

	public static Condition<? super Feed> updateDate(DateTime dateTime) {
		return DescriptiveCondition.builderFor(Feed.class)
				.predicate(feed -> Objects.equals(feed.getUpdateDate(), dateTime))
				.toString(feed -> String.format("%n" +
						"  updateDate equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", dateTime, feed.getUpdateDate()))
				.build();
	}

	public static Condition<? super Feed> url(String url) {
		return DescriptiveCondition.builderFor(Feed.class)
				.predicate(feed -> Objects.equals(feed.getUrl(), url))
				.toString(feed -> String.format("%n" +
						"  url equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", url, feed.getUrl()))
				.build();
	}

	public static Condition<? super Feed> initialDirectory(String initialDirectory) {
		return DescriptiveCondition.builderFor(Feed.class)
				.predicate(feed -> Objects.equals(feed.getInitialDirectory(), initialDirectory))
				.toString(feed -> String.format("%n" +
						"  initialDirectory equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", initialDirectory, feed.getInitialDirectory()))
				.build();
	}

	public static Condition<? super Feed> rootDirectory(String rootDirectory) {
		return DescriptiveCondition.builderFor(Feed.class)
				.predicate(feed -> Objects.equals(feed.getRootDirectory(), rootDirectory))
				.toString(feed -> String.format("%n" +
						"  rootDirectory equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", rootDirectory, feed.getRootDirectory()))
				.build();
	}

	public static Condition<? super Feed> configurationSetup() {
		return DescriptiveCondition.builderFor(Feed.class)
				.predicate(feed -> feed.getHttpConfiguration().isPresent())
				.toString(feed -> "\n  httpConfiguration to be present but was not")
				.build();
	}
}
