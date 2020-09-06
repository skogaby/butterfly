package com.buttongames.butterflyserver.graphql.types.ddr16;

import com.buttongames.butterflydao.hibernate.dao.impl.ButterflyUserDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GhostDataDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GlobalEventDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeaguePeriodDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeagueStatusDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.ProfileDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.UserSongRecordDao;
import com.buttongames.butterflymodel.model.ButterflyUser;
import com.buttongames.butterflymodel.model.ddr16.GhostData;
import com.buttongames.butterflymodel.model.ddr16.GlobalEvent;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeaguePeriod;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeagueStatus;
import com.buttongames.butterflymodel.model.ddr16.UserProfile;
import com.buttongames.butterflymodel.model.ddr16.UserSongRecord;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * This class holds all the top-level GraphQL queries that we want to expose to the API clients for DDR16.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class Ddr16Query {

    /**
     * DAO for interacting with <code>ButterflyUser</code> objects in the database.
     */
    private final ButterflyUserDao butterflyUserDao;

    /**
     * DAO for interacting with <code>Profile</code> objects in the database.
     */
    private final ProfileDao profileDao;

    /**
     * DAO for interacting with <code>UserSongRecord</code> objects in the database.
     */
    private final UserSongRecordDao userSongRecordDao;

    /**
     * DAO for interacting with <code>GhostData</code> objects in the database.
     */
    private final GhostDataDao ghostDataDao;

    /**
     * DAO for interacting with <code>GlobalEvent</code> objects in the database.
     */
    private final GlobalEventDao globalEventDao;

    /**
     * DAO for interacting with <code>GoldenLeaguePeriod</code> objects in the database.
     */
    private final GoldenLeaguePeriodDao goldenLeaguePeriodDao;

    /**
     * DAO for interacting with <code>GoldenLeagueStatus</code> objects in the database.
     */
    private final GoldenLeagueStatusDao goldenLeagueStatusDao;

    @Autowired
    public Ddr16Query(final ButterflyUserDao butterflyUserDao,
                      final ProfileDao profileDao,
                      final UserSongRecordDao userSongRecordDao,
                      final GhostDataDao ghostDataDao,
                      final GlobalEventDao globalEventDao,
                      final GoldenLeaguePeriodDao goldenLeaguePeriodDao,
                      final GoldenLeagueStatusDao goldenLeagueStatusDao) {
        this.butterflyUserDao = butterflyUserDao;
        this.profileDao = profileDao;
        this.userSongRecordDao = userSongRecordDao;
        this.ghostDataDao = ghostDataDao;
        this.globalEventDao = globalEventDao;
        this.goldenLeaguePeriodDao = goldenLeaguePeriodDao;
        this.goldenLeagueStatusDao = goldenLeagueStatusDao;
    }

    /**
     * Finds a DDR 16 profile by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16UserById")
    public UserProfile findDdr16UserById(@GraphQLArgument(name = "id") final long id) {
        return this.profileDao.findById(id);
    }

    /**
     * Finds all DDR 16 profiles.
     * @return
     */
    @GraphQLQuery(name = "findAllDdr16Users")
    public List<UserProfile> findAllDdr16Users() {
        return this.profileDao.findAll();
    }

    /**
     * Finds a DDR 16 profile by its parent account for the server.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16UserByButterflyUserId")
    public UserProfile findDdr16UserByButterflyUserId(@GraphQLArgument(name = "id") final long id) {
        final ButterflyUser user = this.butterflyUserDao.findById(id);
        return this.profileDao.findByUser(user);
    }

    /**
     * Finds a DDR 16 profile by its dancer code.
     * @param dancerCode
     * @return
     */
    @GraphQLQuery(name = "findDdr16UserByDancerCode")
    public UserProfile findDdr16UserByDancerCode(@GraphQLArgument(name = "dancerCode") final int dancerCode) {
        return this.profileDao.findByDancerCode(dancerCode);
    }


    /**
     * Finds the DDR 16 song record given its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16SongRecordById")
    public UserSongRecord findDdr16SongRecordById(@GraphQLArgument(name = "id") final long id) {
        return this.userSongRecordDao.findById(id);
    }

    /**
     * Finds all the DDR 16 song records.
     * @return
     */
    @GraphQLQuery(name = "findAllDdr16SongRecords")
    public List<UserSongRecord> findAllDdr16SongRecords() {
        return this.userSongRecordDao.findAll();
    }

    /**
     * Finds the global top scores for all songs for the given difficulty.
     * @param difficulty
     * @return
     */
    @GraphQLQuery(name = "findTopDdr16SongRecordsForDifficulty")
    public List<UserSongRecord> findTopDdr16SongRecordsForDifficulty(@GraphQLArgument(name = "difficulty") final int difficulty) {
        return this.userSongRecordDao.findTopScoresForDifficulty(difficulty);
    }

    /**
     * Finds the top scores for all songs for the given difficulty and user
     * @param userId
     * @param difficulty
     * @return
     */
    @GraphQLQuery(name = "findTopDdr16SongRecordsForDifficultyByUser")
    public List<UserSongRecord> findTopDdr16SongRecordsForDifficultyByUser(@GraphQLArgument(name = "userId") final int userId,
                                                                           @GraphQLArgument(name = "difficulty") final int difficulty) {
        final UserProfile user = this.profileDao.findById(userId);
        return this.userSongRecordDao.findTopScoresForDifficultyByUser(user, difficulty);
    }

    /**
     * Finds the top scores for all songs for the given difficulty and machine
     * @param pcbid
     * @param difficulty
     * @return
     */
    @GraphQLQuery(name = "findTopDdr16SongRecordsForDifficultyByMachine")
    public List<UserSongRecord> findTopDdr16SongRecordsForDifficultyByMachine(@GraphQLArgument(name = "pcbid") final String pcbid,
                                                                              @GraphQLArgument(name = "difficulty") final int difficulty) {
        return this.userSongRecordDao.findTopScoresForDifficultyByMachine(pcbid, difficulty);
    }

    /**
     * Finds the top scores for all songs for the given difficulty and shop area
     * @param shopArea
     * @param difficulty
     * @return
     */
    @GraphQLQuery(name = "findTopDdr16SongRecordsForDifficultyByShopArea")
    public List<UserSongRecord> findTopDdr16SongRecordsForDifficultyByShopArea(@GraphQLArgument(name = "shopArea") final String shopArea,
                                                                               @GraphQLArgument(name = "difficulty")final int difficulty) {
        return this.userSongRecordDao.findTopScoresForDifficultyByShopArea(shopArea, difficulty);
    }

    /**
     * Finds a DDR 16 ghost data by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16GhostDataById")
    public GhostData findDdr16GhostDataById(@GraphQLArgument(name = "id") final long id) {
        return this.ghostDataDao.findById(id);
    }

    /**
     * Finds all DDR 16 ghost datas.
     * @return
     */
    @GraphQLQuery(name = "findAllDdr16GhostData")
    public List<GhostData> findAllDdr16GhostData() {
        return this.ghostDataDao.findAll();
    }

    /**
     * Finds a DDR 16 global event by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16GlobalEventById")
    public GlobalEvent findDdr16GlobalEventById(@GraphQLArgument(name = "id") final long id) {
        return this.globalEventDao.findById(id);
    }

    /**
     * Finds all DDR 16 global events.
     * @return
     */
    @GraphQLQuery(name = "findAllDdr16GlobalEvents")
    public List<GlobalEvent> findAllDdr16GlobalEvents() {
        return this.globalEventDao.findAll();
    }

    /**
     * Finds a DDR A20 Golden League period by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16GoldenLeaguePeriodById")
    public GoldenLeaguePeriod findDdr16GoldenLeaguePeriodById(@GraphQLArgument(name = "id") final long id) {
        return this.findDdrA20GoldenLeaguePeriodById(id);
    }

    /**
     * Finds a DDR A20 Golden League period by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdrA20GoldenLeaguePeriodById")
    public GoldenLeaguePeriod findDdrA20GoldenLeaguePeriodById(@GraphQLArgument(name = "id") final long id) {
        return this.goldenLeaguePeriodDao.findById(id);
    }

    /**
     * Finds all DDR A20 Golden League periods.
     * @return
     */
    @GraphQLQuery(name = "findAllDdr16GoldenLeaguePeriods")
    public List<GoldenLeaguePeriod> findAllDdr16GoldenLeaguePeriods() {
        return this.findAllDdrA20GoldenLeaguePeriods();
    }

    /**
     * Finds all DDR A20 Golden League periods.
     * @return
     */
    @GraphQLQuery(name = "findAllDdrA20GoldenLeaguePeriods")
    public List<GoldenLeaguePeriod> findAllDdrA20GoldenLeaguePeriods() {
        return this.goldenLeaguePeriodDao.findAll();
    }

    /**
     * Fetches the most recent DDR A20 Golden League period.
     * @return
     */
    @GraphQLQuery(name = "getLastDdr16GoldenLeaguePeriod")
    public GoldenLeaguePeriod getLastDdr16GoldenLeaguePeriod() {
        return this.getLastDdrA20GoldenLeaguePeriod();
    }

    /**
     * Fetches the most recent DDR A20 Golden League period.
     * @return
     */
    @GraphQLQuery(name = "getLastDdrA20GoldenLeaguePeriod")
    public GoldenLeaguePeriod getLastDdrA20GoldenLeaguePeriod() {
        return this.goldenLeaguePeriodDao.getLastGoldenLeaguePeriod();
    }

    /**
     * Fetches a DDR A20 Golden League Period by its ID.
     * @return
     */
    @GraphQLQuery(name = "getDdr16GoldenLeaguePeriodById")
    public GoldenLeaguePeriod getDdr16GoldenLeaguePeriodById(@GraphQLArgument(name = "id") int id) {
        return this.getDdrA20GoldenLeaguePeriodById(id);
    }

    /**
     * Fetches a DDR A20 Golden League Period by its ID.
     * @return
     */
    @GraphQLQuery(name = "getDdrA20GoldenLeaguePeriodById")
    public GoldenLeaguePeriod getDdrA20GoldenLeaguePeriodById(@GraphQLArgument(name = "id") int id) {
        return this.goldenLeaguePeriodDao.getGoldenLeaguePeriodById(id);
    }

    /**
     * Finds a DDR A20 golden league status by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16GoldenLeagueStatusById")
    public GoldenLeagueStatus findDdr16GoldenLeagueStatusById(@GraphQLArgument(name = "id") final long id) {
        return this.findDdrA20GoldenLeagueStatusById(id);
    }

    /**
     * Finds a DDR A20 golden league status by its ID.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdrA20GoldenLeagueStatusById")
    public GoldenLeagueStatus findDdrA20GoldenLeagueStatusById(@GraphQLArgument(name = "id") final long id) {
        return this.goldenLeagueStatusDao.findById(id);
    }

    /**
     * Finds all DDR A20 Golden League statuses.
     * @return
     */
    @GraphQLQuery(name = "findAllDdr16GoldenLeagueStatuses")
    public List<GoldenLeagueStatus> findAllDdr16GoldenLeagueStatuses() {
        return this.findAllDdrA20GoldenLeagueStatuses();
    }

    /**
     * Finds all DDR A20 Golden League statuses.
     * @return
     */
    @GraphQLQuery(name = "findAllDdrA20GoldenLeagueStatuses")
    public List<GoldenLeagueStatus> findAllDdrA20GoldenLeagueStatuses() {
        return this.goldenLeagueStatusDao.findAll();
    }

    /**
     * Finds DDR A20 Golden League statuses by period.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdr16GoldenLeagueStatusesByPeriodId")
    public List<GoldenLeagueStatus> findDdr16GoldenLeagueStatusesByPeriodId(@GraphQLArgument(name = "periodId") final int id) {
        return this.findDdrA20GoldenLeagueStatusesByPeriodId(id);
    }

    /**
     * Finds DDR A20 Golden League statuses by period.
     * @param id
     * @return
     */
    @GraphQLQuery(name = "findDdrA20GoldenLeagueStatusesByPeriodId")
    public List<GoldenLeagueStatus> findDdrA20GoldenLeagueStatusesByPeriodId(@GraphQLArgument(name = "periodId") final int id) {
        final GoldenLeaguePeriod period = this.goldenLeaguePeriodDao.getGoldenLeaguePeriodById(id);
        return this.goldenLeagueStatusDao.findByPeriod(period);
    }

    /**
     * Finds DDR A20 Golden League statuses by period and class.
     * @param id
     * @param goldenLeagueClass
     * @return
     */
    @GraphQLQuery(name = "findDdr16GoldenLeagueStatusesByPeriodIdAndClass")
    public List<GoldenLeagueStatus> findDdr16GoldenLeagueStatusesByPeriodIdAndClass(@GraphQLArgument(name = "periodId") final int id,
                                                                                    @GraphQLArgument(name = "goldenLeagueClass") final int goldenLeagueClass) {
        return this.findDdrA20GoldenLeagueStatusesByPeriodIdAndClass(id, goldenLeagueClass);
    }

    /**
     * Finds DDR A20 Golden League statuses by period and class.
     * @param id
     * @param goldenLeagueClass
     * @return
     */
    @GraphQLQuery(name = "findDdrA20GoldenLeagueStatusesByPeriodIdAndClass")
    public List<GoldenLeagueStatus> findDdrA20GoldenLeagueStatusesByPeriodIdAndClass(@GraphQLArgument(name = "periodId") final int id,
                                                                                     @GraphQLArgument(name = "goldenLeagueClass") final int goldenLeagueClass) {
        final GoldenLeaguePeriod period = this.goldenLeaguePeriodDao.getGoldenLeaguePeriodById(id);
        return this.goldenLeagueStatusDao.findByPeriodAndClass(period, goldenLeagueClass);
    }

    /**
     * Finds a DDR A20 Golden League status by its user and period.
     * @param userId
     * @param periodId
     * @return
     */
    @GraphQLQuery(name = "findDdr16GoldenLeagueStatusesByUserIdAndPeriodId")
    public GoldenLeagueStatus findDdr16GoldenLeagueStatusesByUserIdAndPeriodId(@GraphQLArgument(name = "userId") final long userId,
                                                                               @GraphQLArgument(name = "periodId") final int periodId) {
        return this.findDdrA20GoldenLeagueStatusesByUserIdAndPeriodId(userId, periodId);
    }

    /**
     * Finds a DDR A20 Golden League status by its user and period.
     * @param userId
     * @param periodId
     * @return
     */
    @GraphQLQuery(name = "findDdrA20GoldenLeagueStatusesByUserIdAndPeriodId")
    public GoldenLeagueStatus findDdrA20GoldenLeagueStatusesByUserIdAndPeriodId(@GraphQLArgument(name = "userId") final long userId,
                                                                                @GraphQLArgument(name = "periodId") final int periodId) {
        final UserProfile user = this.profileDao.findById(userId);
        final GoldenLeaguePeriod period = this.goldenLeaguePeriodDao.getGoldenLeaguePeriodById(periodId);
        return this.goldenLeagueStatusDao.findByUserAndPeriod(user, period);
    }







}
