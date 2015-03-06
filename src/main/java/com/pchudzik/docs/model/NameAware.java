package com.pchudzik.docs.model;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.MappedSuperclass;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by pawel on 08.02.15.
 */
@MappedSuperclass
abstract class NameAware extends BaseEntity {
	@Getter String name;

	public void updateName(String name) {
		Preconditions.checkArgument(StringUtils.isNoneBlank(name));
		this.name = name;
	}

	protected abstract static class NameAwareBuilder<B extends NameAwareBuilder, T extends NameAware> extends AbstractEntityBuilder<B, T> {
		protected NameAwareBuilder() {
			addValidator(nameAware -> checkState(isNotBlank(nameAware.name), "Name must not be blank"));
		}

		public B name(String name) {
			return addOperation(nameAware -> nameAware.name = name);
		}
	}
}
