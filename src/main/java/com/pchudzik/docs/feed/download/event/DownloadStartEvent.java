package com.pchudzik.docs.feed.download.event;

import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
public class DownloadStartEvent extends DownloadEvent {
	private final DateTime startDate;

	public DownloadStartEvent(DateTime startDate) {
		super(EventType.START);
		this.startDate = startDate;
	}
}
