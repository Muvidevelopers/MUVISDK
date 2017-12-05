/**
 * SDK initialization, platform and device information classes.
 */


package com.release.muvisdk.api.apiController;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.api.apiModel.APVModel;
import com.release.muvisdk.api.apiModel.CurrencyModel;
import com.release.muvisdk.api.apiModel.Episode_Details_input;
import com.release.muvisdk.api.apiModel.Episode_Details_output;
import com.release.muvisdk.api.apiModel.PPVModel;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * This Class gives all the details about the Episode to the users which they are looking for.
 * Details like Poster, Duration, Price for subscription etc.
 *
 * @author MUVI
 */
public class GetEpisodeDeatailsAsynTask extends AsyncTask<Episode_Details_input, Void, Void> {

    private Episode_Details_input episode_details_input;
    private String responseStr;
    private String movieUniqueId;
    private int status;
    private int is_ppv;
    private int item_count;
    private int isAPV;
    private String message;
    private String permalink;
    private String PACKAGE_NAME;
    private GetEpisodeDetailsListener listener;
    private Context context;

    /**
     * Interface used to allow the caller of a GetEpisodeDeatailsAsynTask to run some code when get
     * responses.
     */

    public interface GetEpisodeDetailsListener {

        /**
         * This method will be invoked before controller start execution.
         * This method to handle pre-execution work.
         */

        void onGetEpisodeDetailsPreExecuteStarted();

        /**
         * This method will be invoked after controller complete execution.
         * This method to handle post-execution work.
         *
         * @param episode_details_output A Model Class which contain responses. To get that responses we need to call the respective getter methods.
         * @param status                 Response Code From The Server
         * @param message                On Success Message
         * @param movieUniqueId          For Movie Unique ID
         */

        void onGetEpisodeDetailsPostExecuteCompleted(Episode_Details_output episode_details_output, int status, int i, String message, String movieUniqueId);


    }


    Episode_Details_output episode_details_output = new Episode_Details_output();
    Episode_Details_output.Episode episode;
    ArrayList<Episode_Details_output.Episode> episodeArray;

    /**
     * Constructor to initialise the private data members.
     *
     * @param episode_details_input A Model Class which is use for background task, we need to set all the attributes through setter methods of input model class,
     *                              For Example: to use this API we have to set following attributes:
     *                              setAuthToken(),setPermalink() etc.
     * @param listener              GetEpisodeDetails Listener
     * @param context               android.content.Context
     */

    public GetEpisodeDeatailsAsynTask(Episode_Details_input episode_details_input, GetEpisodeDetailsListener listener, Context context) {
        this.listener = listener;
        this.context = context;
        this.episode_details_input = episode_details_input;

        PACKAGE_NAME = context.getPackageName();
        Log.v("MUVISDK", "pkgnm :" + PACKAGE_NAME);
        Log.v("MUVISDK", "GetContentListAsynTask");

    }

    /**
     * Background thread to execute.
     *
     * @return null
     * @throws org.apache.http.conn.ConnectTimeoutException,IOException
     */

    @Override
    protected Void doInBackground(Episode_Details_input... params) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(APIUrlConstant.getGetEpisodeDetailsUrl());
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.addHeader(HeaderConstants.AUTH_TOKEN, this.episode_details_input.getAuthtoken());
            httppost.addHeader(HeaderConstants.PERMALINK, this.episode_details_input.getPermalink());
            httppost.addHeader(HeaderConstants.LIMIT, this.episode_details_input.getLimit());
            httppost.addHeader(HeaderConstants.OFFSET, this.episode_details_input.getOffset());
            httppost.addHeader(HeaderConstants.COUNTRY, this.episode_details_input.getCountry());
            httppost.addHeader(HeaderConstants.SERIES_NUMBER, this.episode_details_input.getSeries_number());
            httppost.addHeader(HeaderConstants.LANG_CODE, this.episode_details_input.getLang_code());

            // Execute HTTP Post Request
            try {
                HttpResponse response = httpclient.execute(httppost);
                responseStr = EntityUtils.toString(response.getEntity());


            } catch (org.apache.http.conn.ConnectTimeoutException e) {

                status = 0;
                message = "Error";

            } catch (IOException e) {
                status = 0;
                message = "Error";
            }

            JSONObject myJson = null;
            if (responseStr != null) {
                myJson = new JSONObject(responseStr);
                status = Integer.parseInt(myJson.optString("code"));
                message = myJson.optString("msg");
              /*  is_ppv= Integer.parseInt(myJson.optString("is_ppv"));
                permalink=myJson.optString("permalink");
                Log.v("BKS","permalik episode"+ permalink);
                item_count= Integer.parseInt(myJson.optString("item_count"));
                movieUniqueId=myJson.optString("muvi_uniq_id");*/
                item_count = Integer.parseInt(myJson.optString("item_count"));
                movieUniqueId = myJson.optString("muvi_uniq_id");
                permalink = myJson.optString("permalink");
                episode_details_output.setName(myJson.optString("name"));
            }
            Log.v("SUBHA", "episode api controller" + responseStr);

