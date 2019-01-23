package com.buttongames.butterfly.hibernate.dao.impl;

import com.buttongames.butterfly.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterfly.model.ButterflyUser;
import com.buttongames.butterfly.model.UserPhases;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * DAO for interacting with <code>UserPhases</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class UserPhasesDao extends AbstractHibernateDao<UserPhases> {

    @Autowired
    public UserPhasesDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(UserPhases.class);
    }

    /**
     * Get the phases for a particular user.
     * @param user
     * @return
     */
    public UserPhases getPhasesForUser(final ButterflyUser user) {
        final Query<UserPhases> query = this.getCurrentSession().createQuery("from UserPhases p where p.user = :user");
        query.setParameter("user", user);

        return query.uniqueResult();
    }
}
