package com.buttongames.butterfly.spring.configuration;

import com.buttongames.butterfly.hibernate.dao.impl.ButterflyPcbDao;
import com.buttongames.butterfly.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterfly.util.PathUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.nio.file.Paths;
import java.util.Properties;

/**
 * Spring configuration for the Hibernate beans.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@ComponentScan({"com.buttongames.butterfly.spring.configuration"})
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

    @Bean
    public LocalSessionFactoryBean sessionFactory(DriverManagerDataSource dataSource) {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", this.hbm2ddl);
        hibernateProperties.setProperty("hibernate.dialect", this.dialect);
        hibernateProperties.setProperty("hibernate.show_sql", this.showSql);

        final LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan("com.buttongames.butterfly.model");
        sessionFactory.setHibernateProperties(hibernateProperties);

        return sessionFactory;
    }

    @Bean
    public DriverManagerDataSource dataSource(PathUtils pathUtils) {
        final DriverManagerDataSource source = new DriverManagerDataSource();
        source.setDriverClassName(this.driverClassName);
        source.setUsername(this.username);
        source.setPassword(this.password);

        // locate the database in the user directory, and replace backslashes with forward slashes so it works on
        // Windows correctly, per sqlite-jdbc's spec
        source.setUrl(String.format("jdbc:sqlite:%s",
                Paths.get(pathUtils.externalDirectory, SQLITE_DATABASE).toString().replace('\\', '/')));

        return source;
    }

    @Bean
    public ButterflyUserDao butterflyUserDao(final SessionFactory sessionFactory) {
        return new ButterflyUserDao(sessionFactory);
    }

    @Bean
    public ButterflyPcbDao butterflyPcbDao(final SessionFactory sessionFactory) {
        return new ButterflyPcbDao(sessionFactory);
    }
}
