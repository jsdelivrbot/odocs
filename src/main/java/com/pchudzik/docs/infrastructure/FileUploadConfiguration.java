package com.pchudzik.docs.infrastructure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
* Created by pawel on 11.01.15.
*/
@Configuration
class FileUploadConfiguration {
	@Bean CommonsMultipartResolver multipartResolver() {
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
		multipartResolver.setMaxUploadSize(100 * 1024 * 1024);
		multipartResolver.setMaxInMemorySize(10 * 1024 * 1024);
		return multipartResolver;
	}
}
