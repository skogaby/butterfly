package com.buttongames.butterfly.hibernate;

/**
 * Interface to encapsulate a persistence operation (save, update, etc.) in Hibernate.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@FunctionalInterface
public interface PersistenceOperation {

    void call(Object entity);
}
