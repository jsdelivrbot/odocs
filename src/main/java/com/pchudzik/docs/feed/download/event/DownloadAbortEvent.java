package com.pchudzik.docs.feed.download.event;

import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by pawel on 06.04.15.
 */
@Getter
public class DownloadAbortEvent extends DownloadEvent {
	private final DateTime abortDate;

	DownloadAbortEvent(DateTime abortDate) {
		super(EventType.ABORT);
		this.abortDate = abortDate;
	}
}
