package com.pchudzik.docs.server;

import org.mockito.InOrder;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.inOrder;
import static org.mockito.MockitoAnnotations.initMocks;

public class DeployableCollectionTest {
	@Mock Deployable first;
	@Mock Deployable second;

	DeployableCollection deployables;

	@BeforeMethod void setup() {
		initMocks(this);

		deployables = DeployableCollection.builder()
				.addDeployable(first)
				.addDeployable(second)
				.build();
	}

	@Test
	public void should_run_deployables_in_order() {
		InOrder inOrder = inOrder(first, second);

		deployables.start();

		inOrder.verify(first).start();
		inOrder.verify(second).start();
	}

	@Test
	public void should_run_deployables_in_reverse_order() {
		InOrder inOrder = inOrder(first, second);

		deployables.stop();

		inOrder.verify(second).stop();
		inOrder.verify(first).stop();
	}
}