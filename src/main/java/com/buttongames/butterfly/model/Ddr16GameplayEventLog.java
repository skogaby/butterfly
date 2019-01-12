package com.buttongames.butterfly.model;

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
import java.time.LocalDateTime;

/**
 * Model class that represents a gameplay event from a DDR 16 machine.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_gameplay_event_logs")
public class Ddr16GameplayEventLog implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the event, primary key. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The PCBID of the event. */
    @Column(name = "pcbid")
    private String pcbId;

    /** The model of the event. */
    @Column(name = "model")
    private String model;

    /** The retry count of the event. */
    @Column(name = "retry_count")
    private int retryCount;

    /** The enum ID of the event. */
    @Column(name = "event_id")
    private String eventId;

    /** The order of the event. */
    @Column(name = "event_order")
    private int eventOrder;

    /** The PCB time of the event. */
    @Column(name = "pcb_time")
    private LocalDateTime pcbTime;

    /** The game session of the event. */
    @Column(name = "game_session")
    private long gameSession;

    /** The first piece of string data of the event. */
    @Column(name = "str_data_1")
    private String stringData1;

    /** The second piece of string data of the event. */
    @Column(name = "str_data_2")
    private String stringData2;

    /** The first peice of numeric data for the event. */
    @Column(name = "num_data_1")
    private long numData1;

     /** The second peice of numeric data for the event. */
    @Column(name = "num_data_2")
    private long numData2;

    /** The location ID for the facility of the machine. */
    @Column(name = "location_id")
    private String locationId;

    public Ddr16GameplayEventLog() { }

    public Ddr16GameplayEventLog(final String pcbId, final String model, final int retryCount,
                                 final String eventId, final int eventOrder, final LocalDateTime pcbTime, final long gameSession,
                                 final String stringData1, final String stringData2, final long numData1, final long numData2,
                                 final String locationId) {
        this.pcbId = pcbId;
        this.model = model;
        this.retryCount = retryCount;
        this.eventId = eventId;
        this.eventOrder = eventOrder;
        this.pcbTime = pcbTime;
        this.gameSession = gameSession;
        this.stringData1 = stringData1;
        this.stringData2 = stringData2;
        this.numData1 = numData1;
        this.numData2 = numData2;
        this.locationId = locationId;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeUTF(this.pcbId);
        out.writeUTF(this.model);
        out.writeInt(this.retryCount);
        out.writeUTF(this.eventId);
        out.writeInt(this.eventOrder);
        out.writeObject(this.pcbTime);
        out.writeLong(this.gameSession);
        out.writeUTF(this.stringData1);
        out.writeUTF(this.stringData2);
        out.writeLong(this.numData1);
        out.writeLong(this.numData2);
        out.writeUTF(this.locationId);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setPcbId(in.readUTF());
        this.setModel(in.readUTF());
        this.setRetryCount(in.readInt());
        this.setEventId(in.readUTF());
        this.setEventOrder(in.readInt());
        this.setPcbTime((LocalDateTime) in.readObject());
        this.setGameSession(in.readLong());
        this.setStringData1(in.readUTF());
        this.setStringData2(in.readUTF());
        this.setNumData1(in.readLong());
        this.setNumData2(in.readLong());
        this.setLocationId(in.readUTF());
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public String getPcbId() {
        return pcbId;
    }

    public void setPcbId(String pcbId) {
        this.pcbId = pcbId;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public int getEventOrder() {
        return eventOrder;
    }

    public void setEventOrder(int eventOrder) {
        this.eventOrder = eventOrder;
    }

    public LocalDateTime getPcbTime() {
        return pcbTime;
    }

    public void setPcbTime(LocalDateTime pcbTime) {
        this.pcbTime = pcbTime;
    }

    public long getGameSession() {
        return gameSession;
    }

    public void setGameSession(long gameSession) {
        this.gameSession = gameSession;
    }

    public String getStringData1() {
        return stringData1;
    }

    public void setStringData1(String stringData1) {
        this.stringData1 = stringData1;
    }

    public String getStringData2() {
        return stringData2;
    }

    public void setStringData2(String stringData2) {
        this.stringData2 = stringData2;
    }

    public long getNumData1() {
        return numData1;
    }

    public void setNumData1(long numData1) {
        this.numData1 = numData1;
    }

    public long getNumData2() {
        return numData2;
    }

    public void setNumData2(long numData2) {
        this.numData2 = numData2;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }
}
