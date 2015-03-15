package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Created by pawel on 15.03.15.
 */
@Getter @ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FeedInfo {
	private String name;
	private String additionalInfoHtml;
	private String feedFile;

	public static FeedInfoBuilder builder() {
		return new FeedInfoBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FeedInfoBuilder extends ObjectBuilder<FeedInfoBuilder, FeedInfo> {
		public FeedInfoBuilder name(String name) {
			return addOperation(info -> info.name = name);
		}

		public FeedInfoBuilder additionalInfoHtml(String additionalInfoHtml1) {
			return addOperation(info -> info.additionalInfoHtml = additionalInfoHtml1);
		}

		public FeedInfoBuilder feedFile(String file) {
			return addOperation(info -> info.feedFile = file);
		}

		@Override
		protected FeedInfo createObject() {
			return new FeedInfo();
		}
	}
}
