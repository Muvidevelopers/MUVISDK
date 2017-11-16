package com.release.muvisdk.player.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.player.model.DataModel;

import java.util.StringTokenizer;
import java.util.Timer;

/**
 * Created by User on 08-11-2017.
 */
public class Util {



    public static String LANGUAGE_SHARED_PRE = "SdkLanguage";
    public static String DOWNLOAD_INFO_PREF = "download_info_pref";
    public static final String morlineBB = "getMarlinBBOffline";
    public static final String GetOfflineViewRemainingTime = "GetOfflineViewRemainingTime";

    public static String VideoResolution = "Auto";
    public static String DefaultSubtitle = "Off";
    public static boolean player_description = true;
    public static boolean landscape = true;
    public static boolean hide_pause = false;
    public static boolean call_finish_at_onUserLeaveHint = true;
    public static  boolean Call_API_For_Close_Streming = false;
    public static boolean app_is_in_player_context = false;
    public static Timer timer = new Timer();

    public static DataModel dataModel = null;


   /* public static String rootUrl(){
        //String rootUrl = "https://sonydadc.muvi.com/rest/";
//        String rootUrl = "http://muvistudio.edocent.com/rest/";
        String rootUrl = "https://www.muvi.com/rest/";
        //String rootUrl = "https://www.idogic.com/rest/";
        return rootUrl;

    }*/



    public static String IS_ONE_STEP_REGISTRATION = "IS_ONE_STEP_REGISTRATION";
    public static String DEFAULT_IS_ONE_STEP_REGISTRATION = "0";

    public static String IS_RESTRICT_DEVICE = "IS_RESTRICT_DEVICE";
    public static String DEFAULT_IS_RESTRICT_DEVICE = "0";

    public static String HAS_FAVORITE = "HAS_FAVORITE";
    public static String DEFAULT_HAS_FAVORITE = "0";

    public static String IS_OFFLINE = "IS_OFFLINE";
    public static String DEFAULT_IS_OFFLINE = "0";

    public static String IS_CHROMECAST = "IS_CHROMECAST";
    public static String DEFAULT_IS_CHROMECAST = "0";

    public static String DEFAULT_ADD_A_REVIEW = "Add a Review";
    public static String DEFAULT_REVIEWS = "Reviews";

    public static String ADD_A_REVIEW = "ADD_A_REVIEW";
    public static String REVIEWS = "REVIEWS";

    public static String NO_VIDEO_AVAILABLE = "NO_VIDEO_AVAILABLE";
    public static String DEFAULT_NO_VIDEO_AVAILABLE = "There's some error. Please try again !";



