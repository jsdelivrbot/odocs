package com.pchudzik.docs.utils.http;

import org.springframework.test.web.servlet.ResultMatcher;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Created by pawel on 17.02.15.
 */
public class MockMvcResultMarchers {
	public static ResultMatcher emptyResponse() {
		return result -> assertThat(result.getResponse().getContentAsString()).isEmpty();
	}
}
