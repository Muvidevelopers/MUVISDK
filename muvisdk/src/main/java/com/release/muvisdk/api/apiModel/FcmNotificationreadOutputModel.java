package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Output Attributes For FcmNotificationreadAsynTask.
 *
 * @author Abhishek
 */

public class FcmNotificationreadOutputModel {

    private String message;

    /**
     * This Method is use to Get the Message
     *
     * @return message
     */

    public String getMessage() {
        return message;
    }

    /**
     * This Method is use to Set the Message
     *
     * @param message For Setting The Message
     */

    public void setMessage(String message) {
        this.message = message;
    }
}
