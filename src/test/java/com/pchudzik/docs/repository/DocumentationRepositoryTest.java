package com.pchudzik.docs.repository;

import com.pchudzik.docs.infrastructure.test.RepositoryTestCase;
import com.pchudzik.docs.model.Documentation;
import com.pchudzik.docs.model.DocumentationVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.function.Supplier;

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
	public void should_fetch_documentation_with_all_versions() {
		final DocumentationVersion version1 = persist(createVersion("v1"));
		final DocumentationVersion version2 = persist(createVersion("v2"));
		final Documentation doc = persist(createDocumentation(defaultWorkspace, "doc", version1, version2));

		assertThat(documentationRepository.findOne(doc.getId()))
				.isNotNull()
				.has(id(doc.getId()))
				.has(name("doc"));
	}

	@Test
	public void should_find_all_documentations_with_at_last_one_version_with_file() {
		final Documentation documentationWithoutVersions = persist(createDocumentation(defaultWorkspace, "no versions"));
		final Documentation documentationWithoutFile = persist(createDocumentation(defaultWorkspace, "no file", persist(persist(createVersion("no file")))));
		final Documentation completeDocumentation = persist(createDocumentation(defaultWorkspace, "complete", persist(createVersionWithFile("has file"))));

		assertThat(documentationRepository.findCompleted())
				.hasSize(1)
				.has(id(completeDocumentation.getId()), atIndex(0));
	}

	@Test
	public void version_order_should_be_persisted_and_kept() {
		final DocumentationVersion version1 = persist(createVersionWithFile("1"));
		final DocumentationVersion version2 = persist(createVersionWithFile("1"));
		final DocumentationVersion version3 = persist(createVersionWithFile("1"));
		final Documentation documentation = persist(createDocumentation(defaultWorkspace, "any name", version1, version2, version3));

		//when
		entityManager.flush();
		assertThat(documentationRepository.findOne(documentation.getId()).getVersions())
				//then
				.hasSize(3)
				.has(id(version1.getId()), atIndex(0))
				.has(id(version2.getId()), atIndex(1))
				.has(id(version3.getId()), atIndex(2));

		//when
		documentation.moveVersionUp(version2);
		entityManager.flush();

		assertThat(documentationRepository.findOne(documentation.getId()).getVersions())
				//then7
				.hasSize(3)
				.has(id(version2.getId()), atIndex(0))
				.has(id(version1.getId()), atIndex(1))
				.has(id(version3.getId()), atIndex(2));
	}

	@DataProvider(name = "orderAwareFunction") Object[][] orderAwareFunctionDP() {
		return new Object[][] {
				{(Supplier<List<Documentation>>) () -> documentationRepository.findAll()},
				{(Supplier<List<Documentation>>) () -> documentationRepository.findCompleted()}
		};
	}

	@Test(dataProvider = "orderAwareFunction")
	public void documentation_order_should_be_persisted_and_kept(Supplier<List<Documentation>> listSupplier) {
		final Documentation firstDocumentation = persist(createDocumentation(defaultWorkspace, "first", persist(createVersionWithFile("v1"))));
		final Documentation secondDocumentation = persist(createDocumentation(defaultWorkspace, "second", persist(createVersionWithFile("v2"))));
		final Documentation thirdDocumentation = persist(createDocumentation(defaultWorkspace, "third", persist(createVersionWithFile("v3"))));

		//when
		entityManager.flush();
		assertThat(listSupplier.get())
				//then
				.hasSize(3)
				.has(id(firstDocumentation.getId()), atIndex(0))
				.has(id(secondDocumentation.getId()), atIndex(1))
				.has(id(thirdDocumentation.getId()), atIndex(2));

		//when
		defaultWorkspace.moveDocumentationUp(secondDocumentation);
		entityManager.flush();
		assertThat(listSupplier.get())
				//then
				.hasSize(3)
				.has(id(secondDocumentation.getId()), atIndex(0))
				.has(id(firstDocumentation.getId()), atIndex(1))
				.has(id(thirdDocumentation.getId()), atIndex(2));

		//when
		defaultWorkspace.moveDocumentationDown(firstDocumentation);
		entityManager.flush();
		assertThat(listSupplier.get())
				//then
				.hasSize(3)
				.has(id(secondDocumentation.getId()), atIndex(0))
				.has(id(thirdDocumentation.getId()), atIndex(1))
				.has(id(firstDocumentation.getId()), atIndex(2));

	}
}
