package com.pchudzik.docs.feed.download;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;
import com.pchudzik.docs.feed.download.event.*;
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

	@Getter private String id = UUID.randomUUID().toString();
	private DownloadEventListener downloadEventListener;

	@Getter private DownloadSubmitEvent submitEvent;
	@Getter private DownloadStartEvent startEvent;
	@Getter private DownloadProgressEvent progressEvent;
	@Getter private DownloadAbortEvent abortEvent;
	@Getter private DownloadFinishEvent finishEvent;
	@Getter private DownloadErrorEvent errorEvent;
	@Getter private DownloadRemoveEvent removeEvent;

	public void start(DownloadStartEvent downloadStartEvent) {
		startEvent = downloadStartEvent;
		notifyListener(startEvent);
	}

	public void progress(DownloadProgressEvent newProgress) {
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
		handleAbort();
	}

	public void submit(DownloadSubmitEvent downloadSubmitEvent) {
		this.submitEvent = downloadSubmitEvent;
		notifyListener(submitEvent);
	}

	private void finish() {
		handleAbort();
		handleRemove();
	}

	public void finish(DownloadFinishEvent finishEvent) {
		this.finishEvent = finishEvent;
		notifyListener(finishEvent);
		finish();
	}

	public void finish(DownloadErrorEvent downloadErrorEvent) {
		this.errorEvent = downloadErrorEvent;
		notifyListener(errorEvent);
		finish();
	}

	private void notifyListener(DownloadEvent event) {
		downloadEventListener.onEvent(this, event);
	}

	private void handleAbort() {
		if(abortEvent != null) {
			log.info("Download of {} aborted", getId());
			notifyListener(abortEvent);
		}
	}

	private void handleRemove() {
		if(removeEvent != null) {
			log.info("Download {} removed", getId());
			notifyListener(removeEvent);
		}
	}

	public void requestAbort(DownloadAbortEvent abortEvent) {
		Preconditions.checkState(!isDone(), "Can not abort finished job");
		this.abortEvent = abortEvent;
	}

	public void requestRemove(DownloadRemoveEvent downloadRemoveEvent) {
		Preconditions.checkState(isDone() || abortEvent != null, "Can not remove running job");
		this.removeEvent = downloadRemoveEvent;
		handleRemove();
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

	public boolean isRemoveRequested() {
		return removeEvent != null;
	}

	public boolean isAbortRequested() {
		return abortEvent != null;
	}

	protected boolean isInterrupted() {
		return isAbortRequested() || isRemoveRequested();
	}

	private boolean isDone() {
		return finishEvent != null || errorEvent != null;
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
