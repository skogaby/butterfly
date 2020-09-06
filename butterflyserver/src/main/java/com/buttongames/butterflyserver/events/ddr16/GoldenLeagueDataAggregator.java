package com.buttongames.butterflyserver.events.ddr16;

import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeaguePeriodDao;
import com.buttongames.butterflydao.hibernate.dao.impl.ddr16.GoldenLeagueStatusDao;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeaguePeriod;
import com.buttongames.butterflymodel.model.ddr16.GoldenLeagueStatus;
import com.buttongames.butterflyserver.util.PropertyNames;
import com.google.common.collect.ImmutableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * This class is responsible for aggregating all the Golden League rankings and promotion
 * and demotion criteria, on a scheduled basis, based on the current period's percentile thresholds.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Component
public class GoldenLeagueDataAggregator {

    private final Logger LOG = LogManager.getLogger(GoldenLeagueDataAggregator.class);

    /**
     * The DAO for managing user Golden League progress.
     */
    private final GoldenLeagueStatusDao goldenLeagueStatusDao;

    /**
     * The DAO for managing Golden League periods.
     */
    private final GoldenLeaguePeriodDao goldenLeaguePeriodDao;

    /**
     * Says whether or not we use percentiles for Golden League promotions, or raw thresholds.
     */
    @Value(PropertyNames.GOLDEN_LEAGUE_PERCENTILES)
    private String isGoldenLeaguePercentiles;

    @Autowired
    public GoldenLeagueDataAggregator(final GoldenLeagueStatusDao goldenLeagueStatusDao,
                                      final GoldenLeaguePeriodDao goldenLeaguePeriodDao) {
        this.goldenLeagueStatusDao = goldenLeagueStatusDao;
        this.goldenLeaguePeriodDao = goldenLeaguePeriodDao;
    }

    /**
     * This method aggregates all the data for the current League Period (if it's active),
     * assigning the following values:
     *   * Rankings for each status for this period
     *   * The promotion and demotion EX Scores for each class (if we're using percentile ranking)
     *   * The total number of players for this period
     *   * Period's summary time
     * This method runs every 10 minutes
     */
    @Scheduled(fixedRate = 600000)
    public void aggregateCurrentLeaguePeriodData() {
        final GoldenLeaguePeriod currentPeriod = this.goldenLeaguePeriodDao.getLastGoldenLeaguePeriod();

        if (currentPeriod != null && currentPeriod.isPeriodCurrent()) {
            // pull all the user statuses for this period
            final List<GoldenLeagueStatus> bronzeStatuses = this.goldenLeagueStatusDao.findByPeriodAndClass(currentPeriod, 1);
            final List<GoldenLeagueStatus> silverStatuses = this.goldenLeagueStatusDao.findByPeriodAndClass(currentPeriod, 2);
            final List<GoldenLeagueStatus> goldStatuses = this.goldenLeagueStatusDao.findByPeriodAndClass(currentPeriod, 3);

            // sort them and figure out the ranks
            Collections.sort(bronzeStatuses);
            Collections.sort(silverStatuses);
            Collections.sort(goldStatuses);

            final List<List<GoldenLeagueStatus>> statusesList = ImmutableList.of(bronzeStatuses, silverStatuses, goldStatuses);

            // update the ranks for each status
            for (int i = 0; i < 3; i++) {
                List<GoldenLeagueStatus> statuses = statusesList.get(i);

                for (int j = 0; j < statuses.size(); j++) {
                    final GoldenLeagueStatus status = statuses.get(j);
                    status.setGoldenLeagueRank(j + 1);

                    this.goldenLeagueStatusDao.update(status);
                }
            }

            // update the period total players
            currentPeriod.setNumBronzePlayers(bronzeStatuses.size());
            currentPeriod.setNumSilverPlayers(silverStatuses.size());
            currentPeriod.setNumGoldPlayers(goldStatuses.size());

            // update the period promotion and demotion scores, only if we're using percentile criteria for promotions.
            // if we're not, these just stay as whatever is defined in the database already and that's what is used for
            // promotions and demotions
            if (Boolean.parseBoolean(this.isGoldenLeaguePercentiles)) {
                currentPeriod.setBronzePromotionExScore(
                        bronzeStatuses.get((int) (currentPeriod.getBronzePromotionPercentage() * bronzeStatuses.size())).getTotalExScore());
                currentPeriod.setSilverDemotionExScore(
                        silverStatuses.get((int) (currentPeriod.getSilverDemotionPercentage() * silverStatuses.size())).getTotalExScore());
                currentPeriod.setSilverPromotionExScore(
                        goldStatuses.get((int) (currentPeriod.getSilverPromotionPercentage() * goldStatuses.size())).getTotalExScore());
            }

            // update the period summary time
            currentPeriod.setSummaryTime(LocalDateTime.now());

            this.goldenLeaguePeriodDao.update(currentPeriod);
        }
    }
}
