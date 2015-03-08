package com.pchudzik.docs.model;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Created by pawel on 08.02.15.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Documentation extends NameAware {
	Integer orderIndex = 0;

	@OrderColumn
	@JoinColumn(name = "documentation")
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	List<DocumentationVersion> versions = new ArrayList<>();

	@Transient
	private MovableHelper<DocumentationVersion> versionMoveHelper = new MovableHelper<>(versions);

	@PostLoad private void initialize() {
		versionMoveHelper = new MovableHelper<>(versions);
	}

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
		versionMoveHelper.moveUp(version);
	}

	public void moveVersionDown(DocumentationVersion version) {
		versionMoveHelper.moveDown(version);
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
