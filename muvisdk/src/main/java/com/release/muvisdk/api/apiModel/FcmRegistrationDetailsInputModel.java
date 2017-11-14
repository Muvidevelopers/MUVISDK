package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Input Attributes For FcmRegistrationDetailsAsynTask.
 *
 * @author Abhishek
 */

public class FcmRegistrationDetailsInputModel {

    private String authToken, device_id, fcm_token;
    private int device_type;

    /**
     * This Method is use to Get the Auth Token
     *
     * @return authtoken
     */

    public String getAuthToken() {
        return authToken;
    }

    /**
     * This Method is use to Set the Auth Token
     *
     * @param authToken For Setting The Auth Token
     */


    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * This Method is use to Get the Device Id
     *
     * @return device_id
     */

    public String getDevice_id() {
        return device_id;
    }

    /**
     * This Method is use to Set the Device Id
     *
     * @param device_id For Setting The Device Id
     */

    public void setDevice_id(String device_id) {
        this.device_id = device_id;
    }

    /**
     * This Method is use to Get the FCM Token
     *
     * @return fcm_token
     */

    public String getFcm_token() {
        return fcm_token;
    }

    /**
     * This Method is use to Set the FCM Token
     *
     * @param fcm_token For Setting The FCM Token
     */

    public void setFcm_token(String fcm_token) {
        this.fcm_token = fcm_token;
    }

    /**
     * This Method is use to Get the Device Type
     *
     * @return device_type
     */

    public int getDevice_type() {
        return device_type;
    }

    /**
     * This Method is use to Set the Device Type
     *
     * @param device_type For Setting The Device Type
     */

    public void setDevice_type(int device_type) {
        this.device_type = device_type;
    }
}
