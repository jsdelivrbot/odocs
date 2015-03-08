package com.pchudzik.docs.model;

import com.google.common.collect.Lists;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

/**
 * Created by pawel on 08.03.15.
 */
@Entity
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Workspace extends NameAware {
	@OrderBy("order_index")
	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "workspace", nullable = false)
	private List<Documentation> documentations = Lists.newLinkedList();

	@Transient
	private MovableHelper<Documentation> documentationMove = new MovableHelper<>(documentations);

	public Workspace(String name) {
		this.name = name;
	}

	private void updateDocumentationIndex() {
		for (int i = 0; i < documentations.size(); i++) {
			final Documentation documentation = documentations.get(i);
			documentation.orderIndex = i;
		}
	}

	@PostLoad void initialize() {
		documentationMove = new MovableHelper<>(documentations);
	}

	public void addDocumentation(Documentation documentation) {
		documentations.add(documentation);
		documentation.orderIndex = documentations.size() - 1;
	}

	public void moveDocumentationUp(Documentation documentation) {
		documentationMove.moveUp(documentation);
		updateDocumentationIndex();
	}

	public void moveDocumentationDown(Documentation documentation) {
		documentationMove.moveDown(documentation);
		updateDocumentationIndex();
	}
}
