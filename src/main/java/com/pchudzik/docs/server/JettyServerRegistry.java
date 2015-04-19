package com.pchudzik.docs.server;

import com.google.common.collect.Maps;
import com.pchudzik.docs.infrastructure.annotation.DeploymentsDirectory;
import com.pchudzik.docs.model.DocumentationVersion;
import com.pchudzik.docs.model.UrlRewriteRule;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.rewrite.handler.RewriteRegexRule;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.*;

/**
 * Created by pawel on 18.02.15.
 */
@Slf4j
@Service
public class JettyServerRegistry {
	private final String allowOriginDomain;
	private final FreePortSelector portSelector;
	private File deploymentRoot;

	private final Map<String, Pair<Deployable, DeploymentInfoDto>> serverInstances = Maps.newHashMap();

	@Autowired
	JettyServerRegistry(
			FreePortSelector portSelector,
			@Value("${docs.allowOriginDomain}") String allowOriginDomain,
			@DeploymentsDirectory File deploymentRoot) {
		this.portSelector = portSelector;
		this.deploymentRoot = deploymentRoot;
		this.allowOriginDomain = allowOriginDomain;
	}

	@PreDestroy void tearDown() {
		serverInstances.forEach((id, deployable) -> {
			log.info("Undeploying version {}", id);
			deployable.getLeft().stop();
		});
	}

	public synchronized void deploy(DocumentationVersion version) {
		if(serverInstances.containsKey(version.getId())) {
			undeploy(version);
		}

		final File deploymentDirectory = new File(deploymentRoot, version.getId());
		final int port = portSelector.getAvailablePort();
		final File serverRoot = version.getRootDirectory()
				.map(rootDirPath -> new File(deploymentDirectory, rootDirPath))
				.orElse(deploymentDirectory);

		log.info("Deploying version {} on port {}. Serving files from {}",
				version, port, serverRoot.getAbsolutePath());


		final ArchiveManager archiveManager = ArchiveManager.builder()
				.deploymentRoot(deploymentDirectory)
				.archiveInputStream(new ByteArrayInputStream(version.getVersionFile().getFileContent()))
				.build();
		final HandlerCollection requestHandler = createHandlerCollection(
				createRewriteRules(version.getRewriteRules()),
				Optional.of(new ServerResponseHandler(serverRoot, allowOriginDomain)));
		final JettyServer jettyServer = JettyServer.builder()
				.port(port)
				.requestHandler(requestHandler)
				.build();
		final Deployable deployables = DeployableCollection.builder()
				.addDeployable(archiveManager)
				.addDeployable(jettyServer)
				.build();

		deployables.start();
		serverInstances.put(
				version.getId(),
				Pair.of(deployables, DeploymentInfoDto.builder()
						.protocol("http")
						.host("localhost")
						.port(port)
						.initialDirectory(version.getInitialDirectory())
						.versionId(version.getId())
						.build()));
	}

	@SneakyThrows
	public synchronized void undeploy(DocumentationVersion version) {
		log.info("Undeploying version {}", version);
		Optional.ofNullable(serverInstances.remove(version.getId()))
				.orElseThrow(NoSuchElementException::new)
				.getLeft().stop();
	}

	public DeploymentInfoDto deploymentStatus(String versionId) {
		return serverInstances
				.get(versionId)
				.getRight();
	}

	private Optional<Handler> createRewriteRules(List<UrlRewriteRule> rewriteRules) {
		if(rewriteRules.isEmpty()) {
			return Optional.empty();
		}

		final RewriteHandler handler = new RewriteHandler();
		rewriteRules.stream()
				.map(rule -> {
					final RewriteRegexRule rewritePatternRule = new RewriteRegexRule();
					rewritePatternRule.setReplacement(rule.getReplacement());
					rewritePatternRule.setRegex(rule.getRegexp());
					return rewritePatternRule;
				})
				.forEachOrdered(handler::addRule);
		return Optional.of(handler);
	}

	private HandlerCollection createHandlerCollection(Optional<Handler> ... handlers) {
		final HandlerCollection handlerCollection = new HandlerCollection();
		handlerCollection.setHandlers(Arrays.stream(handlers)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.toArray(Handler[]::new));
		return handlerCollection;
	}
}
