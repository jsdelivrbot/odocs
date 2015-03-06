package com.pchudzik.docs.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.zapodot.jackson.java8.JavaOptionalModule;

import javax.annotation.PostConstruct;

/**
 * Created by pawel on 06.01.15.
 */
@Configuration
class JsonObjectMapperConfiguration {
	@Autowired RequestMappingHandlerAdapter requestMappingHandlerAdapter;

	@Bean ObjectMapper objectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaOptionalModule());
		return om;
	}

	@PostConstruct
	void setup() {
		final MappingJackson2HttpMessageConverter converter = (MappingJackson2HttpMessageConverter) requestMappingHandlerAdapter.getMessageConverters()
				.stream()
				.filter(c -> c instanceof MappingJackson2HttpMessageConverter)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Can not initialize jacksone mapping converter properly"));
		converter.setObjectMapper(objectMapper());
	}
}
