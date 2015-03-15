package com.pchudzik.docs.feed;

import com.google.common.base.Stopwatch;
import com.pchudzik.docs.feed.model.Feed;
import com.pchudzik.docs.feed.model.FeedCategory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Created by pawel on 14.03.15.
 */
@Slf4j
@Service
public class OnlineFeedRepository {
	private static final ParameterizedTypeReference<List<FeedCategory>> categoryListType = new ParameterizedTypeReference<List<FeedCategory>>() {};
	private final String feedUrl;
	private final RestTemplate restTemplate;

	@Autowired
	public OnlineFeedRepository(
			@Value("${docs.feed.url}") String feedUrl,
			RestTemplate restTemplate) {
		this.feedUrl = feedUrl;
		this.restTemplate = restTemplate;
	}

	public List<FeedCategory> listCategories() {
		final String categoriesFeedUrl = feedUrl + "/index.feed.json";
		final Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			return restTemplate
					.exchange(categoriesFeedUrl, HttpMethod.GET, HttpEntity.EMPTY, categoryListType)
					.getBody();
		} finally {
			log.debug("Categories fetched from {} in {}", categoriesFeedUrl, stopwatch.stop());
		}
	}

	public Feed getFeed(String file) {
		final String feedUrl = this.feedUrl + "/" + file;
		final Stopwatch stopwatch = Stopwatch.createStarted();
		try {
			return restTemplate
					.getForEntity(feedUrl, Feed.class)
					.getBody();
		} finally {
			log.debug("Feed fetched from {} in {}", feedUrl, stopwatch.stop());
		}
	}
}
