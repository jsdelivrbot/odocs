package com.pchudzik.docs.model;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Lob;

/**
 * Created by pawel on 14.02.15.
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Embeddable
public class VersionFile {
	private String fileName;
	private String contentType;
	@Lob private byte [] fileContent;

	public static VersionFileBuilder builder() {
		return new VersionFileBuilder();
	}

	public int getFileContentLength() {
		return fileContent.length;
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class VersionFileBuilder extends ObjectBuilder<VersionFileBuilder, VersionFile> {
		@Override
		protected VersionFile createObject() {
			return new VersionFile();
		}

		public VersionFileBuilder fileName(String fileName) {
			return addOperation(version -> version.fileName = fileName);
		}

		public VersionFileBuilder contentType(String contentType) {
			return addOperation(version -> version.contentType = contentType);
		}

		public VersionFileBuilder fileContent(byte [] content) {
			return addOperation(version -> version.fileContent = content);
		}
	}
}
