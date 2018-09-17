package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Input Attributes For ContactUsAsynTask
 *
 * @author MUVI
 */

public class AppHomeFeatureInputModel {
    String authToken = "";
    String featureSectionLimit = "3";
    String getFeatureSectionOffset = "1";
    String lang_code = "";
    String userId = "";
    String featureSectionContentLimit = "6";
    String getFeatureSectionContentOffset = "1";
    String platform_type = "mobile";
    String countryCode;
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }



    public String getPlatform_type() {
        return platform_type;
    }

    public void setPlatform_type(String platform_type) {
        this.platform_type = platform_type;
    }


    public String getFeatureSectionContentLimit() {
        return featureSectionContentLimit;
    }

    public void setFeatureSectionContentLimit(String featureSectionContentLimit) {
        this.featureSectionContentLimit = featureSectionContentLimit;
    }

    public String getGetFeatureSectionContentOffset() {
        return getFeatureSectionContentOffset;
    }

    public void setGetFeatureSectionContentOffset(String getFeatureSectionContentOffset) {
        this.getFeatureSectionContentOffset = getFeatureSectionContentOffset;
    }





    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getFeatureSectionLimit() {
        return featureSectionLimit;
    }

    public void setFeatureSectionLimit(String featureSectionLimit) {
        this.featureSectionLimit = featureSectionLimit;
    }

    public String getGetFeatureSectionOffset() {
        return getFeatureSectionOffset;
    }

    public void setGetFeatureSectionOffset(String getFeatureSectionOffset) {
        this.getFeatureSectionOffset = getFeatureSectionOffset;
    }

    public String getLang_code() {
        return lang_code;
    }

    public void setLang_code(String lang_code) {
        this.lang_code = lang_code;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }





}
