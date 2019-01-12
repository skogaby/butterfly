package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.hibernate.dao.impl.Ddr16ShopDao;
import com.buttongames.butterfly.hibernate.dao.impl.MachineDao;
import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.exception.NoShopForMachineException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.model.Ddr16Shop;
import com.buttongames.butterfly.model.Machine;
import com.buttongames.butterfly.xml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
    private final Ddr16ShopDao shopDao;

    /**
     * DAO for interacting with Machines in the database.
     */
    private final MachineDao machineDao;

    public FacilityRequestHandler(final Ddr16ShopDao shopDao,
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
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>facility.get</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetRequest(final Request request, final Response response) {
        final String reqPcbId = request.attribute("pcbid");
        final Ddr16Shop shop = this.shopDao.findByPcbId(reqPcbId);

        if (shop == null) {
            throw new NoShopForMachineException();
        }

        final Machine machine = this.machineDao.findByPcbId(reqPcbId);

        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("facility")
                    .e("location")
                        .str("id", shop.getLocationId()).up()
                        .str("country", shop.getCountry()).up()
                        .str("region", shop.getRegion()).up()
                        .str("name", shop.getName()).up()
                        .u8("type", 0).up().up()
                    .e("line")
                        .str("id", "").up()
                        .u8("class", 0).up()
                    .e("public")
                        .u8("flag", shop.isPublic() ? 1 : 0).up()
                        .str("name", shop.getName()).up()
                        .str("lattitude", shop.getLatitude()).up()
                        .str("longitude", shop.getLongitude()).up().up()
                    .e("share")
                        .e("eacoin")
                            .s32("notchamount", shop.getNotchAmount()).up()
                            .s32("notchcount", shop.getNotchCount()).up()
                            .s32("supplylimit", shop.getSupplyLimit()).up().up()
                        .e("url")
                            .str("eapass", "http://eagate.573.jp/").up()
                            .str("arcadefan", "http://eagate.573.jp/").up()
                            .str("konaminetdx", "http://eagate.573.jp/").up()
                            .str("konamiid", "http://eagate.573.jp/").up()
                            .str("eagate", "http://eagate.573.jp/").up().up().up()
                    .e("portfw")
                        // TODO: Use our real public IP for this element
                        .ip("globalip", "1.0.0.127").up()
                        .u16("globalport", machine.getPort()).up()
                        .u16("privateport", machine.getPort());

        return this.sendResponse(request, response, respBuilder);
    }
}
