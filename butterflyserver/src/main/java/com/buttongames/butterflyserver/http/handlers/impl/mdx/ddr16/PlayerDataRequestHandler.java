package com.buttongames.butterflyserver.http.handlers.impl.mdx.ddr16;

import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.EventSaveDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GlobalEventDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeaguePeriodDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeagueStatusDao;
import com.buttongames.butterflymodel.model.ddr16.EventSaveData;
import com.buttongames.butterflymodel.model.ddr16.GlobalEvent;
import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.CardDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GhostDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.UserSongRecordDao;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeaguePeriod;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeagueStatus;
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
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import spark.Request;
import spark.Response;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

    private final Logger LOG = LogManager.getLogger(PlayerDataRequestHandler.class);

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

    private static final String NO_DATA_VALUE = "<NODATA>";

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
     * The DAO for managing global events.
     */
    private final GlobalEventDao globalEventDao;

    /**
     * The DAO for managing user events progress.
     */
    private final EventSaveDataDao eventSaveDataDao;

    /**
     * The DAO for managing user Golden League progress.
     */
    private final GoldenLeagueStatusDao goldenLeagueStatusDao;

    /**
     * The DAO for managing Golden League periods.
     */
    private final GoldenLeaguePeriodDao goldenLeaguePeriodDao;

    /**
     * Says whether or not we force every session to have extra stage.
     */
    @Value(PropertyNames.FORCE_EXTRA_STAGE)
    private String isForceExtraStage;

    /**
     * Says whether or not we use percentiles for Golden League promotions, or raw thresholds.
     */
    @Value(PropertyNames.GOLDEN_LEAGUE_PERCENTILES)
    private String isGoldenLeaguePercentiles;

    /**
     * This is a set of base user-level events we create for users when
     * they have no event progress saved, yet.
     */
    private final List<EventSaveData> baseUserEvents;

    public PlayerDataRequestHandler(final ButterflyUserDao userDao, final CardDao cardDao, final ProfileDao profileDao,
                                    final GhostDataDao ghostDataDao, final UserSongRecordDao songRecordDao,
                                    final GlobalEventDao globalEventDao, final EventSaveDataDao eventSaveDataDao,
                                    final GoldenLeagueStatusDao goldenLeagueStatusDao, final GoldenLeaguePeriodDao goldenLeaguePeriodDao) {
        this.userDao = userDao;
        this.cardDao = cardDao;
        this.profileDao = profileDao;
        this.ghostDataDao = ghostDataDao;
        this.songRecordDao = songRecordDao;
        this.globalEventDao = globalEventDao;
        this.eventSaveDataDao = eventSaveDataDao;
        this.goldenLeagueStatusDao = goldenLeagueStatusDao;
        this.goldenLeaguePeriodDao = goldenLeaguePeriodDao;

        this.baseUserEvents = ImmutableList.of(
                // Baby-Lon's Adventure
                new EventSaveData(null, 999, 30, 0, 0, 0, 0, 5)
        );
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
        final List<UserSongRecord> allRecords = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            allRecords.addAll(this.songRecordDao.findTopScoresForDifficultyByMachine(request.attribute("pcbid"), i));
        }

        final HashMap<Integer, HashMap<Integer, UserSongRecord>> topRecords = this.sortTopScores(allRecords);
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
        final List<UserSongRecord> allRecords = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            allRecords.addAll(this.songRecordDao.findTopScoresForDifficultyByShopArea(area, i));
        }

        final HashMap<Integer, HashMap<Integer, UserSongRecord>> topRecords = this.sortTopScores(allRecords);
        return this.sendScoresToClient(request, response, topRecords);
    }

    /**
     * Handles a request for the global server scores.
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleGlobalScoresRequest(final Request request, final Response response) {
        final List<UserSongRecord> allRecords = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            allRecords.addAll(this.songRecordDao.findTopScoresForDifficulty(i));
        }

        final HashMap<Integer, HashMap<Integer, UserSongRecord>> topRecords = this.sortTopScores(allRecords);
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

        final List<UserSongRecord> allRecords = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            allRecords.addAll(this.songRecordDao.findTopScoresForDifficultyByUser(rival, i));
        }

        final HashMap<Integer, HashMap<Integer, UserSongRecord>> topRecords = this.sortTopScores(allRecords);
        return this.sendScoresToClient(request, response, topRecords);
    }

    /**
     * Handles a request for the global server scores.
     * @param request The Spark request
     * @param response The Spark response
     * @param topRecords The sorted list of top records to send to the client
     * @return A response object for Spark
     */
    private Object sendScoresToClient(final Request request, final Response response, final HashMap<Integer, HashMap<Integer, UserSongRecord>> topRecords) {
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .e("data")
                        .s32("recordtype", 0).up();

        // we need to return the top score for every song/difficulty
        for (Map.Entry<Integer, HashMap<Integer, UserSongRecord>> entry : topRecords.entrySet()) {
            // iterate through each difficulty
            for (int i = 0; i < 9; i++) {
                if (entry.getValue().containsKey(i)) {
                    UserSongRecord topRecord = entry.getValue().get(i);

                    respBuilder = respBuilder.e("record")
                            .u32("mcode", topRecord.getSongId()).up()
                            .u8("notetype", i).up()
                            .u8("rank", topRecord.getGrade()).up()
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
     * Handles a request for the user scores and event data.
     * @param refId The refId for the calling card
     * @param request The Spark request
     * @param response The Spark response
     * @return A response object for Spark
     */
    private Object handleUserLoadRequest(final String refId, final Request request, final Response response) {
        KXmlBuilder respBuilder = KXmlBuilder.create("response")
                .e("playerdata")
                    .s32("result", 0).up()
                    .bool("is_new", false).up()
                    .bool("is_refid_locked", false).up();

        // load user data if this was for a particular user
        if (!refId.equals("X0000000000000000000000000000000")) {
            /////////////////////////////////////////////////////////////////////////////////////////////////
            // get the user's scores and sort them out so we can get the top scores
            // for each song/difficulty
            final UserProfile user = this.profileDao.findByUser(
                    this.cardDao.findByRefId(refId).getUser());

            if (user != null) {
                final List<UserSongRecord> userRecords = new ArrayList<>();

                // sort them by song and difficulty, then insert them into the response
                // key for top map is the song ID
                // key for the 2nd map is the difficulty
                for (int i = 0; i < 9; i++) {
                    userRecords.addAll(this.songRecordDao.findTopScoresForDifficultyByUser(user, i));
                }

                final HashMap<Integer, HashMap<Integer, UserSongRecord>> userTopScores = this.sortTopScores(userRecords);
                int count = 0;
                int rank = 0;
                int clearkind = 0;
                int score = 0;
                int ghostid = 0;

                // insert them into the response
                for (Map.Entry<Integer, HashMap<Integer, UserSongRecord>> entry : userTopScores.entrySet()) {
                    respBuilder = respBuilder.e("music")
                            .u32("mcode", entry.getKey()).up();

                    // iterate through each difficulty
                    for (int i = 0; i < 9; i++) {
                        if (entry.getValue().containsKey(i)) {
                            UserSongRecord topRecord = entry.getValue().get(i);

                            count = 1;
                            rank = topRecord.getGrade();
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

                /////////////////////////////////////////////////////////////////////////////////////////////////
                // set the user's dan ranking
                respBuilder.e("grade")
                        .u32("single_grade", user.getSingleClass()).up()
                        .u32("double_grade", user.getDoubleClass()).up().up();

                /////////////////////////////////////////////////////////////////////////////////////////////////
                // set the user's Golden League status as appropriate
                final GoldenLeaguePeriod lastPeriod = this.goldenLeaguePeriodDao.getLastGoldenLeaguePeriod();
                final boolean usePercentileRankings = Boolean.parseBoolean(this.isGoldenLeaguePercentiles);

                if (lastPeriod != null) {
                    // there's a Golden League period defined in the database
                    GoldenLeagueStatus status = this.goldenLeagueStatusDao.findByUserAndPeriod(user, lastPeriod);

                    // create a status for this period and user if none exists, otherwise just update their status for this period
                    if (status == null) {
                        // see if there's a status for the previous period to copy over, otherwise create a fresh one
                        GoldenLeaguePeriod prevPeriod = this.goldenLeaguePeriodDao.getGoldenLeaguePeriodById(lastPeriod.getId() - 1);
                        GoldenLeagueStatus prevStatus = null;

                        if (prevPeriod != null) {
                            prevStatus = this.goldenLeagueStatusDao.findByUserAndPeriod(user, prevPeriod);
                        }

                        if (prevStatus == null) {
                            status = new GoldenLeagueStatus(user, lastPeriod, 1, 0, 0, 1, false);
                        } else {
                            status = new GoldenLeagueStatus(user, lastPeriod, prevStatus.getGoldenLeagueClass(), 0, 0, 1, false);
                        }

                        this.goldenLeagueStatusDao.create(status);
                    }

                    // calculate the promotion and demotion criteria
                    float promotionPercentile = 0.0f;
                    float demotionPercentile = 0.0f;
                    int promotionExScore = 0;
                    int demotionExScore = 0;
                    int totalPlayers = 0;
                    boolean addedPeriodInfo = false;

                    if (status.getGoldenLeagueClass() <= 1) {
                        promotionPercentile = lastPeriod.getBronzePromotionPercentage();
                        promotionExScore = lastPeriod.getBronzePromotionExScore();
                        totalPlayers = lastPeriod.getNumBronzePlayers();
                    } else if (status.getGoldenLeagueClass() == 2) {
                        promotionPercentile = lastPeriod.getSilverPromotionPercentage();
                        promotionExScore = lastPeriod.getSilverPromotionExScore();
                        demotionPercentile = lastPeriod.getSilverDemotionPercentage();
                        demotionExScore = lastPeriod.getSilverDemotionExScore();
                        totalPlayers = lastPeriod.getNumSilverPlayers();
                    } else {
                        demotionPercentile = lastPeriod.getGoldDemotionPercentage();
                        demotionExScore = lastPeriod.getGoldDemotionExScore();
                        totalPlayers = lastPeriod.getNumGoldPlayers();
                    }

                    respBuilder = respBuilder.e("golden_league")
                            .s32("league_class", status.getGoldenLeagueClass()).up();

                    if (!lastPeriod.isPeriodCurrent() &&
                            status.getLeagueEndTransition()) {
                        // the last period is no longer active, so calculate promotions and demotions and show results if necessary
                        status.setLeagueEndTransition(false);

                        // calculate promotions and demotions
                        int origClass = status.getGoldenLeagueClass();

                        if (status.getGoldenLeagueClass() < 3 &&
                                status.getTotalExScore() >= promotionExScore) {
                            status.setGoldenLeagueClass(status.getGoldenLeagueClass() + 1);
                        } else if (status.getGoldenLeagueClass() > 1 &&
                                status.getTotalExScore() <= demotionExScore) {
                            status.setGoldenLeagueClass(status.getGoldenLeagueClass() - 1);
                        }

                        this.goldenLeagueStatusDao.update(status);

                        respBuilder = respBuilder.e("result")
                                .s32("id", lastPeriod.getId()).up()
                                .str("league_name_base64", Base64.getEncoder().encodeToString(lastPeriod.getName().getBytes(StandardCharsets.UTF_8))).up()
                                .u64("start_time", lastPeriod.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).up()
                                .u64("end_time", lastPeriod.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).up()
                                .u64("summary_time", lastPeriod.getSummaryTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).up()
                                .s32("league_status", 0).up()
                                .s32("league_class", origClass).up()
                                .s32("league_class_result", status.getGoldenLeagueClass()).up()
                                .s32("ranking_number", status.getGoldenLeagueRank()).up()
                                .s32("total_exscore", status.getTotalExScore()).up()
                                .s32("total_play_count", status.getNumJoins()).up()
                                .s32("join_number", lastPeriod.getNumBronzePlayers()).up(3);
                        addedPeriodInfo = true;
                    } else if (lastPeriod.isPeriodCurrent()) {
                        // the period is currently active
                        status.setLeagueEndTransition(true);
                        status.setNumJoins(status.getNumJoins() + 1);
                        this.goldenLeagueStatusDao.update(status);

                        respBuilder = respBuilder.e("current")
                                .s32("id", lastPeriod.getId()).up()
                                .str("league_name_base64", Base64.getEncoder().encodeToString(lastPeriod.getName().getBytes(StandardCharsets.UTF_8))).up()
                                .u64("start_time", lastPeriod.getStartTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).up()
                                .u64("end_time", lastPeriod.getEndTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).up()
                                .u64("summary_time", lastPeriod.getSummaryTime().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).up()
                                .s32("league_status", 1).up()
                                .s32("league_class", status.getGoldenLeagueClass()).up()
                                .s32("league_class_result", status.getGoldenLeagueClass()).up()
                                .s32("ranking_number", usePercentileRankings ? status.getGoldenLeagueRank() : status.getTotalExScore()).up() // line on the in-game graph
                                .s32("total_exscore", status.getTotalExScore()).up()
                                .s32("total_play_count", status.getNumJoins()).up();
                        addedPeriodInfo = true;

                        final boolean[] showPromotions = {true, true, false};
                        final boolean[] showDemotions = {false, true, true};

                        if (usePercentileRankings) {
                            // we're using global ranking to determine promotions and demotions, so just use
                            // the *actual* promotion and demotion ranking numbers
                            if (showPromotions[status.getGoldenLeagueClass() - 1]) {
                                respBuilder = respBuilder.s32("promotion_ranking_number", (int) (promotionPercentile * totalPlayers)).up() // promotion line on the graph
                                        .s32("promotion_exscore", promotionExScore).up();
                            }

                            if (showDemotions[status.getGoldenLeagueClass() - 1]) {
                                respBuilder = respBuilder.s32("demotion_ranking_number", (int) (demotionPercentile * totalPlayers)).up() // demotion line on the graph
                                        .s32("demotion_exscore", demotionExScore).up();
                            }

                            respBuilder = respBuilder.s32("join_number", totalPlayers).up(); // max of the in-game graph
                        } else {
                            // if we're not using global ranking, we need to sort of fake out the in-game graph to be relative to the user's
                            // EX score, instead of their ranking on the global leaderboard.
                            if (showPromotions[status.getGoldenLeagueClass() - 1]) {
                                respBuilder = respBuilder.s32("promotion_ranking_number", promotionExScore).up() // promotion line on the graph
                                        .s32("promotion_exscore", promotionExScore).up();
                            }

                            if (showDemotions[status.getGoldenLeagueClass() - 1]) {
                                respBuilder = respBuilder.s32("demotion_ranking_number", demotionExScore).up() // demotion line on the graph
                                        .s32("demotion_exscore", demotionExScore).up();
                            }

                            // max of the in-game graph, we'll make sure it's higher than both the current EX score and
                            // total EX score combined, so everything fits on the graph guaranteed
                            respBuilder = respBuilder.s32("join_number", (int) (1.5 * (status.getTotalExScore() + promotionExScore))).up();
                        }

                        respBuilder = respBuilder.up(2);
                    }

                    if (!addedPeriodInfo) {
                        respBuilder = respBuilder.up();
                    }
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////
                // 20th Anniversary Grand Finale?
                respBuilder = respBuilder.e("championship")
                        .s32("championship_id", 1).up()
                        .str("name_base64", "c2tvZydlbXM=").up()
                        .bool("is_entry", true).up()
                        .e("lang")
                        .str("destinationcodes", "33").up()
                        .str("name_base64", "c2tvZydlbXM=").up(2)
                        .e("music")
                        .u32("mcode", 38222).up()
                        .s8("notetype", 4).up()
                        .s32("playstyle", 0).up(3);

                /////////////////////////////////////////////////////////////////////////////////////////////////
                // enable the song rewards for the "Ichika no BEMANI touhyou senbatsusen 2019" event
                int[] mcodes = {38264, 38289, 38279, 38291, 38292, 38294, 38290, 38293, 38295};
                int eventId = 1010;

                for (int mcode : mcodes) {
                    respBuilder = respBuilder.e("eventdata")
                            .u32("eventid", eventId++).up()
                            .s32("eventtype", 10).up()
                            .u32("eventno", 0).up()
                            .s64("condition", 10000).up()
                            .u32("reward", mcode).up()
                            .s32("comptime", 1).up()
                            .s64("savedata", 0).up().up();
                }

                /////////////////////////////////////////////////////////////////////////////////////////////////
                // set the user's user-level event progress
                final List<EventSaveData> userEvents = this.eventSaveDataDao.findByUser(user);

                if (userEvents.isEmpty()) {
                    // if the user has no event progress, create their base event progress
                    for (EventSaveData event : this.baseUserEvents) {
                        EventSaveData newEvent = new EventSaveData(event);
                        newEvent.setUser(user);

                        this.eventSaveDataDao.create(newEvent);
                        userEvents.add(newEvent);
                    }
                }

                for (EventSaveData event : userEvents) {
                    respBuilder = respBuilder.e("eventdata")
                            .u32("eventid", event.getEventId()).up()
                            .s32("eventtype", event.getEventType()).up()
                            .u32("eventno", event.getEventNo()).up()
                            .s64("condition", event.getEventCondition()).up()
                            .u32("reward", event.getReward()).up()
                            .s32("comptime", (int) event.getCompTime()).up()
                            .s64("savedata", event.getSaveData()).up().up();
                }
            }
        }

        /////////////////////////////////////////////////////////////////////////////////////////////////
        // set the global events on the response
        final List<GlobalEvent> globalEvents = this.globalEventDao.findAll();

        for (GlobalEvent event : globalEvents) {
            respBuilder = respBuilder.e("eventdata")
                    .u32("eventid", event.getEventId()).up()
                    .s32("eventtype", event.getEventType()).up()
                    .u32("eventno", event.getEventNo()).up()
                    .s64("condition", event.getEventCondition()).up()
                    .u32("reward", event.getReward()).up()
                    .s32("comptime", 1).up()
                    .s64("savedata", 0).up().up();
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
                JudgementLayerOption.BACKGROUND, true, 0, null, null, null, defaultLastCsv, 0, 0);
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
                val = (profile == null) ? NO_DATA_VALUE : this.buildCommonCsv(profile);
            } else if (col.equals("OPTION")) {
                val = (profile == null) ? NO_DATA_VALUE : this.buildOptionCsv(profile);
            } else if (col.equals("LAST")) {
                val = (profile == null) ? NO_DATA_VALUE : this.buildLastCsv(profile);
            } else if (col.equals("RIVAL")) {
                val = (profile == null) ? NO_DATA_VALUE : this.buildRivalCsv(profile);
            } else {
                throw new InvalidRequestException();
            }

            builder = builder.str("d",
                    (profile == null) ? val : Base64.getEncoder().encodeToString(val.getBytes())).up();
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
        return profile.getUnkLast();
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

            // update the golden league period's EX score, if applicable
            final GoldenLeaguePeriod currentPeriod = this.goldenLeaguePeriodDao.getLastGoldenLeaguePeriod();

            if (currentPeriod != null && currentPeriod.isPeriodCurrent()) {
                final GoldenLeagueStatus status = this.goldenLeagueStatusDao.findByUserAndPeriod(user, currentPeriod);

                if (status != null) {
                    status.setTotalExScore(status.getTotalExScore() + exScore);
                    this.goldenLeagueStatusDao.update(status);
                }
            }
        }

        // parse out the event progress saving and save them individually
        final NodeList eventNodes = XmlUtils.nodesAtPath(dataNode, "/data/event");

        for (int i = 0; i < eventNodes.getLength(); i++) {
            Element eventNode = (Element) eventNodes.item(i);
            int eventId = XmlUtils.intAtChild(eventNode, "eventid");

            if (eventId == 0) {
                continue;
            }

            int eventType = XmlUtils.intAtChild(eventNode, "eventtype");
            int eventNo = XmlUtils.intAtChild(eventNode, "eventno");
            long compTime = XmlUtils.longAtChild(eventNode, "comptime");
            long saveData = XmlUtils.longAtChild(eventNode, "savedata");

            // see if there's an entry for this event ID already. if there is, update its progress
            EventSaveData eventSaveData = this.eventSaveDataDao.findByUserAndEventId(user, eventId);

            // construct and save
            if (eventSaveData != null) {
                eventSaveData.setEventType(eventType);
                eventSaveData.setEventNo(eventNo);
                eventSaveData.setCompTime(compTime);
                eventSaveData.setSaveData(saveData);
                this.eventSaveDataDao.update(eventSaveData);
            } else {
                eventSaveData = new EventSaveData(user, eventId, eventType, eventNo, compTime, saveData, 0, 0);
                this.eventSaveDataDao.create(eventSaveData);
            }
        }

        // parse out the user's grade and also save that
        final Element gradeNode = (Element) XmlUtils.nodeAtPath(dataNode, "/data/grade");

        if (gradeNode != null) {
            int singleGrade = XmlUtils.intAtChild(gradeNode, "single_grade");
            int doubleGrade = XmlUtils.intAtChild(gradeNode, "double_grade");

            if (user.getSingleClass() != singleGrade ||
                    user.getDoubleClass() != doubleGrade) {
                user.setSingleClass(singleGrade);
                user.setDoubleClass(doubleGrade);

                this.profileDao.update(user);
            }
        }

        // send the response
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
    private HashMap<Integer, HashMap<Integer, UserSongRecord>> sortTopScores(final List<UserSongRecord> records) {
        final HashMap<Integer, HashMap<Integer, UserSongRecord>> topScores = new HashMap<>();

        for (UserSongRecord record : records) {
            if (!topScores.containsKey(record.getSongId())) {
                topScores.put(record.getSongId(), new HashMap<>());
            }

            topScores.get(record.getSongId()).put(record.getNoteType(), record);
        }
        return topScores;
    }
}
