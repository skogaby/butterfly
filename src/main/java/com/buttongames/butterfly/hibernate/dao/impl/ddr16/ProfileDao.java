package com.buttongames.butterfly.hibernate.dao.impl.ddr16;

import com.buttongames.butterfly.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterfly.model.ButterflyUser;
import com.buttongames.butterfly.model.ddr16.UserProfile;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * DAO for interacting with <code>UserProfile</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
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
        this.openCurrentSession();

        final Query<UserProfile> query = this.currentSession.createQuery("from UserProfile where user = :user");
        query.setParameter("user", user);
        final UserProfile result = query.uniqueResult();

        this.closeCurrentSession();
        return result;
    }

    /**
     * Finds a DDR profile by the dancer code.
     * @param dancerCode The dancer code for the profile
     * @return The profile for the given dancer code
     */
    public UserProfile findByDancerCode(final int dancerCode) {
        this.openCurrentSession();

        final Query<UserProfile> query = this.currentSession.createQuery("from UserProfile where dancer_code = :dancerCode");
        query.setParameter("dancerCode", dancerCode);
        final UserProfile result = query.uniqueResult();

        this.closeCurrentSession();
        return result;
    }
}
