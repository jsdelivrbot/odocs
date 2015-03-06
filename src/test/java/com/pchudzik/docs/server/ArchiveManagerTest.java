package com.pchudzik.docs.server;

import com.google.common.collect.ImmutableMap;
import lombok.SneakyThrows;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;

import static com.pchudzik.docs.server.ZipTestFactory.createArchiveStream;
import static java.nio.file.Files.createTempDirectory;
import static org.apache.commons.io.FileUtils.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveManagerTest {
	File deploymentRoot;

	@BeforeMethod
	@SneakyThrows
	public void setup() {
		deploymentRoot = createTempDirectory("odocs_zip_test_").toFile();
		deleteDirectory(deploymentRoot);
	}

	@AfterMethod
	@SneakyThrows
	public void cleanUp() {
		deleteDirectory(deploymentRoot);
	}

	@Test
	@SneakyThrows
	public void should_extract_full_zip_content() {
		//when
		ArchiveManager.builder()
				.deploymentRoot(deploymentRoot)
				.archiveInputStream(createArchiveStream(ImmutableMap.of(
						"1.txt", "1",
						"2.txt", "2")))
				.build()
				.start();

		//then
		assertThat(readFileToString(new File(deploymentRoot, "1.txt")))
				.isEqualTo("1");
		assertThat(readFileToString(new File(deploymentRoot, "2.txt")))
				.isEqualTo("2");
	}

	@Test
	@SneakyThrows
	public void should_extract_zip_content_under_deployment_root() {
		//when
		ArchiveManager.builder()
				.deploymentRoot(deploymentRoot)
				.archiveInputStream(createArchiveStream(ImmutableMap.of("f.txt", "any content")))
				.build().start();

		//then
		assertThat(deploymentRoot).isDirectory();
		assertThat(new File(deploymentRoot, "f.txt"))
				.exists()
				.isFile();
	}

	@Test
	@SneakyThrows
	public void should_remove_old_archive_content() {
		writeStringToFile(
				new File(deploymentRoot, "file.txt"),
				"old content");

		//when
		ArchiveManager.builder()
				.deploymentRoot(deploymentRoot)
				.archiveInputStream(createArchiveStream(ImmutableMap.of("new.txt", "new")))
				.build()
				.start();

		//then
		assertThat(new File(deploymentRoot, "file.txt")).doesNotExist();
		assertThat(new File(deploymentRoot, "new.txt")).hasContent("new");
	}

	@Test
	@SneakyThrows
	public void should_clean_deployment_dir() {
		final ArchiveManager archiveManager = ArchiveManager.builder()
				.deploymentRoot(deploymentRoot)
				.archiveInputStream(createArchiveStream(ImmutableMap.of("file.txt", "content")))
				.build();
		archiveManager.start();

		//when
		archiveManager.stop();

		assertThat(deploymentRoot).doesNotExist();
	}
}