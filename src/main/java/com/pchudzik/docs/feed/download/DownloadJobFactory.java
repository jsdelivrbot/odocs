package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.feed.OnlineFeedRepository;
import com.pchudzik.docs.infrastructure.annotation.TemporaryDirectory;
import com.pchudzik.docs.manage.ManagementService;
import com.pchudzik.docs.utils.http.MultipartFileFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Created by pawel on 28.03.15.
 */
@Service
class DownloadJobFactory {
	final CloseableHttpClient httpClient;
	final ManagementService managementService;
	final OnlineFeedRepository feedRepository;
	final MultipartFileFactory multipartFileFactory;
	final File tmpDir;

	@Autowired
	DownloadJobFactory(
			CloseableHttpClient httpClient,
			ManagementService managementService,
			OnlineFeedRepository feedRepository,
			MultipartFileFactory multipartFileFactory,
			@TemporaryDirectory File tmpDir) {
		this.httpClient = httpClient;
		this.managementService = managementService;
		this.feedRepository = feedRepository;
		this.multipartFileFactory = multipartFileFactory;
		this.tmpDir = tmpDir;
	}

	public DownloadJob create(DownloadInfo downloadInfo) {
		return DownloadJob.builder()
				.downloadInfo(downloadInfo)
				.feedRepository(feedRepository)
				.httpClient(httpClient)
				.managementService(managementService)
				.multipartFileFactory(multipartFileFactory)
				.tmpDir(tmpDir)
				.build();
	}
}
