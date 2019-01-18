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
import com.buttongames.butterfly.xml.builder.KXmlBuilder;
import com.buttongames.butterfly.xml.XmlUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import spark.Request;
import spark.Response;

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
            final String dataid = XmlUtils.strValueAtPath(requestBody, "/playerdata/data/dataid");

            if (mode.equals("userload")) {
                if (refid.equals("X0000000000000000000000000000000") &&
                        dataid.equals("X0000000000000000000000000000000")) {
                    return handleEventsRequest(request, response);
                }
            } else if (mode.equals("rivalload")) {
                int loadFlag = XmlUtils.intValueAtPath(requestBody, "/playerdata/data/loadflag");

                if (loadFlag == 1) {
                    return handleRivalLoad1Request(request, response);
                } else if (loadFlag == 2) {
                    return  handleRivalLoad2Request(request, response);
                } else if (loadFlag == 4) {
                    return handleGlobalScoresRequest(request, response);
                }
            } else if (mode.equals("usernew")) {
                return this.handleNewUserRequest(refid, request, response);
            }
        } else if (requestMethod.equals("usergamedata_send")) {
            return this.handleUserGameDataSendRequest(requestBody, request, response);
        }

        throw new UnsupportedRequestException();
    }
    /**
     * Handles an incoming request for the events.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleEventsRequest(final Request request, final Response response) {
        if (StringUtils.getSanitizedModel(request.attribute("model")).equals("mdx_2018042300")) {
            // TODO: This is almost *definitely* not supposed to be a static response
            return this.sendStaticResponse(request, response, "static_responses/mdx_2018042300/events.xml");
        } else {
            throw new UnsupportedRequestException();
        }
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

        profile = new UserProfile(card.getUser(), null, dancerCode, 1, true, 0, 0, 0, 0, 0.0, DancerOption.RANDOM,
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
                    .s32("code", dancerCode)
                    .str("shoparea", "default");
        return this.sendResponse(request, response, builder);
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
        final String commonValues = new String(Base64.getDecoder().decode(valueNodes.item(0).getTextContent()));
        final String optionValues = new String(Base64.getDecoder().decode(valueNodes.item(1).getTextContent()));
        final String lastValues = new String(Base64.getDecoder().decode(valueNodes.item(2).getTextContent()));
        final String rivalValues = new String(Base64.getDecoder().decode(valueNodes.item(3).getTextContent()));

        // split these out into CSV arrays, and omit the first 2 elements since those are headers
        String[] commonElems =  commonValues.split(",");
        commonElems = Arrays.copyOfRange(commonElems, 2, commonElems.length);

        String[] optionElems = optionValues.split(",");
        optionElems = Arrays.copyOfRange(optionElems, 2, optionElems.length);

        String[] lastElems = lastValues.split(",");
        lastElems = Arrays.copyOfRange(lastElems, 2, lastElems.length);

        String[] rivalElems = rivalValues.split(",");
        rivalElems = Arrays.copyOfRange(rivalElems, 2, rivalElems.length);

        // set all the profile values
        this.updateProfileFromCSVs(profile, commonElems, optionElems, lastElems, rivalElems);

        // send the response
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0);
        return this.sendResponse(request, response, builder);
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
        final int area = Integer.parseInt(common[GAME_COMMON_AREA_OFFSET]);
        final boolean displayWeight = common[GAME_COMMON_WEIGHT_DISPLAY_OFFSET].equals("1") ? true : false;
        final DancerOption character = DancerOption.values()[Integer.parseInt(common[GAME_COMMON_CHARACTER_OFFSET])];
        final int extraCharge = Integer.parseInt(common[GAME_COMMON_EXTRA_CHARGE_OFFSET]);
        final int totalPlays = Integer.parseInt(common[GAME_COMMON_TOTAL_PLAYS_OFFSET]);
        final int singlePlays = Integer.parseInt(common[GAME_COMMON_SINGLE_PLAYS_OFFSET]);
        final int doublePlays = Integer.parseInt(common[GAME_COMMON_DOUBLE_PLAYS_OFFSET]);
        final double weight = Double.parseDouble(common[GAME_COMMON_WEIGHT_OFFSET]);
        final String name = common[GAME_COMMON_NAME_OFFSET];
        final int dancerCode = Integer.parseInt(String.join("", common[GAME_COMMON_SEQ_OFFSET].split("-")));

        // parse out OPTION values
        final SpeedOption speedOption = SpeedOption.values()[Integer.parseInt(option[GAME_OPTION_SPEED_OFFSET])];
        final BoostOption boostOption = BoostOption.values()[Integer.parseInt(option[GAME_OPTION_BOOST_OFFSET])];
        final AppearanceOption appearanceOption = AppearanceOption.values()[Integer.parseInt(option[GAME_OPTION_APPEARANCE_OFFSET])];
        final TurnOption turnOption = TurnOption.values()[Integer.parseInt(option[GAME_OPTION_TURN_OFFSET])];
        final StepZoneOption stepZoneOption = StepZoneOption.values()[Integer.parseInt(option[GAME_OPTION_STEP_ZONE_OFFSET])];
        final ScrollOption scrollOption = ScrollOption.values()[Integer.parseInt(option[GAME_OPTION_SCROLL_OFFSET])];
        final ArrowColorOption arrowColorOption = ArrowColorOption.values()[Integer.parseInt(option[GAME_OPTION_ARROW_COLOR_OFFSET])];
        final CutOption cutOption = CutOption.values()[Integer.parseInt(option[GAME_OPTION_CUT_OFFSET])];
        final FreezeArrowOption freezeArrowOption = FreezeArrowOption.values()[Integer.parseInt(option[GAME_OPTION_FREEZE_OFFSET])];
        final JumpsOption jumpsOption = JumpsOption.values()[Integer.parseInt(option[GAME_OPTION_JUMPS_OFFSET])];
        final ArrowSkinOption arrowSkinOption = ArrowSkinOption.values()[Integer.parseInt(option[GAME_OPTION_ARROW_SKIN_OFFSET])];
        final ScreenFilterOption screenFilterOption = ScreenFilterOption.values()[Integer.parseInt(option[GAME_OPTION_FILTER_OFFSET])];
        final GuideLinesOption guideLinesOption = GuideLinesOption.values()[Integer.parseInt(option[GAME_OPTION_GUIDELINE_OFFSET])];
        final LifeGaugeOption lifeGaugeOption = LifeGaugeOption.values()[Integer.parseInt(option[GAME_OPTION_GAUGE_OFFSET])];
        final JudgementLayerOption judgementLayerOption = JudgementLayerOption.values()[Integer.parseInt(option[GAME_OPTION_COMBO_POSITION_OFFSET])];
        final boolean showFastSlow = option[GAME_OPTION_FAST_SLOW_OFFSET].equals("1") ? true : false;

        // parse out LAST values
        final int lastCalories = Integer.parseInt(last[GAME_LAST_CALORIES_OFFSET]);

        // parse out RIVAL values
        final boolean rival1Active = rival[GAME_RIVAL_SLOT_1_ACTIVE_OFFSET].equals("1") ? true : false;
        final boolean rival2Active = rival[GAME_RIVAL_SLOT_2_ACTIVE_OFFSET].equals("1") ? true : false;
        final boolean rival3Active = rival[GAME_RIVAL_SLOT_3_ACTIVE_OFFSET].equals("1") ? true : false;
        UserProfile rival1 = null;
        UserProfile rival2 = null;
        UserProfile rival3 = null;

        if (rival1Active) {
            rival1 = profileDao.findByDancerCode(Integer.parseInt(rival[GAME_RIVAL_SLOT_1_DDRCODE_OFFSET]));
        }

        if (rival2Active) {
            rival2 = profileDao.findByDancerCode(Integer.parseInt(rival[GAME_RIVAL_SLOT_2_DDRCODE_OFFSET]));
        }

        if (rival3Active) {
            rival3 = profileDao.findByDancerCode(Integer.parseInt(rival[GAME_RIVAL_SLOT_3_DDRCODE_OFFSET]));
        }

        // update the given profile
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

        profile.setLastCalories(lastCalories);

        profile.setRival1(rival1);
        profile.setRival2(rival2);
        profile.setRival3(rival3);

        this.profileDao.update(profile);
    }
}
