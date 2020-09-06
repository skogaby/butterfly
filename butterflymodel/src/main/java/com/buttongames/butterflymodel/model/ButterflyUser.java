package com.buttongames.butterflymodel.model;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.GraphQLQuery;

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
 * Model class for a user within the server. This user is tied to a person -- email
 * address, password, etc. This user also has a PIN, which is applied across any
 * and all cards they possess. <code>ButterflyUser</code> does *NOT* represent a profile for any
 * particular game.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "users")
public class ButterflyUser implements Externalizable {

    private static final long serialVersionUID = 1L;

    /**
     * The integer user ID for this user. Serves as the primary key in the database.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /**
     * The user's card PIN.
     */
    @Column(name = "pin")
    private String pin;

    /**
     * The date and time this user was registered.
     */
    @Column(name = "register_time")
    private LocalDateTime registerTime;

    /**
     * The date and time this user last logged into a game on a card.
     */
    @Column(name = "last_play_time")
    private LocalDateTime lastPlayTime;

    /**
     * The amount of Paseli this user has.
     */
    @Column(name = "paseli_balance")
    private int paseliBalance;

    public ButterflyUser() { }

    public ButterflyUser(final String pin, final LocalDateTime registerTime, final LocalDateTime lastPlayTime,
                         final int paseliBalance) {
        this.pin = pin;
        this.registerTime = registerTime;
        this.lastPlayTime = lastPlayTime;
        this.paseliBalance = paseliBalance;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeUTF(this.pin);
        out.writeObject(this.registerTime);
        out.writeObject(this.lastPlayTime);
        out.writeInt(this.paseliBalance);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setPin(in.readUTF());
        this.setRegisterTime((LocalDateTime) in.readObject());
        this.setLastPlayTime((LocalDateTime) in.readObject());
        this.setPaseliBalance(in.readInt());
    }

    @GraphQLQuery(name = "id")
    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    @GraphQLQuery(name = "registerTime")
    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    @GraphQLIgnore
    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    @GraphQLQuery(name = "lastPlayTime")
    public LocalDateTime getLastPlayTime() {
        return lastPlayTime;
    }

    public void setLastPlayTime(LocalDateTime lastPlayTime) {
        this.lastPlayTime = lastPlayTime;
    }

    @GraphQLIgnore
    public int getPaseliBalance() {
        return paseliBalance;
    }

    public void setPaseliBalance(int paseliBalance) {
        this.paseliBalance = paseliBalance;
    }
}
