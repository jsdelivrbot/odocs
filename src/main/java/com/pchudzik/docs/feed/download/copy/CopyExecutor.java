package com.pchudzik.docs.feed.download.copy;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by pawel on 29.03.15.
 */
public class CopyExecutor {
	private InputStream source;
	private OutputStream destination;

	private int bufferSize = 512 * 1024;
	private ProgressListener progressListener = count -> {};
	private AbortNotifier abortNotifier = () -> false;

	public void start() throws IOException {
		final byte [] buffer = new byte[bufferSize];

		int total = 0;
		int count = 0;
		while ((count = source.read(buffer)) != -1 && !abortNotifier.isAborted()) {
			total += count;
			progressListener.onProgress(total);
			destination.write(buffer, 0, count);
		}
	}

	public static CopyExecutorBuilder builder() {
		return new CopyExecutorBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class CopyExecutorBuilder extends ObjectBuilder<CopyExecutorBuilder, CopyExecutor> {
		public CopyExecutorBuilder source(InputStream src) {
			return addOperation(exec -> exec.source = src);
		}

		public CopyExecutorBuilder destination(OutputStream dst) {
			return addOperation(exec -> exec.destination = dst);
		}

		public CopyExecutorBuilder bufferSize(int bufferSize) {
			return addOperation(exec -> exec.bufferSize = bufferSize);
		}

		public CopyExecutorBuilder progressListener(ProgressListener listener) {
			return addOperation(exec -> exec.progressListener = listener);
		}

		public CopyExecutorBuilder abortNotifier(AbortNotifier notifier) {
			return addOperation(exec -> exec.abortNotifier = notifier);
		}

		@Override
		protected CopyExecutor createObject() {
			return new CopyExecutor();
		}
	}
}
