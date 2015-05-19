package com.pchudzik.docs.feed.download.event;

import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
public class DownloadErrorEvent extends DownloadEvent {
	private final DateTime errorDate;
	private final String errorClass;
	private final String errorMessage;
	private final String stackTrace;

	DownloadErrorEvent(DateTime errorDate, Exception ex) {
		super(EventType.ERROR);

		this.errorDate = errorDate;
		this.errorClass = ex.getClass().getCanonicalName();
		this.errorMessage = ex.getMessage();
		this.stackTrace = ExceptionUtils.getStackTrace(ex);
	}
}
