package com.pchudzik.docs.feed.model;

import com.google.common.collect.Sets;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static java.util.Arrays.asList;

@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpConfiguration {
	private Set<HttpHeader> headers = Sets.newHashSet();

	public Set<HttpHeader> getHeaders() {
		return Collections.unmodifiableSet(headers);
	}

	public static HttpConfigurationBuilder builder() {
		return new HttpConfigurationBuilder();
	}

	private void setHeaders(Collection<HttpHeader> headers) {
		this.headers.clear();
		this.headers.addAll(headers);
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class HttpConfigurationBuilder extends ObjectBuilder<HttpConfigurationBuilder, HttpConfiguration> {
		public HttpConfigurationBuilder headers(HttpHeader ... headers) {
			return headers(asList(headers));
		}

		private HttpConfigurationBuilder headers(Collection<HttpHeader> headers) {
			return addOperation(config -> config.setHeaders(headers));
		}

		@Override
		protected HttpConfiguration createObject() {
			return new HttpConfiguration();
		}
	}
}