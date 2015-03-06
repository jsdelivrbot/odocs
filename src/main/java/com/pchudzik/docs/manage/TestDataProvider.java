package com.pchudzik.docs.manage;

import com.pchudzik.docs.manage.dto.DocumentationDto;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.model.UrlRewriteRule;
import lombok.SneakyThrows;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 22.02.15.
 */
@Component
class TestDataProvider {
	@Autowired ManagementService managementService;

	@PostConstruct
	@Transactional
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
				createFile(new File("/home/pawel/Desktop", fileName)));

		managementService.updateRewriteRules(
				angular.getId(), v1_3_13.getId(),
				asList(UrlRewriteRule.builder()
						.regexp("/docs/api.*")
						.replacement("/docs/")
						.build()));
	}

	@SneakyThrows
	private MultipartFile createFile(File file) {
		DiskFileItemFactory factory = new DiskFileItemFactory(10024, new File("build"));
		final FileItem item = factory.createItem("file", "application/zip", true, file.getName());
		IOUtils.copy(new FileInputStream(file), item.getOutputStream());
		return new CommonsMultipartFile(item);
	}
}
