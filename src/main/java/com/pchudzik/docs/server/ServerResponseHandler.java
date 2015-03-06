package com.pchudzik.docs.server;

import com.google.common.annotations.VisibleForTesting;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Builder;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Files;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.io.IOUtils.copy;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;

/**
 * Created by pawel on 24.08.14.
 */
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
class ServerResponseHandler extends AbstractHandler {
	static final String ACCESS_CONTROL_ALLOW_ORIGIN_HEADER = "Access-Control-Allow-Origin";

	final InputStreamProvider inputStreamProvider;
	final String allowOriginDomain;

	public ServerResponseHandler(File serverRoot, String allowOriginDomain) {
		this(new InputStreamProvider(serverRoot), allowOriginDomain);
	}

	@Override
	public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		addAccessControlHeader(response);

		try {
			ServerResource resource = inputStreamProvider.findResource(request.getPathInfo());
			if(resource.isHtml()) {
				resource = createResourceWithDocumentDomainScript(target, resource);
			}

			response.setStatus(HttpStatus.OK_200);
			response.setContentType(resource.getContentType());
			response.setContentLengthLong(resource.getSize());

			copy(resource.getInputStream(), response.getOutputStream());
		} catch (FileNotFoundException ex) {
			log.debug("File {} not found", target);
			response.setStatus(HttpStatus.NOT_FOUND_404);
		} finally {
			response.getOutputStream().close();
			baseRequest.setHandled(true);
		}
	}

	private ServerResource createResourceWithDocumentDomainScript(String target, ServerResource resource) throws IOException {
		final Document document = Jsoup.parse(resource.getInputStream(), "UTF-8", resolveBaseUri(target));
		document.head().append(documentDomainScript());
		final byte [] resultHtml = document.html().getBytes();
		return ServerResource.builder()
				.inputStream(new ByteArrayInputStream(resultHtml))
				.size(resultHtml.length)
				.name(resource.getName())
				.build();
	}

	String resolveBaseUri(String target) {
		final List<String> pathElements = asList(target.split("/"))
				.stream()
				.filter(input -> isNotBlank(input))
				.collect(toList());

		StringBuilder result = new StringBuilder("/");
		if(pathElements.size() > 1) {
			result.append(join(pathElements.subList(0, pathElements.size() - 1), "/"));
		}
		return result.toString();
	}

	private String documentDomainScript() {
		return String.format("<script>document.domain = '%s';</script>", allowOriginDomain);
	}

	private void addAccessControlHeader(HttpServletResponse response) {
		response.setHeader(ACCESS_CONTROL_ALLOW_ORIGIN_HEADER, allowOriginDomain);
	}

	@RequiredArgsConstructor
	static class InputStreamProvider {
		final List<String> startFileList = asList("index.htm", "index.html");
		final File documentationRoot;

		@SneakyThrows
		public ServerResource findResource(String requestedResource) throws FileNotFoundException {
			File requestedFile = new File(documentationRoot, requestedResource);

			throwExceptionIfMissing(requestedResource, requestedFile);

			if(requestedFile.isDirectory()) {
				requestedFile = searchForStartFile(requestedFile);
			}

			return ServerResource.builder()
					.inputStream(new FileInputStream(requestedFile))
					.contentType(Files.probeContentType(requestedFile.toPath()))
					.name(requestedFile.getName())
					.size(requestedFile.length())
					.build();
		}

		private File searchForStartFile(File requestedFile) throws FileNotFoundException {
			return new File(
					requestedFile,
					startFileList.stream()
					.filter((startFileName) -> {
						File maybeStartFile = new File(requestedFile, startFileName);
						return maybeStartFile.exists();
					})
					.findFirst()
					.orElseThrow(() -> new FileNotFoundException("requested resource is directory")));
		}

		private void throwExceptionIfMissing(String requestedResourceName, File resultFile) throws FileNotFoundException {
			if(!resultFile.exists()) {
				throw new FileNotFoundException("Can not find file " + documentationRoot.getPath() + "/" + requestedResourceName);
			}
		}
	}

	@Builder
	@Getter
	@VisibleForTesting
	protected static class ServerResource {
		private final InputStream inputStream;
		private final String name;
		private final String contentType;
		private final long size;

		public boolean isHtml() {
			String nullSafeName = name.toLowerCase();
			return nullSafeName.endsWith("html") || nullSafeName.endsWith("htm");
		}
	}
}
