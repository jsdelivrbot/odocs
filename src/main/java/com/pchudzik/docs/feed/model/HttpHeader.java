package com.pchudzik.docs.feed.model;

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

	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}
}
