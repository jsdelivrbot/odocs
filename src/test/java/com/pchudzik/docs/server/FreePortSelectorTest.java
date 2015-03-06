package com.pchudzik.docs.server;

import org.testng.annotations.Test;

import java.net.ServerSocket;

import static org.assertj.core.api.Assertions.assertThat;

public class FreePortSelectorTest {
	FreePortSelector portSelector = new FreePortSelector();

	@Test
	public void selected_port_should_be_in_range_from_49152_to_65535() {
		assertThat(portSelector.getAvailablePort())
				.isGreaterThanOrEqualTo(1024)
				.isLessThan(65535);
	}

	@Test
	public void selected_port_should_not_be_used() throws Exception {
		ServerSocket serverSocket = new ServerSocket(portSelector.getAvailablePort());
		serverSocket.close();

		//expect no exception
	}
}