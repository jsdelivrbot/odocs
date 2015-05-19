package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.feed.download.event.DownloadEventFactory;
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
	DownloadEventFactory downloadEventFactory = new DownloadEventFactory(new FakeTimeProvider(new DateTime(2015, 5, 19, 17, 57)));
	private DownloadInfo downloadInfo;

	@BeforeMethod void setup() {
		initMocks(this);
		downloadInfo = DownloadInfo.builder()
				.downloadEventListener(downloadEventListener)
				.build();
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void should_throw_exception_when_abort_of_finished_job() {
		downloadInfo.start(downloadEventFactory.startEvent());
		downloadInfo.finish(downloadEventFactory.finishEvent(ANY_ID));

		//when
		downloadInfo.requestAbort(downloadEventFactory.abortEvent());

		//then exception
	}

	@Test(expectedExceptions = IllegalStateException.class)
	public void should_throw_exception_when_removal_of_not_finished_job() {
		downloadInfo.start(downloadEventFactory.startEvent());

		//when
		downloadInfo.requestRemove(downloadEventFactory.removeEvent());

		//then exception
	}

	@Test
	public void should_remove_finished_job() {
		downloadInfo.start(downloadEventFactory.startEvent());
		downloadInfo.finish(downloadEventFactory.finishEvent(ANY_ID));

		//when
		downloadInfo.requestRemove(downloadEventFactory.removeEvent());

		//then no exception
	}

	@Test
	public void should_remove_aborted_job() {
		downloadInfo.start(downloadEventFactory.startEvent());
		downloadInfo.requestAbort(downloadEventFactory.abortEvent());
		downloadInfo.progress(downloadEventFactory.progressEvent(10, 1));

		//when
		downloadInfo.requestRemove(downloadEventFactory.removeEvent());

		//then no exception
	}

	@Test
	public void should_initialize_progress_if_not_initialized_yet() {
		downloadInfo.start(downloadEventFactory.startEvent());

		downloadInfo.progress(downloadEventFactory.progressEvent(100, 1));

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
		downloadInfo.start(downloadEventFactory.startEvent());

		//when
		downloadInfo.progress(downloadEventFactory.progressEvent(totalBytes, initialBytes));

		//then
		assertThat(downloadInfo.getProgressEvent().getProgress())
				.isEqualTo(initialProgress, doubleCheckThreshold);

		//when
		downloadInfo.progress(downloadEventFactory.progressEvent(totalBytes, (int) (initialBytes + thresholdTrigger / 4.0)));

		//then
		assertThat(downloadInfo.getProgressEvent().getProgress())
				.isEqualTo(initialProgress, doubleCheckThreshold);

		//when
		downloadInfo.progress(downloadEventFactory.progressEvent(totalBytes, finalDownloadBytes));

		//then
		assertThat(downloadInfo.getProgressEvent().getProgress())
				.isEqualTo(finalProgress);
	}
}