package com.pchudzik.docs.feed.download;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joda.time.DateTime;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by pawel on 22.03.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DownloadInfo {
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

	private void updateEvent(DownloadEvent event) {
		downloadEventListener.onEvent(this, event);
	}

	public void submit(DownloadSubmitEvent submitEvent) {
		this.submitEvent = submitEvent;
		updateEvent(submitEvent);
	}

	public void start(DownloadStartEvent downloadStartEvent) {
		this.startEvent = downloadStartEvent;
		updateEvent(downloadStartEvent);
	}

	public void progress(DownloadProgressEvent progressEvent) {
		this.progressEvent = progressEvent;
		updateEvent(progressEvent);
	}

	public void abort(DownloadAbortEvent abortEvent) {
		this.abortEvent = abortEvent;
		updateEvent(abortEvent);
	}

	public void finish(DownloadFinishEvent finishEvent) {
		this.finishEvent = finishEvent;
		updateEvent(finishEvent);
	}

	public void error(DownloadErrorEvent errorEvent) {
		this.errorEvent = errorEvent;
		updateEvent(errorEvent);
	}

	public void remove(DownloadRemoveEvent removeEvent) {
		this.removeEvent = removeEvent;
		updateEvent(removeEvent);
	}

	@JsonIgnore
	public Optional<DateTime> getSubmitDate() {
		return Optional.ofNullable(submitEvent)
				.map(event -> event.getSubmitDate());
	}

	@JsonIgnore
	public Optional<String> getFeedFile() {
		return Optional.ofNullable(submitEvent)
				.map(event -> event.getFeedFile());
	}

	public boolean isInterrupted() {
		return abortRequested || removeRequested;
	}

	@JsonIgnore
	boolean isRunning() {
		return abortEvent == null && removeEvent == null && finishEvent == null && errorEvent == null;
	}

	@JsonIgnore
	public Optional<String> getDocumentationId() {
		return Optional.ofNullable(submitEvent)
				.map(event -> event.getDocumentationId());
	}

	public void requestAbort() {
		Preconditions.checkArgument(!isInterrupted() && isRunning(), "Can not abort interrupted job");
		abortRequested = true;
	}

	public void requestRemove() {
		Preconditions.checkArgument(!isInterrupted(), "Can not abort interrupted job");
		removeRequested = true;
	}

	public static DownloadInfoBuilder builder() {
		return new DownloadInfoBuilder();
	}

	public static class DownloadInfoBuilder extends ObjectBuilder<DownloadInfoBuilder, DownloadInfo> {
		public DownloadInfoBuilder id(String id) {
			return addOperation(info -> info.id = id);
		}
		public DownloadInfoBuilder downloadEventListener(DownloadEventListener listener) {
			return addOperation(info -> info.downloadEventListener = listener);
		}

		@Override
		protected DownloadInfo createObject() {
			return new DownloadInfo();
		}
	}
}
