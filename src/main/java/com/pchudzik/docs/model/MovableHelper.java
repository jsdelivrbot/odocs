package com.pchudzik.docs.model;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static java.util.Collections.swap;

/**
 * Created by pawel on 08.03.15.
 */
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
class MovableHelper<T> {
	final List<T> collection;

	void moveUp(T entity) {
		final int currentEntityIndex = collection.indexOf(entity);
		Preconditions.checkArgument(currentEntityIndex > 0, "Can not move up first item");
		swap(collection, currentEntityIndex, currentEntityIndex - 1);
	}

	void moveDown(T entity) {
		final int currentEntityIndex = collection.indexOf(entity);
		Preconditions.checkArgument(currentEntityIndex < collection.size() - 1, "Can not move down last item");
		swap(collection, currentEntityIndex, currentEntityIndex + 1);
	}
}
