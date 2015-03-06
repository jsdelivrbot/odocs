package com.pchudzik.docs.model;

import com.pchudzik.docs.utils.builder.ObjectBuilder;
import lombok.Getter;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Created by pawel on 08.02.15.
 */
@MappedSuperclass
public abstract class BaseEntity {
	@Getter
	@Id
	protected String id = UUID.randomUUID().toString();

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}

		if (other == null || getClass() != other.getClass()) {
			return false;
		}

		BaseEntity that = (BaseEntity) other;
		return Objects.equals(this.id, that.id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this, ToStringStyle.DEFAULT_STYLE).toString();
	}

	public static abstract class AbstractEntityBuilder<B extends AbstractEntityBuilder, T extends BaseEntity> extends ObjectBuilder<B, T> {
		protected AbstractEntityBuilder() {
			addValidator(entity -> checkState(isNotBlank(entity.id), "id must not be null"));
		}

		public B id(String id) {
			return addOperation(entity -> entity.id = id);
		}
	}
}