    public static String STOP_SAVING_THIS_VIDEO = "STOP_SAVING_THIS_VIDEO";
    public static String DEFAULT_STOP_SAVING_THIS_VIDEO = "Stop saving this video";
    public static String YOUR_VIDEO_WONT_BE_SAVED = "YOUR_VIDEO_WONT_BE_SAVED";
    public static String DEFAULT_YOUR_VIDEO_WONT_BE_SAVED = "Your video can not be saved";
    public static String BTN_KEEP = "BTN_KEEP";
    public static String DEFAULT_BTN_KEEP = "Keep";
    public static String BTN_DISCARD = "BTN_DISCARD";
    public static String DEFAULT_BTN_DISCARD = "Discard";
    public static String WANT_TO_DOWNLOAD = "WANT_TO_DOWNLOAD";
    public static String DEFAULT_WANT_TO_DOWNLOAD = "Want to Download";
    public static String DOWNLOAD_CANCELLED = "DOWNLOAD_CANCELLED";
    public static String DEFAULT_DOWNLOAD_CANCELLED = "Download Cancelled";
    public static String MY_DOWNLOAD = "MY_DOWNLOAD";
    public static String DEFAULT_MY_DOWNLOAD = "My Download";
    public static String DEFAULT_WANT_TO_DELETE = "Want to Delete ?";
    public static String WANT_TO_DELETE = "WANT_TO_DELETE";
    public static String DEFAULT_DELETE_BTN = "Delete";
    public static String DELETE_BTN = "DELETE_BTN";
    public static String SELECTED_LANGUAGE_CODE = "SELECTED_LANGUAGE_CODE";
    public static String VIEW_MORE = "VIEW_MORE";
    public static String RESUME_MESSAGE = "RESUME_MESSAGE";
    public static final String VIEW_LESS = "VIEW_LESS";
    public static final String DEFAULT_VIEW_LESS = "View Less";
    public static String BUTTON_OK = "BUTTON_OK";
    public static String YES = "YES";
    public static String NO = "NO";
    public static String HOME = "HOME";
    public static String SLOW_INTERNET_CONNECTION = "SLOW_INTERNET_CONNECTION";
    public static String NO_INTERNET_CONNECTION = "NO_INTERNET_CONNECTION";
    public static String DOWNLOAD_BUTTON_TITLE = "DOWNLOAD_BUTTON_TITLE";
    public static String CAST_CREW_BUTTON_TITLE = "CAST_CREW_BUTTON_TITLE";
    public static String SORRY = "SORRY";
    public static String NO_DATA = "NO_DATA";
    public static String NO_CONTENT = "NO_CONTENT";
    public static String CANCEL_BUTTON = "CANCEL_BUTTON";
    public static String CONTINUE_BUTTON = "CONTINUE_BUTTON";
    public static String SAVE = "SAVE";
    public static String DEFAULT_SAVE = "Save";
    public static String SAVE_OFFLINE_VIDEO = "SAVE_OFFLINE_VIDEO";
    public static String DEFAULT_SAVE_OFFLINE_VIDEO = "Save Offline Video";
    public static String SIGN_OUT_ERROR = "SIGN_OUT_ERROR";
    public static String MY_LIBRARY = "MY_LIBRARY";
    public static String DOWNLOAD_INTERRUPTED = "DOWNLOAD_INTERRUPTED";
    public static String DEFAULT_DOWNLOAD_INTERRUPTED = "Download Interrupted.";
    public static String DOWNLOAD_COMPLETED = "DOWNLOAD_COMPLETED";
    public static String DEFAULT_DOWNLOAD_COMPLETED = "Download Completed.";
    public static String DEFAULT_CANCEL_BUTTON = "Cancel";
    public static String DEFAULT_DOWNLOAD_BUTTON_TITLE = "DOWNLOAD";
    public static String DEFAULT_MY_LIBRARY = "My Library";
    public static String DEFAULT_SELECTED_LANGUAGE_CODE = "en";
    public static String DEFAULT_HOME = "Home";
    public static String DEFAULT_VIEW_MORE = "View All";
    public static String DEFAULT_BUTTON_OK = "Ok";
    public static String DEFAULT_YES = "Yes";
    public static String DEFAULT_NO = "No";
    public static String DEFAULT_SLOW_INTERNET_CONNECTION = "Slow Internet Connection";
    public static String DEFAULT_NO_INTERNET_CONNECTION = "No Internet Connection";
    public static String DEFAULT_CAST_CREW_BUTTON_TITLE = "Cast and Crew";
    public static String DEFAULT_SORRY = "Sorry !";
    public static String DEFAULT_NO_DATA = "No Data";
    public static String DEFAULT_NO_CONTENT = "There's no matching content found.";
    public static String DEFAULT_SIGN_OUT_ERROR = "Sorry, we can not be able to log out. Try again!.";
    public static String DEFAULT_RESUME_MESSAGE = "Continue watching where you left?";
    public static String DEAFULT_CANCEL_BUTTON = "Cancel";
    public static String DEAFULT_CONTINUE_BUTTON = "Continue";

    public static SharedPreferences getLanguageSharedPrefernce(Context context) {
        SharedPreferences languageSharedPref = context.getSharedPreferences(Util.LANGUAGE_SHARED_PRE, 0);
        return languageSharedPref;

    }

    public static String getTextofLanguage(Context context, String tempKey, String defaultValue) {
        SharedPreferences sp = Util.getLanguageSharedPrefernce(context);
        String str = sp.getString(tempKey,defaultValue);
        return str;
    }

    public static String getAppVersionName(Context context) {
        String versionString = null;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            versionString = info.versionName;
        } catch (Exception e) {
        }
        return versionString;
    }

    public static int HoursToSecond(String time) {

        StringTokenizer tokens = new StringTokenizer(time, ":");
        int hour = Integer.parseInt(tokens.nextToken())*3600;
        int minute = Integer.parseInt(tokens.nextToken())*60;
        int second = Integer.parseInt(tokens.nextToken()) + hour + minute;
        return second;
    }

    public static boolean checkNetwork(Context context){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected==false){
            return false;
        }
        return true;
    }

}
