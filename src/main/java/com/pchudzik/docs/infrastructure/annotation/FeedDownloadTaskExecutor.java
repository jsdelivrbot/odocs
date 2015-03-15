package com.pchudzik.docs.infrastructure.annotation;

import org.springframework.beans.factory.annotation.Qualifier;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by pawel on 14.03.15.
 */
@Inherited
@Qualifier(FeedDownloadTaskExecutor.feedDownloadTaskExecutor)
@Retention(RUNTIME)
@Target(value={FIELD, METHOD, PARAMETER, TYPE, ANNOTATION_TYPE})
public @interface FeedDownloadTaskExecutor {
	final String feedDownloadTaskExecutor = "feedDownloadTaskExecutir";
}
