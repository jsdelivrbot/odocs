package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.utils.TimeProvider;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by pawel on 15.04.15.
 */
public class DownloadInfoRepositoryTest {
	DownloadInfoRepository downloadInfoRepository;

	@BeforeMethod
	void setup() {
		initMocks(this);

		downloadInfoRepository = new DownloadInfoRepository();
	}

	@Test
	public void should_sort_download_info_by_submit_date() {
		final DateTime firstDate = DateTime.now();
		final DateTime secondDate = firstDate.plusMinutes(1);

		downloadInfo("oldest", firstDate);
		downloadInfo("latest", secondDate);

		final List<String> docIds = downloadInfoRepository.listDownloads()
				.stream()
				.map(DownloadInfo::getId)
				.collect(toList());
		assertThat(docIds)
				.containsExactly("latest", "oldest");
	}

	private DownloadInfo downloadInfo(String id,DateTime submitDate) {
		final TimeProvider timeProvider = mock(TimeProvider.class);
		when(timeProvider.now()).thenReturn(submitDate);

		return DownloadInfo.builder()
				.id(id)
				.timeProvider(timeProvider)
				.downloadEventListener(downloadInfoRepository)
				.build();
	}

}