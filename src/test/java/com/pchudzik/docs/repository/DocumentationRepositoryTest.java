package com.pchudzik.docs.repository;

import com.pchudzik.docs.infrastructure.test.RepositoryTestCase;
import com.pchudzik.docs.model.Documentation;
import com.pchudzik.docs.model.DocumentationVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static com.pchudzik.docs.model.BaseEntityAssertHelper.id;
import static com.pchudzik.docs.model.DocumentationTestFactory.createDocumentation;
import static com.pchudzik.docs.model.DocumentationVersionTestFactory.createVersion;
import static com.pchudzik.docs.model.DocumentationVersionTestFactory.createVersionWithFile;
import static com.pchudzik.docs.model.NameAwareAssertHelper.name;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Index.atIndex;

public class DocumentationRepositoryTest extends RepositoryTestCase {
	@Autowired DocumentationRepository documentationRepository;

	@Test
	public void find_all_should_be_ordered_by_name() {
		Documentation doc1 = persist(createDocumentation("aB"));
		Documentation doc2 = persist(createDocumentation("AbC"));
		Documentation doc3 = persist(createDocumentation("BaC"));
		Documentation doc4 = persist(createDocumentation("bCA"));

		assertThat(documentationRepository.findAll())
				.hasSize(4)
				.has(id(doc1.getId()), atIndex(0))
				.has(id(doc2.getId()), atIndex(1))
				.has(id(doc3.getId()), atIndex(2))
				.has(id(doc4.getId()), atIndex(3));
	}

	@Test
	public void should_fetch_documentation_with_all_versions() {
		final DocumentationVersion version1 = persist(createVersion("v1"));
		final DocumentationVersion version2 = persist(createVersion("v2"));
		final Documentation doc = persist(createDocumentation("doc", version1, version2));

		assertThat(documentationRepository.findOne(doc.getId()))
				.isNotNull()
				.has(id(doc.getId()))
				.has(name("doc"));
	}

	@Test
	public void should_find_all_documentations_with_at_last_one_version_with_file() {
		final Documentation documentationWithoutVersions = persist(createDocumentation("no versions"));
		final Documentation documentationWithoutFile = persist(createDocumentation("no file", persist(persist(createVersion("no file")))));
		final Documentation completeDocumentation = persist(createDocumentation("complete", persist(createVersionWithFile("has file"))));

		assertThat(documentationRepository.findCompleted())
				.hasSize(1)
				.has(id(completeDocumentation.getId()), atIndex(0));
	}

	@Test
	public void should_order_documentations_by_name_in_menu() {
		final Documentation doc1 = persist(createDocumentation("aB", persist(createVersionWithFile("v1"))));
		final Documentation doc2 = persist(createDocumentation("AbC", persist(createVersionWithFile("v1"))));
		final Documentation doc3 = persist(createDocumentation("BaC", persist(createVersionWithFile("v1"))));
		final Documentation doc4 = persist(createDocumentation("bCa", persist(createVersionWithFile("v1"))));

		assertThat(documentationRepository.findCompleted())
				.hasSize(4)
				.has(id(doc1.getId()), atIndex(0))
				.has(id(doc2.getId()), atIndex(1))
				.has(id(doc3.getId()), atIndex(2))
				.has(id(doc4.getId()), atIndex(3));
	}

	@Test
	public void version_order_should_be_persisted_and_kept() {
		final DocumentationVersion version1 = persist(createVersionWithFile("1"));
		final DocumentationVersion version2 = persist(createVersionWithFile("1"));
		final DocumentationVersion version3 = persist(createVersionWithFile("1"));
		final Documentation documentation = persist(createDocumentation("any name", version1, version2, version3));

		//when
		entityManager.flush();
		assertThat(documentationRepository.findOne(documentation.getId()).getVersions())
				//then
				.hasSize(3)
				.has(id(version1.getId()), atIndex(0))
				.has(id(version2.getId()), atIndex(1))
				.has(id(version3.getId()), atIndex(2));

		//when
		System.out.println("\n\n\n\nMOVE\n\n\n");
		documentation.moveVersionUp(version2);
		entityManager.flush();

		assertThat(documentationRepository.findOne(documentation.getId()).getVersions())
				//then7
				.hasSize(3)
				.has(id(version2.getId()), atIndex(0))
				.has(id(version1.getId()), atIndex(1))
				.has(id(version3.getId()), atIndex(2));
	}
}
