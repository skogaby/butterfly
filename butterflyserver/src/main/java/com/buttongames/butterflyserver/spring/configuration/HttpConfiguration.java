package com.buttongames.butterflyserver.spring.configuration;

import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.CardDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.EventSaveDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GameplayEventLogDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GhostDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GlobalEventDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeaguePeriodDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeagueStatusDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.PcbEventLogDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ShopDao;
import com.buttongames.butterflydao.hibernate.dao.impl.MachineDao;
import com.buttongames.butterflydao.hibernate.dao.impl.UserPhasesDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.UserSongRecordDao;
import com.buttongames.butterflyserver.graphql.types.ButterflyQuery;
import com.buttongames.butterflyserver.graphql.types.ddr16.Ddr16Query;
import com.buttongames.butterflyserver.http.ButterflyHttpServer;
import com.buttongames.butterflyserver.http.handlers.impl.CardManageRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.EacoinRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.EventLogRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.FacilityRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.MessageRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.PackageRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.PcbEventRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.PcbTrackerRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.mdx.ddr16.BaseDdr16RequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.ServicesRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.SystemRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.mdx.ddr16.PlayerDataRequestHandler;
import com.buttongames.butterflyserver.http.handlers.impl.mdx.ddr16.TaxRequestHandler;
import com.buttongames.butterflycore.util.CardIdUtils;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Bean config class for <code>com.buttongames.butterflyserver.http</code> package.
 *
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@ComponentScan({"com.buttongames.butterflyserver.spring.configuration",
        "com.buttongames.butterflydao.spring.configuration"})
@PropertySource("classpath:butterflyserver.properties")
public class HttpConfiguration {

    @Bean
    public ButterflyHttpServer butterflyHttpServer(final BaseDdr16RequestHandler baseDdr16RequestHandler,
                                                   final MachineDao machineDao,
                                                   final ButterflyUserDao userDao,
                                                   final ButterflyQuery butterflyQuery,
                                                   final Ddr16Query ddr16Query) {
        return new ButterflyHttpServer(baseDdr16RequestHandler, machineDao, userDao, butterflyQuery, ddr16Query);
    }

    @Bean
    public BaseDdr16RequestHandler baseMdxRequestHandler(final ServicesRequestHandler servicesRequestHandler,
                                                         final PcbEventRequestHandler pcbEventRequestHandler,
                                                         final PcbTrackerRequestHandler pcbTrackerRequestHandler,
                                                         final MessageRequestHandler messageRequestHandler,
                                                         final FacilityRequestHandler facilityRequestHandler,
                                                         final PackageRequestHandler packageRequestHandler,
                                                         final EventLogRequestHandler eventLogRequestHandler,
                                                         final TaxRequestHandler taxRequestHandler,
                                                         final PlayerDataRequestHandler playerDataRequestHandler,
                                                         final CardManageRequestHandler cardManageRequestHandler,
                                                         final SystemRequestHandler systemRequestHandler,
                                                         final EacoinRequestHandler eacoinRequestHandler) {
        return new BaseDdr16RequestHandler(servicesRequestHandler, pcbEventRequestHandler, pcbTrackerRequestHandler,
                messageRequestHandler, facilityRequestHandler, packageRequestHandler, eventLogRequestHandler,
                taxRequestHandler, playerDataRequestHandler, cardManageRequestHandler, systemRequestHandler,
                eacoinRequestHandler);
    }

    @Bean
    public CardIdUtils cardIdUtils() {
        return new CardIdUtils();
    }

    @Bean
    public EventLogRequestHandler eventLogRequestHandler(final GameplayEventLogDao gameplayEventLogDao) {
        return new EventLogRequestHandler(gameplayEventLogDao);
    }

    @Bean
    public FacilityRequestHandler facilityRequestHandler(final ShopDao shopDao, final MachineDao machineDao) {
        return new FacilityRequestHandler(shopDao, machineDao);
    }

    @Bean
    public MessageRequestHandler messageRequestHandler() {
        return new MessageRequestHandler();
    }

    @Bean
    public PackageRequestHandler packageRequestHandler() {
        return new PackageRequestHandler();
    }

    @Bean
    public PcbEventRequestHandler pcbEventRequestHandler(final PcbEventLogDao pcbEventLogDao) {
        return new PcbEventRequestHandler(pcbEventLogDao);
    }

    @Bean
    public PcbTrackerRequestHandler pcbTrackerRequestHandler() {
        return new PcbTrackerRequestHandler();
    }

    @Bean
    public ServicesRequestHandler servicesRequestHandler() {
        return new ServicesRequestHandler();
    }

    @Bean
    public PlayerDataRequestHandler playerDataRequestHandler(final ButterflyUserDao userDao, final CardDao cardDao,
                                                             final ProfileDao profileDao, final GhostDataDao ghostDataDao,
                                                             final UserSongRecordDao songRecordDao, final GlobalEventDao globalEventDao,
                                                             final EventSaveDataDao eventSaveDataDao, final GoldenLeagueStatusDao goldenLeagueStatusDao,
                                                             final GoldenLeaguePeriodDao goldenLeaguePeriodDao) {
        return new PlayerDataRequestHandler(userDao, cardDao, profileDao, ghostDataDao, songRecordDao, globalEventDao,
                eventSaveDataDao, goldenLeagueStatusDao, goldenLeaguePeriodDao);
    }

    @Bean
    public TaxRequestHandler taxRequestHandler(final MachineDao machineDao, final UserPhasesDao userPhasesDao) {
        return new TaxRequestHandler(machineDao, userPhasesDao);
    }

    @Bean
    public CardManageRequestHandler cardManageRequestHandler(final CardDao cardDao, final ButterflyUserDao userDao,
                                                             final CardIdUtils cardIdUtils, final ProfileDao ddrProfileDao) {
        return new CardManageRequestHandler(cardDao, userDao, cardIdUtils, ddrProfileDao);
    }

    @Bean
    public SystemRequestHandler systemRequestHandler(final CardIdUtils cardIdUtils) {
        return new SystemRequestHandler(cardIdUtils);
    }

    @Bean
    public EacoinRequestHandler eacoinRequestHandler(final CardDao cardDao) {
        return new EacoinRequestHandler(cardDao);
    }
}
