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

	@PostConstruct
	void insertTestData() {
		saveAngular();
	}

	private void saveAngular() {
		final String fileName = "angular-1.3.13.zip";
		final DocumentationDto angular = managementService.createNewDocumentation(DocumentationDto.builder()
				.name("Angular")
				.build());
		final VersionDto v1_3_13 = managementService.addVersion(
				angular.getId(),
				VersionDto.builder()
						.fileName(fileName)
						.fileSize(0)
						.rootDirectory("angular-1.3.13")
						.initialDirectory("docs")
						.name("1.3.13")
						.build());
		managementService.setVersionFile(
				angular.getId(),
				v1_3_13.getId(),
				MultipartFileFactory.fromFile(new File("/home/pawel/Desktop", fileName)));

		managementService.updateRewriteRules(
				angular.getId(), v1_3_13.getId(),
				asList(UrlRewriteRule.builder()
						.regexp("/docs/api.*")
						.replacement("/docs/")
						.build()));
	}
}
