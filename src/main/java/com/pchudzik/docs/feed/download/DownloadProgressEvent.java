package com.pchudzik.docs.feed.download;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
@NoArgsConstructor
public class DownloadProgressEvent extends DownloadEvent {
	private int totalBytes;
	private int downloadedBytes;

	public double getProgress() {
		if(totalBytes == 0) {
			return 0;
		}

		return downloadedBytes / (double)totalBytes * 100.0;
	}

	public static DownloadProgressEventBuilder builder() {
		return new DownloadProgressEventBuilder();
	}

	public static class DownloadProgressEventBuilder extends DownloadEventBuilder<DownloadProgressEventBuilder, DownloadProgressEvent> {
		protected DownloadProgressEventBuilder() {
			super(EventType.PROGRESS);
		}

		@Override
		protected DownloadProgressEvent createObject() {
			return new DownloadProgressEvent();
		}

		public DownloadProgressEventBuilder totalBytes(int bytes) {
			return addOperation(evt -> evt.totalBytes = bytes);
		}

		public DownloadProgressEventBuilder downloadedBytes(int bytes) {
			return addOperation(evt -> evt.downloadedBytes = bytes);
		}
	}
}
