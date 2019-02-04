package com.buttongames.butterflymodel.model.ddr16;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.time.LocalDateTime;

/**
 * Model class that represents a PCB event from a DDR 16 machine.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_pcb_event_logs")
public class PcbEventLog implements Externalizable {

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

    /** The first time value of the event. */
    @Column(name = "time1")
    private LocalDateTime time1;

    /** The second time value of the event. */
    @Column(name = "time2")
    private LocalDateTime time2;

    /** The sequence value of the event. */
    @Column(name = "sequence")
    private long sequence;

    /** The name of the event. */
    @Column(name = "name")
    private String name;

    /** The value of the event. */
    @Column(name = "value")
    private int value;

    public PcbEventLog() { }

    public PcbEventLog(final String pcbId, String model, final LocalDateTime time1,
                       final LocalDateTime time2, final long sequence, final String name, final int value) {
        this.pcbId = pcbId;
        this.model = model;
        this.time1 = time1;
        this.time2 = time2;
        this.sequence = sequence;
        this.name = name;
        this.value = value;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeUTF(this.pcbId);
        out.writeUTF(this.model);
        out.writeObject(this.time1);
        out.writeObject(this.time2);
        out.writeLong(this.sequence);
        out.writeUTF(this.name);
        out.writeInt(this.value);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setPcbId(in.readUTF());
        this.setModel(in.readUTF());
        this.setTime1((LocalDateTime) in.readObject());
        this.setTime2((LocalDateTime) in.readObject());
        this.setSequence(in.readLong());
        this.setName(in.readUTF());
        this.setValue(in.readInt());
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

    public LocalDateTime getTime1() {
        return time1;
    }

    public void setTime1(LocalDateTime time1) {
        this.time1 = time1;
    }

    public LocalDateTime getTime2() {
        return time2;
    }

    public void setTime2(LocalDateTime time2) {
        this.time2 = time2;
    }

    public long getSequence() {
        return sequence;
    }

    public void setSequence(long sequence) {
        this.sequence = sequence;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
