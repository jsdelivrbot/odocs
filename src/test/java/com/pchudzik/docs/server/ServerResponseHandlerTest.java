package com.pchudzik.docs.server;

import com.pchudzik.docs.infrastructure.test.HttpClientAssertionHelper;
import com.pchudzik.docs.server.ServerResponseHandler.InputStreamProvider;
import com.pchudzik.docs.server.ServerResponseHandler.ServerResource;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Server;
import org.testng.annotations.*;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import static com.pchudzik.docs.infrastructure.test.HttpClientAssertionHelper.allowOriginHeaderWithValue;
import static com.pchudzik.docs.infrastructure.test.HttpClientAssertionHelper.status;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class ServerResponseHandlerTest {
	FreePortSelector portSelector = new FreePortSelector();

	final String allowOriginDomain = "http://example.com";

	InputStreamProvider inputStreamProvider;

	String serverAddress = "http://localhost";
	int jettyPort;
	Server server;
	ServerResponseHandler serverResponseHandler;

	HttpClient httpClient;

	@BeforeClass
	public void setUpServer() throws Exception {
		jettyPort = portSelector.getAvailablePort();
		serverAddress += ":" + jettyPort;

		server = new Server(jettyPort);
		inputStreamProvider = mock(InputStreamProvider.class);
		serverResponseHandler = new ServerResponseHandler(inputStreamProvider, allowOriginDomain);
		server.setHandler(serverResponseHandler);
		server.start();

		httpClient = HttpClientBuilder.create().build();
	}

	@AfterClass
	public void shutdownServer() throws Exception {
		server.stop();
	}

	@BeforeMethod
	public void resetInputStreamProvider() throws Exception {
		reset(inputStreamProvider);
	}

	@Test
	public void should_append_js_script_with_document_domain_to_html_files() throws IOException {
		final String requestedFileName = "file.html";
		final String htmlFileContent = "<html>" +
				"	<head>" +
				"		<title>Hello</title>" +
				"	</head>" +
				"	<body>Hello world</body>" +
				"</html>";
		when(inputStreamProvider.findResource("/" + requestedFileName))
				.thenReturn(ServerResource.builder()
						.inputStream(new ByteArrayInputStream(htmlFileContent.getBytes()))
						.name(requestedFileName)
						.size(htmlFileContent.length())
						.build());

		//when
		final HttpResponse result = httpClient.execute(get(requestedFileName));

		//then
		assertThat(result)
				.has(allowOriginHeaderWithValue(allowOriginDomain))
				.has(status(HttpStatus.OK_200));
		assertThat(HttpClientAssertionHelper.content(result))
				.contains("<script>document.domain = '" + allowOriginDomain + "';</script>");
	}

	@Test
	public void should_attach_access_control_header_for_all_served_files() throws IOException {
		final byte [] requestedFile = new byte[] {1,2,3};
		final String requestedFileName = "image.png";
		when(inputStreamProvider.findResource("/" + requestedFile))
				.thenReturn(ServerResource.builder()
						.inputStream(new ByteArrayInputStream(requestedFile))
						.size(requestedFile.length)
						.name(requestedFileName)
						.build());

		//when
		HttpResponse result = httpClient.execute(get(requestedFileName));

		//then
		assertThat(result)
				.has(allowOriginHeaderWithValue(allowOriginDomain))
				.has(status(HttpStatus.OK_200));
	}

	@Test
	public void should_not_fail_on_missing_resource() throws IOException {
		final String requestedFile = "missingFile";
		doThrow(new FileNotFoundException())
				.when(inputStreamProvider)
				.findResource("/" + requestedFile);

		//when
		HttpResponse result = httpClient.execute(get(requestedFile));

		//then
		assertThat(result)
				.has(allowOriginHeaderWithValue(allowOriginDomain))
				.has(status(HttpStatus.NOT_FOUND_404));
	}

	@DataProvider(name = "baseUriShouldBeResolved")
	Object [][] baseUriShouldBeResolvedDataProvider() {
		return new Object[][] {
				{ "", "/" },
				{ "/", "/" },
				{ "/file.html", "/" },
				{ "/dir/file.img", "/dir" },
				{ "/dir1/dir2/file.img", "/dir1/dir2" }
		};
	}

	@Test(dataProvider = "baseUriShouldBeResolved")
	public void base_uri_should_be_resolved(String requestedResource, String expectedBaseUri) {
		assertThat(serverResponseHandler.resolveBaseUri(requestedResource))
				.isEqualTo(expectedBaseUri);
	}

	private HttpGet get(String requestedFile) {
		return new HttpGet("http://localhost:" + jettyPort + "/" + requestedFile);
	}
}