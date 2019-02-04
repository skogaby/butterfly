package com.buttongames.butterflyserver;

import com.buttongames.butterflyserver.http.ButterflyHttpServer;
import com.buttongames.butterflyserver.spring.configuration.ApplicationConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    /**
     * Spring context that we can load beans from manually.
     */
    private static AnnotationConfigApplicationContext applicationContext;

    /**
     * Program entrypoint.
     * @param args
     */
    public static void main(String[] args) {
        // get the Spring context and start the server
        applicationContext = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        final ButterflyHttpServer httpServer = applicationContext.getBean(ButterflyHttpServer.class);
        httpServer.startServer();
    }
}