            if (status > 0) {

                if (status == 200) {
                    if ((myJson.has("is_ppv")) && myJson.getString("is_ppv").trim() != null && !myJson.getString("is_ppv").trim().isEmpty() && !myJson.getString("is_ppv").trim().equals("null") && !myJson.getString("is_ppv").trim().matches("")) {

                        is_ppv = Integer.parseInt(myJson.getString("is_ppv"));
                    } else {
                        is_ppv = 0;


                    }

                    episode_details_output.setIs_ppv(is_ppv);

                    if ((myJson.has("is_advance")) && myJson.getString("is_advance").trim() != null && !myJson.getString("is_advance").trim().isEmpty() && !myJson.getString("is_advance").trim().equals("null") && !myJson.getString("is_advance").trim().matches("")) {

                        isAPV = Integer.parseInt(myJson.getString("is_advance"));
                    } else {
                        isAPV = 0;
                    }

                    episode_details_output.setIsAPV(isAPV);

                    // Episode_Details_output payment = new Episode_Details_output();

                    if (is_ppv == 1) {
                        JSONObject ppvJson = null;
                        if ((myJson.has("ppv_pricing"))) {

                            PPVModel ppvModel = new PPVModel();
                            ppvJson = myJson.getJSONObject("ppv_pricing");

                            if ((ppvJson.has("price_for_unsubscribed")) && ppvJson.getString("price_for_unsubscribed").trim() != null && !ppvJson.getString("price_for_unsubscribed").trim().isEmpty() && !ppvJson.getString("price_for_unsubscribed").trim().equals("null") && !ppvJson.getString("price_for_unsubscribed").trim().matches("")) {
                                // priceForUnsubscribedStr = ppvJson.getString("price_for_unsubscribed");
                                ppvModel.setPPVPriceForUnsubscribedStr(ppvJson.getString("price_for_unsubscribed"));
                            } else {
                                // priceForUnsubscribedStr = "0.0";
                                ppvModel.setPPVPriceForUnsubscribedStr("0.0");


                            }
                            if ((ppvJson.has("price_for_subscribed")) && ppvJson.getString("price_for_subscribed").trim() != null && !ppvJson.getString("price_for_subscribed").trim().isEmpty() && !ppvJson.getString("price_for_subscribed").trim().equals("null") && !ppvJson.getString("price_for_subscribed").trim().matches("")) {
                                //priceFosubscribedStr = ppvJson.getString("price_for_subscribed");
                                ppvModel.setPPVPriceForUnsubscribedStr(ppvJson.getString("price_for_subscribed"));

                            } else {
                                // priceFosubscribedStr = "0.0";
                                ppvModel.setPPVPriceForUnsubscribedStr("0.0");

                            }
                            if ((ppvJson.has("id")) && ppvJson.getString("id").trim() != null && !ppvJson.getString("id").trim().isEmpty() && !ppvJson.getString("id").trim().equals("null") && !ppvJson.getString("id").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvPlanId(ppvJson.getString("id"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvPlanId("0");

                            }
                            //SubhaShree
                            if ((ppvJson.has("show_unsubscribed")) && ppvJson.getString("show_unsubscribed").trim() != null && !ppvJson.getString("show_unsubscribed").trim().isEmpty() && !ppvJson.getString("show_unsubscribed").trim().equals("null") && !ppvJson.getString("show_unsubscribed").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvShowUnsubscribedStr(ppvJson.getString("show_unsubscribed"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvShowUnsubscribedStr("0.0");


                            }
                            if ((ppvJson.has("show_subscribed")) && ppvJson.getString("show_subscribed").trim() != null && !ppvJson.getString("show_subscribed").trim().isEmpty() && !ppvJson.getString("show_subscribed").trim().equals("null") && !ppvJson.getString("show_subscribed").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvShowSubscribedStr(ppvJson.getString("show_subscribed"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvShowSubscribedStr("0.0");


                            }

                            if ((ppvJson.has("season_unsubscribed")) && ppvJson.getString("season_unsubscribed").trim() != null && !ppvJson.getString("season_unsubscribed").trim().isEmpty() && !ppvJson.getString("season_unsubscribed").trim().equals("null") && !ppvJson.getString("season_unsubscribed").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvSeasonUnsubscribedStr(ppvJson.getString("season_unsubscribed"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvSeasonUnsubscribedStr("0.0");


                            }
                            if ((ppvJson.has("season_subscribed")) && ppvJson.getString("season_subscribed").trim() != null && !ppvJson.getString("season_subscribed").trim().isEmpty() && !ppvJson.getString("season_subscribed").trim().equals("null") && !ppvJson.getString("season_subscribed").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvSeasonSubscribedStr(ppvJson.getString("season_subscribed"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvSeasonSubscribedStr("0.0");


                            }
                            if ((ppvJson.has("episode_unsubscribed")) && ppvJson.getString("episode_unsubscribed").trim() != null && !ppvJson.getString("episode_unsubscribed").trim().isEmpty() && !ppvJson.getString("episode_unsubscribed").trim().equals("null") && !ppvJson.getString("episode_unsubscribed").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvEpisodeUnsubscribedStr(ppvJson.getString("episode_unsubscribed"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvEpisodeUnsubscribedStr("0.0");


                            }
                            if ((ppvJson.has("episode_subscribed")) && ppvJson.getString("episode_subscribed").trim() != null && !ppvJson.getString("episode_subscribed").trim().isEmpty() && !ppvJson.getString("episode_subscribed").trim().equals("null") && !ppvJson.getString("episode_subscribed").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPpvEpisodeSubscribedStr(ppvJson.getString("episode_subscribed"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPpvEpisodeSubscribedStr("0.0");


                            }
                            if ((ppvJson.has("validity_period")) && ppvJson.getString("validity_period").trim() != null && !ppvJson.getString("validity_period").trim().isEmpty() && !ppvJson.getString("validity_period").trim().equals("null") && !ppvJson.getString("validity_period").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setValidity_period(ppvJson.getString("validity_period"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setValidity_period("0");


                            }
                            if ((ppvJson.has("is_show")) && ppvJson.getString("is_show").trim() != null && !ppvJson.getString("is_show").trim().isEmpty() && !ppvJson.getString("is_show").trim().equals("null") && !ppvJson.getString("is_show").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setIsShow(Integer.parseInt(ppvJson.getString("is_show")));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setIsShow(0);


                            }
                            if ((ppvJson.has("is_season")) && ppvJson.getString("is_season").trim() != null && !ppvJson.getString("is_season").trim().isEmpty() && !ppvJson.getString("is_season").trim().equals("null") && !ppvJson.getString("is_season").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setIsSeason(Integer.parseInt(ppvJson.getString("is_season")));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setIsSeason(0);


                            }
                            if ((ppvJson.has("is_episode")) && ppvJson.getString("is_episode").trim() != null && !ppvJson.getString("is_episode").trim().isEmpty() && !ppvJson.getString("is_episode").trim().equals("null") && !ppvJson.getString("is_episode").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setIsEpisode(Integer.parseInt(ppvJson.getString("is_episode")));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setIsEpisode(0);
                            }

                            if ((ppvJson.has("pricing_id")) && ppvJson.getString("pricing_id").trim() != null && !ppvJson.getString("pricing_id").trim().isEmpty() && !ppvJson.getString("pricing_id").trim().equals("null") && !ppvJson.getString("pricing_id").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setPricing_id(ppvJson.optString("pricing_id"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setPricing_id("0");


                            }
                            if ((ppvJson.has("validity_recurrence")) && ppvJson.getString("validity_recurrence").trim() != null && !ppvJson.getString("validity_recurrence").trim().isEmpty() && !ppvJson.getString("validity_recurrence").trim().equals("null") && !ppvJson.getString("validity_recurrence").trim().matches("")) {
                                //  planIdStr = ppvJson.getString("id");
                                ppvModel.setValidity_recurrence(ppvJson.optString("validity_recurrence"));

                            } else {
                                //  planIdStr = "0";
                                ppvModel.setValidity_recurrence("0");


                            }

                            episode_details_output.setPpvDetails(ppvModel);
                        }

                    }

                    if (isAPV == 1) {
                        JSONObject advJson = null;
                        if ((myJson.has("adv_pricing"))) {
                            APVModel aPVModel = new APVModel();

                            advJson = myJson.getJSONObject("adv_pricing");
                            if ((advJson.has("price_for_unsubscribed")) && advJson.optString("price_for_unsubscribed").trim() != null && !advJson.optString("price_for_unsubscribed").trim().isEmpty() && !advJson.optString("price_for_unsubscribed").trim().equals("null") && !advJson.optString("price_for_unsubscribed").trim().matches("")) {
                                aPVModel.setAPVPriceForUnsubscribedStr(advJson.optString("price_for_unsubscribed"));

                            } else {
                                aPVModel.setAPVPriceForUnsubscribedStr("0.0");

                            }
                            if ((advJson.has("price_for_subscribed")) && advJson.optString("price_for_subscribed").trim() != null && !advJson.optString("price_for_subscribed").trim().isEmpty() && !advJson.optString("price_for_subscribed").trim().equals("null") && !advJson.optString("price_for_subscribed").trim().matches("")) {
                                aPVModel.setAPVPriceForsubscribedStr(advJson.optString("price_for_subscribed"));
                            } else {
                                aPVModel.setAPVPriceForsubscribedStr("0.0");

                            }

                            if ((advJson.has("id")) && advJson.getString("id").trim() != null && !advJson.getString("id").trim().isEmpty() && !advJson.getString("id").trim().equals("null") && !advJson.getString("id").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvPlanId(advJson.getString("id"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvPlanId("0");

                            }
                            //SubhaShree
                            if ((advJson.has("show_unsubscribed")) && advJson.getString("show_unsubscribed").trim() != null && !advJson.getString("show_unsubscribed").trim().isEmpty() && !advJson.getString("show_unsubscribed").trim().equals("null") && !advJson.getString("show_unsubscribed").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvShowUnsubscribedStr(advJson.getString("show_unsubscribed"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvShowUnsubscribedStr("0.0");


                            }
                            if ((advJson.has("show_subscribed")) && advJson.getString("show_subscribed").trim() != null && !advJson.getString("show_subscribed").trim().isEmpty() && !advJson.getString("show_subscribed").trim().equals("null") && !advJson.getString("show_subscribed").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvShowSubscribedStr(advJson.getString("show_subscribed"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvShowSubscribedStr("0.0");


                            }

                            if ((advJson.has("season_unsubscribed")) && advJson.getString("season_unsubscribed").trim() != null && !advJson.getString("season_unsubscribed").trim().isEmpty() && !advJson.getString("season_unsubscribed").trim().equals("null") && !advJson.getString("season_unsubscribed").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvSeasonUnsubscribedStr(advJson.getString("season_unsubscribed"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvSeasonUnsubscribedStr("0.0");


                            }
                            if ((advJson.has("season_subscribed")) && advJson.getString("season_subscribed").trim() != null && !advJson.getString("season_subscribed").trim().isEmpty() && !advJson.getString("season_subscribed").trim().equals("null") && !advJson.getString("season_subscribed").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvSeasonSubscribedStr(advJson.getString("season_subscribed"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvSeasonSubscribedStr("0.0");


                            }
                            if ((advJson.has("episode_unsubscribed")) && advJson.getString("episode_unsubscribed").trim() != null && !advJson.getString("episode_unsubscribed").trim().isEmpty() && !advJson.getString("episode_unsubscribed").trim().equals("null") && !advJson.getString("episode_unsubscribed").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvEpisodeUnsubscribedStr(advJson.getString("episode_unsubscribed"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvEpisodeUnsubscribedStr("0.0");


                            }
                            if ((advJson.has("episode_subscribed")) && advJson.getString("episode_subscribed").trim() != null && !advJson.getString("episode_subscribed").trim().isEmpty() && !advJson.getString("episode_subscribed").trim().equals("null") && !advJson.getString("episode_subscribed").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setApvEpisodeSubscribedStr(advJson.getString("episode_subscribed"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setApvEpisodeSubscribedStr("0.0");


                            }
                            if ((advJson.has("validity_period")) && advJson.getString("validity_period").trim() != null && !advJson.getString("validity_period").trim().isEmpty() && !advJson.getString("validity_period").trim().equals("null") && !advJson.getString("validity_period").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setValidity_period(advJson.getString("validity_period"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setValidity_period("0");


                            }
                            if ((advJson.has("is_show")) && advJson.getString("is_show").trim() != null && !advJson.getString("is_show").trim().isEmpty() && !advJson.getString("is_show").trim().equals("null") && !advJson.getString("is_show").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setIsShow(Integer.parseInt(advJson.getString("is_show")));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setIsShow(0);


                            }
                            if ((advJson.has("is_season")) && advJson.getString("is_season").trim() != null && !advJson.getString("is_season").trim().isEmpty() && !advJson.getString("is_season").trim().equals("null") && !advJson.getString("is_season").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setIsSeason(Integer.parseInt(advJson.getString("is_season")));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setIsSeason(0);


                            }
                            if ((advJson.has("is_episode")) && advJson.getString("is_episode").trim() != null && !advJson.getString("is_episode").trim().isEmpty() && !advJson.getString("is_episode").trim().equals("null") && !advJson.getString("is_episode").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setIsEpisode(Integer.parseInt(advJson.getString("is_episode")));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setIsEpisode(0);
                            }

                            if ((advJson.has("pricing_id")) && advJson.getString("pricing_id").trim() != null && !advJson.getString("pricing_id").trim().isEmpty() && !advJson.getString("pricing_id").trim().equals("null") && !advJson.getString("pricing_id").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setPricing_id(advJson.optString("pricing_id"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setPricing_id("0");


                            }
                            if ((advJson.has("validity_recurrence")) && advJson.getString("validity_recurrence").trim() != null && !advJson.getString("validity_recurrence").trim().isEmpty() && !advJson.getString("validity_recurrence").trim().equals("null") && !advJson.getString("validity_recurrence").trim().matches("")) {
                                //  planIdStr = advJson.getString("id");
                                aPVModel.setValidity_recurrence(advJson.optString("validity_recurrence"));

                            } else {
                                //  planIdStr = "0";
                                aPVModel.setValidity_recurrence("0");


                            }
                            episode_details_output.setApvDetails(aPVModel);

                        }

                    }

                    if (is_ppv == 1 || isAPV == 1) {

                        JSONObject currencyJson = null;
                        if (myJson.has("currency") && myJson.optString("currency") != null && !myJson.optString("currency").equals("null")) {
                            currencyJson = myJson.getJSONObject("currency");
                            CurrencyModel currencyModel = new CurrencyModel();

                            if (currencyJson.has("id") && currencyJson.optString("id").trim() != null && !currencyJson.optString("id").trim().isEmpty() && !currencyJson.optString("id").trim().equals("null") && !currencyJson.optString("id").trim().matches("")) {
                                currencyModel.setCurrencyId(currencyJson.optString("id"));
                            } else {
                                currencyModel.setCurrencyId("");

                            }
                            if (currencyJson.has("country_code") && currencyJson.optString("country_code").trim() != null && !currencyJson.optString("country_code").trim().isEmpty() && !currencyJson.optString("country_code").trim().equals("null") && !currencyJson.optString("country_code").trim().matches("")) {
                                currencyModel.setCurrencyCode(currencyJson.optString("country_code"));
                            } else {
                                currencyModel.setCurrencyCode("");
                            }
                            if (currencyJson.has("symbol") && currencyJson.optString("symbol").trim() != null && !currencyJson.optString("symbol").trim().isEmpty() && !currencyJson.optString("symbol").trim().equals("null") && !currencyJson.optString("symbol").trim().matches("")) {
                                currencyModel.setCurrencySymbol(currencyJson.optString("symbol"));
                            } else {
                                currencyModel.setCurrencySymbol("$");
                            }
                            episode_details_output.setCurrencyDetails(currencyModel);


                        }

                    }


                    JSONArray jsonMainNode = myJson.getJSONArray("episode");
                    episodeArray = new ArrayList<>();
                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonChildNode;
                        try {
                            jsonChildNode = jsonMainNode.getJSONObject(i);
                            //Episode_Details_output content = new Episode_Details_output();
                            episode = new Episode_Details_output().new Episode();

                            if ((jsonChildNode.has("episode_title")) && jsonChildNode.optString("episode_title").trim() != null && !jsonChildNode.optString("episode_title").trim().isEmpty() && !jsonChildNode.optString("episode_title").trim().equals("null") && !jsonChildNode.optString("episode_title").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setEpisode_title(jsonChildNode.optString("episode_title"));
                            }
                            if ((jsonChildNode.has("thirdparty_url")) && jsonChildNode.optString("thirdparty_url").trim() != null && !jsonChildNode.optString("thirdparty_url").trim().isEmpty() && !jsonChildNode.optString("thirdparty_url").trim().equals("null") && !jsonChildNode.optString("thirdparty_url").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setThirdparty_url(jsonChildNode.optString("thirdparty_url"));
                            }
                            if ((jsonChildNode.has("poster_url")) && jsonChildNode.optString("poster_url").trim() != null && !jsonChildNode.optString("poster_url").trim().isEmpty() && !jsonChildNode.optString("poster_url").trim().equals("null") && !jsonChildNode.optString("poster_url").trim().matches("")) {
                                episode.setPoster_url(jsonChildNode.optString("poster_url"));

                            }
                            if ((jsonChildNode.has("episode_story")) && jsonChildNode.optString("episode_story").trim() != null
                                    && !jsonChildNode.optString("episode_story").trim().isEmpty() && !jsonChildNode.optString("episode_story").trim().equals("null") && !jsonChildNode.optString("episode_story").trim().matches("")) {
                                episode.setEpisode_story(jsonChildNode.optString("episode_story"));
                            }
                            if ((jsonChildNode.has("embeddedUrl")) && jsonChildNode.optString("embeddedUrl").trim() != null && !jsonChildNode.optString("embeddedUrl").trim().isEmpty() && !jsonChildNode.optString("embeddedUrl").trim().equals("null") && !jsonChildNode.optString("embeddedUrl").trim().matches("")) {
                                episode.setEmbeddedUrl(jsonChildNode.optString("embeddedUrl"));

                            }
                            //videoTypeIdStr = "1";

                            if ((jsonChildNode.has("video_duration")) && jsonChildNode.optString("video_duration").trim() != null && !jsonChildNode.optString("video_duration").trim().isEmpty() && !jsonChildNode.optString("video_duration").trim().equals("null") && !jsonChildNode.optString("video_duration").trim().matches("")) {
                                episode.setVideo_duration(jsonChildNode.optString("video_duration"));

                            }

                            if ((jsonChildNode.has("content_types_id")) && jsonChildNode.optString("content_types_id").trim() != null && !jsonChildNode.optString("content_types_id").trim().isEmpty() && !jsonChildNode.optString("episode_title").trim().equals("null") && !jsonChildNode.optString("content_types_id").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setContent_types_id(jsonChildNode.optInt("content_types_id"));
                            }
                            if ((jsonChildNode.has("movie_stream_uniq_id")) && jsonChildNode.optString("movie_stream_uniq_id").trim() != null && !jsonChildNode.optString("movie_stream_uniq_id").trim().isEmpty() && !jsonChildNode.optString("movie_stream_uniq_id").trim().equals("null") && !jsonChildNode.optString("movie_stream_uniq_id").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setMovie_stream_uniq_id(jsonChildNode.optString("movie_stream_uniq_id"));
                            }

                            if ((jsonChildNode.has("video_url")) && jsonChildNode.optString("video_url").trim() != null && !jsonChildNode.optString("video_url").trim().isEmpty() && !jsonChildNode.optString("video_url").trim().equals("null") && !jsonChildNode.optString("video_url").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setVideo_url(jsonChildNode.optString("video_url"));
                            }

                            if ((jsonChildNode.has("episode_story")) && jsonChildNode.optString("episode_story").trim() != null && !jsonChildNode.optString("episode_story").trim().isEmpty() && !jsonChildNode.optString("episode_story").trim().equals("null") && !jsonChildNode.optString("episode_story").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setEpisode_story(jsonChildNode.optString("episode_story"));
                            } else {
                                episode.setEpisode_story("");

                            }
                            if ((jsonChildNode.has("episode_number")) && jsonChildNode.optString("episode_number").trim() != null && !jsonChildNode.optString("episode_number").trim().isEmpty() && !jsonChildNode.optString("episode_number").trim().equals("null") && !jsonChildNode.optString("episode_number").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                episode.setEpisode_number(jsonChildNode.optString("episode_number"));
                            }

                            if ((jsonChildNode.has("series_number")) && jsonChildNode.getString("series_number").trim() != null && !jsonChildNode.getString("series_number").trim().isEmpty() && !jsonChildNode.getString("series_number").trim().equals("null") && !jsonChildNode.getString("series_number").trim().matches("")) {
                                episode.setSeries_number(jsonChildNode.getString("series_number"));

                            } else {
                                episode.setSeries_number("");

                            }
                            if ((jsonChildNode.has("episode_date")) && jsonChildNode.getString("episode_date").trim() != null && !jsonChildNode.getString("episode_date").trim().isEmpty() && !jsonChildNode.getString("episode_date").trim().equals("null") && !jsonChildNode.getString("episode_date").trim().matches("")) {
                                episode.setEpisode_date(jsonChildNode.getString("episode_date"));

                            } else {
                                episode.setEpisode_date("");

                            }
                            if ((jsonChildNode.has("poster_url")) && jsonChildNode.getString("poster_url").trim() != null && !jsonChildNode.getString("poster_url").trim().isEmpty() && !jsonChildNode.getString("poster_url").trim().equals("null") && !jsonChildNode.getString("poster_url").trim().matches("")) {
                                episode.setPoster_url(jsonChildNode.getString("poster_url"));

                            } else {
                                episode.setPoster_url("");

                            }

                            episodeArray.add(episode);
                        } catch (Exception e) {
                            status = 0;
                            // totalItems = 0;
                            message = "";
                        }
                    }
                    episode_details_output.setEpisodeArray(episodeArray);
                } else {
                    responseStr = "0";
                    status = 0;
                    // totalItems = 0;
                    message = "";
                }
            }
        } catch (Exception e) {
            status = 0;
            // totalItems = 0;
            message = "";
        }


        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onGetEpisodeDetailsPreExecuteStarted();
        status = 0;
        if (!PACKAGE_NAME.equals(SDKInitializer.getUser_Package_Name_At_Api(context))) {
            this.cancel(true);
            message = "Packge Name Not Matched";
            listener.onGetEpisodeDetailsPostExecuteCompleted(episode_details_output, status, item_count, message, movieUniqueId);
            return;
        }
        if (SDKInitializer.getHashKey(context).equals("")) {
            this.cancel(true);
            message = "Hash Key Is Not Available. Please Initialize The SDK";
            listener.onGetEpisodeDetailsPostExecuteCompleted(episode_details_output, status, item_count, message, movieUniqueId);
        }

    }

    @Override
    protected void onPostExecute(Void aVoid) {
        listener.onGetEpisodeDetailsPostExecuteCompleted(episode_details_output, status, item_count, message, movieUniqueId);
    }


    /* Episode_Details_input episode_details_input;
    String responseStr,movieUniqueId;
    int status,is_ppv,item_count;
    String message,permalink,PACKAGE_NAME;


    public interface GetEpisodeDetails {
        void onGetEpisodeDetailsPreExecuteStarted();
        void onGetEpisodeDetailsPostExecuteCompleted(ArrayList<Episode_Details_output> episode_details_output,int status, int i,  String message,String movieUniqueId);
    }
   *//* public class GetContentListAsync extends AsyncTask<Void, Void, Void> {*//*

    private GetEpisodeDetails listener;
    private Context context;
    ArrayList<Episode_Details_output> episode_details_output=new ArrayList<Episode_Details_output>();

    public GetEpisodeDeatailsAsynTask(Episode_Details_input episode_details_input,GetEpisodeDetails listener, Context context) {
        this.listener=listener;
        this.context=context;
        this.episode_details_input = episode_details_input;
        PACKAGE_NAME=context.getPackageName();
        Log.v("MUVISDK", "pkgnm :"+PACKAGE_NAME);
        Log.v("MUVISDK", "GetContentListAsynTask");

    }

    @Override
    protected Void doInBackground(Episode_Details_input... params) {


        try {
            HttpClient httpclient=new DefaultHttpClient();
            HttpPost httppost = new HttpPost(APIUrlConstant.getGetEpisodeDetailsUrl());
            httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
            httppost.addHeader(HeaderConstants.AUTH_TOKEN, this.episode_details_input.getAuthtoken());
            httppost.addHeader(HeaderConstants.PERMALINK, this.episode_details_input.getPermalink());
            httppost.addHeader(HeaderConstants.LIMIT,this.episode_details_input.getLimit());
            httppost.addHeader(HeaderConstants.OFFSET,this.episode_details_input.getOffset());
            httppost.addHeader(HeaderConstants.COUNTRY,this.episode_details_input.getCountry());
            httppost.addHeader(HeaderConstants.SERIES_NUMBER,this.episode_details_input.getSeries_number());
            httppost.addHeader(HeaderConstants.LANG_CODE,this.episode_details_input.getLang_code());

            // Execute HTTP Post Request
            try {
                HttpResponse response = httpclient.execute(httppost);
                responseStr = EntityUtils.toString(response.getEntity());


            } catch (org.apache.http.conn.ConnectTimeoutException e){

                status = 0;
                message = "Error";

            }catch (IOException e) {
                status = 0;
                message = "Error";
            }

            JSONObject myJson =null;
            if(responseStr!=null){
                myJson = new JSONObject(responseStr);
                status = Integer.parseInt(myJson.optString("code"));
                message = myJson.optString("msg");
                is_ppv= Integer.parseInt(myJson.optString("is_ppv"));
                permalink=myJson.optString("permalink");
                Log.v("BKS","permalik episode"+ permalink);
                item_count= Integer.parseInt(myJson.optString("item_count"));
                movieUniqueId=myJson.optString("muvi_uniq_id");
            }
            Log.v("SUBHA","episode api controller"+ responseStr);

            if (status > 0) {

                if (status == 200) {
                  *//*  Episode_Details_output payment = new Episode_Details_output();

                    if (payment.getPpvDetails(). == 1) {
                        JSONObject ppvJson = null;
                        if ((myJson.has("ppv_pricing"))) {

                            PPVModel ppvModel = new PPVModel();
                            ppvJson = myJson.getJSONObject("ppv_pricing");
                            if ((ppvJson.has("price_for_unsubscribed")) && ppvJson.optString("price_for_unsubscribed").trim() != null && !ppvJson.optString("price_for_unsubscribed").trim().isEmpty() && !ppvJson.optString("price_for_unsubscribed").trim().equals("null") && !ppvJson.optString("price_for_unsubscribed").trim().matches("")) {
                                ppvModel.setPPVPriceForUnsubscribedStr(ppvJson.optString("price_for_unsubscribed"));
                            } else {
                                ppvModel.setPPVPriceForUnsubscribedStr("0.0");

                            }
                            if ((ppvJson.has("price_for_subscribed")) && ppvJson.optString("price_for_subscribed").trim() != null && !ppvJson.optString("price_for_subscribed").trim().isEmpty() && !ppvJson.optString("price_for_subscribed").trim().equals("null") && !ppvJson.optString("price_for_subscribed").trim().matches("")) {
                                ppvModel.setPPVPriceForsubscribedStr(ppvJson.optString("price_for_subscribed"));
                            } else {
                                ppvModel.setPPVPriceForsubscribedStr("0.0");

                            }
                            contentDetailsOutput.setPpvDetails(ppvModel);
                        }

                    }
                    if (contentDetailsOutput.getIsApv() == 1) {
                        JSONObject advJson = null;
                        if ((myJson.has("adv_pricing"))) {
                            APVModel aPVModel = new APVModel();

                            advJson = myJson.getJSONObject("adv_pricing");
                            if ((advJson.has("price_for_unsubscribed")) && advJson.optString("price_for_unsubscribed").trim() != null && !advJson.optString("price_for_unsubscribed").trim().isEmpty() && !advJson.optString("price_for_unsubscribed").trim().equals("null") && !advJson.optString("price_for_unsubscribed").trim().matches("")) {
                                aPVModel.setAPVPriceForUnsubscribedStr(advJson.optString("price_for_unsubscribed"));

                            } else {
                                aPVModel.setAPVPriceForUnsubscribedStr("0.0");

                            }
                            if ((advJson.has("price_for_subscribed")) && advJson.optString("price_for_subscribed").trim() != null && !advJson.optString("price_for_subscribed").trim().isEmpty() && !advJson.optString("price_for_subscribed").trim().equals("null") && !advJson.optString("price_for_subscribed").trim().matches("")) {
                                aPVModel.setAPVPriceForsubscribedStr(advJson.optString("price_for_subscribed"));
                            } else {
                                aPVModel.setAPVPriceForsubscribedStr("0.0");

                            }
                            contentDetailsOutput.setApvDetails(aPVModel);

                        }

                    }

                    if (contentDetailsOutput.getIsPpv() == 1 || contentDetailsOutput.getIsApv() == 1) {

                        JSONObject currencyJson = null;
                        if (myJson.has("currency") && myJson.optString("currency") != null && !myJson.optString("currency").equals("null")) {
                            currencyJson = myJson.getJSONObject("currency");
                            CurrencyModel currencyModel = new CurrencyModel();

                            if (currencyJson.has("id") && currencyJson.optString("id").trim() != null && !currencyJson.optString("id").trim().isEmpty() && !currencyJson.optString("id").trim().equals("null") && !currencyJson.optString("id").trim().matches("")) {
                                currencyModel.setCurrencyId(currencyJson.optString("id"));
                            } else {
                                currencyModel.setCurrencyId("");

                            }
                            if (currencyJson.has("country_code") && currencyJson.optString("country_code").trim() != null && !currencyJson.optString("country_code").trim().isEmpty() && !currencyJson.optString("country_code").trim().equals("null") && !currencyJson.optString("country_code").trim().matches("")) {
                                currencyModel.setCurrencyCode(currencyJson.optString("country_code"));
                            } else {
                                currencyModel.setCurrencyCode("");
                            }
                            if (currencyJson.has("symbol") && currencyJson.optString("symbol").trim() != null && !currencyJson.optString("symbol").trim().isEmpty() && !currencyJson.optString("symbol").trim().equals("null") && !currencyJson.optString("symbol").trim().matches("")) {
                                currencyModel.setCurrencySymbol(currencyJson.optString("symbol"));
                            } else {
                                currencyModel.setCurrencySymbol("");
                            }
                            contentDetailsOutput.setCurrencyDetails(currencyModel);


                        }

                    }
*//*

                    JSONArray jsonMainNode = myJson.getJSONArray("episode");
                    int lengthJsonArr = jsonMainNode.length();
                    for (int i = 0; i < lengthJsonArr; i++) {
                        JSONObject jsonChildNode;
                        try {
                            jsonChildNode = jsonMainNode.getJSONObject(i);
                            Episode_Details_output content = new Episode_Details_output();

                            if ((jsonChildNode.has("episode_title")) && jsonChildNode.optString("episode_title").trim() != null && !jsonChildNode.optString("episode_title").trim().isEmpty() && !jsonChildNode.optString("episode_title").trim().equals("null") && !jsonChildNode.optString("episode_title").trim().matches("")) {
                               // String episode_title=jsonChildNode.optString("episode_title");
                                content.setEpisode_title(jsonChildNode.optString("episode_title"));
                            }
                            if ((jsonChildNode.has("thirdparty_url")) && jsonChildNode.optString("thirdparty_url").trim() != null && !jsonChildNode.optString("thirdparty_url").trim().isEmpty() && !jsonChildNode.optString("thirdparty_url").trim().equals("null") && !jsonChildNode.optString("thirdparty_url").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                content.setThirdparty_url(jsonChildNode.optString("thirdparty_url"));
                            }
                            if ((jsonChildNode.has("poster_url")) && jsonChildNode.optString("poster_url").trim() != null && !jsonChildNode.optString("poster_url").trim().isEmpty() && !jsonChildNode.optString("poster_url").trim().equals("null") && !jsonChildNode.optString("poster_url").trim().matches("")) {
                                content.setPoster_url(jsonChildNode.optString("poster_url"));

                            }
                            if ((jsonChildNode.has("episode_story")) && jsonChildNode.optString("episode_story").trim() != null && !jsonChildNode.optString("episode_story").trim().isEmpty() && !jsonChildNode.optString("episode_story").trim().equals("null") && !jsonChildNode.optString("episode_story").trim().matches("")) {
                                content.setEpisode_story(jsonChildNode.optString("episode_story"));
                            }
                           if ((jsonChildNode.has("embeddedUrl")) && jsonChildNode.optString("embeddedUrl").trim() != null && !jsonChildNode.optString("embeddedUrl").trim().isEmpty() && !jsonChildNode.optString("embeddedUrl").trim().equals("null") && !jsonChildNode.optString("embeddedUrl").trim().matches("")) {
                                content.setEmbeddedUrl(jsonChildNode.optString("embeddedUrl"));

                            }
                            //videoTypeIdStr = "1";

                            if ((jsonChildNode.has("video_duration")) && jsonChildNode.optString("video_duration").trim() != null && !jsonChildNode.optString("video_duration").trim().isEmpty() && !jsonChildNode.optString("video_duration").trim().equals("null") && !jsonChildNode.optString("video_duration").trim().matches("")) {
                                content.setVideo_duration(jsonChildNode.optString("video_duration"));

                            }

                            if ((jsonChildNode.has("content_types_id")) && jsonChildNode.optString("content_types_id").trim() != null && !jsonChildNode.optString("content_types_id").trim().isEmpty() && !jsonChildNode.optString("episode_title").trim().equals("null") && !jsonChildNode.optString("content_types_id").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                content.setContent_types_id(jsonChildNode.optInt("content_types_id"));
                            }
                            if ((jsonChildNode.has("movie_stream_uniq_id")) && jsonChildNode.optString("movie_stream_uniq_id").trim() != null && !jsonChildNode.optString("movie_stream_uniq_id").trim().isEmpty() && !jsonChildNode.optString("movie_stream_uniq_id").trim().equals("null") && !jsonChildNode.optString("movie_stream_uniq_id").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                content.setMovie_stream_uniq_id(jsonChildNode.optString("movie_stream_uniq_id"));
                            }

                            if ((jsonChildNode.has("video_url")) && jsonChildNode.optString("video_url").trim() != null && !jsonChildNode.optString("video_url").trim().isEmpty() && !jsonChildNode.optString("video_url").trim().equals("null") && !jsonChildNode.optString("video_url").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                content.setVideo_url(jsonChildNode.optString("video_url"));
                            }

                            if ((jsonChildNode.has("episode_story")) && jsonChildNode.optString("episode_story").trim() != null && !jsonChildNode.optString("episode_story").trim().isEmpty() && !jsonChildNode.optString("episode_story").trim().equals("null") && !jsonChildNode.optString("episode_story").trim().matches("")) {
                                // String episode_title=jsonChildNode.optString("episode_title");
                                content.setEpisode_story(jsonChildNode.optString("episode_story"));
                            }
                                if ((jsonChildNode.has("episode_number")) && jsonChildNode.optString("episode_number").trim() != null && !jsonChildNode.optString("episode_number").trim().isEmpty() && !jsonChildNode.optString("episode_number").trim().equals("null") && !jsonChildNode.optString("episode_number").trim().matches("")) {
                                    // String episode_title=jsonChildNode.optString("episode_title");
                                    content.setEpisode_number(jsonChildNode.optString("episode_number"));
                                }

                           episode_details_output.add(content);
                        } catch (Exception e) {
                            status = 0;
                          // totalItems = 0;
                            message = "";
                        }
                    }
                } else {
                    responseStr = "0";
                    status = 0;
                   // totalItems = 0;
                    message = "";
                }
            }
        } catch (Exception e) {
            status = 0;
           // totalItems = 0;
            message = "";
        }
        return null;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onGetEpisodeDetailsPreExecuteStarted();
        status = 0;
        if(!PACKAGE_NAME.equals(HeaderConstants.user_Package_Name_At_Api))
        {
            this.cancel(true);
            message = "Packge Name Not Matched";
            listener.onGetEpisodeDetailsPostExecuteCompleted(episode_details_output, status, item_count, message,  movieUniqueId);
            return;
        }
        if(HeaderConstants.hashKey.equals(""))
        {
            this.cancel(true);
            message = "Hash Key Is Not Available. Please Initialize The SDK";
            listener.onGetEpisodeDetailsPostExecuteCompleted(episode_details_output, status, item_count, message, movieUniqueId);
        }

    }



    @Override
    protected void onPostExecute(Void result) {
       listener.onGetEpisodeDetailsPostExecuteCompleted(episode_details_output, status, item_count, message, movieUniqueId);

    }
*/
    //}
}
