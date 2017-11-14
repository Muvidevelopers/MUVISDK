package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Input Attributes For FcmNotificationcountAsynTask.
 *
 * @author Abhishek
 */

public class FcmNotificationcountInputModel {

    private String authToken, device_id;

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
}
