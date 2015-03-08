package com.pchudzik.docs.model;

import org.springframework.test.util.ReflectionTestUtils;

public class DocumentationTestFactory {
	public static Documentation createDocumentation(Workspace workspace, String name, DocumentationVersion ... versions) {
		final Documentation documentation = Documentation.builder()
				.name(name)
				.versions(versions)
				.build();
		ReflectionTestUtils.setField(documentation, "id", name);
		workspace.addDocumentation(documentation);
		return documentation;
	}
}