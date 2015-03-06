package com.pchudzik.docs.menu;

import com.pchudzik.docs.model.DocumentationVersion;
import lombok.Getter;

/**
 * Created by pawel on 28.02.15.
 */
@Getter
class MenuVersionItemDto {
	private String name;
	private String id;

	public MenuVersionItemDto(DocumentationVersion version) {
		this.name = version.getName();
		this.id = version.getId();
	}
}
