package com.pchudzik.docs.feed.download.copy;

/**
 * Created by pawel on 29.03.15.
 */
@FunctionalInterface
public interface AbortNotifier {
	boolean isAborted();
}
