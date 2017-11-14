/**
 * SDK initialization, platform and device information classes.
 */


package com.release.muvisdk.api.apiController;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.release.muvisdk.api.APIUrlConstant;

import com.release.muvisdk.api.apiModel.DeleteFavInputModel;
import com.release.muvisdk.api.apiModel.DeleteFavOutputModel;
import com.release.muvisdk.api.apiModel.FcmNotificationcountInputModel;
import com.release.muvisdk.api.apiModel.FcmNotificationcountOutputModel;

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
 * This Class is use to get the notification count from FCM.
 *
 * @author MUVI
 */

public class FcmNotificationcountAsynTask extends AsyncTask<FcmNotificationcountInputModel, Void, Void> {

    private FcmNotificationcountInputModel fcmNotificationcountInputModel;
    private String PACKAGE_NAME;
    private String responseStr;
    private String sucessMsg;
    private FcmNotificationcountListener listener;
    private Context context;
    private int code;

    /**
     * Interface used to allow the caller of a FcmNotificationcountAsynTask to run some code when get
     * responses.
     */


    public interface FcmNotificationcountListener {

        /**
         * This method will be invoked before controller start execution.
         * This method to handle pre-execution work.
         */

        void onFcmNotificationcountPreExecuteStarted();

        /**
         * This method will be invoked after controller complete execution.
         * This method to handle post-execution work.
         *
         * @param fcmNotificationcountOutputModel A Model Class which contain responses. To get that responses we need to call the respective getter methods.
         */

        void onFcmNotificationcountPostExecuteCompleted(FcmNotificationcountOutputModel fcmNotificationcountOutputModel, int code);
    }

    FcmNotificationcountOutputModel fcmNotificationcountOutputModel = new FcmNotificationcountOutputModel();

    /**
     * Constructor to initialise the private data members.
     *
     * @param fcmNotificationcountInputModel A Model Class which is use for background task, we need to set all the attributes through setter methods of input model class,
     *                                       For Example: to use this API we have to set following attributes:
     *                                       getAuthToken(),getDevice_id() etc.
     * @param listener                       FcmNotificationcount Listener
     * @param context                        android.content.Context
     */

    public FcmNotificationcountAsynTask(FcmNotificationcountInputModel fcmNotificationcountInputModel, FcmNotificationcountListener listener, Context context) {
        this.fcmNotificationcountInputModel = fcmNotificationcountInputModel;
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
    protected Void doInBackground(FcmNotificationcountInputModel... params) {
        // String urlRouteList = Util.rootUrl().trim() + Util.DeleteFavList.trim();

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(APIUrlConstant.getNotificationcount());
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.addHeader(HeaderConstants.AUTH_TOKEN, this.fcmNotificationcountInputModel.getAuthToken().trim());
            httppost.addHeader(HeaderConstants.DEVICE_ID, this.fcmNotificationcountInputModel.getDevice_id().trim());

            try {
                HttpResponse response = httpclient.execute(httppost);
                responseStr = EntityUtils.toString(response.getEntity());
                Log.v("MUVISDK", "RES" + responseStr);

            } catch (org.apache.http.conn.ConnectTimeoutException e) {
                code = 0;
                sucessMsg = "";

            } catch (IOException e) {
                code = 0;
                sucessMsg = "";
            }

            JSONObject myJson = null;
            if (responseStr != null) {

                myJson = new JSONObject(responseStr);
                code = Integer.parseInt(myJson.optString("code"));
            }

            if (code == 400) {

                try {

                    fcmNotificationcountOutputModel.setCount(myJson.optInt("count"));
                    fcmNotificationcountOutputModel.setStatus(myJson.optString("status"));


                } catch (Exception e) {
                    e.printStackTrace();
                    code = 0;
                    sucessMsg = "";
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            code = 0;
            sucessMsg = "";
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onFcmNotificationcountPreExecuteStarted();
        code = 0;
        if (!PACKAGE_NAME.equals(SDKInitializer.getUser_Package_Name_At_Api(context))) {
            this.cancel(true);
            sucessMsg = "Packge Name Not Matched";
            listener.onFcmNotificationcountPostExecuteCompleted(fcmNotificationcountOutputModel, code);
            return;
        }
        if (SDKInitializer.getHashKey(context).equals("")) {
            this.cancel(true);
            sucessMsg="Packge Name Not Matched";
            listener.onFcmNotificationcountPostExecuteCompleted(fcmNotificationcountOutputModel, code);
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        listener.onFcmNotificationcountPostExecuteCompleted(fcmNotificationcountOutputModel,code);
    }
}
