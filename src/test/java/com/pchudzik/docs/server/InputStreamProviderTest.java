package com.pchudzik.docs.server;

import com.google.common.io.Files;
import com.pchudzik.docs.server.ServerResponseHandler.InputStreamProvider;
import com.pchudzik.docs.server.ServerResponseHandler.ServerResource;
import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.springframework.util.MimeTypeUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;

import static com.pchudzik.docs.server.ServerResourceAssertHelper.*;
import static org.apache.commons.io.FileUtils.writeStringToFile;
import static org.assertj.core.api.Assertions.assertThat;

public class InputStreamProviderTest {
	File rootDirectory;
	InputStreamProvider inputStreamProvider;

	@BeforeClass
	@SneakyThrows
	public void setupDirectoryStructure() {
		rootDirectory = Files.createTempDir();
		inputStreamProvider = new InputStreamProvider(rootDirectory);
	}

	@AfterClass
	@SneakyThrows
	public void deleteTemporaryDirectories() {
		FileUtils.deleteDirectory(rootDirectory);
	}

	@Test
	@SneakyThrows
	public void should_detect_mime_type() {
		writeStringToFile(new File(rootDirectory, "file.html"), "aaa");

		final ServerResource result = inputStreamProvider.findResource("/file.html");

		assertThat(result)
				.has(contentType(MimeTypeUtils.TEXT_HTML_VALUE));
	}

	@Test
	@SneakyThrows
	public void should_return_requested_resource() {
		writeStringToFile(new File(rootDirectory, "file.html"), "file");

		final ServerResource result = inputStreamProvider.findResource("/file.html");

		assertThat(result)
				.has(content("file"))
				.has(name("file.html"));
	}

	@DataProvider(name = "startFiles") Object[][] startFilesDataProvider() {
		return new Object[][] {
				{"index.html"},
				{"index.htm"}
		};
	}

	@Test(dataProvider = "startFiles")
	@SneakyThrows
	public void should_select_startup_file_if_requested_directory(String startFileName) {
		writeStringToFile(new File(rootDirectory, startFileName), "hello");

		final ServerResource result = inputStreamProvider.findResource("/");

		assertThat(result)
				.has(content("hello"))
				.has(name(startFileName));
	}

	@Test @SneakyThrows
	public void start_file_name_should_be_resolved_relatively_to_requested_dir() {
		final String insideDirectory = "test-directory";
		final File directoryInsideRoot = new File(rootDirectory, insideDirectory);
		directoryInsideRoot.mkdirs();
		writeStringToFile(new File(directoryInsideRoot, "index.html"), "hello");

		final ServerResource resource = inputStreamProvider.findResource("/" + insideDirectory);

		assertThat(resource)
				.has(content("hello"))
				.has(name("index.html"));
	}
}