package com.pchudzik.docs.infrastructure;

/**
 * Created by pawel on 06.01.15.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zapodot.jackson.java8.JavaOptionalModule;

@Configuration
class JsonObjectMapperConfiguration {
	@Bean @Primary
	ObjectMapper objectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaOptionalModule());
		om.registerModule(new JodaModule());
		om.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return om;
	}
}
