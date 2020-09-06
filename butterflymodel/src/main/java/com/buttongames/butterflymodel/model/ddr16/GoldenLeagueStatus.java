package com.buttongames.butterflymodel.model.ddr16;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Model to represent a user's Golden League status for a specific period (rankings, class, etc.)
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_golden_league_statuses")
public class GoldenLeagueStatus implements Externalizable, Comparable<GoldenLeagueStatus> {

    private static final long serialVersionUID = 1L;

    /** ID of the record, primary key */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The user this record belongs to */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;

    /** The Golden League Period this record belongs to */
    @ManyToOne
    @JoinColumn(name = "golden_league_period")
    private GoldenLeaguePeriod leaguePeriod;

    /** The user's current Golden League class */
    @Column(name = "golden_league_class")
    private Integer goldenLeagueClass;

    /** The user's current ranking within their Golden League class */
    @Column(name = "golden_league_rank")
    private Integer goldenLeagueRank;

    /** The total EX Score accumulated this league period */
    @Column(name = "total_ex_score")
    private Integer totalExScore;

    /** The number of times the user has joined a session this period */
    @Column(name = "num_joins")
    private Integer numJoins;

    /** If we're in between Golden League periods, this flag holds whether they've seen the promotion/demotion results screen, yet */
    @Column(name = "league_end_transition")
    private Boolean leagueEndTransition;

    public GoldenLeagueStatus() { }

    public GoldenLeagueStatus(UserProfile user, GoldenLeaguePeriod leaguePeriod, Integer goldenLeagueClass, Integer goldenLeagueRank,
                              Integer totalExScore, Integer numJoins, Boolean leagueEndTransition) {
        this.user = user;
        this.leaguePeriod = leaguePeriod;
        this.goldenLeagueClass = goldenLeagueClass;
        this.goldenLeagueRank = goldenLeagueRank;
        this.totalExScore = totalExScore;
        this.numJoins = numJoins;
        this.leagueEndTransition = leagueEndTransition;
    }

    public GoldenLeagueStatus(GoldenLeagueStatus other) {
        this.user = other.user;
        this.leaguePeriod = other.leaguePeriod;
        this.goldenLeagueClass = other.goldenLeagueClass;
        this.goldenLeagueRank = other.goldenLeagueRank;
        this.totalExScore = other.totalExScore;
        this.numJoins = other.numJoins;
        this.leagueEndTransition = other.leagueEndTransition;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.getUser());
        out.writeObject(this.getLeaguePeriod());
        out.writeInt(this.getGoldenLeagueClass());
        out.writeInt(this.getGoldenLeagueRank());
        out.writeInt(this.getTotalExScore());
        out.writeInt(this.getNumJoins());
        out.writeBoolean(this.getLeagueEndTransition());
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((UserProfile) in.readObject());
        this.setLeaguePeriod((GoldenLeaguePeriod) in.readObject());
        this.setGoldenLeagueClass(in.readInt());
        this.setGoldenLeagueRank(in.readInt());
        this.setTotalExScore(in.readInt());
        this.setNumJoins(in.readInt());
        this.setLeagueEndTransition(in.readBoolean());
    }

    @Override
    public int compareTo(GoldenLeagueStatus other) {
        if (this.getTotalExScore() == other.getTotalExScore()) {
            return 0;
        } else if (this.getTotalExScore() > other.getTotalExScore()) {
            return -1;
        } else {
            return 1;
        }
    }

    @GraphQLQuery(name = "id")
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @GraphQLQuery(name = "user")
    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    @GraphQLQuery(name = "leaguePeriod")
    public GoldenLeaguePeriod getLeaguePeriod() {
        return leaguePeriod;
    }

    public void setLeaguePeriod(GoldenLeaguePeriod leaguePeriod) {
        this.leaguePeriod = leaguePeriod;
    }

    @GraphQLQuery(name = "goldenLeagueClass")
    public Integer getGoldenLeagueClass() {
        return (goldenLeagueClass == null) ? 0 : goldenLeagueClass;
    }

    public void setGoldenLeagueClass(Integer goldenLeagueClass) {
        this.goldenLeagueClass = goldenLeagueClass;
    }

    @GraphQLQuery(name = "goldenLeagueRank")
    public Integer getGoldenLeagueRank() {
        return (goldenLeagueRank == null) ? 0 : goldenLeagueRank;
    }

    public void setGoldenLeagueRank(Integer goldenLeagueRank) {
        this.goldenLeagueRank = goldenLeagueRank;
    }

    @GraphQLQuery(name = "totalExScore")
    public Integer getTotalExScore() {
        return totalExScore;
    }

    public void setTotalExScore(Integer totalExScore) {
        this.totalExScore = totalExScore;
    }

    @GraphQLQuery(name = "numJoins")
    public Integer getNumJoins() {
        return numJoins;
    }

    public void setNumJoins(Integer numJoins) {
        this.numJoins = numJoins;
    }

    @GraphQLIgnore
    public Boolean getLeagueEndTransition() {
        return (leagueEndTransition == null) ? false : leagueEndTransition;
    }

    public void setLeagueEndTransition(Boolean leagueEndTransition) {
        this.leagueEndTransition = leagueEndTransition;
    }
}
