package com.pchudzik.docs.feed;

import com.pchudzik.docs.feed.download.DownloadInfo;
import com.pchudzik.docs.feed.download.DownloadInfoRepository;
import com.pchudzik.docs.feed.download.DownloadInfoService;
import com.pchudzik.docs.feed.model.FeedCategory;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
* Created by pawel on 12.03.15.
*/
@RestController
@RequestMapping("/feeds")
class FeedController {
	private final DownloadInfoRepository downloadInfoRepository;
	private final DownloadInfoService downloadInfoService;
	private final OnlineFeedRepository feedRepository;

	@Autowired
	FeedController(
			DownloadInfoRepository downloadInfoRepository,
			DownloadInfoService downloadInfoService,
			OnlineFeedRepository feedRepository) {
		this.downloadInfoRepository = downloadInfoRepository;
		this.downloadInfoService = downloadInfoService;
		this.feedRepository = feedRepository;
	}

	@RequestMapping
	List<FeedCategory> listCategories() {
		return feedRepository.listCategories();
	}

	@RequestMapping(value = "/downloads", method = RequestMethod.POST)
	DownloadInfo startDownload(@RequestBody FeedSaveRequest saveRequest) {
		return downloadInfoService.startDownload(saveRequest.getDocId(), saveRequest.getFeedName(), saveRequest.getFeedUrl());
	}

	@RequestMapping(value = "/downloads/{id}/actions", method = RequestMethod.POST)
	void runActionOnDownload(@PathVariable String id, @RequestBody ActionRequest actionRequest) {
		if(actionRequest.getAction() == ActionType.ABORT) {
			downloadInfoService.abortDownload(id);
		} else if(actionRequest.getAction() == ActionType.REMOVE) {
			downloadInfoService.removeDownload(id);
		} else {
			throw new UnsupportedOperationException("Unsupported action " + actionRequest.getAction());
		}
	}

	@RequestMapping(value = "/downloads", method = RequestMethod.GET)
	List<DownloadInfo> downloadInfos() {
		return downloadInfoRepository.listDownloads();
	}

	@Getter
	static class FeedSaveRequest {
		String docId;
		String feedName;
		String feedUrl;
	}

	@Getter
	static class ActionRequest {
		ActionType action;
	}

	enum ActionType {
		ABORT,
		REMOVE
	}
}
