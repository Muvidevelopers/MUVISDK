package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Output Attributes For FcmRegistrationDetailsAsynTask.
 *
 * @author Abhishek
 */

public class FcmRegistrationDetailsOutputModel {

    private String responseMsg;

    /**
     * This Method is use to Get the Response Message
     *
     * @return responseMsg
     */

    public String getResponseMsg() {
        return responseMsg;
    }

    /**
     * This Method is use to Set the Response Message
     *
     * @param responseMsg For Setting The Response Code
     */

    public void setResponseMsg(String responseMsg) {
        this.responseMsg = responseMsg;
    }
}
