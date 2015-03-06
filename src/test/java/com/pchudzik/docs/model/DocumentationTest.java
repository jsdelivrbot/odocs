package com.pchudzik.docs.model;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DocumentationTest {
	private DocumentationVersion version3;
	private DocumentationVersion version2;
	private DocumentationVersion version1;
	private Documentation documentation;

	@BeforeMethod void setup() {
		version1 = DocumentationVersion.builder().name("1").build();
		version2 = DocumentationVersion.builder().name("2").build();
		version3 = DocumentationVersion.builder().name("3").build();

		documentation = Documentation.builder()
				.name("doc")
				.versions(version1, version2, version3)
				.build();
	}

	@Test
	public void should_move_version_up() {
		documentation.moveVersionUp(version2);

		assertThat(documentation.getVersions())
				.containsExactly(version2, version1, version3);
	}

	@Test
	public void should_move_version_down() {
		documentation.moveVersionDown(version2);

		assertThat(documentation.getVersions())
				.containsExactly(version1, version3, version2);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void should_reject_moveUp_when_is_first_element() {
		documentation.moveVersionUp(version1);
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void should_reject_moveDown_when_is_last_element() {
		documentation.moveVersionDown(version3);
	}
}