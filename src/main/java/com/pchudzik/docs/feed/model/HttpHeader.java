package com.pchudzik.docs.feed.model;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HttpHeader {
	private String name;
	private String value;

	public static HttpHeaderBuilder builder() {
		return new HttpHeaderBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class  HttpHeaderBuilder extends ObjectBuilder<HttpHeaderBuilder, HttpHeader> {
		public HttpHeaderBuilder name(String name) {
			return addOperation(header -> header.name = name);
		}

		public HttpHeaderBuilder value(String value) {
			return addOperation(header -> header.value = value);
		}

		@Override
		protected HttpHeader createObject() {
			return new HttpHeader();
		}
	}
}
