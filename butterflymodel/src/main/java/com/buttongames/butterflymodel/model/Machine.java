package com.buttongames.butterflymodel.model;

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
 * Model class that represents a machine / PCB on the network.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "machines")
public class Machine implements Externalizable {

    private static final long serialVersionUID = 1L;

    /**
     * ID of the machine, primary key.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /**
     * The user this machine belongs to.
     */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private ButterflyUser user;

    /**
     * The PCBID string for this machine.
     */
    @Column(name = "pcbid")
    private String pcbId;

    /**
     * The date and time this machine was registered.
     */
    @Column(name = "register_time")
    private LocalDateTime registerTime;

    /**
     * Whether this machine is enabled.
     */
    @Column(name = "enabled")
    private boolean enabled;

    /**
     * The port to use for this machine.
     */
    @Column(name = "port")
    private int port;

    public Machine() { }

    public Machine(final ButterflyUser user, final String pcbId, final LocalDateTime registerTime,
                   final boolean enabled, final int port) {
        this.user = user;
        this.pcbId = pcbId;
        this.registerTime = registerTime;
        this.enabled = enabled;
        this.port = port;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.user);
        out.writeUTF(this.pcbId);
        out.writeObject(this.registerTime);
        out.writeBoolean(this.enabled);
        out.writeInt(this.port);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((ButterflyUser) in.readObject());
        this.setPcbId(in.readUTF());
        this.setRegisterTime((LocalDateTime) in.readObject());
        this.setEnabled(in.readBoolean());
        this.setPort(in.readInt());
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public ButterflyUser getUser() {
        return user;
    }

    public void setUser(ButterflyUser user) {
        this.user = user;
    }

    public String getPcbId() {
        return pcbId;
    }

    public void setPcbId(String pcbId) {
        this.pcbId = pcbId;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}