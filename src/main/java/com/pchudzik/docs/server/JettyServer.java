package com.pchudzik.docs.server;

import com.google.common.base.Preconditions;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;

/**
 * Created by pawel on 18.02.15.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class JettyServer implements Deployable {
	private Integer port;
	private Handler requestHandler;

	private Server serverInstance;

	@Override
	@SneakyThrows
	public void start() {
		serverInstance = new Server(port);
		serverInstance.setHandler(requestHandler);
		serverInstance.start();
		log.debug("Starting http server on port {}", port);
	}

	@Override
	@SneakyThrows
	public void stop() {
		serverInstance.stop();
	}

	public static JettyServerBuilder builder() {
		return new JettyServerBuilder();
	}

	public static class JettyServerBuilder extends ObjectBuilder<JettyServerBuilder, JettyServer> {
		private JettyServerBuilder() {
			addValidator(srv -> Preconditions.checkNotNull(srv.port, "Server port must be set"));
			addValidator(srv -> Preconditions.checkNotNull(srv.requestHandler, "Request handler for server must be set"));
		}

		@Override
		protected JettyServer createObject() {
			return new JettyServer();
		}

		public JettyServerBuilder port(int port) {
			return addOperation(srv -> srv.port = port);
		}

		public JettyServerBuilder requestHandler(Handler handler) {
			return addOperation(srv -> srv.requestHandler = handler);
		}
	}
}
