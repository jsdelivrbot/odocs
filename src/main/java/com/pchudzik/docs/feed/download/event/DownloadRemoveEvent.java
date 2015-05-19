package com.pchudzik.docs.feed.download.event;

import org.joda.time.DateTime;

/**
 * Created by pawel on 06.04.15.
 */
public class DownloadRemoveEvent extends DownloadEvent {
	private final DateTime removalDate;

	DownloadRemoveEvent(DateTime removalDate) {
		super(EventType.REMOVE);
		this.removalDate = removalDate;
	}
}
