package com.buttongames.butterfly.hibernate.dao;

import com.buttongames.butterfly.hibernate.PersistenceOperation;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

/**
 * The abstract base class for the DAOs, to handle opening/closing sessions and
 * transactions, things like that.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Repository
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public abstract class AbstractHibernateDao<T extends Serializable> {

    private Class<T> clazz;

    protected final SessionFactory sessionFactory;
    protected Session currentSession;
    protected Transaction currentTransaction;

    @Autowired
    public AbstractHibernateDao(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public final void setClazz(final Class<T> clazzToSet) {
        this.clazz = clazzToSet;
    }

    public Session openCurrentSession() {
        this.currentSession = this.sessionFactory.openSession();
        return this.currentSession;
    }

    public Session openCurrentSessionWithTransaction() {
        this.currentSession = this.sessionFactory.openSession();
        this.currentTransaction = this.currentSession.beginTransaction();
        return currentSession;
    }

    public void closeCurrentSession() {
        this.currentSession.close();
    }

    public void closeCurrentSessionWithTransaction() {
        this.currentTransaction.commit();
        this.currentSession.close();
    }

    public T findById(final long id) {
        this.openCurrentSession();
        final T entity = this.currentSession.get(this.clazz, id);
        this.closeCurrentSession();
        return entity;
    }

    public List<T> findAll() {
        this.openCurrentSession();
        final List<T> entities = this.currentSession.createQuery("from " + this.clazz.getName()).list();
        this.closeCurrentSession();
        return entities;
    }

    public void create(final T... entity) {
        this.performMutation(x -> this.currentSession.saveOrUpdate(x), entity);
    }

    public T[] update(final T... entity) {
        this.performMutation(x -> this.currentSession.saveOrUpdate(x), entity);
        return entity;
    }

    public void delete(final T... entity) {
        this.performMutation(x -> this.currentSession.delete(x), entity);
    }

    public void deleteById(final long entityId) {
        final T entity = this.findById(entityId);
        this.delete(entity);
    }

    public void performMutation(final PersistenceOperation operation, final T... entities) {
        if (entities != null) {
            try {
                this.openCurrentSessionWithTransaction();

                for (int i = 0; i < entities.length; i++) {
                    operation.call(entities[i]);
                }

                this.closeCurrentSessionWithTransaction();
            } catch (Exception e) {
                this.currentTransaction.rollback();
            }
        }
    }
}