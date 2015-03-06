package com.pchudzik.docs.repository;

import com.pchudzik.docs.model.DocumentationVersion;
import org.springframework.stereotype.Repository;

/**
 * Created by pawel on 08.02.15.
 */
@Repository
public class VersionRepository extends EntityRepository<DocumentationVersion, String> {
	@Override
	public DocumentationVersion findOne(String s) {
		throw new UnsupportedOperationException("Use findOne(documentationId, versionId)");
	}

	public DocumentationVersion findOne(String documentationId, String versionId) {
		return entityManager.createQuery(
				"select version " +
				"from Documentation doc " +
				"join doc.versions version " +
				"where version.id = :versionId and doc.id = :docId", DocumentationVersion.class)
				.setParameter("versionId", versionId)
				.setParameter("docId", documentationId)
				.getSingleResult();
	}
}
