package com.buttongames.butterfly.spring.configuration;

import com.buttongames.butterfly.hibernate.dao.impl.Ddr16GameplayEventLogDao;
import com.buttongames.butterfly.hibernate.dao.impl.Ddr16PcbEventLogDao;
import com.buttongames.butterfly.hibernate.dao.impl.Ddr16ShopDao;
import com.buttongames.butterfly.hibernate.dao.impl.MachineDao;
import com.buttongames.butterfly.http.ButterflyHttpServer;
import com.buttongames.butterfly.http.handlers.impl.EventLogRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.FacilityRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.MessageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PackageRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PcbEventRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.PcbTrackerRequestHandler;
import com.buttongames.butterfly.http.handlers.impl.ServicesRequestHandler;
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
                                                   final MachineDao machineDao) {
        return new ButterflyHttpServer(servicesRequestHandler, pcbEventRequestHandler, pcbTrackerRequestHandler,
                messageRequestHandler, facilityRequestHandler, packageRequestHandler, eventLogRequestHandler,
                taxRequestHandler, machineDao);
    }

    @Bean
    public EventLogRequestHandler eventLogRequestHandler(final Ddr16GameplayEventLogDao gameplayEventLogDao) {
        return new EventLogRequestHandler(gameplayEventLogDao);
    }

    @Bean
    public FacilityRequestHandler facilityRequestHandler(final Ddr16ShopDao shopDao, final MachineDao machineDao) {
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
    public PcbEventRequestHandler pcbEventRequestHandler(final Ddr16PcbEventLogDao pcbEventLogDao) {
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
    public TaxRequestHandler taxRequestHandler() {
        return new TaxRequestHandler();
    }
}
