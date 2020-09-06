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

/**
 * Model class to represent a global event entry.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_global_events")
public class GlobalEvent implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the event, primary key */
    @Id
    @Column(name = "event_id")
    private int eventId;

    /** Type of the event */
    @Column(name = "event_type")
    private int eventType;

    /** Number of the event */
    @Column(name = "event_no")
    private int eventNo;

    /** Condition of the event */
    @Column(name = "event_condition")
    private long eventCondition;

    /** Reward for the event */
    @Column(name = "reward")
    private int reward;

    public GlobalEvent() { }

    public GlobalEvent(int eventId, int eventType, int eventNo, long eventCondition, int reward) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventNo = eventNo;
        this.eventCondition = eventCondition;
        this.reward = reward;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.eventId);
        out.writeInt(this.eventType);
        out.writeInt(this.eventNo);
        out.writeLong(this.eventCondition);
        out.writeInt(this.reward);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setEventId(in.readInt());
        this.setEventType(in.readInt());
        this.setEventNo(in.readInt());
        this.setEventCondition(in.readLong());
        this.setReward(in.readInt());
    }

    @GraphQLQuery(name = "eventId")
    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    @GraphQLQuery(name = "eventType")
    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    @GraphQLQuery(name = "eventNo")
    public int getEventNo() {
        return eventNo;
    }

    public void setEventNo(int eventNo) {
        this.eventNo = eventNo;
    }

    @GraphQLQuery(name = "eventCondition")
    public long getEventCondition() {
        return eventCondition;
    }

    public void setEventCondition(long eventCondition) {
        this.eventCondition = eventCondition;
    }

    @GraphQLQuery(name = "reward")
    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
