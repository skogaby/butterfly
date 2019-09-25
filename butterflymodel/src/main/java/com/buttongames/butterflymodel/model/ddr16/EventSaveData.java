package com.buttongames.butterflymodel.model.ddr16;

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
 * Model class that represents a user gameplay event.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_event_save_data")
public class EventSaveData implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the event, primary key */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The user this data belongs to */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;

    /** The id for the event */
    @Column(name = "event_id")
    private int eventId;

    /** The type of the event */
    @Column(name = "event_type")
    private int eventType;

    /** The number for the event */
    @Column(name = "event_no")
    private int eventNo;

    /** Comp time? Not sure what this is. */
    @Column(name = "comp_time")
    private long compTime;

    /** Not sure what this is either. */
    @Column(name = "save_data")
    private long saveData;

    /** Condition of the event */
    @Column(name = "condition")
    private long condition;

    /** Reward for the event */
    @Column(name = "reward")
    private int reward;

    public EventSaveData() { }

    public EventSaveData(UserProfile user, int eventId, int eventType, int eventNo, long compTime, long saveData,
                         long condition, int reward) {
        this.user = user;
        this.eventId = eventId;
        this.eventType = eventType;
        this.eventNo = eventNo;
        this.compTime = compTime;
        this.saveData = saveData;
        this.condition = condition;
        this.reward = reward;
    }

    public EventSaveData(EventSaveData other) {
        this.user = other.user;
        this.eventId = other.eventId;
        this.eventType = other.eventType;
        this.eventNo = other.eventNo;
        this.compTime = other.compTime;
        this.saveData = other.saveData;
        this.condition = other.condition;
        this.reward = other.reward;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(this.user);
        out.writeInt(this.eventId);
        out.writeInt(this.eventType);
        out.writeInt(this.eventNo);
        out.writeLong(this.compTime);
        out.writeLong(this.saveData);
        out.writeLong(this.condition);
        out.writeInt(this.reward);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setUser((UserProfile) in.readObject());
        this.setEventId(in.readInt());
        this.setEventType(in.readInt());
        this.setEventNo(in.readInt());
        this.setCompTime(in.readLong());
        this.setSaveData(in.readLong());
        this.setCondition(in.readLong());
        this.setReward(in.readInt());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public int getEventNo() {
        return eventNo;
    }

    public void setEventNo(int eventNo) {
        this.eventNo = eventNo;
    }

    public long getCompTime() {
        return compTime;
    }

    public void setCompTime(long compTime) {
        this.compTime = compTime;
    }

    public long getSaveData() {
        return saveData;
    }

    public void setSaveData(long saveData) {
        this.saveData = saveData;
    }

    public long getCondition() {
        return condition;
    }

    public void setCondition(long condition) {
        this.condition = condition;
    }

    public int getReward() {
        return reward;
    }

    public void setReward(int reward) {
        this.reward = reward;
    }
}
