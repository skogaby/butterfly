package com.buttongames.butterfly.hibernate.dao.impl.ddr16;

import com.buttongames.butterfly.hibernate.dao.AbstractHibernateDao;
import com.buttongames.butterfly.model.ddr16.GameplayEventLog;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * DAO for interacting with <code>GameplayEventLog</code> objects in the database.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
@Repository
public class GameplayEventLogDao extends AbstractHibernateDao<GameplayEventLog> {

    @Autowired
    public GameplayEventLogDao(final SessionFactory sessionFactory) {
        super(sessionFactory);
        setClazz(GameplayEventLog.class);
    }
}
