package com.buttongames.butterflydao.spring.configuration;

import com.buttongames.butterflydao.hibernate.dao.impl.CardDao;
import com.buttongames.butterflydao.hibernate.dao.impl.MachineDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.EventSaveDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GhostDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GlobalEventDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeaguePeriodDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeagueStatusDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ShopDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GameplayEventLogDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.PcbEventLogDao;
import com.buttongames.butterflydao.hibernate.dao.impl.UserPhasesDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.UserSongRecordDao;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Properties;

/**
 * Spring configuration for the Hibernate beans.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@ComponentScan({"com.buttongames.butterflydao.spring.configuration"})
@PropertySource("classpath:hibernate.properties")
@EnableTransactionManagement
public class HibernateConfiguration {

    /** The name of the sqlite database file */
    private static final String SQLITE_DATABASE = "butterfly.sqlite";

    @Value("${jdbc.driverClassName}")
    private String driverClassName;

    @Value("${jdbc.username}")
    private String username;

    @Value("${jdbc.password}")
    private String password;

    @Value("${hibernate.hbm2ddl.auto}")
    private String hbm2ddl;

    @Value("${hibernate.dialect}")
    private String dialect;

    @Value("${hibernate.show_sql}")
    private String showSql;

    @Value("${hibernate.connection.pool_size}")
    private String connectionLimit;

    @Value("${hibernate.connection.url}")
    private String connectionUrl;

    @Bean
    public LocalSessionFactoryBean sessionFactory(final DriverManagerDataSource dataSource) {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", this.hbm2ddl);
        hibernateProperties.setProperty("hibernate.dialect", this.dialect);
        hibernateProperties.setProperty("hibernate.show_sql", this.showSql);
        hibernateProperties.setProperty("hibernate.connection.pool_size", this.connectionLimit);

        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.buttongames.butterflymodel.model");
        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    @Bean
    public DriverManagerDataSource dataSource() {
        final DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(this.driverClassName);
        source.setUsername(this.username);
        source.setPassword(this.password);
        source.setUrl(this.connectionUrl.replace('\\', '/'));

        return source;
    }

    @Bean
    public HibernateTransactionManager hibernateTransactionManager(final SessionFactory sessionFactory){
        return new HibernateTransactionManager(sessionFactory);
    }

    @Bean
    public ButterflyUserDao butterflyUserDao(final SessionFactory sessionFactory) {
        return new ButterflyUserDao(sessionFactory);
    }

    @Bean
    public MachineDao machineDao(final SessionFactory sessionFactory) {
        return new MachineDao(sessionFactory);
    }

    @Bean
    public UserPhasesDao userPhasesDao(final SessionFactory sessionFactory) {
        return new UserPhasesDao(sessionFactory);
    }

    @Bean
    public GameplayEventLogDao gameplayEventLogDao(final SessionFactory sessionFactory) {
        return new GameplayEventLogDao(sessionFactory);
    }

    @Bean
    public PcbEventLogDao pcbEventLogDao(final SessionFactory sessionFactory) {
        return new PcbEventLogDao(sessionFactory);
    }

    @Bean
    public ShopDao shopDao(final SessionFactory sessionFactory) {
        return new ShopDao(sessionFactory);
    }

    @Bean
    public CardDao cardDao(final SessionFactory sessionFactory) {
        return new CardDao(sessionFactory);
    }

    @Bean
    public ProfileDao userProfileDao(final SessionFactory sessionFactory) {
        return new ProfileDao(sessionFactory);
    }

    @Bean
    public UserSongRecordDao userSongRecordDao(final SessionFactory sessionFactory) {
        return new UserSongRecordDao(sessionFactory);
    }

    @Bean
    public GhostDataDao ghostDataDao(final SessionFactory sessionFactory) {
        return new GhostDataDao(sessionFactory);
    }

    @Bean
    public EventSaveDataDao eventSaveDataDao(final SessionFactory sessionFactory) {
        return new EventSaveDataDao(sessionFactory);
    }

    @Bean
    public GlobalEventDao globalEventDao(final SessionFactory sessionFactory) {
        return new GlobalEventDao(sessionFactory);
    }

    @Bean
    public GoldenLeaguePeriodDao goldenLeaguePeriodDao(final SessionFactory sessionFactory) {
        return new GoldenLeaguePeriodDao(sessionFactory);
    }

    @Bean
    public GoldenLeagueStatusDao goldenLeagueStatusDao(final SessionFactory sessionFactory) {
        return new GoldenLeagueStatusDao(sessionFactory);
    }
}
