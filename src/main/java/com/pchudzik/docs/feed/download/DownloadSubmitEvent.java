package com.pchudzik.docs.feed.download;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadSubmitEvent extends DownloadEvent {
	@Getter private DateTime submitDate;
	@Getter private String documentationId;
	@Getter private String feedName;
	@Getter private String feedFile;


	public static DownloadSubmitEventBuilder builder() {
		return new DownloadSubmitEventBuilder();
	}

	public static class DownloadSubmitEventBuilder extends DownloadEventBuilder<DownloadSubmitEventBuilder, DownloadSubmitEvent> {
		protected DownloadSubmitEventBuilder() {
			super(EventType.SUBMIT);
		}

		@Override
		protected DownloadSubmitEvent createObject() {
			return new DownloadSubmitEvent();
		}

		public DownloadSubmitEventBuilder submitDate(DateTime submitDate) {
			return addOperation(evt -> evt.submitDate = submitDate);
		}

		public DownloadSubmitEventBuilder documentationId(String docId) {
			return addOperation(evt -> evt.documentationId = docId);
		}

		public DownloadSubmitEventBuilder feedName(String feedName) {
			return addOperation(evt -> evt.feedName = feedName);
		}

		public DownloadSubmitEventBuilder feedFile(String feedFile) {
			return addOperation(evt -> evt.feedFile = feedFile);
		}
	}
}
