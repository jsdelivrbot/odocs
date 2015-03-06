package com.pchudzik.docs.utils.http;

import lombok.SneakyThrows;
import org.skyscreamer.jsonassert.JSONParser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

/**
 * Created by pawel on 16.02.15.
 */
public class HttpRequestBuilders {
	public static MockHttpServletRequestBuilder httpGet(String url) {
		return get(url, Collections.emptyMap());
	}

	public static MockHttpServletRequestBuilder get(String urlTemplate, Map<String, Object> params) {
		return MockMvcRequestBuilders.get(expandUrl(urlTemplate, params));
	}

	public static MockHttpServletRequestBuilder httpFileUpload(String url, MockMultipartFile file) {
		return httpFileUpload(url, Collections.emptyMap(), file);
	}

	public static MockHttpServletRequestBuilder httpFileUpload(String urlTemplate, Map<String, Object> params, MockMultipartFile file) {
		return MockMvcRequestBuilders.fileUpload(expandUrl(urlTemplate, params))
				.file(file);
	}

	@SneakyThrows
	public static RequestBuilder httpPost(String url, Map<String, Object> options, String jsonContent) {
		return MockMvcRequestBuilders.post(expandUrl(url, options))
				.content(JSONParser.parseJSON(jsonContent).toString())
				.contentType(MediaType.APPLICATION_JSON);
	}

	@SneakyThrows
	public static RequestBuilder httpPost(String url, String jsonContent) {
		return httpPost(url, Collections.emptyMap(), jsonContent);
	}

	public static RequestBuilder delete(String url) {
		return httpDelete(url, Collections.emptyMap());
	}

	public static RequestBuilder httpDelete(String urlTemplate, Map<String, Object> params) {
		return MockMvcRequestBuilders.delete(expandUrl(urlTemplate, params));
	}

	private static URI expandUrl(String url, Map<String, Object> options) {
		return new UriTemplate(url).expand(options);
	}
}
