package com.pchudzik.docs.server;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DeploymentInfoDto {
	private String host;
	private String protocol;
	private String versionId;
	private int port;
	private Optional<String> initialDirectory;

	public String getUrl() {
		return protocol + "://" + host + ":" + port;
	}

	public String getPathAndQuery() {
		return "/" + initialDirectory.map(dir -> dir +"/").orElse("");
	}

	public String getFullUrl() {
		return getUrl() + getPathAndQuery();
	}

	public static DeploymentInfoDtoBuilder builder() {
		return new DeploymentInfoDtoBuilder();
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class DeploymentInfoDtoBuilder extends ObjectBuilder<DeploymentInfoDtoBuilder, DeploymentInfoDto> {
		@Override
		protected DeploymentInfoDto createObject() {
			return new DeploymentInfoDto();
		}

		public DeploymentInfoDtoBuilder versionId(String versionId) {
			return addOperation(deployment -> deployment.versionId = versionId);
		}

		public DeploymentInfoDtoBuilder host(String host) {
			return addOperation(deployment -> deployment.host = host);
		}

		public DeploymentInfoDtoBuilder protocol(String protocol) {
			return addOperation(deployment -> deployment.protocol = protocol);
		}

		public DeploymentInfoDtoBuilder port(int port) {
			return addOperation(deployment -> deployment.port = port);
		}

		public DeploymentInfoDtoBuilder initialDirectory(Optional<String> initialDirectory) {
			return addOperation(deployment -> deployment.initialDirectory = initialDirectory);
		}
	}
}