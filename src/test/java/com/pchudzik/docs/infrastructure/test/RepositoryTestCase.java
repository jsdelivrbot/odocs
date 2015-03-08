package com.pchudzik.docs.infrastructure.test;

import com.pchudzik.docs.infrastructure.ApplicationPropertiesConfiguration;
import com.pchudzik.docs.infrastructure.DatabaseConfiguration;
import com.pchudzik.docs.model.Workspace;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.ContextHierarchy;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by pawel on 08.02.15.
 */
@ContextHierarchy(@ContextConfiguration(classes = RepositoryTestCase.TestRepositoryConfiguration.class))
public class RepositoryTestCase extends AbstractTransactionalTestNGSpringContextTests {
	@PersistenceContext protected EntityManager entityManager;

	protected Workspace defaultWorkspace;

	@BeforeMethod
	public void setupDefaultWorkspace() {
		defaultWorkspace = persist(new Workspace("default"));
	}

	protected <T extends Object> T persist(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	protected void flush() {
		entityManager.flush();
		entityManager.clear();
	}

	protected <T extends Object> T merge(T entity) {
		return entityManager.merge(entity);
	}

	@Configuration
	@Import(DatabaseConfiguration.class)
	@ComponentScan(
			value = "com.pchudzik.docs",
			useDefaultFilters = false,
			includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, value = Repository.class))
	static class TestRepositoryConfiguration {
		@Bean ApplicationPropertiesConfiguration.DatabaseSettings databaseSettings() {
			return ApplicationPropertiesConfiguration.DatabaseSettings.builder()
					.jdbcUrl("jdbc:h2:mem:test")
					.build();
		}
	}
}
