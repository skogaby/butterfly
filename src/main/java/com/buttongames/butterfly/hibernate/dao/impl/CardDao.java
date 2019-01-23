package com.buttongames.butterfly.hibernate.dao.impl;

import com.buttongames.butterfly.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterfly.model.Card;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for interacting with <code>Card</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class CardDao extends AbstractHibernateDao<Card> {

    @Autowired
    public CardDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(Card.class);
    }

    /**
     * Finds a card by its NFC ID.
     * @param nfcId The NFC ID to query for.
     * @return The matching Card, or null if none are found.
     */
    public Card findByNfcId(final String nfcId) {
        final Query<Card> query = this.getCurrentSession().createQuery("from Card where nfc_id = :nfcId");
        query.setParameter("nfcId", nfcId);

        return query.uniqueResult();
    }

    /**
     * Finds a card by its ref ID.
     * @param refId The ref ID to query for.
     * @return The matching Card, or null if none are found.
     */
    public Card findByRefId(final String refId) {
        final Query<Card> query = this.getCurrentSession().createQuery("from Card where ref_id = :refId");
        query.setParameter("refId", refId);

        return query.uniqueResult();
    }
}
