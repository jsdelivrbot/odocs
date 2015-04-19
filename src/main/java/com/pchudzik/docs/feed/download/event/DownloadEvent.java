package com.pchudzik.docs.feed.download.event;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DownloadEvent {
	private final EventType eventType;

	public enum EventType {
		SUBMIT,
		START,
		PROGRESS,
		ABORT,
		FINISH,
		ERROR,
		REMOVE
	}
}
