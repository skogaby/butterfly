package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.hibernate.dao.impl.MachineDao;
import com.buttongames.butterfly.hibernate.dao.impl.UserPhasesDao;
import com.buttongames.butterfly.http.exception.InvalidRequestMethodException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.model.Machine;
import com.buttongames.butterfly.model.UserPhases;
import com.buttongames.butterfly.xml.kbinxml.KXmlBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import spark.Request;
import spark.Response;

/**
 * Handler for any requests that come to the <code>tax</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class TaxRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(TaxRequestHandler.class);

    /**
     * DAO for interacting with Machines in the database.
     */
    private final MachineDao machineDao;

    /**
     * DAO for interacting with UserPhases in the database.
     */
    private final UserPhasesDao phasesDao;

    @Autowired
    public TaxRequestHandler(final MachineDao machineDao, final UserPhasesDao phasesDao) {
        this.machineDao = machineDao;
        this.phasesDao = phasesDao;
    }

    /**
     * Handles an incoming request for the <code>tax</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        if (requestMethod.equals("get_phase")) {
            return handleGetPhaseRequest(request, response);
        } else {
            throw new InvalidRequestMethodException();
        }
    }

    /**
     * Handles an incoming request for <code>tax.get_phase</code>
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGetPhaseRequest(final Request request, final Response response) {
        final String reqPcbId = request.attribute("pcbid");
        final Machine machine = this.machineDao.findByPcbId(reqPcbId);
        UserPhases userPhases = this.phasesDao.getPhasesForUser(machine.getUser());

        // if there's no phase for this user, just create one
        if (userPhases == null) {
            userPhases = new UserPhases(machine.getUser(), 0);
            phasesDao.create(userPhases);
        }

        // send the response
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("tax")
                    .s32("phase", userPhases.getDdr16Phase());

        return this.sendResponse(request, response, respBuilder);
    }
}
