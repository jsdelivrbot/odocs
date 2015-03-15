package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.utils.TimeProvider;
import org.joda.time.DateTime;
import org.mockito.Mock;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by pawel on 28.03.15.
 */
public class DownloadInfoServiceTest {
	@Mock TimeProvider timeProvider;
	@Mock AsyncTaskExecutor asyncTaskExecutor;
	@Mock DownloadJobFactory downloadJobFactory;
	@Mock SimpMessagingTemplate brokerMessagingTemplate;

	DownloadInfoService downloadInfoService;

	@BeforeMethod void setup() {
		initMocks(this);

		downloadInfoService = new DownloadInfoService(
				timeProvider,
				asyncTaskExecutor,
				brokerMessagingTemplate,
				downloadJobFactory);
	}

	@Test
	public void should_sort_download_info_by_submit_date() {
		final DateTime firstDate = DateTime.now();
		final DateTime secondDate = firstDate.plusMinutes(1);
		when(timeProvider.now()).thenReturn(firstDate, secondDate, null);

		downloadInfoService.startDownload("oldest", "any name", "oldest");
		downloadInfoService.startDownload("latest", "any name", "latest");
		downloadInfoService.startDownload("notSubmitted", "any name", "notSubmitted");

		final List<String> docIds = downloadInfoService.listDownloads()
				.stream()
				.map(info -> info.getDocumentationId())
				.map(maybeDocId -> maybeDocId.get())
				.collect(toList());
		assertThat(docIds)
				.containsExactly("notSubmitted", "latest", "oldest");
	}
}