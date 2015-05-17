package com.pchudzik.docs.manage;

import com.pchudzik.docs.manage.dto.DocumentationDto;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.model.UrlRewriteRule;
import com.pchudzik.docs.repository.DocumentationRepository;
import com.pchudzik.docs.utils.http.MultipartFileFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 22.02.15.
 */
@Component
class TestDataProvider {
	@Autowired ManagementService managementService;
	@Autowired DocumentationRepository documentationRepository;
	@Autowired MultipartFileFactory multipartFileFactory;

	@PostConstruct
	void insertTestData() {
		final DocumentationDto testDocumentation = managementService.createNewDocumentation(DocumentationDto.builder()
				.name("Test")
				.build());

		saveDynamicFeed(testDocumentation.getId());
		saveFramesFeed(testDocumentation.getId());
	}

	private void saveFramesFeed(String documentationId) {
		final String fileName = "frames.zip";

		final VersionDto withFramesVersion = managementService.addVersion(
				documentationId,
				VersionDto.builder()
						.fileName(fileName)
						.fileSize(0)
						.rootDirectory("frames")
						.name("frameset page")
						.build());

		managementService.setVersionFile(
				documentationId,
				withFramesVersion.getId(),
				multipartFileFactory.fromFile(new File("feed/test", fileName)));
	}

	private void saveDynamicFeed(String documentationId) {
		final String fileName = "dynamic.zip";

		final VersionDto html5UrlsVersion = managementService.addVersion(
				documentationId,
				VersionDto.builder()
						.fileName(fileName)
						.fileSize(0)
						.rootDirectory("dynamic")
						.initialDirectory("api")
						.name("html5 mode urls")
						.build());
		managementService.setVersionFile(
				documentationId,
				html5UrlsVersion.getId(),
				multipartFileFactory.fromFile(new File("feed/test", fileName)));

		managementService.updateRewriteRules(
				documentationId, html5UrlsVersion.getId(),
				asList(UrlRewriteRule.builder()
						.regexp("/dynamic/api/*")
						.replacement("/dynamic")
						.build()));
	}
}
