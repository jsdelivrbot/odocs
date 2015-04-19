package com.pchudzik.docs.feed.download;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.pchudzik.docs.feed.download.event.*;
import com.pchudzik.docs.utils.TimeProvider;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.UUID;

/**
 * Created by pawel on 22.03.15.
 */
@Slf4j
@RequiredArgsConstructor
public class DownloadInfo {
	static final double PROGRESS_STEP = 0.002;

	private final TimeProvider timeProvider;

	@Getter private String id = UUID.randomUUID().toString();
	private DownloadEventListener downloadEventListener;

	@Getter private volatile boolean abortRequested;
	@Getter private volatile boolean removeRequested;

	@Getter private DownloadSubmitEvent submitEvent;
	@Getter private DownloadStartEvent startEvent;
	@Getter private DownloadProgressEvent progressEvent;
	@Getter private DownloadAbortEvent abortEvent;
	@Getter private DownloadFinishEvent finishEvent;
	@Getter private DownloadErrorEvent errorEvent;
	@Getter private DownloadRemoveEvent removeEvent;

	public void start() {
		startEvent = new DownloadStartEvent(timeProvider.now());
		notifyListener(startEvent);
	}

	public void progress(int totalBytes, int downloadedBytes) {
		final DownloadProgressEvent newProgress = new DownloadProgressEvent(timeProvider.now(), totalBytes, downloadedBytes);
		if(progressEvent == null) {
			progressEvent = newProgress;
			notifyListener(progressEvent);
		} else {
			if(newProgress.getProgress() - progressEvent.getProgress() >= PROGRESS_STEP) {
				log.trace("downloadId: {} progress: {}", id, newProgress.getProgress());
				progressEvent = newProgress;
				notifyListener(progressEvent);
			}
		}
		handleAbort(newProgress.getProgressDate());
	}

	private void submit(String documentationId, String feedName, String feedFile) {
		this.submitEvent = new DownloadSubmitEvent(timeProvider.now(), documentationId, feedName, feedFile);
		notifyListener(submitEvent);
	}

	public void finish() {
		handleAbort(timeProvider.now());
		handleRemove(timeProvider.now());
	}

	public void finish(String versionId) {
		finishEvent = new DownloadFinishEvent(timeProvider.now(), versionId);
		notifyListener(finishEvent);
		finish();
	}

	public void finish(Exception ex) {
		this.errorEvent = errorEvent;
		notifyListener(errorEvent);
		finish();
	}

	private void notifyListener(DownloadEvent event) {
		downloadEventListener.onEvent(this, event);
	}

	private void handleAbort(DateTime abortDate) {
		if(abortRequested) {
			this.abortEvent = new DownloadAbortEvent(abortDate);
			log.info("Download of {} aborted", getId());
			notifyListener(abortEvent);
		}
	}

	private void handleRemove(DateTime removeDate) {
		if(removeRequested) {
			this.removeEvent = new DownloadRemoveEvent(removeDate);
			log.info("Download {} removed", getId());
			notifyListener(removeEvent);
		}
	}

	public void requestAbort() {
		Preconditions.checkState(!isDone(), "Can not abort finished job");
		abortRequested = true;
	}

	public void requestRemove() {
		Preconditions.checkState(isDone() || abortEvent != null, "Can not remove running job");
		removeRequested = true;
		handleRemove(timeProvider.now());
	}

	@JsonIgnore
	public DateTime getSubmitDate() {
		return submitEvent.getSubmitDate();
	}

	@JsonIgnore
	public String getFeedFile() {
		return submitEvent.getFeedFile();
	}

	@JsonIgnore
	public String getDocumentationId() {
		return submitEvent.getDocumentationId();
	}

	protected boolean isInterrupted() {
		return abortRequested || removeRequested;
	}

	private boolean isDone() {
		return finishEvent != null || errorEvent != null;
	}

	public static DownloadInfoBuilder builder() {
		return new DownloadInfoBuilder();
	}

	public static class DownloadInfoBuilder extends ObjectBuilder<DownloadInfoBuilder, DownloadInfo> {
		private TimeProvider timeProvider;
		private String documentationId;
		private String feedName;
		private String feedFile;

		public DownloadInfoBuilder id(String id) {
			return addOperation(info -> info.id = id);
		}
		public DownloadInfoBuilder downloadEventListener(DownloadEventListener listener) {
			return addOperation(info -> info.downloadEventListener = listener);
		}
		public DownloadInfoBuilder timeProvider(TimeProvider timeProvider) {
			this.timeProvider = timeProvider;
			return this;
		}

		public DownloadInfoBuilder documentationId(String id) {
			this.documentationId = id;
			return this;
		}

		public DownloadInfoBuilder feedName(String feedName) {
			this.feedName = feedName;
			return this;
		}

		public DownloadInfoBuilder feedFile(String feedFile) {
			this.feedFile = feedFile;
			return this;
		}

		@Override
		protected void postConstruct(DownloadInfo object) {
			object.submit(documentationId, feedName, feedFile);
		}

		@Override
		protected DownloadInfo createObject() {
			return new DownloadInfo(timeProvider);
		}
	}
}
