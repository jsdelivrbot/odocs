package com.pchudzik.docs.feed.download.event;

import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
public class DownloadSubmitEvent extends DownloadEvent {
	private final DateTime submitDate;
	private final String documentationId;
	private final String feedName;
	private final String feedFile;

	public DownloadSubmitEvent(DateTime submitDate, String documentationId, String feedName, String feedFile) {
		super(EventType.SUBMIT);
		this.submitDate = submitDate;
		this.documentationId = documentationId;
		this.feedName = feedName;
		this.feedFile = feedFile;
	}
}
