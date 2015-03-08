package com.pchudzik.docs.manage;

import com.pchudzik.docs.model.Workspace;
import com.pchudzik.docs.repository.WorkspaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;

import static com.pchudzik.docs.utils.functional.LambdaTransactionCallbackWrapper.wrapTransactionCallback;

/**
 * Created by pawel on 08.03.15.
 */
@Service
class WorkspaceService {
	final WorkspaceRepository workspaceRepository;
	final TransactionTemplate transactionTemplate;

	@Autowired
	WorkspaceService(PlatformTransactionManager platformTransactionManager, WorkspaceRepository workspaceRepository) {
		this.workspaceRepository = workspaceRepository;
		transactionTemplate = new TransactionTemplate(platformTransactionManager);
	}

	@PostConstruct
	void createDefaultWorkspaceIfNotPresent() {
		transactionTemplate.execute(wrapTransactionCallback(() -> {
			final Workspace defaultWorkspace = workspaceRepository.findDefaultWorkspace();
			if (defaultWorkspace == null) {
				workspaceRepository.persist(new Workspace("default workspace"));
			}
		}));
	}

	@Transactional(readOnly = true)
	public Workspace getDefaultWorkspace() {
		return workspaceRepository.findDefaultWorkspace();
	}
}
