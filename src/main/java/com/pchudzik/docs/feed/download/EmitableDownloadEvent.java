package com.pchudzik.docs.feed.download;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.pchudzik.docs.feed.download.event.DownloadEvent;
import lombok.Getter;

@Getter
class EmitableDownloadEvent {
	private final String id;

	@JsonUnwrapped
	private final DownloadEvent downloadEvent;

	public EmitableDownloadEvent(DownloadInfo source, DownloadEvent event) {
		this.id = source.getId();
		this.downloadEvent = event;
	}
}