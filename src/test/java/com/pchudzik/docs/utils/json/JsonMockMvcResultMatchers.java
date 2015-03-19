package com.pchudzik.docs.utils.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by pawel on 16.02.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonMockMvcResultMatchers {
	private static final boolean strict = false;

	public static JsonMockMvcResultMatchers jsonContent() {
		return new JsonMockMvcResultMatchers();
	}

	public ResultMatcher isEqual(String expectedJson) {
		return result -> assertJsonIsEqual(expectedJson, result, JSONCompareMode.NON_EXTENSIBLE);
	}

	public ResultMatcher isStrictlyEqual(String expectedJson) {
		return result -> assertJsonIsEqual(expectedJson, result, JSONCompareMode.STRICT);
	}

	private void assertJsonIsEqual(String expectedJson, MvcResult result, JSONCompareMode compareMode) throws UnsupportedEncodingException, JSONException {
		final String responseJson = result.getResponse().getContentAsString();
		try {
			JSONAssert.assertEquals(expectedJson, responseJson, compareMode);
		} catch (AssertionError ex) {
			try {
				final ObjectMapper objectMapper = new ObjectMapper();
				final Object expected = objectMapper.readValue(expectedJson, Object.class);
				final Object actual = objectMapper.readValue(responseJson, Object.class);
				throw new AssertionError(
						String.format(
								"Expected json:\n%s\nacutual json\n%s",
								objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(expected),
								objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(actual)),
						ex);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
