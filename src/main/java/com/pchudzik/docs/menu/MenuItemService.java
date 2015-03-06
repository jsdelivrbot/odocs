package com.pchudzik.docs.menu;

import com.pchudzik.docs.repository.DocumentationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by pawel on 28.02.15.
 */
@Service
class MenuItemService {
	final DocumentationRepository documentationRepository;

	@Autowired
	MenuItemService(DocumentationRepository documentationRepository) {
		this.documentationRepository = documentationRepository;
	}

	@Transactional(readOnly = true)
	public List<MenuItemDto> listDocumentations() {
		return documentationRepository.findCompleted().stream()
				.map(MenuItemDto::new)
				.collect(toList());
	}
}
