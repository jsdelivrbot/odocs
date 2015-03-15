package com.pchudzik.docs.feed.model;

import com.google.common.collect.Lists;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.joda.time.DateTime;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by pawel on 12.03.15.
 */
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Feed {
	@Getter DateTime updateDate;

	@Getter String name;
	@Getter String url;
	HttpConfiguration httpConfiguration;

	@Getter String initialDirectory;
	@Getter String rootDirectory;
	List<RewriteRule> rewriteRules = Lists.newLinkedList();

	public static FeedBuilder builder() {
		return new FeedBuilder();
	}

	public List<RewriteRule> getRewriteRules() {
		return Collections.unmodifiableList(rewriteRules);
	}

	public Optional<HttpConfiguration> getHttpConfiguration() {
		return Optional.ofNullable(httpConfiguration);
	}

	public HttpUriRequest httpRequest() {
		final HttpGet getFileRequest = new HttpGet(url);
		getHttpConfiguration().ifPresent(config -> config.getHeaders()
				.forEach(header -> getFileRequest.addHeader(header.getName(), header.getValue())));
		return getFileRequest;
	}

	public VersionDto asVersionDto() {
		return VersionDto.builder()
				.name(name)
				.initialDirectory(initialDirectory)
				.rootDirectory(rootDirectory)
				.build();
	}

	private void setRewriteRules(Collection<RewriteRule> rewriteRules) {
		this.rewriteRules.clear();
		this.rewriteRules.addAll(rewriteRules);
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class FeedBuilder extends ObjectBuilder<FeedBuilder, Feed> {
		public FeedBuilder name(String name) {
			return addOperation(feed -> feed.name = name);
		}

		public FeedBuilder updateDate(DateTime updateDate) {
			return addOperation(feed -> feed.updateDate = updateDate);
		}

		public FeedBuilder url(String url) {
			return addOperation(feed -> feed.url = url);
		}

		public FeedBuilder httpConfiguration(HttpConfiguration configuration) {
			return addOperation(feed -> feed.httpConfiguration = configuration);
		}

		public FeedBuilder rewriteRules(Collection<RewriteRule> urlRewriteRules) {
			return addOperation(feed -> feed.setRewriteRules(urlRewriteRules));
		}
		public FeedBuilder initialDirectory(String dir) {
			return addOperation(feed -> feed.initialDirectory = dir);
		}

		public FeedBuilder rootDirectory(String dir) {
			return addOperation(feed -> feed.rootDirectory = dir);
		}

		@Override
		protected Feed createObject() {
			return new Feed();
		}
	}
}
