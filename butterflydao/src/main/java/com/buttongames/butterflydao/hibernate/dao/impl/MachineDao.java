package com.buttongames.butterflydao.hibernate.dao.impl;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.Machine;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for interacting with <code>Machine</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class MachineDao extends AbstractHibernateDao<Machine> {

    @Autowired
    public MachineDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(Machine.class);
    }

    /**
     * Finds a machine by its PCBID.
     * @param pcbId The PCBID to query for.
     * @return The matching Machine, or null if none are found.
     */
    public Machine findByPcbId(final String pcbId) {
        final Query<Machine> query = this.getCurrentSession().createQuery("from Machine where pcbid = :pcbid");
        query.setParameter("pcbid", pcbId);

        return query.uniqueResult();
    }
}
