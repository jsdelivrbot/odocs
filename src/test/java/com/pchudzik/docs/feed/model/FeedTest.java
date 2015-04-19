package com.pchudzik.docs.feed.model;

import org.apache.http.Header;
import org.apache.http.client.methods.HttpUriRequest;
import org.assertj.core.api.Condition;
import org.testng.annotations.Test;

import java.util.Objects;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.atIndex;

public class FeedTest {
	private static final String fileUrl = "http://example.com/file.zip";

	@Test
	public void should_build_get_query_with_url() {
		final Feed feed = Feed.builder()
				.url(fileUrl)
				.build();

		final HttpUriRequest httpRequest = feed.httpRequest();

		assertThat(httpRequest.getURI().toASCIIString())
				.isEqualTo(fileUrl);
	}

	@Test
	public void should_apply_custom_http_configuration() {
		final Feed feed = Feed.builder()
				.url(fileUrl)
				.httpConfiguration(HttpConfiguration.builder()
						.headers(new HttpHeader("header", "value"))
						.build())
				.build();

		final HttpUriRequest httpRequest = feed.httpRequest();

		assertThat(asList(httpRequest.getAllHeaders()))
				.hasSize(1)
				.has(header(name("header"), value("value")), atIndex(0));
	}

	private Condition<? super Header> header(String name, String value) {
		return new Condition<Header>() {
			@Override
			public boolean matches(Header header) {
				return Objects.equals(header.getName(), name) &&
						Objects.equals(header.getValue(), value);
			}
		};
	}

	private String value(String value) {
		return value;
	}

	private String name(String name) {
		return name;
	}


}