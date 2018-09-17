package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Attributes For HomePageAsynTask
 *
 * @author MUVI
 */

public class HomeFeaturePageBannerModel {

    private String image_path;
    private String banner_url;

    /**
     * This Method is use to Get the Image Path
     *
     * @return image_path
     */

    public String getImage_path() {
        return image_path;
    }

    /**
     * This Method is use to Set the Image Path
     *
     * @param image_path For Setting The Image Path
     */

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    /**
     * This Method is use to Get the Banner URL
     *
     * @return banner_url
     */
    public String getBanner_url() {
        return banner_url;
    }

    /**
     * This Method is use to Set the Banner URL
     *
     * @param banner_url For Setting The Banner URL
     */

    public void setBanner_url(String banner_url) {
        this.banner_url = banner_url;
    }

}
