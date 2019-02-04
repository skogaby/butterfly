package com.buttongames.butterflymodel.model.ddr16;

/**
 * This doesn't get persisted to the database, but it represents a song in the musicdb.
 * @author skogaby (skogabyskogaby@gmail.com)
 */
public class Song {

    private int mcode;
    private String baseName;
    private String title;
    private String artist;

    public Song(final int mcode, final String baseName, final String title, final String artist) {
        this.mcode = mcode;
        this.baseName = baseName;
        this.title = title;
        this.artist = artist;
    }

    public int getMcode() {
        return mcode;
    }

    public void setMcode(int mcode) {
        this.mcode = mcode;
    }

    public String getBaseName() {
        return baseName;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}
