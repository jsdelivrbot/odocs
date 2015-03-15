package com.pchudzik.docs.feed.download;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadErrorEvent extends DownloadEvent {
	private DateTime errorDate;
	private String errorClass;
	private String errorMessage;
	private String stackTrace;

	public static DownloadErrorEventBuilder builder() {
		return new DownloadErrorEventBuilder();
	}

	public static class DownloadErrorEventBuilder extends DownloadEventBuilder<DownloadErrorEventBuilder, DownloadErrorEvent> {
		protected DownloadErrorEventBuilder() {
			super(EventType.ERROR);
		}

		@Override
		protected DownloadErrorEvent createObject() {
			return new DownloadErrorEvent();
		}

		public DownloadErrorEventBuilder errorDate(DateTime errorDate) {
			return addOperation(evt -> evt.errorDate = errorDate);
		}

		public DownloadErrorEventBuilder exception(Exception ex) {
			return addOperation(evt -> evt.errorClass = ex.getClass().getCanonicalName())
					.addOperation(evt -> evt.errorMessage = ex.getMessage())
					.addOperation(evt -> evt.stackTrace = ExceptionUtils.getStackTrace(ex));
		}
	}
}
