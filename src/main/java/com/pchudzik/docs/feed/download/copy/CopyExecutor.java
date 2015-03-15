package com.pchudzik.docs.feed.download.copy;

import lombok.experimental.Builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pawel on 29.03.15.
 */
@Builder
public class CopyExecutor {
	final InputStream source;
	final OutputStream destination;

	int bufferSize;
	ProgressListener progressListener;
	AbortNotifier abortNotifier;

	public void start() throws IOException {
		setup();

		final byte [] buffer = new byte[bufferSize];

		int total = 0;
		int count = 0;
		while ((count = source.read(buffer)) != -1 && !abortNotifier.isAborted()) {
			total += count;
			progressListener.onProgress(total);
			destination.write(buffer, 0, count);
		}
	}

	private void setup() {
		if(bufferSize == 0) {
			bufferSize = 512 * 1024;
		}

		if(abortNotifier == null) {
			abortNotifier = () -> false;
		}

		if(progressListener == null) {
			progressListener = count -> {};
		}
	}
}
