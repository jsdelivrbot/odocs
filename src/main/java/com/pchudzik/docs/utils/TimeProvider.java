package com.pchudzik.docs.utils;

import org.joda.time.DateTime;
import org.springframework.stereotype.Component;

/**
 * Created by pawel on 22.03.15.
 */
@Component
public class TimeProvider {
	public DateTime now() {
		return new DateTime();
	}
}
