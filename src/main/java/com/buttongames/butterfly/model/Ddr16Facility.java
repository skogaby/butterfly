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

/**
 * Model class that represents a facility housing a DDR 16 machine.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_facilities")
public class Ddr16Facility implements Externalizable {

    private static final long serialVersionUID = 1L;

    /** ID of the event, primary key. */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

    /** The user who owns the machine this event occurred on. */
    @ManyToOne
    @JoinColumn(name = "user_id")
    private ButterflyUser machineOwner;

    /** The PCBID for this facility. */
    @Column(name = "pcb_id")
    private String pcbId;

    /** The location ID for this facility. */
    @Column(name = "location_id")
    private String locationId;

    /** The name of the facility. */
    @Column(name = "name")
    private String name;

    /** The country of the facility. */
    @Column(name = "country")
    private String country;

    /** The region of the facility. */
    @Column(name = "region")
    private String region;

    /** Is the facility public. */
    @Column(name = "is_public")
    private boolean isPublic;

    /** The latitude of the facility. */
    @Column(name = "latitude")
    private String latitude;

    /** The longitude of the facility. */
    @Column(name = "longitude")
    private String longitude;

    /** The notch amount for the facility. */
    @Column(name = "notch_amount")
    private int notchAmount;

    /** The notch count for the facility. */
    @Column(name = "notch_count")
    private int notchCount;

    /** The supply limit for the facility. */
    @Column(name = "supply_limit")
    private int supplyLimit;

    public Ddr16Facility() { }

    public Ddr16Facility(final ButterflyUser machineOwner, final String pcbId, final String locationId, final String name,
                         final String country, final String region, final boolean isPublic, final String latitude, final String longitude,
                         final int notchAmount, final int notchCount, final int supplyLimit) {
        this.machineOwner = machineOwner;
        this.pcbId = pcbId;
        this.locationId = locationId;
        this.name = name;
        this.country = country;
        this.region = region;
        this.isPublic = isPublic;
        this.latitude = latitude;
        this.longitude = longitude;
        this.notchAmount = notchAmount;
        this.notchCount = notchCount;
        this.supplyLimit = supplyLimit;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeObject(this.machineOwner);
        out.writeUTF(this.pcbId);
        out.writeUTF(this.locationId);
        out.writeUTF(this.name);
        out.writeUTF(this.country);
        out.writeUTF(this.region);
        out.writeBoolean(this.isPublic);
        out.writeUTF(this.latitude);
        out.writeUTF(this.longitude);
        out.writeInt(this.notchAmount);
        out.writeInt(this.notchCount);
        out.writeInt(this.supplyLimit);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        this.setId(in.readLong());
        this.setMachineOwner((ButterflyUser) in.readObject());
        this.setPcbId(in.readUTF());
        this.setLocationId(in.readUTF());
        this.setName(in.readUTF());
        this.setCountry(in.readUTF());
        this.setRegion(in.readUTF());
        this.setPublic(in.readBoolean());
        this.setLatitude(in.readUTF());
        this.setLongitude(in.readUTF());
        this.setNotchAmount(in.readInt());
        this.setNotchCount(in.readInt());
        this.setSupplyLimit(in.readInt());
    }

    public long getId() {
        return id;
    }

    private void setId(long id) {
        this.id = id;
    }

    public ButterflyUser getMachineOwner() {
        return machineOwner;
    }

    public void setMachineOwner(ButterflyUser machineOwner) {
        this.machineOwner = machineOwner;
    }

    public String getPcbId() {
        return pcbId;
    }

    public void setPcbId(String pcbId) {
        this.pcbId = pcbId;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public int getNotchAmount() {
        return notchAmount;
    }

    public void setNotchAmount(int notchAmount) {
        this.notchAmount = notchAmount;
    }

    public int getNotchCount() {
        return notchCount;
    }

    public void setNotchCount(int notchCount) {
        this.notchCount = notchCount;
    }

    public int getSupplyLimit() {
        return supplyLimit;
    }

    public void setSupplyLimit(int supplyLimit) {
        this.supplyLimit = supplyLimit;
    }
}
