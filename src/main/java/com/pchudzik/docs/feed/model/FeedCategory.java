package com.pchudzik.docs.feed.model;

import com.google.common.collect.Lists;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 15.03.15.
 */
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedCategory {
	private String name;
	private List<FeedInfo> feeds = Lists.newLinkedList();

	public static FeedCategoryBuilder builder() {
		return new FeedCategoryBuilder();
	}

	private void setFeeds(Collection<FeedInfo> feedInfos) {
		this.feeds.clear();
		this.feeds.addAll(feedInfos);
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FeedCategoryBuilder extends ObjectBuilder<FeedCategoryBuilder, FeedCategory> {

		public FeedCategoryBuilder name(String name) {
			return addOperation(category -> category.name = name);
		}

		public FeedCategoryBuilder feeds(FeedInfo ... feeds) {
			return feeds(asList(feeds));
		}

		public FeedCategoryBuilder feeds(Collection<FeedInfo> feedInfos) {
			return addOperation(category -> category.setFeeds(feedInfos));
		}
		@Override
		protected FeedCategory createObject() {
			return new FeedCategory();
		}
	}
}
