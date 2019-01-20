package com.buttongames.butterfly.hibernate.dao.impl.ddr16;

import com.buttongames.butterfly.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterfly.model.ddr16.UserProfile;
import com.buttongames.butterfly.model.ddr16.UserSongRecord;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * DAO for interacting with <code>UserSongRecord</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
public class UserSongRecordDao extends AbstractHibernateDao<UserSongRecord> {

    @Autowired
    public UserSongRecordDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(UserSongRecord.class);
    }

    /**
     * Finds a song record by its endtime and the user.
     * @param endtime
     * @param user
     * @return
     */
    public UserSongRecord findByEndtimeAndUser(final LocalDateTime endtime, final UserProfile user) {
        this.openCurrentSession();

        final Query<UserSongRecord> query = this.currentSession.createQuery("from UserSongRecord where user = :user and endtime = :endtime");
        query.setParameter("user", user);
        query.setParameter("endtime", endtime);
        final UserSongRecord result = query.uniqueResult();

        this.closeCurrentSession();
        return result;
    }
}
