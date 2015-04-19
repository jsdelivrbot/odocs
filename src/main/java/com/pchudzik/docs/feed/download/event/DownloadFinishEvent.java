package com.pchudzik.docs.feed.download.event;

import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
public class DownloadFinishEvent extends DownloadEvent {
	private final DateTime finishDate;
	private final String versionId;

	public DownloadFinishEvent(DateTime finishDate, String versionId) {
		super(EventType.FINISH);
		this.finishDate = finishDate;
		this.versionId = versionId;
	}
}
