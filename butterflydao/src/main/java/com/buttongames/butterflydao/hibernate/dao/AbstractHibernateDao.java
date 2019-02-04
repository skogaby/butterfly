package com.buttongames.butterflydao.hibernate.dao;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.List;

/**
 * The abstract base class for the DAOs, to handle opening/closing sessions and
 * transactions, things like that.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Repository
@Transactional
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Component
public abstract class AbstractHibernateDao<T extends Serializable> {

    private Class<T> clazz;
    protected final SessionFactory sessionFactory;

    @Autowired
    public AbstractHibernateDao(final SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void setClazz(final Class<T> clazzToSet) {
        clazz = clazzToSet;
    }

    public T findById(final long id) {
        return getCurrentSession().get(clazz, id);
    }

    public List<T> findAll(){
        return getCurrentSession().createQuery("from " + clazz.getName()).list();
    }

    public void create(final T entity){
        getCurrentSession().persist(entity);
    }

    public T update(final T entity){
        return (T) getCurrentSession().merge(entity);
    }

    public void delete(final T entity) {
        getCurrentSession().delete(entity);
    }

    public void deleteById(final long id) {
        final T entity = findById(id);
        delete(entity);
    }

    protected final Session getCurrentSession() {
        return sessionFactory.getCurrentSession();
    }
}