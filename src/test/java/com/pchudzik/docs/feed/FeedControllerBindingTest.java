package com.pchudzik.docs.feed;

import com.google.common.collect.ImmutableMap;
import com.pchudzik.docs.feed.download.DownloadInfo;
import com.pchudzik.docs.feed.download.DownloadInfoService;
import com.pchudzik.docs.feed.download.DownloadEventListener;
import com.pchudzik.docs.feed.download.DownloadSubmitEvent;
import com.pchudzik.docs.feed.model.FeedCategory;
import com.pchudzik.docs.feed.model.FeedInfo;
import com.pchudzik.docs.utils.http.ControllerTester;
import lombok.SneakyThrows;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.pchudzik.docs.utils.http.HttpRequestBuilders.httpGet;
import static com.pchudzik.docs.utils.http.HttpRequestBuilders.httpPost;
import static com.pchudzik.docs.utils.json.JsonHelper.fixJson;
import static com.pchudzik.docs.utils.json.JsonHelper.jsonFromTemplate;
import static com.pchudzik.docs.utils.json.JsonMockMvcResultMatchers.jsonContent;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class FeedControllerBindingTest {
	private static final String feedFile = "http://example.com";
	private static final String docId = "docId";
	private static final String feedName = "any name";
	private static final String downloadRequestPayload = jsonFromTemplate("{" +
			"  docId: '${docId}'," +
			"  feedName: '${feedName}'," +
			"  feedUrl: '${feedFile}'" +
			"}",
			ImmutableMap.of(
					"docId", docId,
					"feedName", feedName,
					"feedFile", feedFile));

	ControllerTester controllerTester;

	@Mock DownloadInfoService downloadInfoService;
	@Mock OnlineFeedRepository feedRepository;

	@BeforeMethod
	void setup() {
		initMocks(this);

		controllerTester = ControllerTester.builder()
				.controllers(new FeedController(downloadInfoService, feedRepository))
				.build();
	}

	@Test
	@SneakyThrows
	public void should_initialize_download_and_return_downloadId() {
		final DownloadInfo downloadInfo = DownloadInfo.builder()
				.id("id")
				.downloadEventListener(mock(DownloadEventListener.class))
				.build();
		downloadInfo.submit(DownloadSubmitEvent.builder()
				.feedFile(feedFile)
				.feedName(feedName)
				.documentationId(docId)
				.build());
		when(downloadInfoService.startDownload(docId, feedName, feedFile))
				.thenReturn(downloadInfo);
		downloadInfo.requestRemove();

		//when
		controllerTester.perform(httpPost("/feeds/downloads", downloadRequestPayload))

				//then
				.andExpect(status().isOk())
				.andExpect(jsonContent().isEqual(fixJson("{" +
						"  id: 'id'," +
						"  submitEvent: {" +
						"    eventType: 'SUBMIT'," +
						"    submitDate: null," +
						"    feedName: '" + feedName + "'," +
						"    documentationId: '" + docId + "'," +
						"    feedFile: '" + feedFile + "'," +
						"  }," +
						"  interrupted: true," +
						"  abortRequested: false," +
						"  removeRequested: true," +
						"  startEvent: null," +
						"  progressEvent: null," +
						"  finishEvent: null," +
						"  errorEvent: null," +
						"  abortEvent: null," +
						"  removeEvent: null" +
						"}")));
	}

	@Test
	@SneakyThrows
	public void should_list_feed_categories() {
		final String name = "java";
		final String info = "info";
		when(feedRepository.listCategories()).thenReturn(asList(FeedCategory.builder()
				.name(name)
				.feeds(FeedInfo.builder()
						.feedFile(feedFile)
						.additionalInfoHtml(info)
						.name(name)
						.build())
				.build()));

		//when
		controllerTester.perform(httpGet("/feeds"))

				//then
				.andExpect(status().isOk())
				.andExpect(jsonContent().isEqual(jsonFromTemplate("[{" +
						"  name: '${name}'," +
						"  feeds: [{" +
						"    feedFile: '${feedFile}'," +
						"    name: '${name}'," +
						"    additionalInfoHtml: '${info}'" +
						"  }]" +
						"}]", ImmutableMap.of("name", name, "feedFile", feedFile, "info", info))));
	}

	@Test
	@SneakyThrows
	public void should_execute_requested_action_on_item() {
		final String itemId = "itemId";

		//when
		controllerTester.perform(httpPost("/feeds/downloads/" + itemId + "/actions", fixJson("{action:'REMOVE'}")))
				.andExpect(status().isOk());

		verify(downloadInfoService).execute(itemId, ActionType.REMOVE);
	}
}