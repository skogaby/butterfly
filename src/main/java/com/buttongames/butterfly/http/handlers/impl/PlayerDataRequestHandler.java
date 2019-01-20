package com.buttongames.butterfly.http.handlers.impl;

import com.buttongames.butterfly.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterfly.hibernate.dao.impl.CardDao;
import com.buttongames.butterfly.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterfly.http.exception.InvalidRequestException;
import com.buttongames.butterfly.http.exception.UnsupportedRequestException;
import com.buttongames.butterfly.http.handlers.BaseRequestHandler;
import com.buttongames.butterfly.model.Card;
import com.buttongames.butterfly.model.ddr16.UserProfile;
import com.buttongames.butterfly.model.ddr16.options.AppearanceOption;
import com.buttongames.butterfly.model.ddr16.options.ArrowColorOption;
import com.buttongames.butterfly.model.ddr16.options.ArrowSkinOption;
import com.buttongames.butterfly.model.ddr16.options.BoostOption;
import com.buttongames.butterfly.model.ddr16.options.CutOption;
import com.buttongames.butterfly.model.ddr16.options.DancerOption;
import com.buttongames.butterfly.model.ddr16.options.FreezeArrowOption;
import com.buttongames.butterfly.model.ddr16.options.GuideLinesOption;
import com.buttongames.butterfly.model.ddr16.options.JudgementLayerOption;
import com.buttongames.butterfly.model.ddr16.options.JumpsOption;
import com.buttongames.butterfly.model.ddr16.options.LifeGaugeOption;
import com.buttongames.butterfly.model.ddr16.options.ScreenFilterOption;
import com.buttongames.butterfly.model.ddr16.options.ScrollOption;
import com.buttongames.butterfly.model.ddr16.options.SpeedOption;
import com.buttongames.butterfly.model.ddr16.options.StepZoneOption;
import com.buttongames.butterfly.model.ddr16.options.TurnOption;
import com.buttongames.butterfly.util.StringUtils;
import com.buttongames.butterfly.xml.kbinxml.KXmlBuilder;
import com.buttongames.butterfly.xml.XmlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import spark.Request;
import spark.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * Handler for any requests that come to the <code>playerdata</code> module.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class PlayerDataRequestHandler extends BaseRequestHandler {

    private final Logger LOG = LogManager.getLogger(PcbEventRequestHandler.class);

    // These are all constants for indexing into CSVs for profile data
    private static final int GAME_COMMON_AREA_OFFSET = 1;
    private static final int GAME_COMMON_SEQ_HEX_OFFSET = 2;
    private static final int GAME_COMMON_WEIGHT_DISPLAY_OFFSET = 3;
    private static final int GAME_COMMON_CHARACTER_OFFSET = 4;
    private static final int GAME_COMMON_EXTRA_CHARGE_OFFSET = 5;
    private static final int GAME_COMMON_TOTAL_PLAYS_OFFSET = 9;
    private static final int GAME_COMMON_SINGLE_PLAYS_OFFSET = 11;
    private static final int GAME_COMMON_DOUBLE_PLAYS_OFFSET = 12;
    private static final int GAME_COMMON_WEIGHT_OFFSET = 17;
    private static final int GAME_COMMON_NAME_OFFSET = 25;
    private static final int GAME_COMMON_SEQ_OFFSET = 26;

    private static final int GAME_OPTION_SPEED_OFFSET = 1;
    private static final int GAME_OPTION_BOOST_OFFSET = 2;
    private static final int GAME_OPTION_APPEARANCE_OFFSET = 3;
    private static final int GAME_OPTION_TURN_OFFSET = 4;
    private static final int GAME_OPTION_STEP_ZONE_OFFSET = 5;
    private static final int GAME_OPTION_SCROLL_OFFSET = 6;
    private static final int GAME_OPTION_ARROW_COLOR_OFFSET = 7;
    private static final int GAME_OPTION_CUT_OFFSET = 8;
    private static final int GAME_OPTION_FREEZE_OFFSET = 9;
    private static final int GAME_OPTION_JUMPS_OFFSET = 10;
    private static final int GAME_OPTION_ARROW_SKIN_OFFSET = 11;
    private static final int GAME_OPTION_FILTER_OFFSET = 12;
    private static final int GAME_OPTION_GUIDELINE_OFFSET = 13;
    private static final int GAME_OPTION_GAUGE_OFFSET = 14;
    private static final int GAME_OPTION_COMBO_POSITION_OFFSET = 15;
    private static final int GAME_OPTION_FAST_SLOW_OFFSET = 16;

    private static final int GAME_LAST_CALORIES_OFFSET = 10;

    private static final int GAME_RIVAL_SLOT_1_ACTIVE_OFFSET = 1;
    private static final int GAME_RIVAL_SLOT_2_ACTIVE_OFFSET = 2;
    private static final int GAME_RIVAL_SLOT_3_ACTIVE_OFFSET = 3;
    private static final int GAME_RIVAL_SLOT_1_DDRCODE_OFFSET = 9;
    private static final int GAME_RIVAL_SLOT_2_DDRCODE_OFFSET = 10;
    private static final int GAME_RIVAL_SLOT_3_DDRCODE_OFFSET = 11;

    /**
     * The DAO for managing users in the database.
     */
    private final ButterflyUserDao userDao;

    /**
     * The DAO for managing cards in the database.
     */
    private final CardDao cardDao;

    /**
     * The DAO for managing DDR profiles in the database.
     */
    private final ProfileDao profileDao;

    /**
     * Static list of events for the server.
     */
    private static NodeList EVENTS_2018042300;

    static {
        try {
            final Path path = Paths.get(ClassLoader.getSystemResource("static_responses/mdx_2018042300/events.xml").toURI());
            byte[] respBody = Files.readAllBytes(path);
            final Element doc = XmlUtils.byteArrayToXmlFile(respBody);
            EVENTS_2018042300 = XmlUtils.nodesAtPath(doc, "/events/eventdata");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public PlayerDataRequestHandler(final ButterflyUserDao userDao, final CardDao cardDao, final ProfileDao profileDao) {
        this.userDao = userDao;
        this.cardDao = cardDao;
        this.profileDao = profileDao;
    }

    /**
     * Handles an incoming request for the <code>playerdata</code> module.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    @Override
    public Object handleRequest(final Element requestBody, final Request request, final Response response) {
        final String requestMethod = request.attribute("method");

        // figure out which kind of usergamedata_advanced request this is
        if (requestMethod.equals("usergamedata_advanced")) {
            final String mode = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/mode");
            final String refid = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/refid");

            if (mode.equals("userload")) {
                return this.handleUserLoadRequest(refid, request, response);
            } else if (mode.equals("rivalload")) {
                int loadFlag = XmlUtils.intValueAtPath(requestBody, "/playerdata/data/loadflag");

                if (loadFlag == 1) {
                    return this.handleRivalLoad1Request(request, response);
                } else if (loadFlag == 2) {
                    return this.handleRivalLoad2Request(request, response);
                } else if (loadFlag == 4) {
                    return this.handleGlobalScoresRequest(request, response);
                }
            } else if (mode.equals("usernew")) {
                return this.handleNewUserRequest(refid, request, response);
            } else if (mode.equals("inheritance")) {
                return this.handleInheritanceRequest(request, response);
            }
        } else if (requestMethod.equals("usergamedata_send")) {
            return this.handleUserGameDataSendRequest(requestBody, request, response);
        } else if (requestMethod.equals("usergamedata_recv")) {
            return this.handleUserGameDataRecvRequest(requestBody, request, response);
        }

        throw new UnsupportedRequestException();
    }

    /**
     * Handles a <code>rivalload</code> request with a loadflag of 1.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleRivalLoad1Request(final Request request, final Response response) {
        // TODO: Implement this properly and load rival data...
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .e("data")
                        .s32("recordtype", 0);

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Handles a <code>rivalload</code> request with a loadflag of 2.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleRivalLoad2Request(final Request request, final Response response) {
        // TODO: Implement this properly and load rival data...
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                .e("data")
                    .s32("recordtype", 0);

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Handles a request for the global server scores.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGlobalScoresRequest(final Request request, final Response response) {
        // TODO: Implement this properly and load/save scores...
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .e("data")
                        .s32("recordtype", 0);

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Handles a request for the user scores.
     * @param refId The refId for the calling card
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleUserLoadRequest(final String refId, final Request request, final Response response) {
        if (!StringUtils.getSanitizedModel(request.attribute("model")).equals("mdx_2018042300")) {
            throw new UnsupportedRequestException();
        }

        // TODO: Implement this properly and load/save scores... also, events probably isn't a static response
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .bool("is_new", false).up();

        // TODO: insert the user scores once we have them

        // insert the events
        final Document document = respBuilder.getDocument();
        final Element elem = respBuilder.getElement();

        for (int i = 0; i < EVENTS_2018042300.getLength(); i++) {
            Node tmp = document.importNode(EVENTS_2018042300.item(i), true);
            elem.appendChild(tmp);
        }

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Handles a request to create a new profile.
     * @param refId The refid for the card creating the profile
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleNewUserRequest(final String refId, final Request request, final Response response) {
        // make sure there's not already a profile for the owner of this card
        final Card card = this.cardDao.findByRefId(refId);

        // if there's no card for this refid, throw an error
        if (card == null) {
            throw new InvalidRequestException();
        }

        UserProfile profile = this.profileDao.findByUser(card.getUser());

        // if a profile exists, throw an error
        if (profile != null) {
            throw new InvalidRequestException();
        }

        // create a new profile
        final int dancerCode = new Random().nextInt(99999999);
        String dancerCodeStr = String.format("%08d", dancerCode);
        dancerCodeStr = dancerCodeStr.substring(0, 4) + "-" + dancerCodeStr.substring(4, 8);

        profile = new UserProfile(card.getUser(), null, dancerCode, 33, true, 0, 0, 0, -1, 0.0, DancerOption.RANDOM,
                SpeedOption.X_1_00, BoostOption.NORMAL, AppearanceOption.VISIBLE, TurnOption.OFF, StepZoneOption.ON,
                ScrollOption.NORMAL, ArrowColorOption.RAINBOW, CutOption.OFF, FreezeArrowOption.ON, JumpsOption.ON,
                ArrowSkinOption.NORMAL, ScreenFilterOption.OFF, GuideLinesOption.ARROW_CENTER, LifeGaugeOption.NORMAL,
                JudgementLayerOption.BACKGROUND, true, 0, null, null, null);
        this.profileDao.create(profile);

        // send the response
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .str("seq", dancerCodeStr).up()
                    .s32("code", dancerCode).up()
                    .str("shoparea", "asdf");
        return this.sendResponse(request, response, builder);
    }

    /**
     * Handles a request for user game data.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleUserGameDataRecvRequest(final Element requestBody, final Request request, final Response response) {
        // make sure a profile exists for the given refid
        final String refId = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/refid");
        final Card card = this.cardDao.findByRefId(refId);

        // if there's no card for this refid, throw an error
        if (card == null) {
            throw new InvalidRequestException();
        }

        final UserProfile profile = this.profileDao.findByUser(card.getUser());

        // if a profile doesn't exist, throw an error
        if (profile == null) {
            throw new InvalidRequestException();
        }

        // figure out which fields are requested
        final int recvNum = XmlUtils.intValueAtPath(requestBody, "/playerdata/data/recv_num");
        final String recvCsv = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/recv_csv");
        String[] tmp = recvCsv.split(",", -1);
        String[] colsToSend = new String[recvNum];

        for (int i = 0; i < recvNum; i++) {
            colsToSend[i] = tmp[i * 2];
        }

        // this response needs to be constructed jankily and manually, because XML libraries don't like the idea
        // of embedding a sub-element inside the text content of a node
        String csvStrings = "";

        for (String col : colsToSend) {
            String val = null;

            if (col.equals("COMMON")) {
                val = this.buildCommonCsv(profile);
            } else if (col.equals("OPTION")) {
                val = this.buildOptionCsv(profile);
            } else if (col.equals("LAST")) {
                val = this.buildLastCsv(profile);
            } else if (col.equals("RIVAL")) {
                val = this.buildRivalCsv(profile);
            }

            if (val == null) {
                throw new InvalidRequestException();
            }

            val = "<d __type=\"str\">" + Base64.getEncoder().encodeToString(val.getBytes()) + "<bin1 __type=\"str\"></bin1></d>";
            csvStrings += val;
        }

        final String responseStr = String.format("<?xml version='1.0' encoding='UTF-8'?><response><playerdata>" +
                "<result __type=\"s32\">0</result><player><record_num __type=\"u32\">%d</record_num><record>%s</record>" +
                "</player></playerdata></response>", recvNum, csvStrings);

        // send the response
        return this.sendBytesToClient(responseStr.getBytes(), request, response);
    }

    /**
     * Handles a request to send in user game data.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleUserGameDataSendRequest(final Element requestBody, final Request request, final Response response) {
        // make sure a profile exists for the given refid
        final String refId = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/refid");
        final Card card = this.cardDao.findByRefId(refId);

        // if there's no card for this refid, throw an error
        if (card == null) {
            throw new InvalidRequestException();
        }

        UserProfile profile = this.profileDao.findByUser(card.getUser());

        // if a profile doesn't exist, throw an error
        if (profile == null) {
            throw new InvalidRequestException();
        }

        // read the request data
        final NodeList valueNodes = XmlUtils.nodesAtPath(requestBody, "/playerdata/data/record/d");
        String[] commonElems = null;
        String[] optionElems = null;
        String[] lastElems = null;
        String[] rivalElems = null;

        for (int i = 0; i < valueNodes.getLength(); i++) {
            final String value = new String(Base64.getDecoder().decode(valueNodes.item(i).getTextContent()));

            // split these out into CSV arrays, and omit the first 2 elements since those are headers
            if (value.startsWith("ffffffff,COMMON")) {
                commonElems = value.split(",", -1);
                commonElems = Arrays.copyOfRange(commonElems, 2, commonElems.length);
            } else if (value.startsWith("ffffffff,OPTION")) {
                optionElems = value.split(",", -1);
                optionElems = Arrays.copyOfRange(optionElems, 2, optionElems.length);
            } else if (value.startsWith("ffffffff,LAST")) {
                lastElems = value.split(",", -1);
                lastElems = Arrays.copyOfRange(lastElems, 2, lastElems.length);
            } else if (value.startsWith("ffffffff,RIVAL")) {
                rivalElems = value.split(",", -1);
                rivalElems = Arrays.copyOfRange(rivalElems, 2, rivalElems.length);
            }
        }

        // set all the profile values
        this.updateProfileFromCSVs(profile, commonElems, optionElems, lastElems, rivalElems);

        // send the response
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0);
        return this.sendResponse(request, response, builder);
    }

    /**
     * Return the CSV to send to the client for COMMON profile values.
     * @param profile The profile to construct the CSV for.
     * @return The CSV string.
     */
    private String buildCommonCsv(final UserProfile profile) {
        final String[] elems = "1,0,fffffff,1,0,0,0,0,0,0,0,0,0,0,0,0,0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,0000-0000,,,,,,".split(",", -1);

        // modify the contents to send back
        String dancerCodeStr = String.format("%08d", profile.getDancerCode());
        dancerCodeStr = dancerCodeStr.substring(0, 4) + "-" + dancerCodeStr.substring(4, 8);

        elems[GAME_COMMON_SEQ_HEX_OFFSET] = Integer.toHexString(profile.getDancerCode());
        elems[GAME_COMMON_AREA_OFFSET] = Integer.toHexString(profile.getArea());
        elems[GAME_COMMON_WEIGHT_DISPLAY_OFFSET] = profile.isDisplayWeight() ? "1" : "0";
        elems[GAME_COMMON_CHARACTER_OFFSET] = Integer.toHexString(profile.getCharacter().ordinal());
        elems[GAME_COMMON_EXTRA_CHARGE_OFFSET] = Integer.toHexString(profile.getExtraCharge());
        elems[GAME_COMMON_TOTAL_PLAYS_OFFSET] = (profile.getTotalPlays() == -1) ? "ffffffffffffffff" : Integer.toHexString(profile.getTotalPlays());
        elems[GAME_COMMON_SINGLE_PLAYS_OFFSET] = Integer.toHexString(profile.getSinglesPlays());
        elems[GAME_COMMON_DOUBLE_PLAYS_OFFSET] = Integer.toHexString(profile.getDoublesPlays());
        elems[GAME_COMMON_WEIGHT_OFFSET] = String.valueOf(profile.getWeight());
        elems[GAME_COMMON_NAME_OFFSET] = (profile.getName() == null) ? "" : profile.getName();
        elems[GAME_COMMON_SEQ_OFFSET] = dancerCodeStr;

        // if the total plays was -1, update it to 0 so the user doesn't see the EULA again
        if (profile.getTotalPlays() == -1) {
            profile.setTotalPlays(0);
            this.profileDao.update(profile);
        }

        return String.join(",", elems);
    }

    /**
     * Return the CSV to send to the client for OPTION profile values.
     * @param profile The profile to construct the CSV for.
     * @return The CSV string.
     */
    private String buildOptionCsv(final UserProfile profile) {
        final String[] elems = "1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10.0,10.0,10.0,10.0,0.0,0.0,0.0,0.0,,,,,,,,".split(",", -1);

        // modify the contents to send back
        elems[GAME_OPTION_SPEED_OFFSET] = Integer.toHexString(profile.getSpeedOption().getVal());
        elems[GAME_OPTION_BOOST_OFFSET] = Integer.toHexString(profile.getBoostOption().ordinal());
        elems[GAME_OPTION_APPEARANCE_OFFSET] = Integer.toHexString(profile.getAppearanceOption().getVal());
        elems[GAME_OPTION_TURN_OFFSET] = Integer.toHexString(profile.getTurnOption().ordinal());
        elems[GAME_OPTION_STEP_ZONE_OFFSET] = Integer.toHexString(profile.getStepZoneOption().ordinal());
        elems[GAME_OPTION_SCROLL_OFFSET] = Integer.toHexString(profile.getScrollOption().ordinal());
        elems[GAME_OPTION_ARROW_COLOR_OFFSET] = Integer.toHexString(profile.getArrowColorOption().ordinal());
        elems[GAME_OPTION_CUT_OFFSET] = Integer.toHexString(profile.getCutOption().ordinal());
        elems[GAME_OPTION_FREEZE_OFFSET] = Integer.toHexString(profile.getFreezeArrowOption().ordinal());
        elems[GAME_OPTION_JUMPS_OFFSET] = Integer.toHexString(profile.getJumpsOption().ordinal());
        elems[GAME_OPTION_ARROW_SKIN_OFFSET] = Integer.toHexString(profile.getArrowSkinOption().ordinal());
        elems[GAME_OPTION_FILTER_OFFSET] = Integer.toHexString(profile.getScreenFilterOption().ordinal());
        elems[GAME_OPTION_GUIDELINE_OFFSET] = Integer.toHexString(profile.getGuideLinesOption().ordinal());
        elems[GAME_OPTION_GAUGE_OFFSET] = Integer.toHexString(profile.getLifeGaugeOption().ordinal());
        elems[GAME_OPTION_COMBO_POSITION_OFFSET] = Integer.toHexString(profile.getJudgementLayerOption().ordinal());
        elems[GAME_OPTION_FAST_SLOW_OFFSET] = profile.isShowFastSlow() ? "1" : "0";

        return String.join(",", elems);
    }

    /**
     * Return the CSV to send to the client for LAST profile values.
     * @param profile The profile to construct the CSV for.
     * @return The CSV string.
     */
    private String buildLastCsv(final UserProfile profile) {
        final String[] elems = "1,6c76656c,3431766c,9440,3,1,3,4,1,7753ba,b65b5,8000000000000001,8000000000000001,0,0,5c2d1455,0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,".split(",", -1);

        // modify the contents to send back
        elems[GAME_LAST_CALORIES_OFFSET] = Integer.toHexString(profile.getLastCalories());

        return String.join(",", elems);
    }

    /**
     * Return the CSV to send to the client for RIVAL profile values.
     * @param profile The profile to construct the CSV for.
     * @return The CSV string.
     */
    private String buildRivalCsv(final UserProfile profile) {
        final String[] elems = "1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,".split(",", -1);

        // modify the contents to send back
        elems[GAME_RIVAL_SLOT_1_ACTIVE_OFFSET] = profile.getRival1() == null ? "0" : "1";
        elems[GAME_RIVAL_SLOT_2_ACTIVE_OFFSET] = profile.getRival2() == null ? "0" : "1";
        elems[GAME_RIVAL_SLOT_3_ACTIVE_OFFSET] = profile.getRival3() == null ? "0" : "1";

        if (profile.getRival1() != null) {
            elems[GAME_RIVAL_SLOT_1_DDRCODE_OFFSET] = Integer.toHexString(profile.getRival1().getDancerCode());
        }

        if (profile.getRival2() != null) {
            elems[GAME_RIVAL_SLOT_2_DDRCODE_OFFSET] = Integer.toHexString(profile.getRival2().getDancerCode());
        }

        if (profile.getRival3() != null) {
            elems[GAME_RIVAL_SLOT_3_DDRCODE_OFFSET] = Integer.toHexString(profile.getRival3().getDancerCode());
        }

        return String.join(",", elems);
    }

    /**
     * Updates a user profile based on CSV values
     * @param profile The profile to update
     * @param common The values for COMMON
     * @param option The values for OPTION
     * @param last The values for LAST
     * @param rival The values for RIVAL
     */
    private void updateProfileFromCSVs(final UserProfile profile, final String[] common, final String[] option,
                                       final String[] last, final String[] rival) {
        // parse out the COMMON values
        if (common != null &&
                common.length != 0) {
            final int area = Integer.parseInt(common[GAME_COMMON_AREA_OFFSET], 16);
            final boolean displayWeight = common[GAME_COMMON_WEIGHT_DISPLAY_OFFSET].equals("1") ? true : false;
            final DancerOption character = DancerOption.values()[Integer.parseInt(common[GAME_COMMON_CHARACTER_OFFSET], 16)];
            final int extraCharge = Integer.parseInt(common[GAME_COMMON_EXTRA_CHARGE_OFFSET], 16);
            final int singlePlays = Integer.parseInt(common[GAME_COMMON_SINGLE_PLAYS_OFFSET], 16);
            final int doublePlays = Integer.parseInt(common[GAME_COMMON_DOUBLE_PLAYS_OFFSET], 16);
            final double weight = Double.parseDouble(common[GAME_COMMON_WEIGHT_OFFSET]);
            final String name = common[GAME_COMMON_NAME_OFFSET];
            final int dancerCode = Integer.parseInt(String.join("", common[GAME_COMMON_SEQ_OFFSET].split("-", -1)));

            int totalPlays;

            // this is needed for new profile creation
            try {
                totalPlays = Integer.parseInt(common[GAME_COMMON_TOTAL_PLAYS_OFFSET], 16);
            } catch (NumberFormatException e) {
                totalPlays = -1;
            }

            profile.setArea(area);
            profile.setDisplayWeight(displayWeight);
            profile.setCharacter(character);
            profile.setExtraCharge(extraCharge);
            profile.setTotalPlays(totalPlays);
            profile.setSinglesPlays(singlePlays);
            profile.setDoublesPlays(doublePlays);
            profile.setWeight(weight);
            profile.setName(name);
            profile.setDancerCode(dancerCode);
        }

        // parse out OPTION values
        if (option != null &&
                option.length != 0) {
            final SpeedOption speedOption = SpeedOption.optionForValue(Integer.parseInt(option[GAME_OPTION_SPEED_OFFSET], 16));
            final BoostOption boostOption = BoostOption.values()[Integer.parseInt(option[GAME_OPTION_BOOST_OFFSET], 16)];
            final AppearanceOption appearanceOption = AppearanceOption.optionForValue(Integer.parseInt(option[GAME_OPTION_APPEARANCE_OFFSET], 16));
            final TurnOption turnOption = TurnOption.values()[Integer.parseInt(option[GAME_OPTION_TURN_OFFSET], 16)];
            final StepZoneOption stepZoneOption = StepZoneOption.values()[Integer.parseInt(option[GAME_OPTION_STEP_ZONE_OFFSET], 16)];
            final ScrollOption scrollOption = ScrollOption.values()[Integer.parseInt(option[GAME_OPTION_SCROLL_OFFSET], 16)];
            final ArrowColorOption arrowColorOption = ArrowColorOption.values()[Integer.parseInt(option[GAME_OPTION_ARROW_COLOR_OFFSET], 16)];
            final CutOption cutOption = CutOption.values()[Integer.parseInt(option[GAME_OPTION_CUT_OFFSET], 16)];
            final FreezeArrowOption freezeArrowOption = FreezeArrowOption.values()[Integer.parseInt(option[GAME_OPTION_FREEZE_OFFSET], 16)];
            final JumpsOption jumpsOption = JumpsOption.values()[Integer.parseInt(option[GAME_OPTION_JUMPS_OFFSET], 16)];
            final ArrowSkinOption arrowSkinOption = ArrowSkinOption.values()[Integer.parseInt(option[GAME_OPTION_ARROW_SKIN_OFFSET], 16)];
            final ScreenFilterOption screenFilterOption = ScreenFilterOption.values()[Integer.parseInt(option[GAME_OPTION_FILTER_OFFSET], 16)];
            final GuideLinesOption guideLinesOption = GuideLinesOption.values()[Integer.parseInt(option[GAME_OPTION_GUIDELINE_OFFSET], 16)];
            final LifeGaugeOption lifeGaugeOption = LifeGaugeOption.values()[Integer.parseInt(option[GAME_OPTION_GAUGE_OFFSET], 16)];
            final JudgementLayerOption judgementLayerOption = JudgementLayerOption.values()[Integer.parseInt(option[GAME_OPTION_COMBO_POSITION_OFFSET], 16)];
            final boolean showFastSlow = option[GAME_OPTION_FAST_SLOW_OFFSET].equals("1") ? true : false;

            profile.setSpeedOption(speedOption);
            profile.setBoostOption(boostOption);
            profile.setAppearanceOption(appearanceOption);
            profile.setTurnOption(turnOption);
            profile.setStepZoneOption(stepZoneOption);
            profile.setScrollOption(scrollOption);
            profile.setArrowColorOption(arrowColorOption);
            profile.setCutOption(cutOption);
            profile.setFreezeArrowOption(freezeArrowOption);
            profile.setJumpsOption(jumpsOption);
            profile.setArrowSkinOption(arrowSkinOption);
            profile.setScreenFilterOption(screenFilterOption);
            profile.setGuideLinesOption(guideLinesOption);
            profile.setLifeGaugeOption(lifeGaugeOption);
            profile.setJudgementLayerOption(judgementLayerOption);
            profile.setShowFastSlow(showFastSlow);
        }

        // parse out LAST values
        if (last != null &&
                last.length != 0) {
            final int lastCalories = Integer.parseInt(last[GAME_LAST_CALORIES_OFFSET], 16);

            profile.setLastCalories(lastCalories);
        }

        // parse out RIVAL values
        if (rival != null &&
                rival.length != 0) {
            final boolean rival1Active = rival[GAME_RIVAL_SLOT_1_ACTIVE_OFFSET].equals("1") ? true : false;
            final boolean rival2Active = rival[GAME_RIVAL_SLOT_2_ACTIVE_OFFSET].equals("1") ? true : false;
            final boolean rival3Active = rival[GAME_RIVAL_SLOT_3_ACTIVE_OFFSET].equals("1") ? true : false;
            UserProfile rival1 = null;
            UserProfile rival2 = null;
            UserProfile rival3 = null;

            if (rival1Active) {
                rival1 = profileDao.findByDancerCode(Integer.parseInt(rival[GAME_RIVAL_SLOT_1_DDRCODE_OFFSET], 16));
            }

            if (rival2Active) {
                rival2 = profileDao.findByDancerCode(Integer.parseInt(rival[GAME_RIVAL_SLOT_2_DDRCODE_OFFSET], 16));
            }

            if (rival3Active) {
                rival3 = profileDao.findByDancerCode(Integer.parseInt(rival[GAME_RIVAL_SLOT_3_DDRCODE_OFFSET], 16));
            }

            profile.setRival1(rival1);
            profile.setRival2(rival2);
            profile.setRival3(rival3);
        }

        // update the given profile
        this.profileDao.update(profile);
    }

    /**
     * Handles an inheritance request for a new user.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleInheritanceRequest(final Request request, final Response response) {
        // TODO: Confirm if this value actually matters...
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .s32("InheritanceStatus", 1);

        return this.sendResponse(request, response, builder);
    }
}
