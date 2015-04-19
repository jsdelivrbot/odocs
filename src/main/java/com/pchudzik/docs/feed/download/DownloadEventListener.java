package com.pchudzik.docs.feed.download;

import com.pchudzik.docs.feed.download.event.DownloadEvent;

/**
 * Created by pawel on 28.03.15.
 */
public interface DownloadEventListener {
	void onEvent(DownloadInfo source, DownloadEvent event);
}
