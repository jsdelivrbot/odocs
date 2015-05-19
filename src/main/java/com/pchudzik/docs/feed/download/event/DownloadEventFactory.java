package com.pchudzik.docs.feed.download.event;

import com.pchudzik.docs.utils.TimeProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Created by pawel on 19.05.15.
 */
@Service
public class DownloadEventFactory {
	private final TimeProvider timeProvider;

	@Autowired
	public DownloadEventFactory(TimeProvider timeProvider) {
		this.timeProvider = timeProvider;
	}

	public DownloadSubmitEvent downloadSubmitEvent(String documentationId, String feedName, String feedFile) {
		return new DownloadSubmitEvent(timeProvider.now(), documentationId, feedName, feedFile);
	}

	public DownloadStartEvent startEvent() {
		return new DownloadStartEvent(timeProvider.now());
	}

	public DownloadProgressEvent progressEvent(int totalBytes, int downloadedBytes) {
		return new DownloadProgressEvent(timeProvider.now(), totalBytes, downloadedBytes);
	}

	public DownloadFinishEvent finishEventWithoutResult() {
		return new DownloadFinishEvent(timeProvider.now(), Optional.<String>empty());
	}

	public DownloadFinishEvent finishEvent(String versionId) {
		return new DownloadFinishEvent(timeProvider.now(), Optional.of(versionId));
	}

	public DownloadErrorEvent errorEvent(Exception ex) {
		return new DownloadErrorEvent(timeProvider.now(), ex);
	}

	public DownloadRemoveEvent removeEvent() {
		return new DownloadRemoveEvent(timeProvider.now());
	}

	public DownloadAbortEvent abortEvent() {
		return new DownloadAbortEvent(timeProvider.now());
	}
}
