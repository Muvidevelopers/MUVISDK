package com.release.muvisdk.api.apiModel;

import java.util.ArrayList;

/**
 * This Model Class Holds All The Attributes For HomePageAsynTask
 *
 * @author MUVI
 */

public class HomeFeaturePageSectionModel {

    String studio_id ="";
    String language_id ="";
    String title ="";
    String section_id ="";
    String section_type ="";
    String total ="";
    ArrayList<HomeFeaturePageSectionDetailsModel> homeFeaturePageSectionDetailsModel = new ArrayList<>();


    public ArrayList<HomeFeaturePageSectionDetailsModel> getHomeFeaturePageSectionDetailsModel() {
        return homeFeaturePageSectionDetailsModel;
    }

    public void setHomeFeaturePageSectionDetailsModel(ArrayList<HomeFeaturePageSectionDetailsModel> homeFeaturePageSectionDetailsModel) {
        this.homeFeaturePageSectionDetailsModel = homeFeaturePageSectionDetailsModel;
    }


    public String getStudio_id() {
        return studio_id;
    }

    public void setStudio_id(String studio_id) {
        this.studio_id = studio_id;
    }

    public String getLanguage_id() {
        return language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSection_id() {
        return section_id;
    }

    public void setSection_id(String section_id) {
        this.section_id = section_id;
    }

    public String getSection_type() {
        return section_type;
    }

    public void setSection_type(String section_type) {
        this.section_type = section_type;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }



}
