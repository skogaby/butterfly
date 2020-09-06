package com.buttongames.butterflymodel.model.ddr16;

import io.leangen.graphql.annotations.GraphQLQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

/**
 * Model class that represents a single period in the
 * Golden League event.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_golden_league_periods")
public class GoldenLeaguePeriod implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the event, primary key */
    @Id
    @Column(name = "id")
    private Integer id;

    /** The name of this League period */
    @Column(name = "period_name")
    private String name;

    /** The start time of this League period */
    @Column(name = "start_time")
    private LocalDateTime startTime;

    /** The end time of this League period */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** The percentile ranking you need to be promoted from Bronze (i.e. 0.25 -> must be above the bottom 25% to be promoted) */
    @Column(name = "bronze_promotion_percentage")
    private Float bronzePromotionPercentage;

    /** The EX Score you need to be promoted from Bronze */
    @Column(name = "bronze_promotion_ex_score")
    private Integer bronzePromotionExScore;

    /** The percentile ranking you need to be demoted from Silver (i.e. 0.25 -> must be below the bottom 25% to be demoted) */
    @Column(name = "silver_demotion_percentage")
    private Float silverDemotionPercentage;

    /** The EX Score you need to be demoted from Silver */
    @Column(name = "silver_demotion_ex_score")
    private Integer silverDemotionExScore;

    /** The percentile ranking you need to be demoted from Silver (i.e. 0.25 -> must be below the bottom 25% to be promoted) */
    @Column(name = "silver_promotion_percentage")
    private Float silverPromotionPercentage;

    /** The EX Score you need to be promoted from Silver */
    @Column(name = "silver_promotion_ex_score")
    private Integer silverPromotionExScore;

    /** The percentile ranking you need to be demoted from Gold (i.e. 0.25 -> must be below the bottom 25% to be promoted) */
    @Column(name = "gold_demotion_percentage")
    private Float goldDemotionPercentage;

    /** The EX Score you need to be demoted from Gold */
    @Column(name = "gold_demotion_ex_score")
    private Integer goldDemotionExScore;

    /** The number of players that have joined this period as bronze */
    @Column(name = "num_bronze_players")
    private Integer numBronzePlayers;

    /** The number of players that have joined this period as silver */
    @Column(name = "num_silver_players")
    private Integer numSilverPlayers;

    /** The number of players that have joined this period as gold */
    @Column(name = "num_gold_players")
    private Integer numGoldPlayers;

    /** The time the rank/num players was calculated (summary time) */
    @Column(name = "summary_time")
    private LocalDateTime summaryTime;

    public GoldenLeaguePeriod() { }

    public GoldenLeaguePeriod(Integer id, String name, LocalDateTime startTime, LocalDateTime endTime, Float bronzePromotionPercentage,
                              Integer bronzePromotionExScore, Float silverDemotionPercentage, Integer silverDemotionExScore,
                              Float silverPromotionPercentage, Integer silverPromotionExScore, Float goldDemotionPercentage,
                              Integer goldDemotionExScore, Integer numBronzePlayers, Integer numSilverPlayers, Integer numGoldPlayers,
                              LocalDateTime summaryTime) {
        this.id = id;
        this.name = name;
        this.startTime = startTime;
        this.endTime = endTime;
        this.bronzePromotionPercentage = bronzePromotionPercentage;
        this.bronzePromotionExScore = bronzePromotionExScore;
        this.silverDemotionPercentage = silverDemotionPercentage;
        this.silverDemotionExScore = silverDemotionExScore;
        this.silverPromotionPercentage = silverPromotionPercentage;
        this.silverPromotionExScore = silverPromotionExScore;
        this.goldDemotionPercentage = goldDemotionPercentage;
        this.goldDemotionExScore = goldDemotionExScore;
        this.numBronzePlayers = numBronzePlayers;
        this.numSilverPlayers = numSilverPlayers;
        this.numGoldPlayers = numGoldPlayers;
        this.summaryTime = summaryTime;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.getId());
        out.writeUTF(this.getName());
        out.writeObject(this.getStartTime());
        out.writeObject(this.getEndTime());
        out.writeFloat(this.getBronzePromotionPercentage());
        out.writeInt(this.getBronzePromotionExScore());
        out.writeFloat(this.getSilverDemotionPercentage());
        out.writeInt(this.getSilverDemotionExScore());
        out.writeFloat(this.getSilverPromotionPercentage());
        out.writeInt(this.getSilverPromotionExScore());
        out.writeFloat(this.getGoldDemotionPercentage());
        out.writeInt(this.getGoldDemotionExScore());
        out.writeInt(this.getNumBronzePlayers());
        out.writeInt(this.getNumSilverPlayers());
        out.writeInt(this.getNumGoldPlayers());
        out.writeObject(this.getSummaryTime());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readInt());
        this.setName(in.readUTF());
        this.setStartTime((LocalDateTime) in.readObject());
        this.setEndTime((LocalDateTime) in.readObject());
        this.setBronzePromotionPercentage(in.readFloat());
        this.setBronzePromotionExScore(in.readInt());
        this.setSilverDemotionPercentage(in.readFloat());
        this.setSilverDemotionExScore(in.readInt());
        this.setSilverPromotionPercentage(in.readFloat());
        this.setSilverPromotionExScore(in.readInt());
        this.setGoldDemotionPercentage(in.readFloat());
        this.setGoldDemotionExScore(in.readInt());
        this.setNumBronzePlayers(in.readInt());
        this.setNumSilverPlayers(in.readInt());
        this.setNumGoldPlayers(in.readInt());
        this.setSummaryTime((LocalDateTime) in.readObject());
    }

    @GraphQLQuery(name = "id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @GraphQLQuery(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @GraphQLQuery(name = "startTime")
    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @GraphQLQuery(name = "endTime")
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @GraphQLQuery(name = "bronzePromotionPercentage")
    public Float getBronzePromotionPercentage() {
        return bronzePromotionPercentage;
    }

    public void setBronzePromotionPercentage(Float bronzePromotionPercentage) {
        this.bronzePromotionPercentage = bronzePromotionPercentage;
    }

    @GraphQLQuery(name = "bronzePromotionExScore")
    public Integer getBronzePromotionExScore() {
        return bronzePromotionExScore;
    }

    public void setBronzePromotionExScore(Integer bronzePromotionExScore) {
        this.bronzePromotionExScore = bronzePromotionExScore;
    }

    @GraphQLQuery(name = "silverDemotionPercentage")
    public Float getSilverDemotionPercentage() {
        return silverDemotionPercentage;
    }

    public void setSilverDemotionPercentage(Float silverDemotionPercentage) {
        this.silverDemotionPercentage = silverDemotionPercentage;
    }

    @GraphQLQuery(name = "silverDemotionExScore")
    public Integer getSilverDemotionExScore() {
        return silverDemotionExScore;
    }

    public void setSilverDemotionExScore(Integer silverDemotionExScore) {
        this.silverDemotionExScore = silverDemotionExScore;
    }

    @GraphQLQuery(name = "silverPromotionPercentage")
    public Float getSilverPromotionPercentage() {
        return silverPromotionPercentage;
    }

    public void setSilverPromotionPercentage(Float silverPromotionPercentage) {
        this.silverPromotionPercentage = silverPromotionPercentage;
    }

    @GraphQLQuery(name = "silverPromotionExScore")
    public Integer getSilverPromotionExScore() {
        return silverPromotionExScore;
    }

    public void setSilverPromotionExScore(Integer silverPromotionExScore) {
        this.silverPromotionExScore = silverPromotionExScore;
    }

    @GraphQLQuery(name = "goldDemotionPercentage")
    public Float getGoldDemotionPercentage() {
        return goldDemotionPercentage;
    }

    public void setGoldDemotionPercentage(Float goldDemotionPercentage) {
        this.goldDemotionPercentage = goldDemotionPercentage;
    }

    @GraphQLQuery(name = "goldDemotionExScore")
    public Integer getGoldDemotionExScore() {
        return goldDemotionExScore;
    }

    public void setGoldDemotionExScore(Integer goldDemotionExScore) {
        this.goldDemotionExScore = goldDemotionExScore;
    }

    @GraphQLQuery(name = "numBronzePlayers")
    public Integer getNumBronzePlayers() {
        return numBronzePlayers;
    }

    public void setNumBronzePlayers(Integer numBronzePlayers) {
        this.numBronzePlayers = numBronzePlayers;
    }

    @GraphQLQuery(name = "numSilverPlayers")
    public Integer getNumSilverPlayers() {
        return numSilverPlayers;
    }

    public void setNumSilverPlayers(Integer numSilverPlayers) {
        this.numSilverPlayers = numSilverPlayers;
    }

    @GraphQLQuery(name = "numGoldPlayers")
    public Integer getNumGoldPlayers() {
        return numGoldPlayers;
    }

    public void setNumGoldPlayers(Integer numGoldPlayers) {
        this.numGoldPlayers = numGoldPlayers;
    }

    @GraphQLQuery(name = "summaryTime")
    public LocalDateTime getSummaryTime() {
        return summaryTime;
    }

    public void setSummaryTime(LocalDateTime summaryTime) {
        this.summaryTime = summaryTime;
    }

    @GraphQLQuery(name = "isPeriodCurrent")
    public boolean isPeriodCurrent() {
        final LocalDateTime now = LocalDateTime.now();

        return this.getStartTime().isBefore(now) &&
                this.getEndTime().isAfter(now);
    }
}
