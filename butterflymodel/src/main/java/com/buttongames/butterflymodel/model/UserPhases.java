package com.buttongames.butterflymodel.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Model class to represent the phases a user has set for different games
 * on the network. This applies to all PCBs belonging to a particular user.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "user_phases")
public class UserPhases implements Externalizable {

    private static final long serialVersionUID = 1L;

    /**
     * ID of the phases, primary key.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /**
     * The user these phases belong to.
     */
    @OneToOne
    @JoinColumn(name = "user_id")
    private ButterflyUser user;

    /**
     * The user's phase for DDR Ace.
     */
    @Column(name = "ddr_16_phase")
    private int ddr16Phase;

    public UserPhases() { }

    public UserPhases(final ButterflyUser user, final int ddr16Phase) {
        this.user = user;
        this.ddr16Phase = ddr16Phase;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.user);
        out.writeInt(this.ddr16Phase);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setUser((ButterflyUser) in.readObject());
        this.setDdr16Phase(in.readInt());
    }

    private long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ButterflyUser getUser() {
        return user;
    }

    public void setUser(ButterflyUser user) {
        this.user = user;
    }

    public int getDdr16Phase() {
        return ddr16Phase;
    }

    public void setDdr16Phase(int ddr16Phase) {
        this.ddr16Phase = ddr16Phase;
    }
}
