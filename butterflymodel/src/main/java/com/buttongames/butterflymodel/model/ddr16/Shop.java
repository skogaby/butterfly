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

/**
 * Model class that represents a shop housing a DDR 16 machine.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
@Entity
@Table(name = "ddr_16_shops")
public class Shop implements Externalizable {

    private static final long serialVersionUID = 1L;

    /**
     * ID of the object, primary key.
     */
    @Id
    @GeneratedValue
    @Column(name = "id")
    private long id;

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

    public Shop() { }

    public Shop(final String pcbId, final String locationId, final String name,
                final String country, final String region, final boolean isPublic) {
        this.pcbId = pcbId;
        this.locationId = locationId;
        this.name = name;
        this.country = country;
        this.region = region;
        this.isPublic = isPublic;
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(this.id);
        out.writeUTF(this.pcbId);
        out.writeUTF(this.locationId);
        out.writeUTF(this.name);
        out.writeUTF(this.country);
        out.writeUTF(this.region);
        out.writeBoolean(this.isPublic);
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        this.setId(in.readLong());
        this.setPcbId(in.readUTF());
        this.setLocationId(in.readUTF());
        this.setName(in.readUTF());
        this.setCountry(in.readUTF());
        this.setRegion(in.readUTF());
        this.setPublic(in.readBoolean());
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
}
