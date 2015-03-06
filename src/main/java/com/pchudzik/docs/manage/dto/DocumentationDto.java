package com.pchudzik.docs.manage.dto;

import com.pchudzik.docs.model.Documentation;
import lombok.*;
import lombok.experimental.Builder;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by pawel on 08.02.15.
 */
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@ToString @EqualsAndHashCode
public class DocumentationDto {
	String id;
	String name;
	List<VersionDto> versions;

	public DocumentationDto(Documentation documentation) {
		this.id = documentation.getId();
		this.name = documentation.getName();
		this.versions = documentation.getVersions().stream()
				.map(VersionDto::new)
				.collect(toList());
	}

}
