package com.pchudzik.docs.feed.download;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadFinishEvent extends DownloadEvent {
	private DateTime finishDate;
	private String versionId;

	public static DownloadFinishEventBuilder builder() {
		return new DownloadFinishEventBuilder();
	}

	public static class DownloadFinishEventBuilder extends DownloadEventBuilder<DownloadFinishEventBuilder, DownloadFinishEvent> {
		protected DownloadFinishEventBuilder() {
			super(EventType.FINISH);
		}

		@Override
		protected DownloadFinishEvent createObject() {
			return new DownloadFinishEvent();
		}

		public DownloadFinishEventBuilder finishDate(DateTime finishDate) {
			return addOperation(evt -> evt.finishDate = finishDate);
		}

		public DownloadFinishEventBuilder versionId(String versionId) {
			return addOperation(evt -> evt.versionId = versionId);
		}
	}
}
