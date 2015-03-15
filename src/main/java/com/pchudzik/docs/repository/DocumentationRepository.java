package com.pchudzik.docs.repository;

import com.mysema.query.jpa.JPASubQuery;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.expr.BooleanExpression;
import com.pchudzik.docs.model.Documentation;
import com.pchudzik.docs.model.QWorkspace;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.pchudzik.docs.model.QDocumentation.documentation;
import static com.pchudzik.docs.model.QDocumentationVersion.documentationVersion;
import static com.pchudzik.docs.model.QWorkspace.workspace;

/**
 * Created by pawel on 08.02.15.
 */
@Repository
public class DocumentationRepository extends EntityRepository<Documentation, String> {
	@Override
	public List<Documentation> findAll() {
		return new JPAQuery(entityManager)
				.from(workspace)
				.join(workspace.documentations, documentation)
				.where(workspaceIsDefault())
				.orderBy(documentation.orderIndex.asc())
				.list(documentation);
	}

	public List<Documentation> findCompleted() {
		return new JPAQuery(entityManager)
				.from(workspace)
				.join(workspace.documentations, documentation)
				.join(documentation.versions, documentationVersion).fetch()
				.where(
						workspaceIsDefault(),
						documentationVersion.versionFile.isNotNull(),
						documentation.versions.size().gt(0)
				)
				.distinct()
				.orderBy(documentation.orderIndex.asc())
				.list(documentation);
	}

	private BooleanExpression workspaceIsDefault() {
		QWorkspace defaultWorkspace = new QWorkspace("defaultWorkspace");
		return workspace.id.eq(new JPASubQuery().from(defaultWorkspace).unique(defaultWorkspace.id));
	}
}
