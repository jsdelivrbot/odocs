package com.pchudzik.docs.repository;

import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.OrderSpecifier;
import com.pchudzik.docs.model.Documentation;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pchudzik.docs.model.QDocumentation.documentation;
import static com.pchudzik.docs.model.QDocumentationVersion.documentationVersion;

/**
 * Created by pawel on 08.02.15.
 */
@Repository
public class DocumentationRepository extends EntityRepository<Documentation, String> {
	@Override
	public List<Documentation> findAll() {
		return new JPAQuery(entityManager)
				.from(documentation)
				.orderBy(byNameDocumentationOrder())
				.list(documentation);
	}

	public List<Documentation> findCompleted() {
		return new JPAQuery(entityManager)
				.from(documentation)
				.join(documentation.versions, documentationVersion).fetch()
				.where(
						documentation.versions.size().gt(0),
						documentationVersion.versionFile.isNotNull())
				.orderBy(byNameDocumentationOrder())
				.list(documentation);
	}

	private OrderSpecifier<String> byNameDocumentationOrder() {
		return documentation.name.lower().asc();
	}
}
