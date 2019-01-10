package com.buttongames.butterfly.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

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
    private long userId;

    /**
     * The user's card PIN.
     */
    @Column(name = "pin")
    private String pin;

    public ButterflyUser() {

    }

    public ButterflyUser(final String pin) {
        this.pin = pin;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.userId);
        out.writeUTF(this.pin);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.userId = in.readLong();
        this.pin = in.readUTF();
    }

    public long getUserId() {
        return userId;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
