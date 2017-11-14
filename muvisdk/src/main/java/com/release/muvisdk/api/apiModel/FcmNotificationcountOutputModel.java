package com.release.muvisdk.api.apiModel;

/**
 * This Model Class Holds All The Output Attributes For FcmNotificationlistsAsynTask.
 *
 * @author Abhishek
 */

public class FcmNotificationcountOutputModel {

    private String status;
    private int count;

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
     * This Method is use to Get the Count
     *
     * @return count
     */

    public int getCount() {
        return count;
    }

    /**
     * This Method is use to Set the Count
     *
     * @param count For Setting The Count
     */

    public void setCount(int count) {
        this.count = count;
    }
}
