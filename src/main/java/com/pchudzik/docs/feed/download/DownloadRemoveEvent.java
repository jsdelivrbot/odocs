package com.pchudzik.docs.feed.download;

import org.joda.time.DateTime;

/**
 * Created by pawel on 06.04.15.
 */
public class DownloadRemoveEvent extends DownloadEvent {
	public DateTime removalDate;

	public static DownloadRemoveEventBuilder builder() {
		return new DownloadRemoveEventBuilder();
	}

	public static class DownloadRemoveEventBuilder extends DownloadEventBuilder<DownloadRemoveEventBuilder, DownloadRemoveEvent> {
		protected DownloadRemoveEventBuilder() {
			super(EventType.REMOVE);
		}

		public DownloadRemoveEventBuilder removalDate(DateTime date) {
			return addOperation(evt -> evt.removalDate = date);
		}

		@Override
		protected DownloadRemoveEvent createObject() {
			return new DownloadRemoveEvent();
		}
	}
}
