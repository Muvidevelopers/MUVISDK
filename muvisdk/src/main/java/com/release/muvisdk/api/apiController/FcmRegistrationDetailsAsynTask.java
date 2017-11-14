/**
 * SDK initialization, platform and device information classes.
 */


package com.release.muvisdk.api.apiController;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.release.muvisdk.api.APIUrlConstant;

import com.release.muvisdk.api.apiModel.FcmRegistrationDetailsInputModel;
import com.release.muvisdk.api.apiModel.FcmRegistrationDetailsOutputModel;

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
 * This Class is use to get the Registration details of FCM.
 *
 * @author Abhishek
 */

public class FcmRegistrationDetailsAsynTask extends AsyncTask<FcmRegistrationDetailsInputModel, Void, Void> {

    private FcmRegistrationDetailsInputModel fcmRegistrationDetailsInputModel;
    private FcmRegistrationDetailsListener listener;
    private Context context;
    private String PACKAGE_NAME;
    private String responseStr;
    private String responseMsg;
    private int code;
    String message;


    /**
     * Interface used to allow the caller of a FcmRegistrationDetailsAsynTask to run some code when get
     * responses.
     */


    public interface FcmRegistrationDetailsListener {

        /**
         * This method will be invoked before controller start execution.
         * This method to handle pre-execution work.
         */

        void onFcmRegistrationDetailsPreExecuteStarted();

        /**
         * This method will be invoked after controller complete execution.
         * This method to handle post-execution work.
         *
         * @param fcmRegistrationDetailsOutputModel A Model Class which contain responses. To get that responses we need to call the respective getter methods.
         * @param message                           On Success Message
         * @param code                              Response Code From The Server
         */

        void onFcmRegistrationDetailsPostExecuteCompleted(FcmRegistrationDetailsOutputModel fcmRegistrationDetailsOutputModel, String message, int code);
    }

    FcmRegistrationDetailsOutputModel fcmRegistrationDetailsOutputModel = new FcmRegistrationDetailsOutputModel();

    /**
     * Constructor to initialise the private data members.
     *
     * @param fcmRegistrationDetailsInputModel A Model Class which is use for background task, we need to set all the attributes through setter methods of input model class,
     *                                         For Example: to use this API we have to set following attributes:
     *                                         getAuthToken(),getMovieUniqueId() etc.
     * @param listener                         FcmRegistrationDetailsListener
     * @param context                          android.content.Context
     */

    public FcmRegistrationDetailsAsynTask(FcmRegistrationDetailsInputModel fcmRegistrationDetailsInputModel, FcmRegistrationDetailsListener listener, Context context) {
        this.fcmRegistrationDetailsInputModel = fcmRegistrationDetailsInputModel;
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
    protected Void doInBackground(FcmRegistrationDetailsInputModel... params) {
        // String urlRouteList = Util.rootUrl().trim() + Util.DeleteFavList.trim();

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(APIUrlConstant.getFcmregistration());
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.addHeader(HeaderConstants.AUTH_TOKEN, this.fcmRegistrationDetailsInputModel.getAuthToken().trim());
            httppost.addHeader(HeaderConstants.DEVICE_ID, this.fcmRegistrationDetailsInputModel.getDevice_id().trim());
            httppost.addHeader(HeaderConstants.FCM_TOKEN, this.fcmRegistrationDetailsInputModel.getFcm_token().trim());
            httppost.addHeader(HeaderConstants.DEVICE_TYPE, "1");

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

                    responseMsg = myJson.getString("status");
                    fcmRegistrationDetailsOutputModel.setResponseMsg(responseMsg);


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            code = 0;
            responseMsg = "";
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onFcmRegistrationDetailsPreExecuteStarted();
        code = 0;
        if (!PACKAGE_NAME.equals(SDKInitializer.getUser_Package_Name_At_Api(context))) {
            this.cancel(true);
            message = "Packge Name Not Matched";
            listener.onFcmRegistrationDetailsPostExecuteCompleted(fcmRegistrationDetailsOutputModel, message, code);
            return;
        }
        if (SDKInitializer.getHashKey(context).equals("")) {
            this.cancel(true);
            message = "Hash Key Is Not Available. Please Initialize The SDK";
            listener.onFcmRegistrationDetailsPostExecuteCompleted(fcmRegistrationDetailsOutputModel, message, code);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFcmRegistrationDetailsPostExecuteCompleted(fcmRegistrationDetailsOutputModel, message, code);
    }
}