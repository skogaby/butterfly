package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.hibernate.dao.impl.CardDao;
import com.buttongames.butterfly.http.exception.InvalidRequestException;
import com.buttongames.butterfly.http.exception.UnsupportedRequestException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.model.Card;
import com.buttongames.butterfly.xml.XmlUtils;
import com.buttongames.butterfly.xml.kbinxml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>eacoin</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class EacoinRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(EacoinRequestHandler.class);

    /**
     * How much Paseli to use when it's unlimited (for now, it's always unlimited).
     */
    private static final int INFINITE_PASELI_AMOUNT = 573;

    /**
     * DAO for interacting with cards in the database.
     */
    private final CardDao cardDao;

    public EacoinRequestHandler(final CardDao cardDao) {
        this.cardDao = cardDao;
    }

    /**
     * Handles an incoming request for the <code>eacoin</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("checkin")) {
            return this.handleCheckinRequest(requestBody, request, response);
        } else if (requestMethod.equals("consume")) {
            return this.handleConsumeRequest(request, response);
        } else if (requestMethod.equals("checkout")) {
            return this.handleCheckoutRequest(request, response);
        }

        throw new UnsupportedRequestException();
    }

    /**
     * Handles an incoming request for the <code>eacoin.checkin</code> method.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleCheckinRequest(final Element requestBody, final Request request, final Response response) {
        final String cardId = XmlUtils.strAtPath(requestBody, "/eacoin/cardid");
        final String pin = XmlUtils.strAtPath(requestBody, "/eacoin/passwd");

        // verify the pin for sanity
        // check the pin against the pin of the owner of the ref ID
        final Card card = this.cardDao.findByNfcId(cardId);

        if (card == null ||
                card.getUser() == null ||
                !card.getUser().getPin().equals(pin)) {
            throw new InvalidRequestException();
        }

        // send a response
        // TODO: send the actual Paseli balance
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("eacoin")
                    .s16("sequence", 1).up()
                    .u8("acstatus", 1).up()
                    .str("acid", "X").up()
                    .str("acname", "X").up()
                    .s32("balance", INFINITE_PASELI_AMOUNT).up()
                    .str("sessid", "X");

        return this.sendResponse(request, response, builder);
    }

    /**
     * Handles an incoming request for the <code>eacoin.checkout</code> method.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleCheckoutRequest(final Request request, final Response response) {
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("eacoin");

        return this.sendResponse(request, response, builder);
    }

    /**
     * Handles an incoming request for the <code>eacoin.consume</code> method.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleConsumeRequest(final Request request, final Response response) {
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("eacoin")
                    .u8("acstatus", 0).up()
                    .u8("autocharge", 0).up()
                    .s32("balance", INFINITE_PASELI_AMOUNT);

        return this.sendResponse(request, response, builder);
    }
}
