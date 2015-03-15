package com.pchudzik.docs.feed.download;

import com.google.common.base.Stopwatch;
import com.pchudzik.docs.feed.OnlineFeedRepository;
import com.pchudzik.docs.feed.download.copy.CopyExecutor;
import com.pchudzik.docs.feed.download.copy.ProgressListener;
import com.pchudzik.docs.feed.model.Feed;
import com.pchudzik.docs.manage.ManagementService;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.utils.TimeProvider;
import com.pchudzik.docs.utils.http.MultipartFileFactory;
import lombok.RequiredArgsConstructor;
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

import java.io.File;
import java.io.FileOutputStream;
import java.util.function.Supplier;

/**
 * Created by pawel on 28.03.15.
 */
@Slf4j
@Builder
class DownloadJob implements Runnable {
	final DownloadInfo downloadInfo;

	final TimeProvider timeProvider;
	final CloseableHttpClient httpClient;
	final ManagementService managementService;
	final OnlineFeedRepository feedRepository;
	final MultipartFileFactory multipartFileFactory;
	final File tmpDir;

	@Override
	public void run() {
		downloadInfo.start(DownloadStartEvent.builder()
				.startDate(timeProvider.now())
				.build());

		final Feed feed = feedRepository.getFeed(downloadInfo.getFeedFile()
				.orElseThrow(startingNotSubmittedJobException()));

		File savedFile = null;
		try (final CloseableHttpResponse httpResponse = httpClient.execute(feed.httpRequest())){
			savedFile = downloadFile(httpResponse);

			if(!downloadInfo.isInterrupted()) {
				final VersionDto versionDto = managementService.updateVersion(
						downloadInfo.getDocumentationId().orElseThrow(startingNotSubmittedJobException()),
						feed,
						multipartFileFactory.fromFile(savedFile));
				downloadInfo.finish(DownloadFinishEvent.builder()
						.finishDate(timeProvider.now())
						.versionId(versionDto.getId())
						.build());
			} else {
				if(downloadInfo.isAbortRequested()) {
					abortDownloadInfo();
				} else if(downloadInfo.isRemoveRequested()) {
					removeDownloadInfo();
				}
			}

		} catch (Exception ex) {
			downloadInfo.error(DownloadErrorEvent.builder()
					.errorDate(timeProvider.now())
					.exception(ex)
					.build());
		} finally {
			if(savedFile != null) {
				FileUtils.deleteQuietly(savedFile);
			}
		}
	}

	private Supplier<IllegalStateException> startingNotSubmittedJobException() {
		return () -> new IllegalStateException("Starting not submitted job");
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
					.progressListener(new FileDownloadProgressListener((int) totalBytes))
					.source(httpEntity.getContent())
					.destination(fos)
					.build()
					.start();
			return dstFile;
		} finally {
			log.debug("{} bytes downloaded in {} to {}", totalBytes, stopwatch.stop(), dstFile.getAbsolutePath());
		}
	}

	private void abortDownloadInfo() {
		log.info("Download of {} aborted", downloadInfo.getId());
		downloadInfo.abort(DownloadAbortEvent.builder()
				.abortDate(timeProvider.now())
				.build());
	}

	private void removeDownloadInfo() {
		log.info("Download {} removed", downloadInfo.getId());
		downloadInfo.remove(DownloadRemoveEvent.builder()
				.removalDate(timeProvider.now())
				.build());
	}

	@RequiredArgsConstructor
	private class FileDownloadProgressListener implements ProgressListener {
		final int totalBytes;

		@Override
		public void onProgress(int transferredBytes) {
			final DownloadProgressEvent newProgress = DownloadProgressEvent.builder()
					.totalBytes(totalBytes)
					.downloadedBytes(transferredBytes)
					.build();
			if(downloadInfo.getProgressEvent() == null) {
				downloadInfo.progress(newProgress);
			} else {
				if(newProgress.getProgress() - downloadInfo.getProgressEvent().getProgress() >= 0.2) {
					log.trace("downloadId: {} progress: {}", downloadInfo.getId(), newProgress.getProgress());
					downloadInfo.progress(newProgress);
				}
			}
		}
	}
}
