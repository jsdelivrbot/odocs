package com.pchudzik.docs.feed.download;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by pawel on 06.04.15.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadAbortEvent extends DownloadEvent {
	private final boolean abort = true;
	private DateTime abortDate;

	public static AbortEventBuilder builder() {
		return new AbortEventBuilder();
	}

	public static class AbortEventBuilder extends DownloadEventBuilder<AbortEventBuilder , DownloadAbortEvent> {
		private AbortEventBuilder() {
			super(EventType.ABORT);
		}

		public AbortEventBuilder abortDate(DateTime abortDate) {
			return addOperation(evt -> evt.abortDate = abortDate);
		}

		@Override
		protected DownloadAbortEvent createObject() {
			return new DownloadAbortEvent();
		}
	}
}
