package com.pchudzik.docs.feed.download;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
@NoArgsConstructor
public class DownloadStartEvent extends DownloadEvent {
	private DateTime startDate;

	public static DownloadStartEventBuilder builder() {
		return new DownloadStartEventBuilder();
	}

	public static class DownloadStartEventBuilder extends DownloadEventBuilder<DownloadStartEventBuilder, DownloadStartEvent> {
		protected DownloadStartEventBuilder() {
			super(EventType.START);
		}

		@Override
		protected DownloadStartEvent createObject() {
			return new DownloadStartEvent();
		}

		public DownloadStartEventBuilder startDate(DateTime startDate) {
			return addOperation(evt -> evt.startDate = startDate);
		}
	}
}
