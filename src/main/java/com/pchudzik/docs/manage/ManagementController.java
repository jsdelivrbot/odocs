package com.pchudzik.docs.manage;

import com.pchudzik.docs.manage.dto.DocumentationDto;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.model.UrlRewriteRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by pawel on 08.02.15.
 */
@RestController
@RequestMapping("/manage/documentations")
class ManagementController {
	final ManagementService managementService;

	@Autowired
	ManagementController(ManagementService managementService) {
		this.managementService = managementService;
	}

	@RequestMapping(method = RequestMethod.GET)
	List<DocumentationDto> findAll() {
		return managementService.findAll();
	}

	@RequestMapping(method = RequestMethod.POST)
	DocumentationDto create(@RequestBody DocumentationDto documentation) {
		return managementService.createNewDocumentation(documentation);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	void deleteDocumentation(@PathVariable String id) {
		managementService.removeDocumentation(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.POST)
	void updateDocumentation(@PathVariable String id, @RequestBody DocumentationDto documentation) {
		managementService.updateDocumentation(id, documentation);
	}

	@RequestMapping(value = "/{id}/move-up", method = RequestMethod.PUT)
	void moveDocumentationUp(@PathVariable String id) {
		managementService.moveDocumentationUp(id);
	}

	@RequestMapping(value = "/{id}/move-down", method = RequestMethod.PUT)
	void moveDocumentationDown(@PathVariable String id) {
		managementService.moveDocumentationDown(id);
	}

	@RequestMapping(value = "/{id}/versions", method = RequestMethod.POST)
	VersionDto createVersion(@PathVariable String id, @RequestBody VersionDto versionDto) {
		return managementService.addVersion(id, versionDto);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}", method = RequestMethod.DELETE)
	void deleteVersion(@PathVariable String documentationId, @PathVariable String versionId) {
		managementService.removeVersion(documentationId, versionId);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}", method = RequestMethod.POST)
	void updateVersion(@PathVariable String documentationId, @PathVariable String versionId, @RequestBody VersionDto versionDto) {
		managementService.updateVersion(documentationId, versionId, versionDto);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}/move-up", method = RequestMethod.PUT)
	void moveVersionUp(@PathVariable String documentationId, @PathVariable String versionId) {
		managementService.moveVersionUp(documentationId, versionId);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}/move-down", method = RequestMethod.PUT)
	void moveVersionDown(@PathVariable String documentationId, @PathVariable String versionId) {
		managementService.moveVersionDown(documentationId, versionId);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}/file", method = RequestMethod.POST)
	void saveFile(@PathVariable String documentationId, @PathVariable String versionId, MultipartFile file) {
		managementService.setVersionFile(documentationId, versionId, file);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}/rules", method = RequestMethod.GET)
	List<UrlRewriteRule> listRules(@PathVariable String documentationId, @PathVariable String versionId) {
		return managementService.listRules(documentationId, versionId);
	}

	@RequestMapping(value = "/{documentationId}/versions/{versionId}/rules", method = RequestMethod.POST)
	void updateRewriteRules(@PathVariable String documentationId, @PathVariable String versionId, @RequestBody List<UrlRewriteRule> rewriteRules) {
		managementService.updateRewriteRules(documentationId, versionId, rewriteRules);
	}
}
