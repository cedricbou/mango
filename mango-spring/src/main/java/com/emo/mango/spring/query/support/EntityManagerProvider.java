package com.emo.mango.spring.query.support;

import javax.persistence.EntityManager;

public interface EntityManagerProvider {

	public EntityManager get();
}
