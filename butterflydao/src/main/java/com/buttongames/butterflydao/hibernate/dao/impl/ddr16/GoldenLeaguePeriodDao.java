package com.buttongames.butterflydao.hibernate.dao.impl.ddr16;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeaguePeriod;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;

/**
 * DAO for interacting with <code>GoldenLeaguePeriod</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class GoldenLeaguePeriodDao extends AbstractHibernateDao<GoldenLeaguePeriod> {

    @Autowired
    public GoldenLeaguePeriodDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(GoldenLeaguePeriod.class);
    }

    /**
     * Fetches the most recent Golden League period.
     * @return The most recent Golden League period
     */
    public GoldenLeaguePeriod getLastGoldenLeaguePeriod() {
        final Query<GoldenLeaguePeriod> query = this.getCurrentSession().createQuery("from GoldenLeaguePeriod p where p.startTime <= :time order by p.startTime desc")
                .setParameter("time", LocalDateTime.now())
                .setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Fetches a Golden League Period by its ID.
     * @return The matching Golden League period, otherwise null.
     */
    public GoldenLeaguePeriod getGoldenLeaguePeriodById(int id) {
        final Query<GoldenLeaguePeriod> query = this.getCurrentSession().createQuery("from GoldenLeaguePeriod p where p.id = :id")
                .setParameter("id", id)
                .setMaxResults(1);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
