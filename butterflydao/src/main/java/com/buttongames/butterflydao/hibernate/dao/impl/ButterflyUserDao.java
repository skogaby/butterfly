package com.buttongames.butterflydao.hibernate.dao.impl;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ButterflyUser;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for interacting with <code>ButterflyUser</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class ButterflyUserDao extends AbstractHibernateDao<ButterflyUser> {

    @Autowired
    public ButterflyUserDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(ButterflyUser.class);
    }
}
