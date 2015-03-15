package com.pchudzik.docs.feed;

import com.beust.jcommander.internal.Lists;
import com.pchudzik.docs.feed.model.*;
import com.pchudzik.docs.infrastructure.HttpClientsConfiguration;
import lombok.SneakyThrows;
import org.joda.time.DateTime;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.pchudzik.docs.feed.model.FeedAssertHelper.*;
import static com.pchudzik.docs.feed.model.FeedCategoryAssertHelper.name;
import static com.pchudzik.docs.feed.model.FeedInfoAssertHelper.additionalInfoHtml;
import static com.pchudzik.docs.feed.model.FeedInfoAssertHelper.url;
import static com.pchudzik.docs.feed.model.RewriteRuleAssertHelper.regexp;
import static com.pchudzik.docs.feed.model.RewriteRuleAssertHelper.replacement;
import static com.pchudzik.docs.utils.json.JsonHelper.fixJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public class OnlineFeedRepositoryTest {
	private static final String FEED_URL = "http://example.com";

	RestTemplate restTemplate;
	MockRestServiceServer mockRestServiceServer;

	OnlineFeedRepository feedRepository;

	@BeforeMethod
	void setupRepository() {
		restTemplate = new HttpClientsConfiguration().restTemplate();
		mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
		feedRepository = new OnlineFeedRepository(FEED_URL, restTemplate);
	}

	@Test
	public void should_download_categories_feed() {
		final String feedCategoryJson = fixJson("[{" +
				"  name: 'Java'," +
				"  feeds: [{" +
				"    name: 'Java SE API'," +
				"    additionalInfoHtml: 'Oracle license'," +
				"    feedFile: 'java/java-se-api.feed.json'" +
				"  }]" +
				"}]");
		mockRestServiceServer.expect(requestTo(FEED_URL + "/index.feed.json"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(feedCategoryJson, MediaType.APPLICATION_JSON));

		//when
		final List<FeedCategory> feedCategories = feedRepository.listCategories();

		//then
		mockRestServiceServer.verify();
		assertThat(feedCategories)
				.hasSize(1)
				.has(name("Java"), atIndex(0));
		assertThat(getOnlyElement(feedCategories).getFeeds())
				.hasSize(1)
				.has(FeedInfoAssertHelper.name("Java SE API"), atIndex(0))
				.has(additionalInfoHtml("Oracle license"), atIndex(0))
				.has(url("java/java-se-api.feed.json"), atIndex(0));
	}

	@Test
	@SneakyThrows
	public void should_download_requested_feed() {
		final String feedResponseJson = fixJson("{" +
				"  updateDate: '2015-03-15T11:34:32.000Z'," +
				"  name: 'Java SE API'," +
				"  url: 'http://oracle.com/download/java.zip'," +
				"  initialDirectory: 'api', " +
				"  rootDirectory: 'java'," +
				"  rewriteRules: [{" +
				"    regexp: 'java/*'," +
				"    replacement: '/'" +
				"  }]," +
				"  httpConfiguration: {" +
				"    headers: [{ name: 'cookie', value: 'oracle' }]" +
				"  }" +
				"}");

		mockRestServiceServer.expect(requestTo(FEED_URL + "/java-se-api.feed.json"))
				.andExpect(method(HttpMethod.GET))
				.andRespond(withSuccess(feedResponseJson, MediaType.APPLICATION_JSON));

		final Feed feed = feedRepository.getFeed("java-se-api.feed.json");

		mockRestServiceServer.verify();
		assertThat(feed)
				.has(FeedAssertHelper.name("Java SE API"))
				.has(FeedAssertHelper.url("http://oracle.com/download/java.zip"))
				.has(updateDate(DateTime.parse("2015-03-15T11:34:32.000Z")))
				.has(initialDirectory("api"))
				.has(rootDirectory("java"))
				.has(configurationSetup());
		assertThat(feed.getRewriteRules())
				.hasSize(1)
				.has(regexp("java/*"), atIndex(0))
				.has(replacement("/"), atIndex(0));
		assertThat(Lists.newArrayList(feed.getHttpConfiguration().get().getHeaders()))
				.hasSize(1)
				.has(HttpHeaderAssertHelper.name("cookie"), atIndex(0))
				.has(HttpHeaderAssertHelper.value("oracle"), atIndex(0));
	}

}