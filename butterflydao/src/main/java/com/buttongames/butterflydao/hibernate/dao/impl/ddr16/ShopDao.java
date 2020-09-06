package com.buttongames.butterflydao.hibernate.dao.impl.ddr16;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ddr16.Shop;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

/**
 * DAO for interacting with <code>Shop</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class ShopDao extends AbstractHibernateDao<Shop> {

    @Autowired
    public ShopDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(Shop.class);
    }

    /**
     * Finds a shop by its PCBID.
     * @param pcbId The PCBID to query for.
     * @return The matching Shop, or null if none are found.
     */
    public Shop findByPcbId(final String pcbId) {
        final Query<Shop> query = this.getCurrentSession().createQuery("from Shop where pcb_id = :pcbid");
        query.setParameter("pcbid", pcbId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
