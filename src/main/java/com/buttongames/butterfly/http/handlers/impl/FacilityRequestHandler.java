package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.hibernate.dao.impl.ddr16.ShopDao;
import com.buttongames.butterfly.hibernate.dao.impl.MachineDao;
import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.exception.UnsupportedRequestException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.model.ddr16.Shop;
import com.buttongames.butterfly.util.StringUtils;
import com.buttongames.butterfly.xml.kbinxml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>facility</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class FacilityRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(FacilityRequestHandler.class);

    /**
     * DAO for interacting with Ddr16Shops in the database.
     */
    private final ShopDao shopDao;

    /**
     * DAO for interacting with Machines in the database.
     */
    private final MachineDao machineDao;

    @Autowired
    public FacilityRequestHandler(final ShopDao shopDao,
                                  final MachineDao machineDao) {
        this.shopDao = shopDao;
        this.machineDao = machineDao;
    }

    /**
     * Handles an incoming request for the <code>facility</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("get")) {
            return handleGetRequest(request, response);
        }

        throw new UnsupportedRequestException();
    }

    /**
     * Handles an incoming request for <code>facility.get</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetRequest(final Request request, final Response response) {
        final String reqPcbId = request.attribute("pcbid");
        Shop shop = this.shopDao.findByPcbId(reqPcbId);

        // if no shop exists for this PCB, just make a default one
        if (shop == null) {
            shop = new Shop(reqPcbId, StringUtils.getRandomHexString(8), "BUTTERFLY", "US", "1", true);
            shopDao.create(shop);
        }

        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("facility")
                    .e("location")
                        .str("id", shop.getLocationId()).up()
                        .str("country", shop.getCountry()).up()
                        .str("region", shop.getRegion()).up()
                        .str("name", shop.getName()).up()
                        .u8("type", 0).up(2)
                    .e("line")
                        .str("id", "").up()
                        .u8("class", 0).up(2)
                    .e("portfw")
                        // TODO: Use our real IP here
                        .ip("globalip", "1.0.0.127").up()
                        .u16("globalport", 5700).up()
                        .u16("privateport", 5700).up(2)
                    .e("public")
                        .u8("flag", 0).up()
                        .str("name", "").up()
                        .str("latitude", "").up()
                        .str("longitude", "").up(2)
                    .e("share")
                        .e("eacoin")
                            .s32("notchamount", 0).up()
                            .s32("notchcount", 0).up()
                            .s32("supplylimit", 100000).up(2)
                        .e("url")
                            .str("eapass", "http://eagate.573.jp/").up()
                            .str("arcadefan", "http://eagate.573.jp/").up()
                            .str("konaminetdx", "http://eagate.573.jp/").up()
                            .str("konamiid", "http://eagate.573.jp/").up()
                            .str("eagate", "http://eagate.573.jp/");

        return this.sendResponse(request, response, respBuilder);
    }
}
