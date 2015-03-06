package com.pchudzik.docs.server;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import com.pchudzik.docs.server.ServerResponseHandler.ServerResource;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Condition;

import java.util.Objects;

import static com.pchudzik.docs.utils.functional.ExceptionWrapper.wrapPredicate;
import static com.pchudzik.docs.utils.functional.ExceptionWrapper.wrapSupplier;

public class ServerResourceAssertHelper {
	public static Condition<? super ServerResource> content(String content) {
		return DescriptiveCondition.builderFor(ServerResource.class)
				.predicate(wrapPredicate(result -> Objects.equals(IOUtils.toString(result.getInputStream()), content)))
				.toString(resource -> String.format("%n" +
						"  content equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", content, wrapSupplier(() -> IOUtils.toString(resource.getInputStream()))))
				.build();
	}

	public static Condition<? super ServerResource> contentType(String contentType) {
		return DescriptiveCondition.builderFor(ServerResource.class)
				.predicate(resource -> StringUtils.equals(resource.getContentType(), contentType))
				.toString(resource -> String.format("%n" +
						"  mime type:%n" +
						"    <%s>%n" +
						"  but was:%n" +
						"    <%s>", contentType, resource.getContentType()))
				.build();
	}

	public static Condition<? super ServerResource> name(String fileName) {
		return DescriptiveCondition.builderFor(ServerResource.class)
				.predicate(resource -> Objects.equals(resource.getName(), fileName))
				.toString(resource -> String.format("%n" +
						"  fileName equal to:%n" +
						"    <%s>%n" +
						"  but was%n" +
						"    <%s>%n", fileName, resource.getName()))
				.build();
	}
}