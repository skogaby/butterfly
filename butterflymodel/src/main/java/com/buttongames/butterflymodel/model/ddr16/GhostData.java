package com.buttongames.butterflymodel.model.ddr16;

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

    /** The mcode of the song for this data */
    @Column(name = "mcode")
    private int mcode;

    /** The difficulty the ghost data was for */
    @Column(name = "note_type")
    private int noteType;

    public GhostData() { }

    public GhostData(UserProfile user, String ghostData, int mcode, int noteType) {
        this.user = user;
        this.ghostData = ghostData;
        this.mcode = mcode;
        this.noteType = noteType;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.user);
        out.writeUTF(this.ghostData);
        out.writeInt(this.mcode);
        out.writeInt(this.noteType);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((UserProfile) in.readObject());
        this.setGhostData(in.readUTF());
        this.setMcode(in.readInt());
        this.setNoteType(in.readInt());
    }

    @GraphQLQuery(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @GraphQLQuery(name = "user")
    public UserProfile getUser() {
        return user;
    }

    public void setUser(UserProfile user) {
        this.user = user;
    }

    @GraphQLQuery(name = "ghostData")
    public String getGhostData() {
        return ghostData;
    }

    public void setGhostData(String ghostData) {
        this.ghostData = ghostData;
    }

    @GraphQLQuery(name = "mcode")
    public int getMcode() {
        return mcode;
    }

    public void setMcode(int mcode) {
        this.mcode = mcode;
    }

    @GraphQLQuery(name = "noteType")
    public int getNoteType() {
        return noteType;
    }

    public void setNoteType(int noteType) {
        this.noteType = noteType;
    }
}
