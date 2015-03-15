package com.pchudzik.docs.feed.download.copy;

import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by pawel on 29.03.15.
 */
public class CopyExecutorTest {
	private static final int streamEnd = -1;
	final int offset = 0;
	final int readBytes = 1;

	@Mock InputStream inputStream;
	@Mock OutputStream outputStream;

	@Mock AbortNotifier abortNotifier;
	@Mock ProgressListener progressListener;

	@BeforeMethod void setup() {
		initMocks(this);
	}

	@Test
	@SneakyThrows
	public void should_abort_download() {
		when(inputStream.read(any(byte[].class))).thenReturn(readBytes);
		when(abortNotifier.isAborted()).thenReturn(false, true);

		//when
		CopyExecutor.builder()
				.abortNotifier(abortNotifier)
				.source(inputStream)
				.destination(outputStream)
				.build()
				.start();

		//then
		verify(outputStream, times(1)).write(any(byte[].class), eq(offset), eq(readBytes));
	}

	@Test
	@SneakyThrows
	public void should_notify_on_progress() {
		when(inputStream.read(any(byte[].class))).thenReturn(readBytes, readBytes, streamEnd);

		//when
		CopyExecutor.builder()
				.source(inputStream)
				.destination(outputStream)
				.progressListener(progressListener)
				.build()
				.start();

		//then
		verify(progressListener).onProgress(readBytes);
		verify(progressListener).onProgress(2 * readBytes);
	}

	@Test
	@SneakyThrows
	public void should_copy_stream_fully() {
		final byte[] FIVE_MB = RandomUtils.nextBytes(1024);

		final ByteArrayInputStream inputStream = new ByteArrayInputStream(FIVE_MB);
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		//when
		CopyExecutor.builder()
				.bufferSize(10)
				.source(inputStream)
				.destination(outputStream)
				.build()
				.start();

		//then
		assertThat(outputStream.toByteArray()).isEqualTo(FIVE_MB);
	}

}