package com.pchudzik.docs.feed.download;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.pchudzik.docs.feed.download.event.DownloadEvent;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * Created by pawel on 15.04.15.
 */
@Service
public class DownloadInfoRepository implements DownloadEventListener {
	private final Map<String, DownloadInfo> downloadRepository = Maps.newConcurrentMap();

	public List<DownloadInfo> listDownloads() {
		final ArrayList<DownloadInfo> result = Lists.newArrayList(downloadRepository.values());
		result.sort(byDateComparator().reversed());
		return result;
	}

	private Comparator<DownloadInfo> byDateComparator() {
		return (o1, o2) -> ObjectUtils.compare(o1.getSubmitDate(), o2.getSubmitDate());
	}

	public DownloadInfo findOne(String downloadId) {
		return Optional.ofNullable(downloadRepository.get(downloadId))
				.orElseThrow(() -> new NoResultException("No download job with id " + downloadId));
	}

	@Override
	public void onEvent(DownloadInfo source, DownloadEvent event) {
		if(DownloadEvent.EventType.SUBMIT == event.getEventType()) {
			downloadRepository.put(source.getId(), source);
		} else if(DownloadEvent.EventType.REMOVE == event.getEventType()) {
			downloadRepository.remove(source.getId());
		}
	}
}
