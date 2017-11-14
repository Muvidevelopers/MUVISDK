/**
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 * <p>
 * SDK initialization, platform and device information classes.
 */


/**
 * SDK initialization, platform and device information classes.
 */


package com.release.muvisdk.api.apiController;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.api.apiModel.FcmNotificationreadInputModel;
import com.release.muvisdk.api.apiModel.FcmNotificationreadOutputModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * This Class is use to read FCM Notification.
 *
 * @author MUVI
 */

public class FcmNotificationreadAsynTask extends AsyncTask<FcmNotificationreadInputModel, Void, Void> {

    private FcmNotificationreadInputModel fcmNotificationreadInputModel;
    private FcmNotificationreadListener listener;
    private Context context;
    private String PACKAGE_NAME;
    private String responseStr;
    private String responseMsg;
    private int code;
    private String message;

    /**
     * Interface used to allow the caller of a FcmNotificationreadAsynTask to run some code when get
     * responses.
     */


    public interface FcmNotificationreadListener {

        /**
         * This method will be invoked before controller start execution.
         * This method to handle pre-execution work.
         */

        void onFcmNotificationreadPreExecuteStarted();

        /**
         * This method will be invoked after controller complete execution.
         * This method to handle post-execution work.
         *
         * @param fcmNotificationreadOutputModel A Model Class which contain responses. To get that responses we need to call the respective getter methods.
         * @param message                        On Success Message
         * @param code                           Response Code From The Server
         */

        void onFcmNotificationreadPostExecuteCompleted(FcmNotificationreadOutputModel fcmNotificationreadOutputModel, String message, int code);
    }

    FcmNotificationreadOutputModel fcmNotificationreadOutputModel = new FcmNotificationreadOutputModel();

    /**
     * Constructor to initialise the private data members.
     *
     * @param fcmNotificationreadInputModel A Model Class which is use for background task, we need to set all the attributes through setter methods of input model class,
     *                            For Example: to use this API we have to set following attributes:
     *                            getAuthToken(),getMovieUniqueId() etc.
     * @param listener            FcmNotificationreadListener
     * @param context             android.content.Context
     */

    public FcmNotificationreadAsynTask(FcmNotificationreadInputModel fcmNotificationreadInputModel, FcmNotificationreadListener listener, Context context) {
        this.fcmNotificationreadInputModel = fcmNotificationreadInputModel;
        this.listener = listener;
        this.context = context;
        PACKAGE_NAME = context.getPackageName();
    }

    /**
     * Background thread to execute.
     *
     * @return null
     * @throws org.apache.http.conn.ConnectTimeoutException,IOException,JSONException
     */


    @Override
    protected Void doInBackground(FcmNotificationreadInputModel... params) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(APIUrlConstant.getReadallnotification());
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.addHeader(HeaderConstants.AUTH_TOKEN, this.fcmNotificationreadInputModel.getAuthToken().trim());
            httppost.addHeader(HeaderConstants.DEVICE_ID, this.fcmNotificationreadInputModel.getDevice_id().trim());

            try {
                HttpResponse response = httpclient.execute(httppost);
                responseStr = EntityUtils.toString(response.getEntity());
                Log.v("MUVISDK", "RES" + responseStr);

            } catch (org.apache.http.conn.ConnectTimeoutException e) {
                code = 0;
                responseMsg = "";

            } catch (IOException e) {
                code = 0;
                responseMsg = "";
            }

            JSONObject myJson = null;
            if (responseStr != null) {
                myJson = new JSONObject(responseStr);
                code = Integer.parseInt(myJson.optString("code"));
                message = myJson.optString("msg");
            }
            if (code == 200) {

                try {

                    if (myJson.has("status")) {

                        responseMsg = myJson.optString("status");
                        fcmNotificationreadOutputModel.setMessage(responseMsg);
                    }


                } catch (Exception e) {
                    code = 0;
                    responseMsg = "";
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onFcmNotificationreadPreExecuteStarted();
        code=0;
        if (!PACKAGE_NAME.equals(SDKInitializer.getUser_Package_Name_At_Api(context))) {
            this.cancel(true);
            message="Packge Name Not Matched";
            listener.onFcmNotificationreadPostExecuteCompleted(fcmNotificationreadOutputModel,message,code);
            return;
        }
        if (SDKInitializer.getHashKey(context).equals("")) {
            this.cancel(true);
            message = "Hash Key Is Not Available. Please Initialize The SDK";
            listener.onFcmNotificationreadPostExecuteCompleted(fcmNotificationreadOutputModel,message,code);
        }
    }

    @Override
    protected void onPostExecute(Void avoid) {
        super.onPostExecute(avoid);
        listener.onFcmNotificationreadPostExecuteCompleted(fcmNotificationreadOutputModel, message, code);
    }
}
