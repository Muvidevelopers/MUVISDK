package com.release.muvisdk.api.apiModel;

import java.util.ArrayList;

/**
 * This Model Class Holds All The Input Attributes For LoadFilterVideoAsync
 *
 * @author MUVI
 */

public class LoadFilterVideoInput {

    String limit = "10", offset = "0";
    String orderby;
    String authToken;
    String permalink;
    String country;
    String Language;


    public ArrayList<String> getGenreArray() {
        return genreArray;
    }

    public void setGenreArray(ArrayList<String> genreArray) {
        this.genreArray = genreArray;
    }

    public  ArrayList<String> genreArray;


    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getOrderby() {
        return orderby;
    }

    public void setOrderby(String orderby) {
        this.orderby = orderby;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return Language;
    }

    public void setLanguage(String language) {
        Language = language;
    }
}
