package com.pchudzik.docs.server;

import com.google.common.collect.Lists;
import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by pawel on 19.02.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class DeployableCollection implements Deployable {
	private final LinkedList<Deployable> deployables = Lists.newLinkedList();

	public static DeployableCollectionBuilder builder() {
		return new DeployableCollectionBuilder();
	}

	@Override
	public void start() {
		deployables
				.forEach(Deployable::start);
	}

	@Override
	public void stop() {
		newArrayList(deployables.descendingIterator())
				.forEach(Deployable::stop);
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class DeployableCollectionBuilder extends ObjectBuilder<DeployableCollectionBuilder, DeployableCollection> {
		public DeployableCollectionBuilder addDeployable(Deployable deployable) {
			return addOperation(collection -> collection.deployables.add(deployable));
		}

		@Override
		protected DeployableCollection createObject() {
			return new DeployableCollection();
		}
	}
}
