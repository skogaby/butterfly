package com.buttongames.butterflyserver.spring.configuration;

import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GhostDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GlobalEventDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeaguePeriodDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeagueStatusDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.UserSongRecordDao;
import com.buttongames.butterflyserver.events.ddr16.GoldenLeagueDataAggregator;
import com.buttongames.butterflyserver.graphql.types.ButterflyQuery;
import com.buttongames.butterflyserver.graphql.types.ddr16.Ddr16Query;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Bean config class for the top-level application.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@EnableScheduling
@ComponentScan({"com.buttongames.butterflyserver.spring.configuration"})
public class ApplicationConfiguration {

    @Bean
    public GoldenLeagueDataAggregator goldenLeagueDataAggregator(final GoldenLeagueStatusDao goldenLeagueStatusDao,
                                                                 final GoldenLeaguePeriodDao goldenLeaguePeriodDao) {
        return new GoldenLeagueDataAggregator(goldenLeagueStatusDao, goldenLeaguePeriodDao);
    }

    @Bean
    public ButterflyQuery butterflyQuery(final ButterflyUserDao butterflyUserDao) {
        return new ButterflyQuery(butterflyUserDao);
    }

    @Bean
    public Ddr16Query ddr16Query(final ButterflyUserDao butterflyUserDao,
                                 final ProfileDao profileDao,
                                 final UserSongRecordDao userSongRecordDao,
                                 final GhostDataDao ghostDataDao,
                                 final GlobalEventDao globalEventDao,
                                 final GoldenLeaguePeriodDao goldenLeaguePeriodDao,
                                 final GoldenLeagueStatusDao goldenLeagueStatusDao) {
        return new Ddr16Query(butterflyUserDao, profileDao, userSongRecordDao, ghostDataDao, globalEventDao, goldenLeaguePeriodDao, goldenLeagueStatusDao);
    }
}
