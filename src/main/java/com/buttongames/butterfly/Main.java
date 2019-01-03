package com.buttongames.butterfly;

import com.buttongames.butterfly.http.ButterflyHttpServer;

public class Main {

    public static void main(String[] args) {
        ButterflyHttpServer httpServer = new ButterflyHttpServer();
        httpServer.startServer();
    }
}
