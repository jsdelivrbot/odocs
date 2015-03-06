package com.pchudzik.docs.menu;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by pawel on 28.02.15.
 */
@RestController
@RequestMapping("/menu")
class MenuController {
	final MenuItemService menuItemService;

	@Autowired
	MenuController(MenuItemService menuItemService) {
		this.menuItemService = menuItemService;
	}

	@RequestMapping("/documentations")
	List<MenuItemDto> listDocumentations() {
		return menuItemService.listDocumentations();
	}
}
