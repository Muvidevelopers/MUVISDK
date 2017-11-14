/**
 * SDK initialization, platform and device information classes.
 */


package com.release.muvisdk.api.apiController;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.api.apiModel.ValidateUserInput;
import com.release.muvisdk.api.apiModel.ValidateUserOutput;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This Class checks whether the user is validate or not.
 * If user is not validate then it won't allow that user to see any video.
 *
 * @author MUVI
 */
public class GetValidateUserAsynTask extends AsyncTask<ValidateUserInput, Void, Void> {

    private ValidateUserInput validateUserInput;
    private String responseStr;
    private int status;
    private String message;
    private String PACKAGE_NAME;
    private String validuser_str;
    private String isSubscribed;
    private GetValidateUserListener listener;
    private Context context;

    /**
     * Interface used to allow the caller of a GetValidateUserAsynTask to run some code when get
     * responses.
     */

    public interface GetValidateUserListener {

        /**
         * This method will be invoked before controller start execution.
         * This method to handle pre-execution work.
         */

        void onGetValidateUserPreExecuteStarted();

        /**
         * This method will be invoked after controller complete execution.
         * This method to handle post-execution work.
         *
         * @param validateUserOutput A Model Class which contain responses. To get that responses we need to call the respective getter methods.
         * @param status             Response Code From The Server
         * @param message            On Success Message
         */

        void onGetValidateUserPostExecuteCompleted(ValidateUserOutput validateUserOutput, int status, String message);
    }


    ValidateUserOutput validateUserOutput = new ValidateUserOutput();

    /**
     * Constructor to initialise the private data members.
     *
     * @param validateUserInput A Model Class which is use for background task, we need to set all the attributes through setter methods of input model class,
     *                          For Example: to use this API we have to set following attributes:
     *                          setAuthToken(),setMuviUniqueId() etc.
     * @param listener          GetValidateUser Listener
     * @param context           android.content.Context
     */

    public GetValidateUserAsynTask(ValidateUserInput validateUserInput, GetValidateUserListener listener, Context context) {
        this.listener = listener;
        this.context = context;

        this.validateUserInput = validateUserInput;
        PACKAGE_NAME = context.getPackageName();

    }

    /**
     * Background thread to execute.
     *
     * @return null
     * @throws org.apache.http.conn.ConnectTimeoutException,IOException,JSONException
     */

    @Override
    protected Void doInBackground(ValidateUserInput... params) {


        try {

            // Execute HTTP Post Request
            try {
                URL url = new URL(APIUrlConstant.getValidateUserForContentUrl());
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(20000);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getUserId());
                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getMuviUniqueId());
                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getAuthToken());

                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getEpisodeStreamUniqueId());
                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getSeasonId());
                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getLanguageCode());
                Log.v("MUVISDK", "this.validateUserInput.getUserId()" + this.validateUserInput.getPurchaseType());

                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(HeaderConstants.AUTH_TOKEN, this.validateUserInput.getAuthToken())
                        .appendQueryParameter(HeaderConstants.USER_ID, this.validateUserInput.getUserId())
                        .appendQueryParameter(HeaderConstants.MOVIE_ID, this.validateUserInput.getMuviUniqueId())
                        .appendQueryParameter(HeaderConstants.EPISODE_ID, this.validateUserInput.getEpisodeStreamUniqueId())
                        .appendQueryParameter(HeaderConstants.SEASON_ID, this.validateUserInput.getSeasonId())
                        .appendQueryParameter(HeaderConstants.LANG_CODE, this.validateUserInput.getLanguageCode())
                        .appendQueryParameter(HeaderConstants.PURCHASE_TYPE, this.validateUserInput.getPurchaseType());
                String query = builder.build().getEncodedQuery();

                Log.v("MUVISDK", "authToken" + this.validateUserInput.getAuthToken());
                Log.v("MUVISDK", "user_id" + this.validateUserInput.getUserId());
                Log.v("MUVISDK", "movie_id" + this.validateUserInput.getMuviUniqueId());
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();


                int responseCode = conn.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    final InputStream err = conn.getErrorStream();
                    try {
                    } finally {

                        InputStreamReader isr = new InputStreamReader(err);
                        BufferedReader in = new BufferedReader(isr);

                        String inputLine;

                        while ((inputLine = in.readLine()) != null) {
                            System.out.println(inputLine);
                            responseStr = inputLine;
                            Log.v("MUVISDK", "responseStr" + responseStr);

                        }
                        in.close();
                        err.close();
                    }
                } else {
                    InputStream ins = conn.getInputStream();

                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                        Log.v("MUVISDK", "responseStr" + responseStr);

                    }
                    in.close();
                }


            } catch (org.apache.http.conn.ConnectTimeoutException e) {

                status = 0;
                message = "Error";
                Log.v("MUVISDK", "ConnectTimeoutException" + e);


            } catch (IOException e) {
                status = 0;
                message = "Error";
                Log.v("MUVISDK", "IOException" + e);

            }

            JSONObject mainJson = null;
            if (responseStr != null) {
                mainJson = new JSONObject(responseStr);
                status = Integer.parseInt(mainJson.optString("code"));
                if ((mainJson.has("msg")) && mainJson.optString("msg").trim() != null && !mainJson.optString("msg").trim().isEmpty() && !mainJson.optString("msg").trim().equals("null") && !mainJson.optString("msg").trim().matches("")) {
                    validateUserOutput.setMessage(mainJson.optString("msg"));
                    message = mainJson.optString("msg");

                }
                if ((mainJson.has("member_subscribed")) && mainJson.optString("member_subscribed").trim() != null && !mainJson.optString("member_subscribed").trim().isEmpty() && !mainJson.optString("member_subscribed").trim().equals("null") && !mainJson.optString("member_subscribed").trim().matches("")) {
                    validateUserOutput.setIsMemberSubscribed(mainJson.optString("member_subscribed"));
                    isSubscribed = mainJson.optString("member_subscribed");

                }

                if ((mainJson.has("status")) && mainJson.optString("status").trim() != null && !mainJson.optString("status").trim().isEmpty() && !mainJson.optString("status").trim().equals("null") && !mainJson.optString("status").trim().matches("")) {
                    validateUserOutput.setValiduser_str(mainJson.optString("status"));
                    validuser_str = mainJson.optString("status");

                }
            } else {
                responseStr = "0";
                status = 0;
                message = "Error";
            }
        } catch (final JSONException e1) {
            Log.v("MUVISDK", "JSONException" + e1);

            responseStr = "0";
            status = 0;
            message = "Error";
        } catch (Exception e) {
            Log.v("MUVISDK", "Exception" + e);

            responseStr = "0";
            status = 0;
            message = "Error";
        }
        return null;


    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onGetValidateUserPreExecuteStarted();

        status = 0;
        if (!PACKAGE_NAME.equals(SDKInitializer.getUser_Package_Name_At_Api(context))) {
            this.cancel(true);
            message = "Packge Name Not Matched";
            listener.onGetValidateUserPostExecuteCompleted(validateUserOutput, status, message);
            return;
        }
        if (SDKInitializer.getHashKey(context).equals("")) {
            this.cancel(true);
            message = "Hash Key Is Not Available. Please Initialize The SDK";
            listener.onGetValidateUserPostExecuteCompleted(validateUserOutput, status, message);
        }

    }


    @Override
    protected void onPostExecute(Void result) {
        listener.onGetValidateUserPostExecuteCompleted(validateUserOutput, status, message);

    }

}
