package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.utils.TimeProvider;
import org.mockito.Mock;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.testng.annotations.BeforeMethod;

import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by pawel on 28.03.15.
 */
public class DownloadInfoServiceTest {
	@Mock DownloadInfoRepository downloadInfoRepository;
	@Mock TimeProvider timeProvider;
	@Mock AsyncTaskExecutor asyncTaskExecutor;
	@Mock DownloadJobFactory downloadJobFactory;
	@Mock SimpMessagingTemplate brokerMessagingTemplate;

	DownloadInfoService downloadInfoService;

	@BeforeMethod void setup() {
		initMocks(this);

		downloadInfoService = new DownloadInfoService(
				downloadInfoRepository,
				timeProvider,
				asyncTaskExecutor,
				brokerMessagingTemplate,
				downloadJobFactory);
	}
}