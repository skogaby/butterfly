package com.buttongames.butterfly.model.ddr16;

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
 * Model class that represents ghost data for a user's song record.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_ghost_data")
public class GhostData implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the ghost data, primary key */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The user this data belongs to */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserProfile user;

    /** The actual ghost steps for this data */
    @Column(name = "ghost_data", columnDefinition = "TEXT")
    private String ghostData;

    public GhostData() { }

    public GhostData(UserProfile user, String ghostData) {
        this.user = user;
        this.ghostData = ghostData;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.user);
        out.writeUTF(this.ghostData);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((UserProfile) in.readObject());
        this.setGhostData(in.readUTF());
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    public String getGhostData() {
        return ghostData;
    }

    public void setGhostData(String ghostData) {
        this.ghostData = ghostData;
    }
}
