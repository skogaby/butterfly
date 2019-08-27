package com.buttongames.butterflyserver.http.handlers.impl;

import com.buttongames.butterflyserver.Main;
import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.CardDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GhostDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.UserSongRecordDao;
import com.buttongames.butterflyserver.http.exception.InvalidRequestException;
import com.buttongames.butterflyserver.http.exception.UnsupportedRequestException;
import com.buttongames.butterflyserver.http.handlers.BaseRequestHandler;
import com.buttongames.butterflymodel.model.Card;
import com.buttongames.butterflymodel.model.ddr16.GhostData;
import com.buttongames.butterflymodel.model.ddr16.UserProfile;
import com.buttongames.butterflymodel.model.ddr16.UserSongRecord;
import com.buttongames.butterflymodel.model.ddr16.options.AppearanceOption;
import com.buttongames.butterflymodel.model.ddr16.options.ArrowColorOption;
import com.buttongames.butterflymodel.model.ddr16.options.ArrowSkinOption;
import com.buttongames.butterflymodel.model.ddr16.options.BoostOption;
import com.buttongames.butterflymodel.model.ddr16.options.CutOption;
import com.buttongames.butterflymodel.model.ddr16.options.DancerOption;
import com.buttongames.butterflymodel.model.ddr16.options.FreezeArrowOption;
import com.buttongames.butterflymodel.model.ddr16.options.GuideLinesOption;
import com.buttongames.butterflymodel.model.ddr16.options.JudgementLayerOption;
import com.buttongames.butterflymodel.model.ddr16.options.JumpsOption;
import com.buttongames.butterflymodel.model.ddr16.options.LifeGaugeOption;
import com.buttongames.butterflymodel.model.ddr16.options.ScreenFilterOption;
import com.buttongames.butterflymodel.model.ddr16.options.ScrollOption;
import com.buttongames.butterflymodel.model.ddr16.options.SpeedOption;
import com.buttongames.butterflymodel.model.ddr16.options.StepZoneOption;
import com.buttongames.butterflymodel.model.ddr16.options.TurnOption;
import com.buttongames.butterflycore.util.TimeUtils;
import com.buttongames.butterflycore.xml.kbinxml.KXmlBuilder;
import com.buttongames.butterflycore.xml.XmlUtils;
import com.buttongames.butterflyserver.util.PropertyNames;
import com.google.common.io.ByteStreams;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import spark.Request;
import spark.Response;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final int GAME_LAST_SONG_OFFSET = 3;
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
     * The DAO for managing ghost step data in the database.
     */
    private final GhostDataDao ghostDataDao;

    /**
     * The DAO for managing song scores.
     */
    private final UserSongRecordDao songRecordDao;

    /**
     * Says whether or not we force every session to have extra stage.
     */
    @Value(PropertyNames.FORCE_EXTRA_STAGE)
    private String isForceExtraStage;

    /**
     * Static list of events for the server.
     */
    private static NodeList EVENTS;

    static {
        loadEvents();
    }

    public PlayerDataRequestHandler(final ButterflyUserDao userDao, final CardDao cardDao, final ProfileDao profileDao,
                                    final GhostDataDao ghostDataDao, final UserSongRecordDao songRecordDao) {
        this.userDao = userDao;
        this.cardDao = cardDao;
        this.profileDao = profileDao;
        this.ghostDataDao = ghostDataDao;
        this.songRecordDao = songRecordDao;
    }

    /**
     * Load the events data into memory.
     */
    private static void loadEvents() {
        try {
            final byte[] respBody = ByteStreams.toByteArray(
                    Main.class.getResourceAsStream("/static_responses/mdx/events.xml"));
            final Element doc = XmlUtils.byteArrayToXmlFile(respBody);
            EVENTS = XmlUtils.nodesAtPath(doc, "/response/playerdata/eventdata");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
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

        // handle usergamedata_advanced requests
        if (requestMethod.equals("usergamedata_advanced")) {
            final String mode = XmlUtils.strAtPath(requestBody, "/playerdata/data/mode");
            final String refid = XmlUtils.strAtPath(requestBody, "/playerdata/data/refid");
            final String shopArea = XmlUtils.strAtPath(requestBody, "/playerdata/data/shoparea");

            // handle usergamedata_advanced.userload requests
            if (mode.equals("userload")) {
                return this.handleUserLoadRequest(refid, request, response);
            // handle usergamedata_advanced.rivalload requests
            } else if (mode.equals("rivalload")) {
                int loadFlag = XmlUtils.intAtPath(requestBody, "/playerdata/data/loadflag");

                if (loadFlag == 1) {
                    return this.handleMachineScoresRequest(request, response);
                } else if (loadFlag == 2) {
                    return this.handleAreaScoresRequest(shopArea, request, response);
                } else if (loadFlag == 4) {
                    return this.handleGlobalScoresRequest(request, response);
                } else if (loadFlag == 8) {
                    return this.handleRivalScoresRequest(refid, request, response, 1);
                } else if (loadFlag == 16) {
                    return this.handleRivalScoresRequest(refid, request, response, 2);
                } else if (loadFlag == 32) {
                    return this.handleRivalScoresRequest(refid, request, response, 3);
                }
            // handle usergamedata_advanced.usernew requests
            } else if (mode.equals("usernew")) {
                return this.handleNewUserRequest(refid, request, response);
            // handle usergamedata_advanced.inheritance requests
            } else if (mode.equals("inheritance")) {
                return this.handleInheritanceRequest(request, response);
            // handle usergamedata_advanced.ghostload requests
            } else if (mode.equals("ghostload")) {
                return this.handleGhostLoadRequest(XmlUtils.intAtPath(requestBody, "/playerdata/data/ghostid"), request, response);
            // handle usergamedata_advanced.usersave requests, for saving scores, ghost data, etc.
            } else if (mode.equals("usersave")) {
                return this.handleUserSaveRequest(requestBody, request, response);
            // handle usergamedata_advanced.minidump requests
            } else if (mode.equals("minidump")) {
                return this.handleMinidumpRequest(request, response);
            }
        // handle usergamedata_send requests
        } else if (requestMethod.equals("usergamedata_send")) {
            return this.handleUserGameDataSendRequest(requestBody, request, response);
        // handle usergamedata_recv requests
        } else if (requestMethod.equals("usergamedata_recv")) {
            return this.handleUserGameDataRecvRequest(requestBody, request, response);
        }

        throw new UnsupportedRequestException();
    }

    /**
     * Handles a request to get the machine high scores.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleMachineScoresRequest(final Request request, final Response response) {
        final List<UserSongRecord> allRecords = this.songRecordDao.findByMachine(request.attribute("pcbid"));
        final HashMap<Integer, HashMap<Integer, Object[]>> topRecords = this.sortScoresByTopScore(allRecords);

        return this.sendScoresToClient(request, response, topRecords);
    }

    /**
     * Handles a request for area high scores
     * @param area The area to load scores for
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleAreaScoresRequest(final String area, final Request request, final Response response) {
        final List<UserSongRecord> allRecords = this.songRecordDao.findByShopArea(area);
        final HashMap<Integer, HashMap<Integer, Object[]>> topRecords = this.sortScoresByTopScore(allRecords);

        return this.sendScoresToClient(request, response, topRecords);
    }

    /**
     * Handles a request for the global server scores.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGlobalScoresRequest(final Request request, final Response response) {
        final List<UserSongRecord> allRecords = this.songRecordDao.findAll();
        final HashMap<Integer, HashMap<Integer, Object[]>> topRecords = this.sortScoresByTopScore(allRecords);

        return this.sendScoresToClient(request, response, topRecords);
    }

    /**
     * Handle a request to load the scores for a particular rival.
     * @param refId The refId for the calling card
     * @param request The Spark request
     * @param response The Spark response
     * @param which Which rival to load (1-3)
     * @return A response object for Spark
     */
    private Object handleRivalScoresRequest(final String refId, final Request request, final Response response, final int which) {
        final UserProfile user = this.profileDao.findByUser(this.cardDao.findByRefId(refId).getUser());
        UserProfile rival;

        if (which == 1) {
            rival = user.getRival1();
        } else if (which == 2) {
            rival = user.getRival2();
        } else {
            rival = user.getRival3();
        }

        final List<UserSongRecord> rivalRecords = this.songRecordDao.findByUser(rival);
        final HashMap<Integer, HashMap<Integer, Object[]>> topRecords = this.sortScoresByTopScore(rivalRecords);

        return this.sendScoresToClient(request, response, topRecords);
    }

    /**
     * Handles a request for the global server scores.
     * @param request The Spark request
     * @param response The Spark response
     * @param topRecords The sorted list of top records to send to the client
     * @return A response object for Spark
     */
    private Object sendScoresToClient(final Request request, final Response response, final HashMap<Integer, HashMap<Integer, Object[]>> topRecords) {
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .e("data")
                        .s32("recordtype", 0).up();

        // we need to return the top score for every song/difficulty
        for (Map.Entry<Integer, HashMap<Integer, Object[]>> entry : topRecords.entrySet()) {
            // iterate through each difficulty
            for (int i = 0; i < 9; i++) {
                if (entry.getValue().containsKey(i)) {
                    Object[] record = entry.getValue().get(i);
                    UserSongRecord topRecord = (UserSongRecord) record[1];

                    respBuilder = respBuilder.e("record")
                            .u32("mcode", topRecord.getSongId()).up()
                            .u8("notetype", i).up()
                            .u8("rank", topRecord.getRank()).up()
                            .u8("clearkind", topRecord.getClearKind()).up()
                            .u8("flagdata", 0).up()
                            .str("name", topRecord.getUser().getName()).up()
                            .s32("area", topRecord.getArea()).up()
                            .s32("code", topRecord.getUser().getDancerCode()).up()
                            .s32("score", topRecord.getScore()).up()
                            .s32("ghostid", ((Long) topRecord.getGhostData().getId()).intValue()).up(2);
                }
            }
        }

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
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .bool("is_new", false).up();

        // load user scores if this was for a particular user
        if (!refId.equals("X0000000000000000000000000000000")) {
            // get the user's scores and sort them out so we can get the top scores
            // for each song/difficulty
            final List<UserSongRecord> userRecords =
                    this.songRecordDao.findByUser(
                            this.profileDao.findByUser(
                                    this.cardDao.findByRefId(refId).getUser()));

            // sort them by song and difficulty, then insert them into the response
            // key for top map is the song ID
            // key for the 2nd map is the difficulty
            // the array: a[0] = count, a[1] = top UserSongRecord
            final HashMap<Integer, HashMap<Integer, Object[]>> userTopScores = this.sortScoresByTopScore(userRecords);

            int count = 0;
            int rank = 0;
            int clearkind = 0;
            int score = 0;
            int ghostid = 0;

            // insert them into the response
            for (Map.Entry<Integer, HashMap<Integer, Object[]>> entry : userTopScores.entrySet()) {
                respBuilder = respBuilder.e("music")
                        .u32("mcode", entry.getKey()).up();

                // iterate through each difficulty
                for (int i = 0; i < 9; i++) {
                    if (entry.getValue().containsKey(i)) {
                        Object[] record = entry.getValue().get(i);
                        UserSongRecord topRecord = (UserSongRecord) record[1];

                        count = (Integer) record[0];
                        rank = topRecord.getRank();
                        clearkind = topRecord.getClearKind();
                        score = topRecord.getScore();
                        ghostid = ((Long) topRecord.getGhostData().getId()).intValue();
                    } else {
                        count = 0;
                        rank = 0;
                        clearkind = 0;
                        score = 0;
                        ghostid = 0;
                    }

                    respBuilder = respBuilder.e("note")
                            .u16("count", count).up()
                            .u8("rank", rank).up()
                            .u8("clearkind", clearkind).up()
                            .s32("score", score).up()
                            .s32("ghostid", ghostid).up().up();
                }

                respBuilder = respBuilder.up();
            }
        }

        // TODO: Events isn't supposed to be a static response
        final Document document = respBuilder.getDocument();
        final Element elem = respBuilder.getElement();

        for (int i = 0; i < EVENTS.getLength(); i++) {
            Node tmp = document.importNode(EVENTS.item(i), true);
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
        // TODO: Figure out what all these are. Right now we're just saving it as-is and sending it back as-is for the
        // next session and that seems to be working...
        final String defaultLastCsv = "1,6c76656c,3431766c,1ad,3,1,3,4,1,7753ba,b65b5,8000000000000001,8000000000000001,0,0,5c2d1455,0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,,,,,,,,";

        profile = new UserProfile(card.getUser(), null, dancerCode, 33, true, 0, 0, 0, -1, 0.0, DancerOption.BABYLON,
                SpeedOption.X_1_00, BoostOption.NORMAL, AppearanceOption.VISIBLE, TurnOption.OFF, StepZoneOption.ON,
                ScrollOption.NORMAL, ArrowColorOption.RAINBOW, CutOption.OFF, FreezeArrowOption.ON, JumpsOption.ON,
                ArrowSkinOption.NORMAL, ScreenFilterOption.DARK, GuideLinesOption.OFF, LifeGaugeOption.NORMAL,
                JudgementLayerOption.BACKGROUND, true, 0, null, null, null, defaultLastCsv);
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
        final String refId = XmlUtils.strAtPath(requestBody, "/playerdata/data/refid");
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
        final int recvNum = XmlUtils.intAtPath(requestBody, "/playerdata/data/recv_num");
        final String recvCsv = XmlUtils.strAtPath(requestBody, "/playerdata/data/recv_csv");
        String[] tmp = recvCsv.split(",", -1);
        String[] colsToSend = new String[recvNum];

        for (int i = 0; i < recvNum; i++) {
            colsToSend[i] = tmp[i * 2];
        }

        // construct the response
        KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .e("player")
                        .u32("record_num", recvNum).up()
                        .e("record");

        String val;

        for (String col : colsToSend) {
            if (col.equals("COMMON")) {
                val = this.buildCommonCsv(profile);
            } else if (col.equals("OPTION")) {
                val = this.buildOptionCsv(profile);
            } else if (col.equals("LAST")) {
                val = this.buildLastCsv(profile);
            } else if (col.equals("RIVAL")) {
                val = this.buildRivalCsv(profile);
            } else {
                throw new InvalidRequestException();
            }

            builder = builder.str("d", Base64.getEncoder().encodeToString(val.getBytes())).up();
        }

        // send the response
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
        final String refId = XmlUtils.strAtPath(requestBody, "/playerdata/data/refid");
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
        final boolean forceExtraStage = Boolean.parseBoolean(this.isForceExtraStage);

        // modify the contents to send back
        String dancerCodeStr = String.format("%08d", profile.getDancerCode());
        dancerCodeStr = dancerCodeStr.substring(0, 4) + "-" + dancerCodeStr.substring(4, 8);

        elems[GAME_COMMON_SEQ_HEX_OFFSET] = Integer.toHexString(profile.getDancerCode());
        elems[GAME_COMMON_AREA_OFFSET] = Integer.toHexString(profile.getArea());
        elems[GAME_COMMON_WEIGHT_DISPLAY_OFFSET] = profile.isDisplayWeight() ? "1" : "0";
        elems[GAME_COMMON_CHARACTER_OFFSET] = Integer.toHexString(profile.getCharacter().ordinal());
        elems[GAME_COMMON_EXTRA_CHARGE_OFFSET] = Integer.toHexString(forceExtraStage ? 10 : profile.getExtraCharge());
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
        final String[] elems = profile.getUnkLast().split(",", -1);

        // modify the last song to be whatever they last played
        final UserSongRecord lastScore = this.songRecordDao.findLatestScoreForUser(profile);

        if (lastScore != null) {
            elems[GAME_LAST_SONG_OFFSET] = Integer.toHexString(lastScore.getSongId());
        }

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
        elems[GAME_RIVAL_SLOT_2_ACTIVE_OFFSET] = profile.getRival2() == null ? "0" : "2";
        elems[GAME_RIVAL_SLOT_3_ACTIVE_OFFSET] = profile.getRival3() == null ? "0" : "3";

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
            profile.setUnkLast(String.join(",", last));
        }

        // parse out RIVAL values
        if (rival != null &&
                rival.length != 0) {
            final boolean rival1Active = rival[GAME_RIVAL_SLOT_1_ACTIVE_OFFSET].equals("0") ? false : true;
            final boolean rival2Active = rival[GAME_RIVAL_SLOT_2_ACTIVE_OFFSET].equals("0") ? false : true;
            final boolean rival3Active = rival[GAME_RIVAL_SLOT_3_ACTIVE_OFFSET].equals("0") ? false : true;
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
        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .s32("InheritanceStatus", 1);

        return this.sendResponse(request, response, builder);
    }

    /**
     * Handles a request to load a particular ghost data set.
     * @param ghostId The ghost ID to load.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGhostLoadRequest(final int ghostId, final Request request, final Response response) {
        KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up();

        final GhostData ghostData = this.ghostDataDao.findById((long) ghostId);

        if (ghostData != null) {
            builder = builder.e("ghostdata")
                        .s32("code", 0).up()
                        .u32("mcode", ghostData.getMcode()).up()
                        .u8("notetype", ghostData.getNoteType()).up()
                        .s32("ghostsize", ghostData.getGhostData().length()).up()
                        .str("ghost", ghostData.getGhostData());
        }

        return this.sendResponse(request, response, builder);
    }


    /**
     * Handles a request to save scores, options, etc.
     * @param requestBody The XML document of the incoming request.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleUserSaveRequest(final Element requestBody, final Request request, final Response response) {
        // make sure the user actually exists for this score and the name matches
        final Element dataNode = (Element) XmlUtils.nodeAtPath(requestBody, "/playerdata/data");

        final int dancerCode = XmlUtils.intAtChild(dataNode, "ddrcode");
        final String username = XmlUtils.strAtChild(dataNode, "name");
        final UserProfile user = this.profileDao.findByDancerCode(dancerCode);

        if (user == null ||
                user.getName() == null ||
                !user.getName().equals(username)) {
            throw new InvalidRequestException();
        }

        // get the top-level info first
        final int playSide = XmlUtils.intAtChild(dataNode, "playside");
        final int playStyle = XmlUtils.intAtChild(dataNode, "playstyle");
        final int area = XmlUtils.intAtChild(dataNode, "area");
        final int weight100 = XmlUtils.intAtChild(dataNode, "weight100");
        final String shopName = XmlUtils.strAtChild(dataNode, "shopname");
        final boolean isPremium = XmlUtils.boolAtChild(dataNode, "ispremium");
        final boolean isEaPass = XmlUtils.boolAtChild(dataNode, "iseapass");
        final boolean isTakeover = XmlUtils.boolAtChild(dataNode, "istakeover");
        final boolean isRepeater = XmlUtils.boolAtChild(dataNode, "isrepeater");
        final boolean isGameover = XmlUtils.boolAtChild(dataNode, "isgameover");
        final String locationId = XmlUtils.strAtChild(dataNode, "locid");
        final String shopArea = XmlUtils.strAtChild(dataNode, "shoparea");

        // parse out the nodes for each record and save them individually
        final NodeList recordNodes = XmlUtils.nodesAtPath(dataNode, "/data/note");

        for (int i = 0; i < recordNodes.getLength(); i++) {
            Element recordNode = (Element) recordNodes.item(i);
            int songId = XmlUtils.intAtChild(recordNode, "mcode");

            // if the song ID is 0, this record is empty
            if (songId == 0) {
                continue;
            }

            LocalDateTime endtime = TimeUtils.timeFromEpoch(XmlUtils.longAtChild(recordNode, "endtime"));

            // make sure we haven't already written this record, since Ace re-sends records at the end
            // of a play session for all played songs. username + endtime should be a unique combination.
            if (songRecordDao.findByEndtimeAndUser(endtime, user) != null) {
                continue;
            }

            // passed the sanity checks, let's parse and save the new record
            int stageNum = XmlUtils.intAtChild(recordNode, "stagenum");
            int noteType = XmlUtils.intAtChild(recordNode, "notetype");
            int rank = XmlUtils.intAtChild(recordNode, "rank");
            int clearKind = XmlUtils.intAtChild(recordNode, "clearkind");
            int score = XmlUtils.intAtChild(recordNode, "score");
            int exScore = XmlUtils.intAtChild(recordNode, "exscore");
            int maxCombo = XmlUtils.intAtChild(recordNode, "maxcombo");
            int life = XmlUtils.intAtChild(recordNode, "life");
            int fastCount = XmlUtils.intAtChild(recordNode, "fastcount");
            int slowCount = XmlUtils.intAtChild(recordNode, "slowcount");
            int marvelousCount = XmlUtils.intAtChild(recordNode, "judge_marvelous");
            int perfectCount = XmlUtils.intAtChild(recordNode, "judge_perfect");
            int greatCount = XmlUtils.intAtChild(recordNode, "judge_great");
            int goodCount = XmlUtils.intAtChild(recordNode, "judge_good");
            int booCount = XmlUtils.intAtChild(recordNode, "judge_boo");
            int missCount = XmlUtils.intAtChild(recordNode, "judge_miss");
            int okCount = XmlUtils.intAtChild(recordNode, "judge_ok");
            int ngCount = XmlUtils.intAtChild(recordNode, "judge_ng");
            int calories = XmlUtils.intAtChild(recordNode, "calorie");
            String ghostStr = XmlUtils.strAtChild(recordNode, "ghost");
            SpeedOption speedOption = SpeedOption.optionForValue(XmlUtils.intAtChild(recordNode, "opt_speed"));
            BoostOption boostOption = BoostOption.values()[XmlUtils.intAtChild(recordNode, "opt_boost")];
            AppearanceOption appearanceOption = AppearanceOption.optionForValue(XmlUtils.intAtChild(recordNode, "opt_appearance"));
            TurnOption turnOption = TurnOption.values()[XmlUtils.intAtChild(recordNode, "opt_turn")];
            StepZoneOption stepZoneOption = StepZoneOption.values()[XmlUtils.intAtChild(recordNode, "opt_dark")];
            ScrollOption scrollOption = ScrollOption.values()[XmlUtils.intAtChild(recordNode, "opt_scroll")];
            ArrowColorOption arrowColorOption = ArrowColorOption.values()[XmlUtils.intAtChild(recordNode, "opt_arrowcolor")];
            CutOption cutOption = CutOption.values()[XmlUtils.intAtChild(recordNode, "opt_cut")];
            FreezeArrowOption freezeArrowOption = FreezeArrowOption.values()[XmlUtils.intAtChild(recordNode, "opt_freeze")];
            JumpsOption jumpsOption = JumpsOption.values()[XmlUtils.intAtChild(recordNode, "opt_jump")];
            ArrowSkinOption arrowSkinOption = ArrowSkinOption.values()[XmlUtils.intAtChild(recordNode, "opt_arrowshape")];
            ScreenFilterOption screenFilterOption = ScreenFilterOption.values()[XmlUtils.intAtChild(recordNode, "opt_filter")];
            GuideLinesOption guideLinesOption = GuideLinesOption.values()[XmlUtils.intAtChild(recordNode, "opt_guideline")];
            LifeGaugeOption lifeGaugeOption = LifeGaugeOption.values()[XmlUtils.intAtChild(recordNode, "opt_gauge")];
            JudgementLayerOption judgementLayerOption = JudgementLayerOption.values()[XmlUtils.intAtChild(recordNode, "opt_judgepriority")];
            boolean showFastSlow = XmlUtils.boolAtChild(recordNode, "opt_timing");
            String songBaseName = XmlUtils.strAtChild(recordNode, "basename");
            String songTitle = new String(Base64.getDecoder().decode(XmlUtils.strAtChild(recordNode, "title_b64")));
            String songArtist = new String(Base64.getDecoder().decode(XmlUtils.strAtChild(recordNode, "artist_b64")));
            int bpmMax = XmlUtils.intAtChild(recordNode, "bpmMax");
            int bpmMin = XmlUtils.intAtChild(recordNode, "bpmMin");
            int level = XmlUtils.intAtChild(recordNode, "level");
            int series = XmlUtils.intAtChild(recordNode, "series");
            int bemaniFlag = XmlUtils.intAtChild(recordNode, "bemaniFlag");
            int genreFlag = XmlUtils.intAtChild(recordNode, "genreFlag");
            int limited = XmlUtils.intAtChild(recordNode, "limited");
            int region = XmlUtils.intAtChild(recordNode, "region");
            int grVoltage = XmlUtils.intAtChild(recordNode, "gr_voltage");
            int grStream = XmlUtils.intAtChild(recordNode, "gr_stream");
            int grChaos = XmlUtils.intAtChild(recordNode, "gr_chaos");
            int grFreeze = XmlUtils.intAtChild(recordNode, "gr_freeze");
            int grAir = XmlUtils.intAtChild(recordNode, "gr_air");
            boolean share = XmlUtils.boolAtChild(recordNode, "share");
            int folder = XmlUtils.intAtChild(recordNode, "folder");

            // construct and save
            GhostData newGhostData = new GhostData(user, ghostStr, songId, noteType);
            this.ghostDataDao.create(newGhostData);

            UserSongRecord newRecord = new UserSongRecord(user, request.attribute("pcbid"), playSide, playStyle, area, weight100,
                    shopName, isPremium, isEaPass, isTakeover, isRepeater, isGameover, locationId, shopArea, stageNum, songId,
                    noteType, rank, clearKind, score, exScore, maxCombo, life, fastCount, slowCount, marvelousCount, perfectCount,
                    greatCount, goodCount, booCount, missCount, okCount, ngCount, calories, newGhostData, speedOption, boostOption,
                    appearanceOption, turnOption, stepZoneOption, scrollOption, arrowColorOption, cutOption, freezeArrowOption,
                    jumpsOption, arrowSkinOption, screenFilterOption, guideLinesOption, lifeGaugeOption, judgementLayerOption,
                    showFastSlow, songBaseName, songTitle, songArtist, bpmMin, bpmMax, level, series, bemaniFlag, genreFlag,
                    limited, region, grVoltage, grStream, grChaos, grFreeze, grAir, share, endtime, folder);
            this.songRecordDao.create(newRecord);
        }

        final KXmlBuilder builder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up();

        return this.sendResponse(request, response, builder);
    }

    /**
     * Handles a request to post a client mini dump.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleMinidumpRequest(final Request request, final Response response) {
        // TODO: Save this eventually
        final KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0);

        return this.sendResponse(request, response, respBuilder);
    }

    /**
     * Sorts scores into a hierarchy of top score by song/difficulty. Used for the various score requests.
     * @param records The records to sort
     * @return The sorted results
     */
    private HashMap<Integer, HashMap<Integer, Object[]>> sortScoresByTopScore(final List<UserSongRecord> records) {
        // sort them by song and difficulty, then insert them into the response
        // key for top map is the song ID
        // key for the 2nd map is the difficulty
        // the array: a[0] = count, a[1] = top UserSongRecord
        final HashMap<Integer, HashMap<Integer, Object[]>> topScores = new HashMap<>();

        for (UserSongRecord record : records) {
            if (!topScores.containsKey(record.getSongId())) {
                topScores.put(record.getSongId(), new HashMap<>());
                topScores.get(record.getSongId()).put(record.getNoteType(), new Object[] { 1, record });
            } else {
                if (!topScores.get(record.getSongId()).containsKey(record.getNoteType())) {
                    topScores.get(record.getSongId()).put(record.getNoteType(), new Object[] { 1, record });
                } else {
                    Object[] currRecord = topScores.get(record.getSongId()).get(record.getNoteType());
                    currRecord[0] = ((Integer) currRecord[0]) + 1;

                    if (((UserSongRecord) currRecord[1]).getScore() < record.getScore()) {
                        currRecord[1] = record;
                    }

                    topScores.get(record.getSongId()).put(record.getNoteType(), currRecord);
                }
            }
        }

        return topScores;
    }
}
