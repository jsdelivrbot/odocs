package com.pchudzik.docs.server;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * Created by pawel on 20.02.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ZipTestFactory {
	public static InputStream createArchiveStream(Map<String, String> entries) {
		return new ByteArrayInputStream(createArchive(entries));
	}

	@SneakyThrows
	public static byte[] createArchive(Map<String, String> entries) {
		final ByteArrayOutputStream result = new ByteArrayOutputStream();
		final ZipOutputStream zip = new ZipOutputStream(result);
		for(Map.Entry<String, String> entry : entries.entrySet()) {
			zip.putNextEntry(new ZipEntry(entry.getKey()));
			zip.write(entry.getValue().getBytes());
			zip.closeEntry();
		}
		zip.close();
		return result.toByteArray();
	}
}
