package com.pchudzik.docs.repository;

import com.mysema.query.jpa.impl.JPAQuery;
import com.pchudzik.docs.model.Workspace;
import org.springframework.stereotype.Repository;

import static com.pchudzik.docs.model.QWorkspace.workspace;

/**
 * Created by pawel on 08.03.15.
 */
@Repository
public class WorkspaceRepository extends EntityRepository<Workspace, String> {
	public Workspace findDefaultWorkspace() {
		return new JPAQuery(entityManager)
				.from(workspace)
				.singleResult(workspace);
	}
}
