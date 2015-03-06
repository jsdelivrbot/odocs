package com.pchudzik.docs.server;

import lombok.SneakyThrows;
import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Component;

import java.net.ServerSocket;

/**
 * Created by pawel on 30.08.14.
 */
@Component
class FreePortSelector {
	@SneakyThrows
	public synchronized int getAvailablePort() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(0);
			return  serverSocket.getLocalPort();
		} finally {
			IOUtils.closeQuietly(serverSocket);
		}
	}
}
