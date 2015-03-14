package com.pchudzik.docs.utils.http;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by pawel on 14.03.15.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MultipartFileFactory {
	@SneakyThrows
	public static MultipartFile fromFile(File file) {
		@Cleanup final FileInputStream fileInputStream = new FileInputStream(file);
		final DiskFileItemFactory factory = new DiskFileItemFactory();
		final FileItem item = factory.createItem("file", "application/zip", false, file.getName());
		IOUtils.copy(fileInputStream, item.getOutputStream());
		return new CommonsMultipartFile(item);
	}
}
