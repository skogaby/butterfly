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
import java.util.List;

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
     * @param endtime The endtime of the record
     * @param user The user of the record
     * @return A matching record, or null
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

    /**
     * Finds the top score for a given song and difficulty
     * @param mcode The mcode of the song to search for
     * @param difficulty The difficulty to search for
     * @return A matching record, or null
     */
    public UserSongRecord findTopScoreForSongDifficulty(int mcode, int difficulty) {
        this.openCurrentSession();

        final Query<UserSongRecord> query = this.currentSession.createQuery(
                "from UserSongRecord r where r.songId = :songId and r.noteType = :noteType order by r.score desc")
                .setMaxResults(1);
        query.setParameter("songId", mcode);
        query.setParameter("noteType", difficulty);
        final UserSongRecord result = query.uniqueResult();

        this.closeCurrentSession();
        return result;
    }

    /**
     * Finds all song records for a user.
     * @param user The user of the records
     * @return A list of matching records
     */
    public List<UserSongRecord> findByUser(final UserProfile user) {
        this.openCurrentSession();

        final Query<UserSongRecord> query = this.currentSession.createQuery("from UserSongRecord where user = :user");
        query.setParameter("user", user);
        final List<UserSongRecord> result = query.getResultList();

        this.closeCurrentSession();
        return result;
    }
}
