package com.pchudzik.docs.utils.http;

import com.pchudzik.docs.infrastructure.JsonObjectMapperConfiguration;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder;

/**
 * Created by pawel on 16.02.15.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ControllerTester {
	private final StandaloneMockMvcBuilder mockMvcBuilder;

	@Delegate
	private MockMvc mockMvc;

	public static ControllerTesterBuilder builder() {
		return new ControllerTesterBuilder();
	}

	private void initialize() {
		mockMvc = mockMvcBuilder.build();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ControllerTesterBuilder extends ObjectBuilder<ControllerTesterBuilder, ControllerTester> {
		Object [] controllersUnderTest;

		public ControllerTesterBuilder controllers(Object ... controllers) {
			controllersUnderTest = controllers;
			return this;
		}

		@Override
		protected ControllerTester createObject() {
			return new ControllerTester(MockMvcBuilders.standaloneSetup(controllersUnderTest));
		}

		@Override
		public ControllerTester build() {
			final ControllerTester tester = super.build();
			tester.mockMvcBuilder.setMessageConverters(new HttpMessageConverter[]{
					new StringHttpMessageConverter(),
					new ByteArrayHttpMessageConverter(),
					new ResourceHttpMessageConverter(),
					new SourceHttpMessageConverter<>(),
					new AllEncompassingFormHttpMessageConverter(),
					new MappingJackson2HttpMessageConverter(new JsonObjectMapperConfiguration().objectMapper())
			});
			tester.initialize();
			return tester;
		}
	}
}
