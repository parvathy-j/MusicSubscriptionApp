package com.example.musicsubscription.model;

/**
 * Represents a model for music items. This class is used to store properties of a music item such as title,
 * artist, year of release, and an image URL associated with the music.
 */
public class musicmodel {

    private String title;
    private String artist;
    private String year;
    private String img_url;

    /**
     * Gets the image URL of the music item.
     * @return A string representing the URL of the music item's associated image.
     */
    public String getImg_url() {
        return img_url;
    }

    /**
     * Sets the image URL of the music item.
     * @param img_url A string containing the URL to set for the music item's image.
     */
    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    /**
     * Gets the title of the music item.
     * @return A string representing the title of the music.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the artist of the music item.
     * @return A string representing the artist of the music.
     */
    public String getArtist() {
        return artist;
    }

    /**
     * Gets the release year of the music item.
     * @return A string representing the year the music was released.
     */
    public String getYear() {
        return year;
    }

    /**
     * Sets the title of the music item.
     * @param title A string containing the title to set for the music.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Sets the artist of the music item.
     * @param artist A string containing the artist to set for the music.
     */
    public void setArtist(String artist) {
        this.artist = artist;
    }

    /**
     * Sets the year the music item was released.
     * @param year A string containing the year to set for the release of the music.
     */
    public void setYear(String year) {
        this.year = year;
    }

}
