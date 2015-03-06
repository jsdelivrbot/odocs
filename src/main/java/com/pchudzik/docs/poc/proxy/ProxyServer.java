package com.pchudzik.docs.poc.proxy;

import com.steadystate.css.parser.CSSOMParser;
import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.w3c.css.sac.InputSource;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSStyleRule;
import org.w3c.dom.css.CSSStyleSheet;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by pawel on 07.02.15.
 */
@Controller
@RequestMapping("/proxy")
public class ProxyServer {
	@SneakyThrows
	@RequestMapping
	void proxy(@RequestParam String url, HttpServletResponse response) {
		final URLConnection request = new URL(url).openConnection();
		if(request.getContentType().toLowerCase().contains("text/html")) {
			response.getWriter().write(proxifyHtml(request, url));
		} else if(request.getContentType().contains("text/css")) {
			response.getWriter().write(proxifyCss(url, request));
		} else {
			IOUtils.copy(request.getInputStream(), response.getOutputStream());
		}
	}

	private String proxifyCss(String url, URLConnection request) throws IOException {
		StringBuilder sb = new StringBuilder();
		CSSOMParser parser = new CSSOMParser();
		final CSSStyleSheet cssStyleSheet = parser.parseStyleSheet(
				new InputSource(new InputStreamReader(request.getInputStream())),
				null,
				null);
		final CSSRuleList cssRules = cssStyleSheet.getCssRules();
		for(int i = 0; i < cssRules.getLength(); i++) {
			final CSSStyleRule item = (CSSStyleRule) cssRules.item(i);

			sb.append(replaceCssUrl(url, item.getCssText()));
		}
		return sb.toString();
	}

	private String replaceCssUrl(String requestedurl, String cssText) {
		StringBuilder sb = new StringBuilder();
		int startIndex;
		int nextIndex = cssText.indexOf("url(");
		int endIndex;

		while(nextIndex >= 0) {
			startIndex = cssText.indexOf("url(");
			endIndex = cssText.indexOf(')', startIndex);
			String beforeUrl = cssText.substring(0, startIndex);
			String url = cssText.substring(startIndex+ "url(".length(), endIndex);
			sb.append(beforeUrl + " url(" + resolveProxiedUrl(requestedurl, url) + ")");

			cssText = cssText.substring(endIndex + 1);

			nextIndex = cssText.indexOf("url(");
		}
		sb.append(cssText);
		return sb.toString();
	}

	private String proxifyHtml(URLConnection request, String requestedUrl) throws IOException {
		final String responseBody = IOUtils.toString(request.getInputStream(), "UTF-8");
		Document doc = Jsoup.parse(responseBody);
		proxifyLinks(doc, requestedUrl);
		parseLinks(doc, requestedUrl);
		parseImages(doc, requestedUrl);
		parseScripts(doc, requestedUrl);
		parseFrames(doc, requestedUrl);
		return doc.html();
	}

	private void parseFrames(Document doc, String requestedUrl) {
		processHref("src", requestedUrl, doc.select("frame"));
		processHref("src", requestedUrl, doc.select("iframe"));
	}

	private void parseScripts(Document doc, String requestedUrl) {
		processHref("src", requestedUrl, doc.select("script"));
	}

	private void parseImages(Document doc, String requestedUrl) {
		final Elements images = doc.select("img");
		processHref("src", requestedUrl, images);
		processHref("data-src", requestedUrl, images);
	}

	private void parseLinks(Document doc, String requestedUrl) {
		final Elements links = doc.select("link");
		processHref("href", requestedUrl, links);
	}

	private String addEndingSlash(String url) {
		return url.endsWith("/")
				? url
				: url + "/";
	}
	private void processHref(String hrefAttribute, String requestedUrl, Elements links) {
		for(Element link : links) {
			final String href = link.attr(hrefAttribute);
			final String destinationUrl = resolveProxiedUrl(requestedUrl, href);
			link.attr(hrefAttribute, destinationUrl);
		}
	}

	private String resolveProxiedUrl(String requestedUrl, String href) {
		if(href.startsWith("http")) {
			return proxyifyUrl(href);
		} else if(href.startsWith("/")) {
			return proxyifyUrl(requestedUrl + addEndingSlash(requestedUrl) + href);
		} else {
			final String urlBase = requestedUrl.substring(0, requestedUrl.lastIndexOf('/'));
			return proxyifyUrl(addEndingSlash(urlBase) + href);
		}
	}

	private void proxifyLinks(Document doc, String requestedUrl) {
		final Elements links = doc.select("a");
		processHref("href", requestedUrl, links);
	}

	@SneakyThrows
	private String proxyifyUrl(String href) {
		return "http://localhost:8080/api/proxy?url=" + URLEncoder.encode(href, "UTF-8");
	}
}
