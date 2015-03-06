package com.pchudzik.docs.menu;

import com.google.common.collect.Lists;
import com.pchudzik.docs.model.Documentation;
import lombok.Getter;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

/**
 * Created by pawel on 28.02.15.
 */
@Getter
class MenuItemDto {
	private String id;
	private String name;
	private List<MenuVersionItemDto> versions = Lists.newLinkedList();

	public MenuItemDto(Documentation documentation) {
		id = documentation.getId();
		name = documentation.getName();
		versions = newArrayList(documentation.getVersions()).stream()
				.map(MenuVersionItemDto::new)
				.collect(toList());
	}
}
