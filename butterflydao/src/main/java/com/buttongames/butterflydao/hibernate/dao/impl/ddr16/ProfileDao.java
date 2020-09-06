package com.buttongames.butterflydao.hibernate.dao.impl.ddr16;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ButterflyUser;
import com.buttongames.butterflymodel.model.ddr16.UserProfile;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;

/**
 * DAO for interacting with <code>UserProfile</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class ProfileDao extends AbstractHibernateDao<UserProfile> {

    @Autowired
    public ProfileDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(UserProfile.class);
    }

    /**
     * Find a DDR profile by the owner.
     * @param user The owning account
     * @return The profile for the given user
     */
    public UserProfile findByUser(final ButterflyUser user) {
        final Query<UserProfile> query = this.getCurrentSession().createQuery("from UserProfile where user = :user");
        query.setParameter("user", user);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds a DDR profile by the dancer code.
     * @param dancerCode The dancer code for the profile
     * @return The profile for the given dancer code
     */
    public UserProfile findByDancerCode(final int dancerCode) {
        final Query<UserProfile> query = this.getCurrentSession().createQuery("from UserProfile where dancer_code = :dancerCode");
        query.setParameter("dancerCode", dancerCode);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
