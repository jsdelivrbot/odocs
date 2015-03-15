package com.pchudzik.docs.utils.http;

import com.google.common.base.Preconditions;
import com.pchudzik.docs.infrastructure.annotation.TemporaryDirectory;
import lombok.SneakyThrows;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created by pawel on 14.03.15.
 */
@Service
public class MultipartFileFactory {
	private final File temporaryDirRoot;

	private File temporaryDir;

	@Autowired
	public MultipartFileFactory(@TemporaryDirectory File temporaryDirRoot) {
		this.temporaryDirRoot = temporaryDirRoot;
	}

	@PostConstruct
	void createTemporaryDir() {
		temporaryDir = new File(temporaryDirRoot, "downloaded");
		cleanupTemporaryDir();

		final boolean created = temporaryDir.mkdirs();
		Preconditions.checkState(created, "Can not create directory " + temporaryDir.getAbsolutePath());
	}

	@PreDestroy
	@SneakyThrows
	void cleanupTemporaryDir() {
		FileUtils.deleteDirectory(temporaryDir);
	}

	@SneakyThrows
	public MultipartFile fromFile(File file) {
		try (final FileInputStream fileInputStream = new FileInputStream(file)) {
			final DiskFileItemFactory factory = new DiskFileItemFactory(10024, temporaryDir);
			final FileItem item = factory.createItem("file", "application/zip", false, file.getName());
			IOUtils.copy(fileInputStream, item.getOutputStream());
			return new CommonsMultipartFile(item);
		}
	}
}
