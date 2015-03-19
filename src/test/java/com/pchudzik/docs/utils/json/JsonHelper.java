package com.pchudzik.docs.utils.json;

import lombok.SneakyThrows;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.skyscreamer.jsonassert.JSONParser;

import java.util.Map;

/**
 * Created by pawel on 18.03.15.
 */
public class JsonHelper {
	@SneakyThrows
	public static String fixJson(String json) {
		return JSONParser.parseJSON(json.replaceAll("'", "\"")).toString();
	}

	public static String jsonFromTemplate(String template, Map<String, Object> params) {
		return fixJson(new StrSubstitutor(params)
				.replace(template));
	}
}
