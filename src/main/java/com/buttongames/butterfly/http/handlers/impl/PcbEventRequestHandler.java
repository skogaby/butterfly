package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.hibernate.dao.impl.ddr16.PcbEventLogDao;
import com.buttongames.butterfly.http.exception.UnsupportedRequestException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.model.ddr16.PcbEventLog;
import com.buttongames.butterfly.util.TimeUtils;
import com.buttongames.butterfly.xml.kbinxml.KXmlBuilder;
import com.buttongames.butterfly.xml.XmlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;

/**
 * Handler for any requests that come to the <code>pcbevent</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PcbEventRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PcbEventRequestHandler.class);

    /**
     * The DAO for creating new PCB event logs.
     */
    private final PcbEventLogDao pcbEventLogDao;

    @Autowired
    public PcbEventRequestHandler(final PcbEventLogDao pcbEventLogDao) {
        this.pcbEventLogDao = pcbEventLogDao;
    }

    /**
     * Handles an incoming request for the <code>pcbevent</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("put")) {
            return handlePutRequest(requestBody, request, response);
        }

        throw new UnsupportedRequestException();
    }

    /**
     * Handles an incoming request for <code>pcbevent.put</code>
     * @param requestBody The XML body of the request
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handlePutRequest(final Element requestBody, final Request request, final Response response) {
        // log the event to the database
        final String reqModel = request.attribute("model");
        final String reqPcbId = request.attribute("pcbid");
        final LocalDateTime time1 = TimeUtils.timeFromEpoch(XmlUtils.longValueAtPath(requestBody, "/pcbevent/time"));
        final long sequence = XmlUtils.longValueAtPath(requestBody, "/pcbevent/seq");
        final String name = XmlUtils.strValueAtPath(requestBody, "/pcbevent/item/name");
        final int value = XmlUtils.intValueAtPath(requestBody, "/pcbevent/item/value");
        final LocalDateTime time2 = TimeUtils.timeFromEpoch(XmlUtils.longValueAtPath(requestBody, "/pcbevent/item/time"));

        final PcbEventLog event = new PcbEventLog(reqPcbId, reqModel, time1, time2, sequence, name, value);
        this.pcbEventLogDao.create(event);

        // send the response
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("pcbevent");
        return this.sendResponse(request, response, respBuilder);
    }
}
