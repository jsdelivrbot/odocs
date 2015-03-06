package com.pchudzik.docs.model;

import com.google.common.collect.Lists;
import com.pchudzik.docs.manage.dto.VersionDto;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderColumn;
import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by pawel on 08.02.15.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DocumentationVersion extends NameAware {
	Integer index;
	String initialDirectory;
	String rootDirectory;

	@Getter
	@Embedded
	VersionFile versionFile;

	@OneToMany(orphanRemoval = true)
	@OrderColumn
	List<UrlRewriteRule> rewriteRules = Lists.newLinkedList();

	public static DocumentationVersionBuilder builder() {
		return new DocumentationVersionBuilder();
	}

	@SneakyThrows
	public void updateFile(String originalFilename, String contentType, InputStream inputStream) {
		versionFile = VersionFile.builder()
				.fileName(originalFilename)
				.contentType(contentType)
				.fileContent(IOUtils.toByteArray(inputStream))
				.build();
	}

	public Optional<String> getInitialDirectory() {
		return Optional.ofNullable(initialDirectory);
	}

	public Optional<String> getRootDirectory() {
		return Optional.ofNullable(rootDirectory);
	}

	public void updateFileSettings(VersionDto versionDto) {
		setInitialDirectory(versionDto.getInitialDirectory());
		setRootDirectory(versionDto.getRootDirectory());
	}

	public void updateRewriteRules(Collection<UrlRewriteRule> newRules) {
		rewriteRules.clear();
		rewriteRules.addAll(newRules);
	}

	public List<UrlRewriteRule> getRewriteRules() {
		return Collections.unmodifiableList(rewriteRules);
	}

	private void setInitialDirectory(String initialDirectory) {
		this.initialDirectory = initialDirectory;
	}

	private void setRootDirectory(String rootDirectory) {
		this.rootDirectory = rootDirectory;
	}

	private static String addSeparatorToPathIfMissing(String path) {
		if(StringUtils.isBlank(path)) {
			return null;
		} else {
			return path.trim().endsWith(File.separator)
					? path
					: path.trim() + File.separator;
		}
	}

	public static class DocumentationVersionBuilder extends NameAwareBuilder<DocumentationVersionBuilder, DocumentationVersion> {
		@Override
		protected DocumentationVersion createObject() {
			return new DocumentationVersion();
		}

		public DocumentationVersionBuilder initialDirectory(String path) {
			return addOperation(version -> version.setInitialDirectory(path));
		}

		public DocumentationVersionBuilder rootDirectory(String path) {
			return addOperation(version -> version.setRootDirectory(path));
		}

		public DocumentationVersionBuilder versionFile(VersionFile file) {
			return addOperation(version -> version.versionFile = file);
		}
	}
}
