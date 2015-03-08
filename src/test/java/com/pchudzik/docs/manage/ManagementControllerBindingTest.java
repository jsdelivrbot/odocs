package com.pchudzik.docs.manage;

import com.google.common.collect.ImmutableMap;
import com.pchudzik.docs.manage.dto.DocumentationDto;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.model.UrlRewriteRule;
import com.pchudzik.docs.utils.http.ControllerTester;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.mock.web.MockMultipartFile;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Iterables.getOnlyElement;
import static com.pchudzik.docs.model.UrlRewriteRuleAsserthelper.regexp;
import static com.pchudzik.docs.model.UrlRewriteRuleAsserthelper.replacement;
import static com.pchudzik.docs.utils.http.HttpRequestBuilders.*;
import static com.pchudzik.docs.utils.http.JsonMockMvcResultMatchers.fixJson;
import static com.pchudzik.docs.utils.http.JsonMockMvcResultMatchers.jsonContent;
import static com.pchudzik.docs.utils.http.MockMvcResultMarchers.emptyResponse;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ManagementControllerBindingTest {
	private final String versionId = "version";
	private final String docId = "doc";
	@Mock ManagementService managementService;
	ControllerTester controllerTester;

	@BeforeMethod void setup() {
		initMocks(this);

		controllerTester = ControllerTester.builder()
				.controllers(new ManagementController(managementService))
				.build();
	}

	@Test
	@SneakyThrows
	public void should_list_documentations() {
		when(managementService.findAll())
				.thenReturn(asList(
						DocumentationDto.builder()
								.id("1")
								.name("doc1")
								.versions(asList(createVersionDto(versionId, "dir", 10, "file.zip")))
								.build()));

		//when
		controllerTester.perform(httpGet("/manage/documentations"))

				//then
				.andExpect(status().isOk())
				.andExpect(jsonContent().isEqual(fixJson("[{" +
						"id: '1'," +
						"name: 'doc1'," +
						"versions: [" + createVersionDtoJson(versionId, "dir", 10, "file.zip") + "]" +
						"}]")));
	}

	@Test
	@SneakyThrows
	public void should_create_new_documentation() {
		when(managementService.createNewDocumentation(DocumentationDto.builder().name(docId).build()))
				.thenReturn(DocumentationDto.builder()
						.id("1")
						.name(docId)
						.versions(Collections.emptyList())
						.build());

		//when
		controllerTester.perform(httpPost("/manage/documentations", fixJson("{name: 'doc'}")))

				//then
				.andExpect(status().isOk())
				.andExpect(jsonContent().isEqual(fixJson("{" +
						"  id: '1'," +
						"  name: 'doc'," +
						"  versions: []" +
						"}")));
	}

	@Test
	@SneakyThrows
	public void should_delete_documentation_by_id() {
		final String id = "id to remove";

		//when
		controllerTester.perform(httpDelete("/manage/documentations/{id}", ImmutableMap.of("id", id)))

				//then
				.andExpect(status().isOk())
				.andExpect(emptyResponse());

		verify(managementService).removeDocumentation(id);
	}

	@Test
	@SneakyThrows
	public void should_update_documentation() {
		final String id = "documentation to update";

		//when
		controllerTester.perform(httpPost(
						"/manage/documentations/{id}",
						ImmutableMap.of("id", id),
						fixJson("{name: 'new name'}")))

				//then
				.andExpect(status().isOk());

		verify(managementService).updateDocumentation(id, DocumentationDto.builder()
				.name("new name")
				.build());
	}

	@Test
	@SneakyThrows
	public void should_create_new_version_in_documentation() {
		when(managementService.addVersion("id", VersionDto.builder()
						.name(versionId)
						.rootDirectory("dir")
						.initialDirectory("dir")
						.build()))
				.thenReturn(createVersionDto(versionId, "dir"));

		//when
		controllerTester.perform(httpPost(
						"/manage/documentations/{id}/versions",
						ImmutableMap.of("id", "id"),
						fixJson("{" +
								"  name: 'version'," +
								"  initialDirectory: 'dir'," +
								"  rootDirectory: 'dir'" +
								"}}")))

				//then
				.andExpect(status().isOk())
				.andExpect(jsonContent().isEqual(createVersionDtoJson(versionId, "dir")));
	}

	@Test
	@SneakyThrows
	public void should_delete_version() {
		//when
		controllerTester.perform(httpDelete(
						"/manage/documentations/{docId}/versions/{versionId}",
				docIdVersionIdParams()))

				//then
				.andExpect(status().isOk())
				.andExpect(emptyResponse());

		verify(managementService).removeVersion(docId, versionId);
	}

	@Test
	@SneakyThrows
	public void should_update_version() {
		//when
		controllerTester.perform(httpPost(
				"/manage/documentations/{docId}/versions/{versionId}",
				docIdVersionIdParams(),
				fixJson("{" +
						"  name: 'old'," +
						"  rootDirectory: 'old'," +
						"  initialDirectory: 'old'" +
						"}")))

				//then
				.andExpect(status().isOk());

		verify(managementService).updateVersion(docId, versionId, VersionDto.builder()
				.name("old")
				.rootDirectory("old")
				.initialDirectory("old")
				.build());
	}

	@Test
	@SneakyThrows
	public void should_upload_file() {
		final MockMultipartFile file = new MockMultipartFile("file", "content".getBytes());

		//when
		controllerTester.perform(httpFileUpload(
				"/manage/documentations/{docId}/versions/{versionId}/file",
				docIdVersionIdParams(),
				file))

				//then
				.andExpect(status().isOk());

		verify(managementService).setVersionFile(docId, versionId, file);
	}

	@Test
	@SneakyThrows
	public void should_save_url_rewrite_rules() {
		final ArgumentCaptor<List> argumentCaptor = ArgumentCaptor.forClass(List.class);

		//when
		controllerTester.perform(httpPost(
				"/manage/documentations/{docId}/versions/{versionId}/rules",
				docIdVersionIdParams(),
				fixJson("[{regexp: 'a', replacement: 'b'}]")))

				//then
				.andExpect(status().isOk());

		verify(managementService).updateRewriteRules(eq(docId), eq(versionId), argumentCaptor.capture());
		List<UrlRewriteRule> rules = argumentCaptor.getValue();
		assertThat(getOnlyElement(rules))
				.has(regexp("a"))
				.has(replacement("b"));
	}

	private ImmutableMap<String, Object> docIdVersionIdParams() {
		return ImmutableMap.of(
				"docId", docId,
				"versionId", versionId);
	}

	private String createVersionDtoJson(String versionAndName, String directories) {
		return createVersionDtoJson(versionAndName, directories, 0, null);
	}

	private String createVersionDtoJson(String versionAndName, String directories, int fileSize, String filename) {
		return fixJson("{" +
				"  id: '" + versionAndName +"'," +
				"  name: '" + versionAndName + "'," +
				"  rootDirectory: '" + directories + "'," +
				"  initialDirectory: '" + directories + "'," +
				"  fileSize: " + fileSize + "," +
				"  fileName: " + (StringUtils.isBlank(filename) ? "null" : ("'" + filename + "'")) +
				"}");
	}

	private VersionDto createVersionDto(String versionAndName, String directories, int fileSize, String fileName) {
		return VersionDto.builder()
				.id(versionAndName)
				.name(versionAndName)
				.rootDirectory(directories)
				.initialDirectory(directories)
				.fileSize(fileSize)
				.fileName(fileName)
				.build();
	}

	private VersionDto createVersionDto(String versionAndName, String directories) {
		return createVersionDto(versionAndName, directories, 0, null);
	}
}