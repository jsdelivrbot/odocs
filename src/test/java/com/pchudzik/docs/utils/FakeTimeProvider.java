package com.pchudzik.docs.utils;

import org.joda.time.DateTime;

/**
 * Created by pawel on 16.04.15.
 */
public class FakeTimeProvider extends TimeProvider {
	final DateTime time;

	public FakeTimeProvider(DateTime time) {
		this.time = time;
	}
	public FakeTimeProvider(String time) {
		this.time = new DateTime(time);
	}

	@Override
	public DateTime now() {
		return time;
	}
}