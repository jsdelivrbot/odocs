package com.pchudzik.docs.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * Created by pawel on 04.01.15.
 */
@Slf4j
@EnableWebMvc
@Configuration
public class WebApplicationConfiguration extends AbstractAnnotationConfigDispatcherServletInitializer {
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class[] {
				ComponentScanConfiguration.class,
				WebApplicationConfiguration.class
		};
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[0];
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] {
				"/api/*"
		};
	}
}
