package com.buttongames.butterfly.spring.configuration;

import com.buttongames.butterfly.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterfly.hibernate.dao.impl.CardDao;
import com.buttongames.butterfly.hibernate.dao.impl.ddr16.GameplayEventLogDao;
import com.buttongames.butterfly.hibernate.dao.impl.ddr16.PcbEventLogDao;
import com.buttongames.butterfly.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterfly.hibernate.dao.impl.ddr16.ShopDao;
import com.buttongames.butterfly.hibernate.dao.impl.MachineDao;
import com.buttongames.butterfly.hibernate.dao.impl.UserPhasesDao;
import com.buttongames.butterfly.http.ButterflyHttpServer;
import com.buttongames.butterfly.http.handlers.impl.CardManageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.EacoinRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.EventLogRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.FacilityRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.MessageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PackageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PcbEventRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PcbTrackerRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PlayerDataRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.ServicesRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.SystemRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.TaxRequestHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * Bean config class for <code>com.buttongames.butterfly.http</code> package.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Configuration
@ComponentScan({"com.buttongames.butterfly.spring.configuration"})
@PropertySource("classpath:butterfly.properties")
public class HttpConfiguration {

    @Bean
    public ButterflyHttpServer butterflyHttpServer(final ServicesRequestHandler servicesRequestHandler,
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
                                                   final EacoinRequestHandler eacoinRequestHandler,
                                                   final MachineDao machineDao,
                                                   final ButterflyUserDao userDao) {
        return new ButterflyHttpServer(servicesRequestHandler, pcbEventRequestHandler, pcbTrackerRequestHandler,
                messageRequestHandler, facilityRequestHandler, packageRequestHandler, eventLogRequestHandler,
                taxRequestHandler, playerDataRequestHandler, cardManageRequestHandler, systemRequestHandler,
                eacoinRequestHandler, machineDao, userDao);
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
                                                             final ProfileDao profileDao) {
        return new PlayerDataRequestHandler(userDao, cardDao, profileDao);
    }

    @Bean
    public TaxRequestHandler taxRequestHandler(final MachineDao machineDao, final UserPhasesDao userPhasesDao) {
        return new TaxRequestHandler(machineDao, userPhasesDao);
    }

    @Bean
    public CardManageRequestHandler cardManageRequestHandler(final CardDao cardDao, final ButterflyUserDao userDao) {
        return new CardManageRequestHandler(cardDao, userDao);
    }

    @Bean
    public SystemRequestHandler systemRequestHandler() {
        return new SystemRequestHandler();
    }

    @Bean
    public EacoinRequestHandler eacoinRequestHandler() {
        return new EacoinRequestHandler();
    }
}
