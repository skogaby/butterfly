package com.buttongames.butterflydao.hibernate.dao.impl.ddr16;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ddr16.UserProfile;
import com.buttongames.butterflymodel.model.ddr16.UserSongRecord;
import org.hibernate.SessionFactory;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DAO for interacting with <code>UserSongRecord</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class UserSongRecordDao extends AbstractHibernateDao<UserSongRecord> {

    /**
     * The base of the String we use to query for top scores for various levels.
     */
    private static final String BASE_TOP_SCORE_QUERY = "SELECT * FROM ddr_16_user_song_records r ";

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
        final Query<UserSongRecord> query = this.getCurrentSession().createQuery("from UserSongRecord where user = :user and endtime = :endtime");
        query.setParameter("user", user);
        query.setParameter("endtime", endtime);
        query.setTimeout(20);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    /**
     * Finds the global top scores for all songs for the given difficulty.
     * @param difficulty The difficulty to query the top songs for
     * @return A list of the top record for every song for the given difficulty
     */
    public List<UserSongRecord> findTopScoresForDifficulty(final int difficulty) {
        final NativeQuery<UserSongRecord> query = this.getCurrentSession().createSQLQuery(BASE_TOP_SCORE_QUERY +
                "INNER JOIN (" +
                "    SELECT song_id, MAX(score) score, note_type " +
                "    FROM ddr_16_user_song_records " +
                "    WHERE note_type = :difficulty " +
                "    GROUP BY song_id, note_type " +
                ") s ON r.song_id = s.song_id AND r.score = s.score and r.note_type = s.note_type");
        query.addEntity(UserSongRecord.class);
        query.setParameter("difficulty", difficulty);
        query.setTimeout(20);

        return query.getResultList();
    }

    /**
     * Finds the top scores for all songs for the given difficulty and user
     * @param user The user to query the top scores for
     * @param difficulty The difficulty to query the top songs for
     * @return A list of the top record for every song for the given difficulty
     */
    public List<UserSongRecord> findTopScoresForDifficultyByUser(final UserProfile user, final int difficulty) {
        final NativeQuery<UserSongRecord> query = this.getCurrentSession().createSQLQuery(BASE_TOP_SCORE_QUERY +
                "INNER JOIN (" +
                "    SELECT song_id, MAX(score) score, MAX(clear_kind) clear_kind, note_type, user_id " +
                "    FROM ddr_16_user_song_records " +
                "    WHERE note_type = :difficulty AND user_id = :user " +
                "    GROUP BY song_id, note_type, user_id " +
                ") s ON r.song_id = s.song_id AND r.score = s.score AND r.note_type = s.note_type AND r.user_id = s.user_id");
        query.addEntity(UserSongRecord.class);
        query.setParameter("difficulty", difficulty);
        query.setParameter("user", user.getId());
        query.setTimeout(20);

        return query.getResultList();
    }

    /**
     * Finds the top scores for all songs for the given difficulty and machine
     * @param pcbid The pcbid to query the top scores for
     * @param difficulty The difficulty to query the top songs for
     * @return A list of the top record for every song for the given difficulty
     */
    public List<UserSongRecord> findTopScoresForDifficultyByMachine(final String pcbid, final int difficulty) {
        final NativeQuery<UserSongRecord> query = this.getCurrentSession().createSQLQuery(BASE_TOP_SCORE_QUERY +
                "INNER JOIN (" +
                "    SELECT song_id, MAX(score) score, note_type, machine_pcbid " +
                "    FROM ddr_16_user_song_records " +
                "    WHERE note_type = :difficulty AND machine_pcbid = :pcbid " +
                "    GROUP BY song_id, note_type, machine_pcbid " +
                ") s ON r.song_id = s.song_id AND r.score = s.score AND r.note_type = s.note_type AND r.machine_pcbid = s.machine_pcbid");
        query.addEntity(UserSongRecord.class);
        query.setParameter("difficulty", difficulty);
        query.setParameter("pcbid", pcbid);
        query.setTimeout(20);

        return query.getResultList();
    }

    /**
     * Finds the top scores for all songs for the given difficulty and shop area
     * @param shopArea The shop area to query the top scores for
     * @param difficulty The difficulty to query the top songs for
     * @return A list of the top record for every song for the given difficulty
     */
    public List<UserSongRecord> findTopScoresForDifficultyByShopArea(final String shopArea, final int difficulty) {
        final NativeQuery<UserSongRecord> query = this.getCurrentSession().createSQLQuery(BASE_TOP_SCORE_QUERY +
                "INNER JOIN (" +
                "    SELECT song_id, MAX(score) score, note_type, shop_area " +
                "    FROM ddr_16_user_song_records " +
                "    WHERE note_type = :difficulty AND shop_area = :shopArea " +
                "    GROUP BY song_id, note_type, shop_area " +
                ") s ON r.song_id = s.song_id AND r.score = s.score AND r.note_type = s.note_type AND r.shop_area = s.shop_area");
        query.addEntity(UserSongRecord.class);
        query.setParameter("difficulty", difficulty);
        query.setParameter("shopArea", shopArea);
        query.setTimeout(20);

        return query.getResultList();
    }
}
