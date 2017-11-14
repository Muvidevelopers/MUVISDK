package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Output Attributes For AuthUserPaymentInfoAsyncTask
 *
 * @author MUVI
 */

public class AuthUserPaymentInfoOutputModel {

    String card_type;
    String card_last_fourdigit;
    String profile_id;
    String response_text;
    String token;

    /**
     * This Method is use to Get the Status
     *
     * @return status
     */
    public String getStatus() {
        return status;
    }

    /**
     * This Method is use to Set the Status
     *
     * @param status For Setting The Status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * This Method is use to Get the Card Type
     *
     * @return card_type
     */
    public String getCard_type() {
        return card_type;
    }

    /**
     * This Method is use to Set the Card Type
     *
     * @param card_type For Setting The Card Type
     */
    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    /**
     * This Method is use to Get the Card Last Four Digit
     *
     * @return card_last_fourdigit
     */
    public String getCard_last_fourdigit() {
        return card_last_fourdigit;
    }

    /**
     * This Method is use to Set the Card Last Four Digit
     *
     * @param card_last_fourdigit For Setting The Card Last Four Digit
     */
    public void setCard_last_fourdigit(String card_last_fourdigit) {
        this.card_last_fourdigit = card_last_fourdigit;
    }

    /**
     * This Method is use to Get the Profile ID
     *
     * @return profile_id
     */
    public String getProfile_id() {
        return profile_id;
    }

    /**
     * This Method is use to Get the Profile ID
     *
     * @param profile_id For Setting The Profile ID
     */
    public void setProfile_id(String profile_id) {
        this.profile_id = profile_id;
    }

    /**
     * This Method is use to Get the Response Text
     *
     * @return response_text
     */
    public String getResponse_text() {
        return response_text;
    }

    /**
     * This Method is use to Set the Response Text
     *
     * @param response_text For Setting The Response Text
     */
    public void setResponse_text(String response_text) {
        this.response_text = response_text;
    }

    /**
     * This Method is use to Get the Token
     *
     * @return token
     */
    public String getToken() {
        return token;
    }

    /**
     * This Method is use to Set the Token
     *
     * @param token For Setting The Token
     */
    public void setToken(String token) {
        this.token = token;
    }

    String status;

}
