package com.pchudzik.docs.server;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by pawel on 19.02.15.
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
class ArchiveManager implements Deployable {
	File deploymentRoot;
	InputStream archiveInputStream;

	@Override
	public void start() {
		deploymentRoot.mkdirs();
		clearDirectoryIfExists(deploymentRoot);
		extract(archiveInputStream, deploymentRoot);
	}

	@Override
	@SneakyThrows
	public void stop() {
		FileUtils.deleteDirectory(deploymentRoot);
	}

	public static ArchiveManagerBuilder builder() {
		return new ArchiveManagerBuilder();
	}

	@SneakyThrows
	private void extract(InputStream archiveInputStream, File destination) {
		final ZipInputStream zis = new ZipInputStream(archiveInputStream);

		ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			File entryDst = new File(destination, entry.getName());
			entryDst.getParentFile().mkdirs();
			if(entry.isDirectory()) {
				entryDst.mkdirs();
			} else {
				FileOutputStream fileOutputStream = new FileOutputStream(entryDst);
				IOUtils.copy(zis, fileOutputStream);
				IOUtils.closeQuietly(fileOutputStream);
			}
		}
		IOUtils.closeQuietly(zis);

		log.debug("Zip files extracted to {}", destination.getAbsolutePath());
	}

	@SneakyThrows
	private void clearDirectoryIfExists(File directory) {
		if(directory.exists()) {
			FileUtils.cleanDirectory(directory);
			log.debug("Removing directory {}", directory.getAbsolutePath());
		}
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class ArchiveManagerBuilder extends ObjectBuilder<ArchiveManagerBuilder, ArchiveManager> {
		public ArchiveManagerBuilder deploymentRoot(File deplymentRoot) {
			return addOperation(archiveManager -> archiveManager.deploymentRoot = deplymentRoot);
		}

		public ArchiveManagerBuilder archiveInputStream(InputStream inputStream) {
			return addOperation(archiveManager -> archiveManager.archiveInputStream = inputStream);
		}

		@Override
		protected ArchiveManager createObject() {
			return new ArchiveManager();
		}
	}
}
