package com.pchudzik.docs.model;

public class DocumentationTestFactory {
	public static Documentation createDocumentation(String name, DocumentationVersion ... versions) {
		return Documentation.builder()
				.name(name)
				.versions(versions)
				.build();
	}
}