package com.pchudzik.docs.model;

import java.io.ByteArrayInputStream;

public class DocumentationVersionTestFactory {
	public static DocumentationVersion createVersion(String name) {
		return DocumentationVersion.builder()
				.name(name)
				.build();
	}

	public static DocumentationVersion createVersionWithFile(String name) {
		final DocumentationVersion version = createVersion(name);
		version.updateFile(name + ".zip", "application/zip", new ByteArrayInputStream(name.getBytes()));
		return version;
	}
}