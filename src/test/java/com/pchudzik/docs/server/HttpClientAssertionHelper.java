package com.pchudzik.docs.server;

import com.pchudzik.docs.infrastructure.test.DescriptiveCondition;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.assertj.core.api.Condition;

import java.util.Objects;
import java.util.function.Function;

/**
 * Created by pawel on 20.02.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class HttpClientAssertionHelper {

	@SneakyThrows
	public static String content(HttpResponse response) {
		return IOUtils.toString(response.getEntity().getContent());
	}

	public static Condition<? super HttpResponse> status(int status) {
		return DescriptiveCondition.builderFor(HttpResponse.class)
				.predicate(response -> response.getStatusLine().getStatusCode() == status)
				.toString(response -> String.format("\n" +
						"  Expecting status to be:\n" +
						"<%s>\n" +
						"  but was:\n" +
						"<%s>\n", status, response.getStatusLine().getStatusCode()))
				.build();
	}

	public static Condition<? super HttpResponse> allowOriginHeaderWithValue(String allowOriginDomain) {
		final Function<HttpResponse, String> getAccessControlAllowOrganHeader = response -> response.getFirstHeader(ServerResponseHandler.ACCESS_CONTROL_ALLOW_ORIGIN_HEADER).getValue();
		return DescriptiveCondition.builderFor(HttpResponse.class)
				.predicate(response -> Objects.equals(
						getAccessControlAllowOrganHeader.apply(response),
						allowOriginDomain))
				.toString(response -> String.format("%n" +
						"  Expecting header Access-Control-Allow-Origin to be:%n" +
						"    <%s>%n" +
						"  but was:%n" +
						"    <%s>%n", allowOriginDomain, getAccessControlAllowOrganHeader.apply(response)))
				.build();
	}
}
