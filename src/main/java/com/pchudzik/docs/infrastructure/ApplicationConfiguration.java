package com.pchudzik.docs.infrastructure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.DispatcherServlet;

import static org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration.DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME;

/**
 * Created by pawel on 04.01.15.
 */
@Slf4j
@SpringBootApplication
@ComponentScan("com.pchudzik.docs")
class ApplicationConfiguration {
	@Bean
	public TomcatEmbeddedServletContainerFactory tomcatEmbeddedServletContainerFactory() {
		return new TomcatEmbeddedServletContainerFactory();
	}

	@Bean
	ServletRegistrationBean servletRegistrationBean(DispatcherServlet dispatcherServlet) {
		final ServletRegistrationBean bean = new ServletRegistrationBean(dispatcherServlet, "/api/*");
		bean.setName(DEFAULT_DISPATCHER_SERVLET_REGISTRATION_BEAN_NAME);
		return bean;
	}

	public static void main(String[] args) {
		SpringApplication.run(ApplicationConfiguration.class, args);
	}
}
