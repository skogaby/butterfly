package com.buttongames.butterfly.hibernate.dao.impl;

import com.buttongames.butterfly.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterfly.model.Ddr16Shop;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * DAO for interacting with <code>Ddr16Shop</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
public class Ddr16ShopDao extends AbstractHibernateDao<Ddr16Shop> {

    @Autowired
    public Ddr16ShopDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(Ddr16Shop.class);
    }
}
