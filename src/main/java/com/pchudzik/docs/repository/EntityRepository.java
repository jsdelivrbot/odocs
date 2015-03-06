package com.pchudzik.docs.repository;

import com.pchudzik.docs.model.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * Created by pawel on 08.02.15.
 */
public abstract class EntityRepository<T extends BaseEntity, ID extends Serializable> {
	@PersistenceContext
	protected EntityManager entityManager;
	protected final Class<T> persistentClass;

	@SuppressWarnings("unchecked")
	public EntityRepository() {
		this.persistentClass = (Class<T>) ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}

	public T persist(T entity) {
		entityManager.persist(entity);
		return entity;
	}

	public <S extends T> void persist(Iterable<S> entities) {
		entities.forEach(this::persist);
	}

	public void delete(T entity) {
		entityManager.remove(entity);
	}

	public <S extends T> void delete(Iterable<S> entities) {
		entities.forEach(this::delete);
	}

	public void delete(ID id) {
		delete(findOne(id));
	}

	public T findOne(ID id) {
		return entityManager.find(persistentClass, id);
	}

	public List<T> findAll() {
		return entityManager
				.createQuery("from " + persistentClass.getSimpleName(), persistentClass)
				.getResultList();
	}
}
