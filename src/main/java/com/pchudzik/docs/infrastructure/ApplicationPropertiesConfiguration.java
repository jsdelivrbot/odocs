package com.pchudzik.docs.infrastructure;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Properties;

@Configuration
public class ApplicationPropertiesConfiguration {
	@Value("${db.url:jdbc:h2:mem:test}") private String jdbcUrl;

	@SneakyThrows
	@Bean
	static PropertySourcesPlaceholderConfigurer properties() throws IOException {
		final Properties properties = new Properties();
		properties.putAll(newProperties(new ClassPathResource("/application.properties")));

		final PropertySourcesPlaceholderConfigurer result = new PropertySourcesPlaceholderConfigurer();
		result.setProperties(properties);
		return result;
	}

	@SneakyThrows
	private static Properties newProperties(ClassPathResource classPathResource) {
		final Properties properties = new Properties();
		properties.load(classPathResource.getInputStream());
		return properties;
	}

	@Bean DatabaseSettings databaseSettings() {
		return DatabaseSettings.builder()
				.jdbcUrl(jdbcUrl)
				.build();
	}

	@Builder
	@Getter
	public static class DatabaseSettings {
		private String jdbcUrl;
	}
}
