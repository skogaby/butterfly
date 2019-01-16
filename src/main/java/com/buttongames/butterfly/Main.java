package com.buttongames.butterfly;

import com.buttongames.butterfly.http.ButterflyHttpServer;
import com.buttongames.butterfly.spring.configuration.ApplicationConfiguration;
import com.buttongames.butterfly.util.CardIdUtils;
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
        /*applicationContext = new AnnotationConfigApplicationContext(ApplicationConfiguration.class);

        final ButterflyHttpServer httpServer = applicationContext.getBean(ButterflyHttpServer.class);
        httpServer.startServer();*/

        System.out.println(CardIdUtils.encodeCardId("E004D62EDB3C9000"));
    }
}
