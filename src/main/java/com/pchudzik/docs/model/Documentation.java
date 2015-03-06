package com.pchudzik.docs.model;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.swap;

/**
 * Created by pawel on 08.02.15.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Documentation extends NameAware {
	@OrderColumn
	@JoinColumn(name = "documentation_id")		//TODO test if it actually works
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	List<DocumentationVersion> versions = new ArrayList<>();

	public List<DocumentationVersion> getVersions() {
		return Collections.unmodifiableList(versions);
	}

	public static DocumentationBuilder builder() {
		return new DocumentationBuilder();
	}

	public void addVersion(DocumentationVersion version) {
		Preconditions.checkArgument(version != null, "Version must not be null");
		versions.add(version);
	}

	public void moveVersionUp(DocumentationVersion version) {
		final int currentVersionIndex = versions.indexOf(version);
		Preconditions.checkArgument(currentVersionIndex > 0, "Can not move up first version");
		swap(versions, currentVersionIndex, currentVersionIndex - 1);
	}

	public void moveVersionDown(DocumentationVersion version) {
		final int currentVersionIndex = versions.indexOf(version);
		Preconditions.checkArgument(currentVersionIndex < versions.size() - 1, "Can not move down last version");
		swap(versions, currentVersionIndex, currentVersionIndex + 1);
	}

	public void removeVersion(DocumentationVersion version) {
		versions.remove(version);
	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	public static class DocumentationBuilder extends NameAwareBuilder<DocumentationBuilder, Documentation> {
		public DocumentationBuilder versions(DocumentationVersion ... versions) {
			Preconditions.checkState(versions != null);
			return versions(asList(versions));
		}

		public DocumentationBuilder versions(Iterable<DocumentationVersion> versions) {
			return addOperation(documentation -> versions.forEach(documentation::addVersion));
		}

		@Override
		protected Documentation createObject() {
			return new Documentation();
		}
	}
}
