package com.buttongames.butterflydao.hibernate.dao.impl.ddr16;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ddr16.EventSaveData;
import com.buttongames.butterflymodel.model.ddr16.UserProfile;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * DAO for interacting with <code>EventSaveData</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class EventSaveDataDao extends AbstractHibernateDao<EventSaveData> {

    @Autowired
    public EventSaveDataDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(EventSaveData.class);
    }

    /**
     * Finds all event progress for a user.
     * @param user The user for the events
     * @return A list of matching records
     */
    public List<EventSaveData> findByUser(final UserProfile user) {
        final Query<EventSaveData> query = this.getCurrentSession().createQuery("from EventSaveData where user = :user");
        query.setParameter("user", user);

        return query.getResultList();
    }

    /**
     * Finds an entry for an event for a user based on its event id.
     * @param user The user for the events
     * @param eventId The id of the event
     * @return The matching records
     */
    public EventSaveData findByUserAndEventId(final UserProfile user, final int eventId) {
        final Query<EventSaveData> query = this.getCurrentSession().createQuery(
                "from EventSaveData where user = :user and eventId = :eventId")
                .setMaxResults(1);
        query.setParameter("user", user);
        query.setParameter("eventId", eventId);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
