package com.pchudzik.docs.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by pawel on 21.02.15.
 */
@RestController
@RequestMapping("/deployment")
class DeploymentStatusController {
	@Autowired JettyServerRegistry serverRegistry;

	@RequestMapping("/status/{versionId}")
	DeploymentInfoDto getDeploymentStatus(@PathVariable String versionId) {
		return serverRegistry.deploymentStatus(versionId);
	}
}
