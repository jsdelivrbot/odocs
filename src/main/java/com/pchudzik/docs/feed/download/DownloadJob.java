package com.pchudzik.docs.feed.download;

import com.google.common.base.Stopwatch;
import com.pchudzik.docs.feed.OnlineFeedRepository;
import com.pchudzik.docs.feed.download.copy.CopyExecutor;
import com.pchudzik.docs.feed.download.event.DownloadEventFactory;
import com.pchudzik.docs.feed.model.Feed;
import com.pchudzik.docs.manage.ManagementService;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.utils.http.MultipartFileFactory;
import lombok.SneakyThrows;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by pawel on 28.03.15.
 */
@Slf4j
@Builder
class DownloadJob implements Runnable {
	final DownloadInfo downloadInfo;

	final CloseableHttpClient httpClient;
	final ManagementService managementService;
	final OnlineFeedRepository feedRepository;
	final MultipartFileFactory multipartFileFactory;
	final DownloadEventFactory downloadEventFactory;
	final File tmpDir;

	@Override
	public void run() {
		downloadInfo.start(downloadEventFactory.startEvent());

		final Feed feed = feedRepository.getFeed(downloadInfo.getFeedFile());

		File savedFile = null;
		try (final CloseableHttpResponse httpResponse = httpClient.execute(feed.httpRequest())){
			savedFile = downloadFile(httpResponse);

			if(downloadInfo.isInterrupted()) {
				downloadInfo.finish(downloadEventFactory.finishEventWithoutResult());
			} else {
				final VersionDto versionDto = managementService.updateVersion(
						downloadInfo.getDocumentationId(),
						feed,
						multipartFileFactory.fromFile(savedFile));
				downloadInfo.finish(downloadEventFactory.finishEvent(versionDto.getId()));
			}
		} catch (Exception ex) {
			downloadInfo.finish(downloadEventFactory.errorEvent(ex));
		} finally {
			FileUtils.deleteQuietly(savedFile);
		}
	}

	@SneakyThrows
	private File downloadFile(HttpResponse response) {
		if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new IllegalStateException("Expected status 200 but was " + response.getStatusLine().getStatusCode());
		}

		final HttpEntity httpEntity = response.getEntity();
		final long totalBytes = httpEntity.getContentLength();
		final File dstFile = new File(tmpDir, RandomStringUtils.random(8, true, true));
		final Stopwatch stopwatch = Stopwatch.createStarted();
		try (final FileOutputStream fos = new FileOutputStream(dstFile)) {
			CopyExecutor.builder()
					.abortNotifier(downloadInfo::isInterrupted)
					.progressListener(bytes -> downloadInfo.progress(downloadEventFactory.progressEvent((int)totalBytes, bytes)))
					.source(new BufferedInputStream(httpEntity.getContent()))
					.destination(fos)
					.build()
					.start();
			return dstFile;
		} finally {
			log.debug("{} bytes downloaded in {} to {}", totalBytes, stopwatch.stop(), dstFile.getAbsolutePath());
		}
	}
}
