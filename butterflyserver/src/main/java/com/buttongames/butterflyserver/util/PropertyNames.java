package com.buttongames.butterflyserver.util;

/**
 * Simple class to hold our config / property keys for reading properties files.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class PropertyNames {

    public static final String PORT = "${server.port}";
    public static final String URL = "${server.hosturl}";
    public static final String MAINT_MODE = "${server.maintenance}";
    public static final String PASELI_ENABLE = "${server.paselienable}";

    //DDR A
    public static final String FORCE_EXTRA_STAGE = "${server.forceextrastage}";

    //DDR A20
    public static final String GOLDEN_LEAGUE_PERCENTILES = "${server.goldenleague.usepercentiles}";

}
