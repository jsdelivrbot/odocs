package com.pchudzik.docs.feed.download;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pchudzik.docs.feed.ActionType;
import com.pchudzik.docs.infrastructure.annotation.FeedDownloadTaskExecutor;
import com.pchudzik.docs.utils.TimeProvider;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created by pawel on 28.03.15.
 */
@Service
public class DownloadInfoService implements DownloadEventListener {
	private static final boolean NULL_FIRST = true;

	private final Map<String, DownloadInfo> downloadRepository = Maps.newConcurrentMap();

	private final TimeProvider timeProvider;
	private final AsyncTaskExecutor downloadTaskExecutor;
	private final DownloadJobFactory downloadJobFactory;
	private final SimpMessagingTemplate brokerMessagingTemplate;

	@Autowired
	public DownloadInfoService(
			TimeProvider timeProvider,
			@FeedDownloadTaskExecutor AsyncTaskExecutor downloadTaskExecutor,
			SimpMessagingTemplate brokerMessagingTemplate,
			DownloadJobFactory downloadJobFactory) {
		this.timeProvider = timeProvider;
		this.downloadTaskExecutor = downloadTaskExecutor;
		this.downloadJobFactory = downloadJobFactory;
		this.brokerMessagingTemplate = brokerMessagingTemplate;
	}

	@Override
	public void onEvent(DownloadInfo source, DownloadEvent event) {
		if(event.getEventType() == DownloadEvent.EventType.REMOVE) {
			downloadRepository.remove(source.getId());
		}

		brokerMessagingTemplate.convertAndSend("/feeds/downloads/events", new EmitableDownloadEvent(source, event));
	}

	public DownloadInfo startDownload(String documentationId, String feedName, String feedFile) {
		final DownloadInfo downloadInfo = DownloadInfo.builder()
				.downloadEventListener(this)
				.build();
		downloadInfo.submit(DownloadSubmitEvent.builder()
				.feedFile(feedFile)
				.feedName(feedName)
				.documentationId(documentationId)
				.submitDate(timeProvider.now())
				.build());
		downloadTaskExecutor.submit(downloadJobFactory.create(downloadInfo));
		downloadRepository.put(downloadInfo.getId(), downloadInfo);
		return downloadInfo;
	}

	public List<DownloadInfo> listDownloads() {
		final ArrayList<DownloadInfo> result = Lists.newArrayList(downloadRepository.values());
		result.sort(byDateComparator().reversed());
		return result;
	}

	private Comparator<DownloadInfo> byDateComparator() {
		return (o1, o2) -> ObjectUtils.compare(
				o1.getSubmitDate().orElse(null),
				o2.getSubmitDate().orElse(null),
				NULL_FIRST);
	}

	public void execute(String downloadId, ActionType action) {
		final DownloadInfo pendingDownload = Optional.ofNullable(downloadRepository.get(downloadId))
				.orElseThrow(() -> new NoResultException("No download job with id " + downloadId));

		if(action == ActionType.ABORT) {
			pendingDownload.requestAbort();
		} else if(action == ActionType.REMOVE) {
			if(pendingDownload.isRunning()) {
				pendingDownload.requestRemove();
			} else {
				pendingDownload.remove(DownloadRemoveEvent.builder()
						.removalDate(timeProvider.now())
						.build());
			}
		} else {
			throw new UnsupportedOperationException("Can not execute " + action + " on " + downloadId);
		}
	}

	@Getter
	private static class EmitableDownloadEvent {
		private final String id;

		@JsonUnwrapped
		private final DownloadEvent downloadEvent;

		public EmitableDownloadEvent(DownloadInfo source, DownloadEvent event) {
			this.id = source.getId();
			this.downloadEvent = event;
		}
	}
}
