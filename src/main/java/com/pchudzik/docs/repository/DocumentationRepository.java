package com.pchudzik.docs.repository;

import com.pchudzik.docs.model.Documentation;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by pawel on 08.02.15.
 */
@Repository
public class DocumentationRepository extends EntityRepository<Documentation, String> {
	@Override
	public List<Documentation> findAll() {
		return entityManager.createQuery(
						"from Documentation order by lower(name)",
						Documentation.class)
				.getResultList();
	}

	public List<Documentation> findCompleted() {
		return entityManager.createQuery(
				"select doc " +
				"from Documentation doc " +
				"join fetch doc.versions version " +
				"where " +
				"  size(doc.versions) > 0 " +
				"  and version.versionFile is not null " +
				"order by lower(doc.name)",
				Documentation.class)
				.getResultList();
	}
}
