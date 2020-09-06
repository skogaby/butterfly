package com.buttongames.butterflydao.hibernate.dao.impl.ddr16;

import com.buttongames.butterflydao.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeaguePeriod;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeagueStatus;
import com.buttongames.butterflymodel.model.ddr16.UserProfile;
import io.leangen.graphql.annotations.GraphQLArgument;
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
 * DAO for interacting with <code>GoldenLeagueStatus</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
@Transactional
public class GoldenLeagueStatusDao extends AbstractHibernateDao<GoldenLeagueStatus> {

    @Autowired
    public GoldenLeagueStatusDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(GoldenLeagueStatus.class);
    }

    /**
     * Finds Golden League statuses by period.
     * @param period The Golden League period of the record
     * @return A list of matching records
     */
    public List<GoldenLeagueStatus> findByPeriod(@GraphQLArgument(name = "period") final GoldenLeaguePeriod period) {
        final Query<GoldenLeagueStatus> query = this.getCurrentSession().createQuery("from GoldenLeagueStatus where leaguePeriod = :leaguePeriod");
        query.setParameter("leaguePeriod", period);

        return query.getResultList();
    }

    /**
     * Finds Golden League statuses by period and class.
     * @param period The Golden League period of the record
     * @param goldenLeagueClass The class to search for statuses for (1 = bronze, 2 = silver, 3 = gold)
     * @return A matching re
     */
    public List<GoldenLeagueStatus> findByPeriodAndClass(final GoldenLeaguePeriod period,
                                                         final int goldenLeagueClass) {
        final Query<GoldenLeagueStatus> query = this.getCurrentSession().createQuery("from GoldenLeagueStatus where leaguePeriod = :leaguePeriod and goldenLeagueClass = :goldenLeagueClass");
        query.setParameter("leaguePeriod", period);
        query.setParameter("goldenLeagueClass", goldenLeagueClass);

        return query.getResultList();
    }

    /**
     * Finds a Golden League status by its user and period.
     * @param user The user of the record
     * @param period The Golden League period of the record
     * @return A matching record, or null
     */
    public GoldenLeagueStatus findByUserAndPeriod(final UserProfile user,
                                                  final GoldenLeaguePeriod period) {
        final Query<GoldenLeagueStatus> query = this.getCurrentSession()
                .createQuery("from GoldenLeagueStatus where user = :user and leaguePeriod = :leaguePeriod")
                .setMaxResults(1);
        query.setParameter("user", user);
        query.setParameter("leaguePeriod", period);

        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }
}
