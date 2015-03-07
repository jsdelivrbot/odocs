package com.pchudzik.docs.server;

import com.google.common.collect.ImmutableMap;
import com.pchudzik.docs.infrastructure.test.HttpClientAssertionHelper;
import com.pchudzik.docs.model.DocumentationVersion;
import com.pchudzik.docs.model.UrlRewriteRule;
import com.pchudzik.docs.model.VersionFile;
import lombok.SneakyThrows;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.impl.client.HttpClientBuilder;
import org.mockito.Mock;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.util.Optional;

import static com.pchudzik.docs.infrastructure.test.HttpClientAssertionHelper.allowOriginHeaderWithValue;
import static com.pchudzik.docs.infrastructure.test.HttpClientAssertionHelper.status;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class JettyServerRegistryIntegratingTest {
	private static final String html = "<html><body>Hello</body></html>";
	private static final String ALLOW_ORIGIN_DOMAIN = "http://localhost";

	@Mock FreePortSelector portSelectorMock;
	JettyServerRegistry jettyServerRegistry;

	private int port;
	private HttpClient httpClient;

	@SneakyThrows
	@BeforeClass
	void setup() {
		initMocks(this);
		port = new FreePortSelector().getAvailablePort();
		when(portSelectorMock.getAvailablePort()).thenReturn(port);

		jettyServerRegistry = new JettyServerRegistry(portSelectorMock);
		jettyServerRegistry.allowOriginDomain = ALLOW_ORIGIN_DOMAIN;
		jettyServerRegistry.deploymentRootPath = "build/deployments";
		jettyServerRegistry.initialize();

		httpClient = HttpClientBuilder.create().build();
	}


	@DataProvider(name = "rootAndInitialDirectoryDataProvider")
	Object [][] rootAndInitialDirectoryDataprovider() {
		return new Object[][] {
				//  rootDirectory          initial directory
				{ Optional.of("archived"), Optional.of("docs") },
				{ Optional.of("archived"), Optional.empty() },
				{ Optional.empty(),        Optional.of("docs") },
				{ Optional.empty(),        Optional.empty() },

		};
	}

	@Test(dataProvider = "rootAndInitialDirectoryDataProvider")
	@SneakyThrows
	public void should_deploy_and_undeploy_version_archive_with_root_dir(Optional<String> maybeRootDirectory, Optional<String> maybeInitialDirectory) {
		final String url = "http://localhost:" + port + maybeInitialDirectory.map(dir -> "/" + dir).orElse("");
		final DocumentationVersion version = createVersion(
				maybeRootDirectory.orElse(null),
				maybeInitialDirectory.orElse(null));

		//when
		jettyServerRegistry.deploy(version);

		//then
		final HttpResponse httpResponse = httpClient.execute(new HttpGet(url));
		assertThat(httpResponse)
				.has(status(HttpStatus.SC_OK))
				.has(allowOriginHeaderWithValue(ALLOW_ORIGIN_DOMAIN));
		assertThat(HttpClientAssertionHelper.content(httpResponse))
				.contains("Hello")
				.contains("<script>document.domain = 'http://localhost';</script>");

		//when
		jettyServerRegistry.undeploy(version);

		//then
		try {
			httpClient.execute(new HttpGet(url));
			fail("Expected exception");
		} catch (Exception ex) {
			assertThat(ex)
					.isInstanceOf(HttpHostConnectException.class)
					.hasMessageStartingWith("Connect to localhost")
					.hasMessageEndingWith("Connection refused");
		}
	}

	@Test
	@SneakyThrows
	public void rewrite_rules_should_be_applied() {
		final DocumentationVersion version = createVersion("archived", "docs");
		try {
			version.updateRewriteRules(asList(UrlRewriteRule.builder()
					.regexp("/docs/app/.*")
					.replacement("/docs/")
					.build()));

			//when
			jettyServerRegistry.deploy(version);

			//then
			final HttpResponse httpResponse = httpClient.execute(new HttpGet("http://localhost:" + port + "/docs/app/any/path"));
			assertThat(httpResponse)
					.has(status(HttpStatus.SC_OK))
					.has(allowOriginHeaderWithValue(ALLOW_ORIGIN_DOMAIN));
			assertThat(HttpClientAssertionHelper.content(httpResponse))
					.contains("Hello");
		} finally {
			jettyServerRegistry.undeploy(version);
		}
	}

	private DocumentationVersion createVersion(String rootDirectory, String initialDirectory) {
		final String entryPath = asList(rootDirectory, initialDirectory, "index.html")
				.stream()
				.filter(element -> element != null)
				.collect(joining(File.separator));
		return DocumentationVersion.builder()
				.name("any version")
				.initialDirectory(initialDirectory)
				.rootDirectory(rootDirectory)
				.versionFile(VersionFile.builder()
						.fileName("archive.zip")
						.contentType("application/zip")
						.fileContent(ZipTestFactory.createArchive(ImmutableMap.of(entryPath, html)))
						.build())
				.build();
	}
}