package com.pchudzik.docs.infrastructure;

import com.pchudzik.docs.infrastructure.annotation.DeploymentsDirectory;
import com.pchudzik.docs.infrastructure.annotation.TemporaryDirectory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Created by pawel on 14.03.15.
 */
@Configuration
class StorageConfiguration {
	@Value("${docs.tmpData}") String tmpDataLocation;
	@Value("${docs.deployments}") String deploymentRootPath;

	@Bean(name = TemporaryDirectory.tmpDir) File docsTmpDir() {
		final File tmpDir = new File(tmpDataLocation);
		tmpDir.mkdirs();
		return tmpDir;
	}

	@Bean(name = DeploymentsDirectory.deploymentDir) File deploymentDir() {
		final File deploymentDir = new File(deploymentRootPath);
		deploymentDir.mkdirs();
		return deploymentDir;
	}
}
