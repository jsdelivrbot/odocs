package com.pchudzik.docs.manage;

import com.pchudzik.docs.manage.dto.DocumentationDto;
import com.pchudzik.docs.manage.dto.VersionDto;
import com.pchudzik.docs.model.Documentation;
import com.pchudzik.docs.model.DocumentationVersion;
import com.pchudzik.docs.model.UrlRewriteRule;
import com.pchudzik.docs.model.Workspace;
import com.pchudzik.docs.repository.DocumentationRepository;
import com.pchudzik.docs.repository.UrlRewriteRuleRepository;
import com.pchudzik.docs.repository.VersionRepository;
import com.pchudzik.docs.server.JettyServerRegistry;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * Created by pawel on 08.02.15.
 */
@Service
class ManagementService {
	final DocumentationRepository documentationRepository;
	final VersionRepository versionRepository;
	final UrlRewriteRuleRepository urlRewriteRuleRepository;
	final WorkspaceService workspaceService;
	final JettyServerRegistry serverRegistry;

	@Autowired
	ManagementService(
			DocumentationRepository documentationRepository,
			VersionRepository versionRepository,
			UrlRewriteRuleRepository urlRewriteRuleRepository,
			WorkspaceService workspaceService, JettyServerRegistry serverRegistry) {
		this.documentationRepository = documentationRepository;
		this.versionRepository = versionRepository;
		this.urlRewriteRuleRepository = urlRewriteRuleRepository;
		this.workspaceService = workspaceService;
		this.serverRegistry = serverRegistry;
	}

	@Transactional
	public DocumentationDto createNewDocumentation(DocumentationDto documentationDto) {
		final Documentation documentation = Documentation.builder()
				.name(documentationDto.getName())
				.build();
		workspaceService.getDefaultWorkspace().addDocumentation(documentation);
		return new DocumentationDto(documentation);
	}

	@Transactional
	public VersionDto addVersion(String documentationId, VersionDto versionDto) {
		final Documentation documentation = documentationRepository.findOne(documentationId);
		final DocumentationVersion version = versionRepository.persist(DocumentationVersion.builder()
				.name(versionDto.getName())
				.initialDirectory(versionDto.getInitialDirectory())
				.rootDirectory(versionDto.getRootDirectory())
				.build());
		documentation.addVersion(version);
		return new VersionDto(version);
	}

	@Transactional(readOnly = true)
	public List<DocumentationDto> findAll() {
		return documentationRepository.findAll()
				.stream()
				.map(DocumentationDto::new)
				.collect(toList());
	}

	@SneakyThrows
	@Transactional
	public void setVersionFile(String documentationId, String versionId, MultipartFile file) {
		final DocumentationVersion version = versionRepository.findOne(documentationId, versionId);
		version.updateFile(file.getOriginalFilename(), file.getContentType(), file.getInputStream());

		serverRegistry.deploy(version);
	}

	@Transactional
	public void updateVersion(String documentationId, String versionId, VersionDto versionDto) {
		final DocumentationVersion version = versionRepository.findOne(documentationId, versionId);
		version.updateName(versionDto.getName());
		version.updateFileSettings(versionDto);

		serverRegistry.deploy(version);
	}

	@Transactional
	public void moveVersionUp(String documentationId, String versionId) {
		final Documentation documentation = documentationRepository.findOne(documentationId);
		final DocumentationVersion version = versionRepository.findOne(documentationId, versionId);
		documentation.moveVersionUp(version);
	}

	@Transactional
	public void moveVersionDown(String documentationId, String versionId) {
		final Documentation documentation = documentationRepository.findOne(documentationId);
		final DocumentationVersion version = versionRepository.findOne(documentationId, versionId);
		documentation.moveVersionDown(version);
	}

	@Transactional
	public void moveDocumentationUp(String documentationId) {
		final Workspace defaultWorkspace = workspaceService.getDefaultWorkspace();
		final Documentation documentation = documentationRepository.findOne(documentationId);
		defaultWorkspace.moveDocumentationUp(documentation);
	}

	@Transactional
	public void moveDocumentationDown(String documentationId) {
		final Workspace defaultWorkspace = workspaceService.getDefaultWorkspace();
		final Documentation documentation = documentationRepository.findOne(documentationId);
		defaultWorkspace.moveDocumentationDown(documentation);
	}

	@Transactional
	public void updateDocumentation(String id, DocumentationDto documentationDto) {
		final Documentation documentation = documentationRepository.findOne(id);
		documentation.updateName(documentationDto.getName());
	}

	@Transactional
	public void removeDocumentation(String id) {
		final Documentation documentation = documentationRepository.findOne(id);
		documentation.getVersions().forEach(serverRegistry::undeploy);
		documentationRepository.delete(documentation);
	}

	@Transactional
	public void removeVersion(String documentationId, String versionId) {
		final Documentation documentation = documentationRepository.findOne(documentationId);
		final DocumentationVersion version = versionRepository.findOne(documentationId, versionId);
		serverRegistry.undeploy(version);
		documentation.removeVersion(version);
	}

	@Transactional
	public void updateRewriteRules(String documentationId, String versionId, List<UrlRewriteRule> newRules) {
		final DocumentationVersion version = versionRepository.findOne(documentationId, versionId);
		version.updateRewriteRules(newRules.stream()
				.map(UrlRewriteRule::new)
				.collect(Collectors.toList()));
		urlRewriteRuleRepository.persist(version.getRewriteRules());
		serverRegistry.deploy(version);
	}

	@Transactional(readOnly = true)
	public List<UrlRewriteRule> listRules(String documentationId, String versionId) {
		final List<UrlRewriteRule> rewriteRules = versionRepository.findOne(documentationId, versionId).getRewriteRules();
		rewriteRules.size();
		return rewriteRules;
	}
}
