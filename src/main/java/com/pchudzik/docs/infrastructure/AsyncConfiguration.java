package com.pchudzik.docs.infrastructure;

import com.pchudzik.docs.infrastructure.annotation.FeedDownloadTaskExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Created by pawel on 12.03.15.
 */
@Configuration
@EnableAsync
@EnableScheduling
class AsyncConfiguration {
	@Value("${docs.feed.downloadPoolsSize}") Integer maxPoolsSize;

	@Bean(name = FeedDownloadTaskExecutor.feedDownloadTaskExecutor)
	AsyncTaskExecutor downloadTaskExecutor() {
		final ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
		threadPoolTaskExecutor.setMaxPoolSize(maxPoolsSize);
		threadPoolTaskExecutor.setCorePoolSize(maxPoolsSize);
		return threadPoolTaskExecutor;
	}
}
