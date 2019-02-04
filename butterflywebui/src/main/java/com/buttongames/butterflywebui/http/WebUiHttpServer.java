package com.buttongames.butterflywebui.http;

import com.buttongames.butterflywebui.util.PropertyNames;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static spark.Spark.port;
import static spark.Spark.stop;
import static spark.Spark.threadPool;

/**
 * The main HTTP server. This class is responsible for the top-level handling of incoming
 * requests, then delegates the responsibility to the appropriate controller.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class WebUiHttpServer {

    private static final Logger LOG = LogManager.getLogger(WebUiHttpServer.class);

    /** The port the server listens on */
    @Value(PropertyNames.PORT)
    private String port;

    @Autowired
    public WebUiHttpServer() {

    }

    /**
     * Configures the routes on our server and begins listening.
     */
    public void startServer() {
        // configure the server properties
        int maxThreads = 20;
        int minThreads = 2;
        int timeOutMillis = 30000;

        // once routes are configured, the server automatically begins
        threadPool(maxThreads, minThreads, timeOutMillis);
        port(Integer.parseInt(this.port));
        this.configureRoutesAndExceptions();
    }

    /**
     * Stops the HTTP server.
     */
    public void stopServer() {
        stop();
    }

    /**
     * Configures the routes on the server, and the exception handlers.
     */
    private void configureRoutesAndExceptions() {

    }
}
