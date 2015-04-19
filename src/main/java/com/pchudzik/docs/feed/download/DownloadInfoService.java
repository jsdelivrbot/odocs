package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.feed.download.event.DownloadEvent;
import com.pchudzik.docs.infrastructure.annotation.FeedDownloadTaskExecutor;
import com.pchudzik.docs.utils.TimeProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 28.03.15.
 */
@Service
public class DownloadInfoService implements DownloadEventListener {
	private final DownloadInfoRepository downloadInfoRepository;
	private final TimeProvider timeProvider;
	private final AsyncTaskExecutor downloadTaskExecutor;
	private final DownloadJobFactory downloadJobFactory;
	private final SimpMessagingTemplate brokerMessagingTemplate;

	@Autowired
	public DownloadInfoService(
			DownloadInfoRepository downloadInfoRepository, TimeProvider timeProvider,
			@FeedDownloadTaskExecutor AsyncTaskExecutor downloadTaskExecutor,
			SimpMessagingTemplate brokerMessagingTemplate,
			DownloadJobFactory downloadJobFactory) {
		this.downloadInfoRepository = downloadInfoRepository;
		this.timeProvider = timeProvider;
		this.downloadTaskExecutor = downloadTaskExecutor;
		this.downloadJobFactory = downloadJobFactory;
		this.brokerMessagingTemplate = brokerMessagingTemplate;
	}

	@Override
	public void onEvent(DownloadInfo source, DownloadEvent event) {
		brokerMessagingTemplate.convertAndSend("/feeds/downloads/events", new EmitableDownloadEvent(source, event));
	}

	public DownloadInfo startDownload(String documentationId, String feedName, String feedFile) {
		final DownloadInfo downloadInfo = DownloadInfo.builder()
				.timeProvider(timeProvider)
				.downloadEventListener(new CompositeListnener(asList(this, downloadInfoRepository)))
				.feedFile(feedFile)
				.feedName(feedName)
				.documentationId(documentationId)
				.build();
		downloadTaskExecutor.submit(downloadJobFactory.create(downloadInfo));
		return downloadInfo;
	}

	public void removeDownload(String id) {
		downloadInfoRepository.findOne(id).requestRemove();
	}

	public void abortDownload(String id) {
		downloadInfoRepository.findOne(id).requestAbort();
	}

	@RequiredArgsConstructor
	private static class CompositeListnener implements DownloadEventListener {
		private final List<DownloadEventListener> listeners;

		@Override
		public void onEvent(DownloadInfo source, DownloadEvent event) {
			listeners.forEach(lst -> lst.onEvent(source, event));
		}
	}
}
