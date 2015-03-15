package com.pchudzik.docs.feed.download;

/**
 * Created by pawel on 28.03.15.
 */
public interface DownloadEventListener {
	void onEvent(DownloadInfo source, DownloadEvent event);
}
