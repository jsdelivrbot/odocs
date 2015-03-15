package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by pawel on 28.03.15.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class DownloadEvent {
	EventType eventType;


	public abstract static class DownloadEventBuilder<B extends DownloadEventBuilder, T extends DownloadEvent> extends ObjectBuilder<B, T> {
		protected DownloadEventBuilder(EventType eventType) {
			addOperation(evt -> evt.eventType = eventType);
			addValidator(evt -> checkNotNull(evt.eventType));
		}
	}

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
