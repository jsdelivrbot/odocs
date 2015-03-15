package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.feed.OnlineFeedRepository;
import com.pchudzik.docs.feed.model.Feed;
import com.pchudzik.docs.infrastructure.HttpClientsConfiguration;
import com.pchudzik.docs.manage.ManagementService;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.model.DocumentationVersion;
import com.pchudzik.docs.utils.TimeProvider;
import com.pchudzik.docs.utils.http.MultipartFileFactory;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.matchers.Times;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;
import org.springframework.web.multipart.MultipartFile;
import org.testng.annotations.*;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by pawel on 29.03.15.
 */
public class DownloadJobTest {
	private static final int SERVER_PORT = 11201;
	private static final String FEED_FILE = "feedFile";

	private final byte [] SMALL_FILE = RandomUtils.nextBytes(1);
	private final DateTime NOW = DateTime.now();

	@Mock OnlineFeedRepository feedRepository;
	@Mock TimeProvider timeProvider;
	@Mock ManagementService managementService;
	@Mock DownloadInfo downloadInfo;
	MultipartFileFactory multipartFileFactory;
	File tmpDirectory;

	DownloadJobFactory downloadJobFactory;


	private ClientAndServer mockServer;

	@BeforeClass
	void setupServer() {
		mockServer = new ClientAndServer(SERVER_PORT);
	}

	@AfterClass
	public void tearDownServer() {
		mockServer.stop();
	}

	@SneakyThrows
	@BeforeMethod
	void setup() {
		initMocks(this);
		mockServer.reset();
		tmpDirectory = Files.createTempDirectory(new File("build").toPath(), "DownloadJobTest").toFile();
		when(timeProvider.now()).thenReturn(NOW);
		multipartFileFactory = new MultipartFileFactory(tmpDirectory);

		downloadJobFactory = new DownloadJobFactory(
				timeProvider,
				new HttpClientsConfiguration().httpClient(),
				managementService,
				feedRepository,
				multipartFileFactory,
				tmpDirectory);
	}

	@SneakyThrows
	@AfterMethod
	void tearDown() {
		FileUtils.deleteDirectory(tmpDirectory);
	}

	@Test
	public void should_broadcast_on_start_event() {
		final DownloadJob job = setupSimpleFileDownload(SC_OK, FEED_FILE, SMALL_FILE);

		//when
		job.run();

		verify(downloadInfo).start(any(DownloadStartEvent.class));
	}

	@Test
	public void should_broadcast_on_progress_event() {
		final DownloadJob job = setupSimpleFileDownload(SC_OK, FEED_FILE, SMALL_FILE);

		//when
		job.run();

		//then
		verify(downloadInfo, atLeastOnce()).progress(any(DownloadProgressEvent.class));
	}

	@Test
	public void should_broadcast_on_finish_event() {
		final DownloadJob downloadJob = setupSimpleFileDownload(SC_OK, FEED_FILE, SMALL_FILE);
		when(managementService.updateVersion(anyString(), any(Feed.class), any(MultipartFile.class)))
				.thenReturn(new VersionDto(DocumentationVersion.builder()
						.id("id")
						.name("name")
						.build()));

		//when
		downloadJob.run();

		//then
		verify(downloadInfo).finish(any(DownloadFinishEvent.class));
	}

	@Test
	public void should_broadcast_on_error_event() {
		final DownloadJob downloadJob = setupSimpleFileDownload(SC_NOT_FOUND, FEED_FILE, SMALL_FILE);

		//when
		downloadJob.run();

		//then
		verify(downloadInfo).error(any(DownloadErrorEvent.class));
	}

	@Test
	public void should_discard_aborted_download() {
		when(downloadInfo.isInterrupted()).thenReturn(false, true);
		final DownloadJob downloadJob = setupSimpleFileDownload(SC_OK, FEED_FILE, SMALL_FILE);

		downloadJob.run();

		//then
		verify(managementService, never()).updateVersion(anyString(), any(Feed.class), any(MultipartFile.class));
	}

	@Test
	@SneakyThrows
	public void should_create_version_with_downloaded_file() {
		final byte [] file = RandomUtils.nextBytes(1024);
		final DownloadJob downloadJob = setupSimpleFileDownload(SC_OK, FEED_FILE, file);

		//when
		downloadJob.run();

		//then
		ArgumentCaptor<MultipartFile> fileArgumentCaptor = ArgumentCaptor.forClass(MultipartFile.class);
		verify(managementService).updateVersion(anyString(), any(Feed.class), fileArgumentCaptor.capture());
		assertThat(fileArgumentCaptor.getValue().getBytes()).isEqualTo(file);
	}

	private DownloadJob setupSimpleFileDownload(int statusCode, String feedFile, byte[] file) {
		when(downloadInfo.getFeedFile()).thenReturn(Optional.of(FEED_FILE));
		when(downloadInfo.getId()).thenReturn("id");
		when(downloadInfo.getDocumentationId()).thenReturn(Optional.of("docId"));
		mockServer
				.when(HttpRequest.request()
								.withMethod("GET")
								.withPath("/example.zip"),
						Times.exactly(1))
				.respond(HttpResponse.response()
						.withStatusCode(statusCode)
						.withBody(file));
		when(feedRepository.getFeed(feedFile))
				.thenReturn(Feed.builder()
						.url("http://localhost:" + SERVER_PORT + "/example.zip")
						.name("version")
						.build());
		return downloadJobFactory.create(downloadInfo);
	}
}