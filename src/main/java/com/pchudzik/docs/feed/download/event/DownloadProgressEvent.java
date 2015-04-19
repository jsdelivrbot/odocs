package com.pchudzik.docs.feed.download.event;

import lombok.Getter;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
public class DownloadProgressEvent extends DownloadEvent {
	private final DateTime progressDate;
	private final int totalBytes;
	private final int downloadedBytes;

	public DownloadProgressEvent(DateTime progressDate, int totalBytes, int downloadedBytes) {
		super(EventType.PROGRESS);

		this.progressDate = progressDate;
		this.totalBytes = totalBytes;
		this.downloadedBytes = downloadedBytes;
	}

	public double getProgress() {
		if(totalBytes == 0) {
			return 0;
		}

		return downloadedBytes / (double)totalBytes;
	}
}
