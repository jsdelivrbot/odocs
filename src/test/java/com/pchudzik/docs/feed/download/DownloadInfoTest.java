package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.utils.FakeTimeProvider;
import org.assertj.core.data.Offset;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.pchudzik.docs.feed.download.DownloadInfo.PROGRESS_STEP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.offset;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by pawel on 14.04.15.
 */
public class DownloadInfoTest {
	public static final String ANY_ID = "any id";
	@Mock DownloadEventListener downloadEventListener;
	private DownloadInfo downloadInfo;

	@BeforeMethod void setup() {
		initMocks(this);
		downloadInfo = DownloadInfo.builder()
				.timeProvider(new FakeTimeProvider(DateTime.now()))
				.downloadEventListener(downloadEventListener)
				.build();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void should_throw_exception_when_abort_of_finished_job() {
		downloadInfo.start();
		downloadInfo.finish(ANY_ID);

		//when
		downloadInfo.requestAbort();

		//then exception
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void should_throw_exception_when_removal_of_not_finished_job() {
		downloadInfo.start();

		//when
		downloadInfo.requestRemove();

		//then exception
	}

	@Test
	public void should_remove_finished_job() {
		downloadInfo.start();
		downloadInfo.finish(ANY_ID);

		//when
		downloadInfo.requestRemove();

		//then no exception
	}

	@Test
	public void should_remove_aborted_job() {
		downloadInfo.start();
		downloadInfo.requestAbort();
		downloadInfo.progress(10, 1);

		//when
		downloadInfo.requestRemove();

		//then no exception
	}

	@Test
	public void should_initialize_progress_if_not_initialized_yet() {
		downloadInfo.start();

		downloadInfo.progress(100, 1);

		assertThat(downloadInfo.getProgressEvent()).isNotNull();
	}

	@Test
	public void should_update_progress_if_threshold_reached() {
		final Offset<Double> doubleCheckThreshold = offset(0.001);
		final int totalBytes = 10_000;
		final int thresholdTrigger = (int)(totalBytes * PROGRESS_STEP);

		final int initialBytes = 1;
		final int finalDownloadBytes = initialBytes + thresholdTrigger * 2;

		final double initialProgress = initialBytes / (double)totalBytes;
		final double finalProgress = finalDownloadBytes / (double)totalBytes;
		downloadInfo.start();

		//when
		downloadInfo.progress(totalBytes, initialBytes);

		//then
		assertThat(downloadInfo.getProgressEvent().getProgress())
				.isEqualTo(initialProgress, doubleCheckThreshold);

		//when
		downloadInfo.progress(totalBytes, (int) (initialBytes + thresholdTrigger / 4.0));

		//then
		assertThat(downloadInfo.getProgressEvent().getProgress())
				.isEqualTo(initialProgress, doubleCheckThreshold);

		//when
		downloadInfo.progress(totalBytes, finalDownloadBytes);

		//then
		assertThat(downloadInfo.getProgressEvent().getProgress())
				.isEqualTo(finalProgress);
	}
}