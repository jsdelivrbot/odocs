package com.pchudzik.docs.manage.dto;

import com.pchudzik.docs.model.DocumentationVersion;
import com.pchudzik.docs.model.VersionFile;
import lombok.*;
import lombok.experimental.Builder;

import java.util.Optional;

/**
 * Created by pawel on 08.02.15.
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode @ToString
public class VersionDto {
	String id;
	String name;

	String fileName;
	int fileSize;
	String initialDirectory;
	String rootDirectory;

	public VersionDto(DocumentationVersion version) {
		this.id = version.getId();
		this.name = version.getName();
		this.initialDirectory = version.getInitialDirectory().orElse(null);
		this.rootDirectory = version.getRootDirectory().orElse(null);
		this.fileName = Optional.ofNullable(version.getVersionFile())
				.map(VersionFile::getFileName)
				.orElse(null);
		this.fileSize = Optional.ofNullable(version.getVersionFile())
				.map(VersionFile::getFileContentLength)
				.orElse(0);
	}
}
