
package com.release.muvisdk.player.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdErrorEvent;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsManagerLoadedEvent;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.player.ContentProgressProvider;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;
import com.intertrust.wasabi.ErrorCodeException;
import com.intertrust.wasabi.Runtime;
import com.intertrust.wasabi.media.PlaylistProxy;
import com.intertrust.wasabi.media.PlaylistProxyListener;
import com.release.muvi.muvisdk.R;
import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.player.adapter.DownloadOptionAdapter;
import com.release.muvisdk.player.controller.WebApiController;
import com.release.muvisdk.player.model.DownloadContentModel;
import com.release.muvisdk.player.model.Player;
import com.release.muvisdk.player.model.SubtitleModel;
import com.release.muvisdk.player.service.DataConsumptionService;
import com.release.muvisdk.player.service.PopUpService;
import com.release.muvisdk.player.subtitle_support.Caption;
import com.release.muvisdk.player.subtitle_support.FormatSRT;
import com.release.muvisdk.player.subtitle_support.FormatSRT_WithoutCaption;
import com.release.muvisdk.player.subtitle_support.TimedTextObject;
import com.release.muvisdk.player.utils.DBHelper;
import com.release.muvisdk.player.utils.ProgressBarHandler;
import com.release.muvisdk.player.utils.ResizableCustomView;
import com.release.muvisdk.player.utils.SensorOrientationChangeNotifier;
import com.release.muvisdk.player.utils.Util;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import static android.content.res.Configuration.SCREENLAYOUT_SIZE_LARGE;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_MASK;
import static android.content.res.Configuration.SCREENLAYOUT_SIZE_XLARGE;
import static com.release.muvisdk.player.utils.Constants.ADD_REQUEST_CODE;
import static com.release.muvisdk.player.utils.Constants.APP_NAME_MSG;
import static com.release.muvisdk.player.utils.Constants.ASYNC_BUFFERLOG_DETAILS;
import static com.release.muvisdk.player.utils.Constants.ASYNC_FF_VODEOLOG_DETAILS;
import static com.release.muvisdk.player.utils.Constants.ASYNC_RESUME_VODEOLOG_DETAILS;
import static com.release.muvisdk.player.utils.Constants.ASYNC_RESUME_VODEOLOG_DETAILS_HOME_CLICKED;
import static com.release.muvisdk.player.utils.Constants.ASYNC_VODEOLOG_DETAILS;
import static com.release.muvisdk.player.utils.Constants.CHECK_USER_ID_MSG;
import static com.release.muvisdk.player.utils.Constants.LIVE_STREAM;
import static com.release.muvisdk.player.utils.Constants.OVERLAY_PERMISSION_REQUEST_CODE;
import static com.release.muvisdk.player.utils.Constants.RESUME_VIDEO_REQUEST_CODE;
import static com.release.muvisdk.player.utils.Constants.SECOND_ADD_REQUEST_CODE;
import static com.release.muvisdk.player.utils.Constants.SUBTITLE_REQUEST_CODE;
import static com.release.muvisdk.player.utils.Constants.TAG;
import static com.release.muvisdk.player.utils.Constants.VALID_AUTHTOKEN;
import static com.release.muvisdk.player.utils.Constants.VALID_EMAIL;


enum ContentTypes2 {
    DASH("application/dash+xml"), HLS("application/vnd.apple.mpegurl"), PDCF(
            "video/mp4"), M4F("video/mp4"), DCF("application/vnd.oma.drm.dcf"), BBTS(
            "video/mp2t");
    String mediaSourceParamsContentType = null;

    private ContentTypes2(String mediaSourceParamsContentType) {
        this.mediaSourceParamsContentType = mediaSourceParamsContentType;
    }
    public String getMediaSourceParamsContentType() {
        return mediaSourceParamsContentType;
    }
}

public class PlayerActivity extends AppCompatActivity implements SensorOrientationChangeNotifier.Listener, PlaylistProxyListener, AdEvent.AdEventListener, AdErrorEvent.AdErrorListener ,WebApiController.TaskCompleteListener,CastCrew.AppInterface {


    private static final int REQUEST_STORAGE = 1;
    private static final int MAX_LINES = 2;
    int played_length = 0;
    int playerStartPosition = 0;
    int player_start_time = 0;
    int lengthfile = 0;
    int playerPosition = 0;
    int player_layout_height, player_layout_width;
    int screenWidth, screenHeight;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    int selected_download_format = 0;
    int seekBarProgress = 0;
    int content_types_id = 0;
    int current_played_length = 0;
    int playerPreviousPosition = 0;


    long PreviousUsedData = 0;
    long CurrentUsedData = 0;
    long PreviousUsedData_By_DownloadContent = 0;
    long DataUsedByChrmoeCast = 0;
    long Current_Sesion_DataUsedByChrmoeCast = 0;
    long cast_disconnected_position = 0;
    long previous_matching_time = 0;
    long current_matching_time = 0;
    long enqueue;


    float file_size;


    String adDetails[];
    String log_temp_id = "0";
    String filename, path;
    String mlvfile = "";
    String token = "";
    String fileExtenstion;
    String emailIdStr = "";
    String userIdStr = "";
    String movieId = "";
    String episodeId = "0";
    String videoLogId = "0";
    String restrict_stream_id = "0";
    String watchStatus = "start";
    String licensetoken;
    String resolution = "BEST";
    String ipAddressStr = "";
    String videoBufferLogId = "0";
    String videoBufferLogUniqueId = "0";
    String Location = "0";
    String active_track_index = "0";
    String Current_Time, TotalTime;
    String Dwonload_Complete_Msg = "";
    String authToken = "";
    String rootUrl = "";
    String packgeName = "";

    boolean censor_layout = true;
    boolean video_completed = false;
    boolean center_pause_paly_timer_is_running = false;
    boolean compressed = true;
    boolean stopLogTimer = false;
    boolean video_prepared = false;
    boolean callWithoutCaption = true;
    boolean video_completed_at_chromecast = false;
    boolean isDrm = false;
    boolean change_resolution = false;
    boolean is_paused = false;
    boolean mIsAdDisplayed;
    boolean downloading;
    boolean isFastForward = false;
    boolean castButtonClicked = false;


    Timer MovableTimer;
    Timer timer;
    TimerTask timerTask;
    Timer center_pause_paly_timer;
    Timer CheckAvailabilityOfChromecast;


    TextView percentg;
    TextView ipAddressTextView;
    TextView emailAddressTextView;
    TextView dateTextView;
    TextView current_time,total_time;
    TextView videoTitle, GenreTextView, videoDurationTextView, videoCensorRatingTextView;
    TextView story,videoCensorRatingTextView1, videoReleaseDateTextView,videoCastCrewTitleTextView;
    TextView volume_bright_value;
    TextView subtitleText;


    ImageView download;
    ImageView subtitle_change_btn;
    ImageView compress_expand;


    ImageButton latest_center_play_pause;
    ImageButton volume_brightness_control;
    ImageButton back, center_play_pause;


    RelativeLayout download_layout;
    RelativeLayout player_layout;


    LinearLayout primary_ll, last_ll;
    LinearLayout linearLayout1;
    LinearLayout volume_brightness_control_layout;


    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();
    ArrayList<String> ResolutionFormat = new ArrayList<>();
    ArrayList<String> ResolutionUrl = new ArrayList<>();
    ArrayList<String> List_Of_FileSize = new ArrayList<>();
    ArrayList<String> List_Of_Resolution_Format = new ArrayList<>();
    ArrayList<String> List_Of_Resolution_Url = new ArrayList<>();
    ArrayList<String> List_Of_Resolution_Url_Used_For_Download = new ArrayList<>();


    public Handler threadHandler = new Handler();
    public Handler mHandler = new Handler();
    public Handler subtitleDisplayHandler;
    public Handler exoplayerdownloadhandler;


    ProgressBar Progress;
    ProgressBar progressView;


    ProgressBarHandler mDialog;
    ProgressBarHandler pDialog_for_gettig_filesize;


    private PlaybackLocation mLocation;
    private PlaybackState mPlaybackState;
    private AQuery mAquery;
    private MediaInfo mSelectedMedia;
    private CastContext mCastContext;
    private SessionManagerListener<CastSession> mSessionManagerListener = null;
    public CastSession mCastSession = null;
    MediaInfo mediaInfo;
    MediaRouteButton mediaRouteButton;
    RemoteMediaClient remoteMediaClient;


    private ViewGroup mAdUiContainer;
    private ImaSdkFactory mSdkFactory;
    private AdsLoader mAdsLoader;
    private AdsManager mAdsManager;


    SeekBar seekBar;
    Player playerModel;
    Animation myAnim;
    Window mWindow;
    AudioManager am;
    AlertDialog alert;
    View view;
    PlaylistProxy playerProxy;
    DownloadManager downloadManager;
    SharedPreferences downloadInfoPref;
    AsynWithdrm asynWithdrm;
    DownloadContentModel audio, audio_1;
    File mediaStorageDir, mediaStorageDir1;
    DBHelper dbHelper;
    AsynGetIpAddress asynGetIpAddress;


    private SubtitleProcessingTask subsFetchTask;
    public TimedTextObject srt;
    private EMVideoView emVideoView;

    Context context;

    @Override
    public void getCastCrewDetails(String movieId) {
        // Notthing to do.
    }


    public enum PlaybackLocation { LOCAL,REMOTE }
    public enum PlaybackState { PLAYING, PAUSED, BUFFERING, IDLE}


    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);


    @Override
    protected void onResume() {
        super.onResume();

//        getSupportActionBar().hide();

        Util.call_finish_at_onUserLeaveHint = true;

        /*
            This API is used to get IP Address .
         */

        AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
        asynGetIpAddress.executeOnExecutor(threadPoolExecutor);

        //********************************* END ************************************//

        /*
            Following block is used to handle chromecast visibility .
         */

        CheckAvailabilityOfChromecast = new Timer();
        CheckAvailabilityOfChromecast.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.v("PINTU","CheckAvailabilityOfChromecast called");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(video_prepared){
                            if (mediaRouteButton.isEnabled() && playerModel.getChromeCastEnable()) {
                                mediaRouteButton.setVisibility(View.VISIBLE);
                            } else {
                                mediaRouteButton.setVisibility(View.GONE);
                            }
                        }
                    }
                });
            }
        },3000,3000);

        //**************************************** END **********************************************//

        SensorOrientationChangeNotifier.getInstance(PlayerActivity.this).addListener(this);

        /*
            Used for Advertisement in player.
         */

        if (playerModel.getAdNetworkId() == 3) {
            if (mAdsManager != null && mIsAdDisplayed) {
                mAdsManager.resume();
            } else {
                Util.call_finish_at_onUserLeaveHint = true;
                watchStatus = "halfplay";
                emVideoView.start();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                latest_center_play_pause.setVisibility(View.GONE);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);
                seekBar.setProgress(emVideoView.getCurrentPosition());
                updateProgressBar();
            }
        }

        //**************************************** END **********************************************//

    }


    /**
     * Initializing elements.
     */
    private void _init(){

        player_layout = (RelativeLayout) findViewById(R.id.player_layout);
        player_layout_height = player_layout.getHeight();
        player_layout_width = player_layout.getWidth();

        primary_ll = (LinearLayout) findViewById(R.id.primary_ll);
        last_ll = (LinearLayout) findViewById(R.id.last_ll);
        last_ll = (LinearLayout) findViewById(R.id.last_ll);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);

        ipAddressTextView = (TextView) findViewById(R.id.emailAddressTextView);
        emailAddressTextView = (TextView) findViewById(R.id.ipAddressTextView);
        dateTextView = (TextView) findViewById(R.id.dateTextView);

        download = (ImageView) findViewById(R.id.downloadImageView);
        Progress = (ProgressBar) findViewById(R.id.progressBar);
        percentg = (TextView) findViewById(R.id.percentage);

        mediaRouteButton = (MediaRouteButton) findViewById(R.id.media_route_button);
        download_layout = (RelativeLayout) findViewById(R.id.downloadRelativeLayout);
        downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

        emVideoView = (EMVideoView) findViewById(R.id.emVideoView);
        subtitleText = (TextView) findViewById(R.id.offLine_subtitleText);
        subtitle_change_btn = (ImageView) findViewById(R.id.subtitle_change_btn);
        latest_center_play_pause = (ImageButton) findViewById(R.id.latest_center_play_pause);
        videoTitle = (TextView) findViewById(R.id.videoTitle);
        GenreTextView = (TextView) findViewById(R.id.GenreTextView);
        videoDurationTextView = (TextView) findViewById(R.id.videoDurationTextView);
        videoCensorRatingTextView = (TextView) findViewById(R.id.videoCensorRatingTextView);
        videoCensorRatingTextView1 = (TextView) findViewById(R.id.videoCensorRatingTextView1);
        videoReleaseDateTextView = (TextView) findViewById(R.id.videoReleaseDateTextView);
        videoCastCrewTitleTextView = (TextView) findViewById(R.id.videoCastCrewTitleTextView);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_exoplayer);

        _init();

        try{
            getSupportActionBar().hide();
        }catch(Exception e){}

        /*
         This code is responsible for change volume and brightness using swipe control .. & Player center buton animation.
          */

        myAnim= AnimationUtils.loadAnimation(this, R.anim.bounce);
        am = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        mWindow = getWindow();
        emVideoView.setOnTouchListener(clickFrameSwipeListener);

        volume_brightness_control_layout = (LinearLayout) findViewById(R.id.volume_brightness_control_layout);
        volume_brightness_control = (ImageButton) findViewById(R.id.volume_brightness_control);
        volume_bright_value = (TextView) findViewById(R.id.volume_bright_value);


        //************************************** END ******************************************//


        /*
            Getting data for player model.
         */

        playerModel = (Player) getIntent().getSerializableExtra("PlayerModel");
        content_types_id = playerModel.getContentTypesId();
        played_length = playerModel.getPlayPos() * 1000;
        current_played_length = played_length;
        movieId = playerModel.getMovieUniqueId();
        episodeId = playerModel.getEpisode_id();

        //************************************** END ******************************************//


        if (playerModel.getVideoUrl().contains(".mpd")) {
            isDrm = true;
        } else {
            isDrm = false;
        }


        /*
            Checking availability of mandatory inputs.
         */
        /*if (playerModel != null && playerModel.getDomainName() != null && !playerModel.getDomainName().trim().matches("")) {
            rootUrl = playerModel.getDomainName();
        } else {
            Toast.makeText(PlayerActivity.this,DOMAIN_NAME_MSG, Toast.LENGTH_LONG).show();
            finish();
            return;
        }*/


      /*  packgeName = getApplicationContext().getPackageName();

        if (!packgeName.equals(SDKInitializer.getUser_Package_Name_At_Api(getApplicationContext()))) {
            Toast.makeText(PlayerActivity.this, PACKAGE_NAME_MSG, Toast.LENGTH_LONG).show();
            finish();
            return;
        } else {
            if (SDKInitializer.getHashKey(getApplicationContext()).equals("")) {
                Toast.makeText(PlayerActivity.this, HASH_KEY_MSG, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }*/


        rootUrl = APIUrlConstant.BASE_URl;

        if (playerModel != null && playerModel.getAuthToken() != null && !playerModel.getAuthToken().trim().matches("")) {
            authToken = playerModel.getAuthToken();
        } else {
            Toast.makeText(PlayerActivity.this,VALID_AUTHTOKEN, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (playerModel != null && playerModel.getUserId() != null && !playerModel.getUserId().trim().matches("")) {
            userIdStr = playerModel.getUserId();
        } else {
            Toast.makeText(PlayerActivity.this, CHECK_USER_ID_MSG, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (playerModel != null && playerModel.getEmailId() != null && !playerModel.getEmailId().trim().matches("")) {
            emailIdStr = playerModel.getEmailId();
        } else {
            Toast.makeText(PlayerActivity.this, VALID_EMAIL, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if (playerModel != null && playerModel.getAppName() != null && !playerModel.getAppName().trim().matches("")) {
        } else {
            Toast.makeText(PlayerActivity.this,APP_NAME_MSG, Toast.LENGTH_LONG).show();
            finish();
            return;
        }



        if (!playerModel.getVideoUrl().trim().equals("")) {
            if (playerModel.isThirdPartyPlayer()) {

                if (playerModel.getVideoUrl().contains("://www.youtube") || playerModel.getVideoUrl().contains("://www.youtu.be")) {
                    if (playerModel.getVideoUrl().contains("live_stream?channel")) {
                        final Intent playVideoIntent = new Intent(PlayerActivity.this, ThirdPartyPlayer.class);
                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        playVideoIntent.putExtra("PlayerModel", playerModel);
                        startActivity(playVideoIntent);
                        finish();
                        return;
                    } else {
                        final Intent playVideoIntent = new Intent(PlayerActivity.this, YouTubeAPIActivity.class);
                        playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        playVideoIntent.putExtra("PlayerModel", playerModel);
                        startActivity(playVideoIntent);
                        finish();
                        return;
                    }
                } else {
                    final Intent playVideoIntent = new Intent(PlayerActivity.this, ThirdPartyPlayer.class);
                    playVideoIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    playVideoIntent.putExtra("PlayerModel", playerModel);
                    startActivity(playVideoIntent);
                    finish();
                    return;

                }
            }
        } else {
            finish();
            return;
        }


        //**************************************** END ***********************************************//


        /**
         * Used for Advertisement in player .
         */

        mAdUiContainer = (ViewGroup) findViewById(R.id.videoPlayerWithAdPlayback);
        mSdkFactory = ImaSdkFactory.getInstance();
        mAdsLoader = mSdkFactory.createAdsLoader(this);
        // Add listeners for when ads are loaded and for errors.
        mAdsLoader.addAdErrorListener(this);
        mAdsLoader.addAdsLoadedListener(new AdsLoader.AdsLoadedListener() {
            @Override
            public void onAdsManagerLoaded(AdsManagerLoadedEvent adsManagerLoadedEvent) {
                // Ads were successfully loaded, so get the AdsManager instance. AdsManager has
                // events for ad playback and errors.
                mAdsManager = adsManagerLoadedEvent.getAdsManager();

                // Attach event and error event listeners.
                mAdsManager.addAdErrorListener(PlayerActivity.this);
                mAdsManager.addAdEventListener(PlayerActivity.this);
                mAdsManager.init();
            }
        });

        //**************************************** END ***********************************************//


        /**
         * Calculation of Bandwidth used by APP before entering into Player .
         */

        PreviousUsedDataByApp(true);
        PreviousUsedData_By_DownloadContent = DataUsedByDownloadContent();

        //**************************************** END ************************************************//



        /**
          * Initializing Database for future usage & getting information about downloaded content.
         */

        exoplayerdownloadhandler = new Handler();
        dbHelper = new DBHelper(PlayerActivity.this);
        dbHelper.getWritableDatabase();
        audio_1 = dbHelper.getContact(playerModel.getStreamUniqueId() + emailIdStr);

        if (audio_1 != null) {
            if (audio_1.getUSERNAME().trim().equals(emailIdStr.trim())) {
                checkDownLoadStatusFromDownloadManager1(audio_1, false);
            }
        }

        if (content_types_id!=LIVE_STREAM && playerModel.getDownloadStatus()) {
            download_layout.setVisibility(View.VISIBLE);
        }

        //**************************************** END ************************************************//

        /**
         * Initializing Chromecast & styling the chromecast button.
         */

        mAquery = new AQuery(PlayerActivity.this);
        setupCastListener();
        mCastContext = CastContext.getSharedInstance(PlayerActivity.this);
        mCastContext.registerLifecycleCallbacksBeforeIceCreamSandwich(PlayerActivity.this, savedInstanceState);
        mCastSession = CastContext.getSharedInstance(PlayerActivity.this).getSessionManager().getCurrentCastSession();
        mCastContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);


        boolean shouldStartPlayback = false;
        int startPosition = 0;

        if (shouldStartPlayback) {
            // PlayerActivity.this will be the case only if we are coming from the
            // CastControllerActivity by disconnecting from a device
            mPlaybackState = PlaybackState.PLAYING;
            updatePlaybackLocation(PlaybackLocation.LOCAL);
            updatePlayButton(mPlaybackState);
            if (startPosition > 0) {
            }
        } else {
            // we should load the video but pause it and show the album art.
            if (mCastSession != null && mCastSession.isConnected()) {
                updatePlaybackLocation(PlaybackLocation.REMOTE);
            } else {
                updatePlaybackLocation(PlaybackLocation.LOCAL);
            }
            mPlaybackState = PlaybackState.IDLE;
            updatePlayButton(mPlaybackState);
        }


        Context castContext = new ContextThemeWrapper(PlayerActivity.this, android.support.v7.mediarouter.R.style.Theme_MediaRouter);
        Drawable drawable = null;
        TypedArray a = castContext.obtainStyledAttributes(null, android.support.v7.mediarouter.R.styleable.MediaRouteButton, android.support.v7.mediarouter.R.attr.mediaRouteButtonStyle, 0);
        drawable = a.getDrawable(android.support.v7.mediarouter.R.styleable.MediaRouteButton_externalRouteEnabledDrawable);
        a.recycle();
        DrawableCompat.setTint(drawable, getResources().getColor(R.color.white));


        CastButtonFactory.setUpMediaRouteButton(PlayerActivity.this, mediaRouteButton);
        mediaRouteButton.setRemoteIndicatorDrawable(drawable);

        //************************************************ END ********************************************************//


        downloadInfoPref = getSharedPreferences(Util.DOWNLOAD_INFO_PREF,0);
        SharedPreferences.Editor editor = downloadInfoPref.edit();
        editor.putString("authToken",authToken);
        editor.putString("email_id",emailIdStr);
        editor.putString("user_id",userIdStr);
        editor.putString("rootUrl",rootUrl);
        editor.commit();


        /*
            Applying font in player.
         */

        try{
            Typeface videoTitleface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_regular));
            videoTitle.setTypeface(videoTitleface);
            Typeface GenreTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            GenreTextView.setTypeface(GenreTextViewface);
            Typeface videoDurationTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            videoDurationTextView.setTypeface(videoDurationTextViewface);
            Typeface videoCensorRatingTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            videoCensorRatingTextView.setTypeface(videoCensorRatingTextViewface);
            Typeface videoCensorRatingTextView1face = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            videoCensorRatingTextView1.setTypeface(videoCensorRatingTextView1face);
            Typeface videoReleaseDateTextViewface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            videoReleaseDateTextView.setTypeface(videoReleaseDateTextViewface);
            story = (TextView) findViewById(R.id.story);
            Typeface storyTypeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            story.setTypeface(storyTypeface);
            Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts));
            videoCastCrewTitleTextView.setTypeface(watchTrailerButtonTypeface);
            videoCastCrewTitleTextView.setText(Util.getTextofLanguage(PlayerActivity.this, Util.CAST_CREW_BUTTON_TITLE, Util.DEFAULT_CAST_CREW_BUTTON_TITLE));

        }catch (Exception e){
            Log.v("BIBHU11","Exception during font applying ="+e.toString());}


        //************************************************ END ********************************************************//


        /*
            This service is responsible to calculate downloading content bandwidth .
         */
        startService(new Intent(PlayerActivity.this, DataConsumptionService.class));

        //************************************************ END ********************************************************//


        /*
            Following receiver is used to handle multiple download option feature .
         */
        registerReceiver(SelectedUrl, new IntentFilter("UrlPosition"));

        //************************************************ END ********************************************************//


        /**
         *  Gathering info. on subtitle feature.
         */



        if (playerModel.getSubTitleName() != null) {
            SubTitleName = playerModel.getSubTitleName();
        } else {
            SubTitleName.clear();
        }

        if (playerModel.getSubTitlePath() != null) {
            SubTitlePath = playerModel.getSubTitlePath();
        } else {
            SubTitlePath.clear();
        }

//************************************************ END ********************************************************//

        /**
         * Following code is responsible for resolution change feature .
         */

        if (!isDrm) {

            Util.VideoResolution = "Auto";
            /**ad **/

            if (playerModel.getMidRoll() == 1) {
                adDetails = playerModel.getAdDetails().split(",");
            }

            /**ad **/
         /*   if (playerModel.getResolutionFormat() != null) {
                ResolutionFormat = playerModel.getResolutionFormat();
            } else {
                ResolutionFormat.clear();

            }

            if (playerModel.getResolutionUrl() != null) {
                ResolutionUrl = playerModel.getResolutionUrl();
            } else {
                ResolutionUrl.clear();
            }*/

            if (playerModel.getResolutionFormat() != null) {
//                ResolutionFormat = playerModel.getResolutionFormat();

                for(int i =0 ;i<playerModel.getResolutionFormat().size() ;i++){
                    ResolutionFormat.add(playerModel.getResolutionFormat().get(i));
                }

            } else {
                ResolutionFormat.clear();

            }

            if (playerModel.getResolutionUrl() != null) {
                for(int i =0 ;i<playerModel.getResolutionUrl().size() ;i++){
                    ResolutionUrl.add(playerModel.getResolutionUrl().get(i));
                }

            } else {
                ResolutionUrl.clear();
            }



            if (ResolutionUrl.size() < 1) {
                Log.v("SUBHA", "resolution image Invisible called");
            } else {
                ResolutionUrl.add(playerModel.getVideoUrl().trim());
                ResolutionFormat.add("Auto");
            }

            if (ResolutionFormat.size() > 0) {
                Collections.reverse(ResolutionFormat);
                for (int m = 0; m < ResolutionFormat.size(); m++) {
                }
            }
            if (ResolutionUrl.size() > 0) {
                Collections.reverse(ResolutionUrl);
                for (int n = 0; n < ResolutionUrl.size(); n++) {
                }
            }


        }
        //************************************************ END ********************************************************//


        /**
         * Following code is responsible for subtitle change feature .
         */

        if (isDrm) {
            if (SubTitlePath.size() < 1) {
                subtitle_change_btn.setVisibility(View.GONE);
            } else {
                subtitle_change_btn.setBackgroundResource(R.drawable.cc_button_radious);
                subtitle_change_btn.setImageResource(R.drawable.subtitle_image_drm);
                subtitle_change_btn.setVisibility(View.VISIBLE);
            }
        } else {
            if ((SubTitlePath.size() < 1) && (ResolutionUrl.size() < 1)) {
                subtitle_change_btn.setVisibility(View.GONE);
            } else {
                subtitle_change_btn.setBackgroundResource(0);
                subtitle_change_btn.setImageResource(R.drawable.subtitle_image);
                subtitle_change_btn.setVisibility(View.VISIBLE);
            }
        }

        //************************************************ END ********************************************************//


        subtitle_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Util.call_finish_at_onUserLeaveHint = false;

                    if (isDrm) {
                        Intent intent = new Intent(PlayerActivity.this, SubtitleList.class);
                        intent.putExtra("SubTitleName", SubTitleName);
                        intent.putExtra("SubTitlePath", SubTitlePath);
                        startActivityForResult(intent, SUBTITLE_REQUEST_CODE);
                    } else {
                        Intent intent = new Intent(PlayerActivity.this, Subtitle_Resolution.class);
                        intent.putExtra("ResolutionFormat", ResolutionFormat);
                        intent.putExtra("ResolutionUrl", ResolutionUrl);
                        intent.putExtra("SubTitleName", SubTitleName);
                        intent.putExtra("SubTitlePath", SubTitlePath);
                        startActivityForResult(intent, SUBTITLE_REQUEST_CODE);
                    }


                } catch (Exception e) { }

            }
        });

        // Displaying video related information.

        try {
            if (playerModel.getVideoTitle().trim() != null && !playerModel.getVideoTitle().trim().matches("")) {
                videoTitle.setText(playerModel.getVideoTitle().trim());
                videoTitle.setVisibility(View.VISIBLE);
            } else {
                videoTitle.setVisibility(View.GONE);
            }


            if (playerModel.getVideoGenre().trim() != null && !playerModel.getVideoGenre().trim().matches("")) {
                GenreTextView.setText(playerModel.getVideoGenre().trim());
                GenreTextView.setVisibility(View.VISIBLE);
            } else {
                GenreTextView.setVisibility(View.GONE);
            }


            if (playerModel.getVideoDuration().trim() != null && !playerModel.getVideoDuration().trim().matches("")) {
                videoDurationTextView.setText(playerModel.getVideoDuration().trim());
                videoDurationTextView.setVisibility(View.VISIBLE);
                censor_layout = false;
            } else {
                videoDurationTextView.setVisibility(View.GONE);
            }
            if (playerModel.getCensorRating().trim() != null && !playerModel.getCensorRating().trim().matches("")) {
                if ((playerModel.getCensorRating().trim()).contains("_")) {
                    String Data[] = (playerModel.getCensorRating().trim()).split("-");
                    videoCensorRatingTextView.setVisibility(View.VISIBLE);
                    videoCensorRatingTextView1.setVisibility(View.VISIBLE);
                    videoCensorRatingTextView.setText(Data[0]);
                    videoCensorRatingTextView1.setText(Data[1]);
                    censor_layout = false;
                } else {
                    censor_layout = false;
                    videoCensorRatingTextView.setVisibility(View.VISIBLE);
                    videoCensorRatingTextView1.setVisibility(View.GONE);
                    videoCensorRatingTextView.setText(playerModel.getCensorRating().trim());
                }
            } else {
                videoCensorRatingTextView.setVisibility(View.GONE);
                videoCensorRatingTextView1.setVisibility(View.GONE);
            }
            if (playerModel.getCensorRating().trim() != null && playerModel.getCensorRating().trim().equalsIgnoreCase(Util.getTextofLanguage(PlayerActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA))) {
                videoCensorRatingTextView.setVisibility(View.GONE);
                videoCensorRatingTextView1.setVisibility(View.GONE);
            }

            if (playerModel.getVideoReleaseDate().trim() != null && !playerModel.getVideoReleaseDate().trim().matches("")) {
                videoReleaseDateTextView.setText(playerModel.getVideoReleaseDate().trim());
                videoReleaseDateTextView.setVisibility(View.VISIBLE);
                censor_layout = false;
            } else {
                videoReleaseDateTextView.setVisibility(View.GONE);
            }

            if (censor_layout) {
                ((LinearLayout) findViewById(R.id.durationratingLiearLayout)).setVisibility(View.GONE);
            }
            if (playerModel.getVideoStory().trim() != null && !playerModel.getVideoStory().trim().matches("")) {
                story.setText(playerModel.getVideoStory());
                story.setVisibility(View.VISIBLE);
                ResizableCustomView.doResizeTextView(PlayerActivity.this, story, MAX_LINES, Util.getTextofLanguage(PlayerActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);
            } else {
                story.setVisibility(View.GONE);
            }

            if (playerModel.isCastCrew() == true) {
                videoCastCrewTitleTextView.setVisibility(View.VISIBLE);
            } else {
                videoCastCrewTitleTextView.setVisibility(View.GONE);
            }

        } catch (Exception e) {
        }



        videoCastCrewTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                castButtonClicked = true;
                Util.call_finish_at_onUserLeaveHint = false;
                CastCrew.startCastCrewActivity(movieId);
            }
        });


  /*      videoCastCrewTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.checkNetwork(PlayerActivity.this)) {
                    //Will Add Some Data to send
                    Util.call_finish_at_onUserLeaveHint = false;
                    Util.hide_pause = true;
                    ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
                    latest_center_play_pause.setVisibility(View.VISIBLE);

                    if (emVideoView.isPlaying()) {
                        emVideoView.pause();
                        latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                        center_play_pause.setImageResource(R.drawable.ic_media_play);
                        mHandler.removeCallbacks(updateTimeTask);
                    }


                    if (center_pause_paly_timer_is_running) {
                        center_pause_paly_timer.cancel();
                        center_pause_paly_timer_is_running = false;

                        subtitle_change_btn.setVisibility(View.GONE);
                        primary_ll.setVisibility(View.GONE);
                        last_ll.setVisibility(View.GONE);
                        center_play_pause.setVisibility(View.GONE);
                        current_time.setVisibility(View.GONE);
                    }


                    final Intent detailsIntent = new Intent(PlayerActivity.this, CastAndCrewActivity.class);
                    detailsIntent.putExtra("cast_movie_id", movieId.trim());
                    detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(getApplicationContext(), Util.getTextofLanguage(PlayerActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                }
            }
        });*/





        /*
            This timer is only responsible to active movable timer .
         */

        if(playerModel.getWaterMark()){
            MovableTimer = new Timer();
            MovableTimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            try {
                                if (playerModel.getUseIpStatus())
                                    ipAddressTextView.setVisibility(View.VISIBLE);
                                else
                                    ipAddressTextView.setVisibility(View.GONE);
                                ipAddressTextView.setText(ipAddressStr);

                                if (playerModel.getUseEmailStatus())
                                    emailAddressTextView.setVisibility(View.VISIBLE);
                                else
                                    emailAddressTextView.setVisibility(View.GONE);
                                emailAddressTextView.setText(emailIdStr);

                                if (playerModel.getUseDateStatus())
                                    dateTextView.setVisibility(View.VISIBLE);
                                else
                                    dateTextView.setVisibility(View.GONE);
                                dateTextView.setText("" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            } catch (Exception e) {
                                Log.v("BIBHU11", "Exception =" + e.toString());
                            }
                        }
                    });
                    MoveWaterMark();
                }
            }, 2000, 2000);
        }

        //*************************************************** END ***********************************************************//




        compress_expand = (ImageView) findViewById(R.id.compress_expand);
        back = (ImageButton) findViewById(R.id.back);

        seekBar = (SeekBar) findViewById(R.id.progress);
        center_play_pause = (ImageButton) findViewById(R.id.center_play_pause);

        current_time = (TextView) findViewById(R.id.current_time);
        total_time = (TextView) findViewById(R.id.total_time);
        progressView = (ProgressBar) findViewById(R.id.progress_view);


        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        Util.player_description = true;


        /*
            This is block is responsible to display default player size .
         */


        LinearLayout.LayoutParams params1 = null;
        if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
            if (PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);
            } else {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);
            }
        } else {
            if (PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);
            } else {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);
            }
        }
        player_layout.setLayoutParams(params1);

        // ********************************************************* END ******************************************************//




        if (content_types_id == LIVE_STREAM) {
            seekBar.setEnabled(false);
            seekBar.setProgress(0);
        } else {
            seekBar.setEnabled(true);
            seekBar.setProgress(0);
        }

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mHandler.removeCallbacks(updateTimeTask);
                playerStartPosition = emVideoView.getCurrentPosition();

                /**
                 * Call New Video Log Api.
                 */
                CallVideoLog(ASYNC_VODEOLOG_DETAILS);

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                subtitleText.setText("");
                mHandler.removeCallbacks(updateTimeTask);
                emVideoView.seekTo(seekBar.getProgress());
                current_time.setVisibility(View.VISIBLE);
                current_time.setVisibility(View.GONE);
                showCurrentTime();
                current_time.setVisibility(View.VISIBLE);
                updateProgressBar();

                isFastForward = true;
                playerPreviousPosition = playerStartPosition;

                log_temp_id = "0";
                player_start_time = millisecondsToString(emVideoView.getCurrentPosition());
                playerPosition = player_start_time;
            }
        });

        seekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Instant_End_Timer();

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    Start_Timer();
                }
                return false;
            }
        });


        /**
         * Handling portrait & landscape mode in player.
         */
        compress_expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (compressed) {
                    compressed = false;
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT);
                    player_layout.setLayoutParams(params);
                    compress_expand.setImageResource(R.drawable.ic_media_fullscreen_shrink);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                    });
                    Util.player_description = false;
                    Util.landscape = true;
                    hideSystemUI();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {

                    Util.player_description = true;
                    LinearLayout.LayoutParams params1 = null;
                    if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);

                        } else {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);
                        }
                    } else {
                        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);

                        } else {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);
                        }
                    }
                    player_layout.setLayoutParams(params1);
                    compressed = true;
                    compress_expand.setImageResource(R.drawable.ic_media_fullscreen_stretch);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                    });
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    showSystemUI();


                }


            }
        });


        center_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Execute_Pause_Play();
            }
        });
        latest_center_play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                latest_center_play_pause.startAnimation(myAnim);

                if (mCastSession != null && mCastSession.isConnected()) {
                    if (Util.hide_pause) {
                        Util.hide_pause = false;
                        latest_center_play_pause.setVisibility(View.GONE);
                    }
                    Execute_Pause_Play();

                } else {
                    Execute_Pause_Play();

                }

            }
        });


        emVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {

                video_prepared = true;
                if (change_resolution) {

                    change_resolution = false;
                    emVideoView.start();
                    emVideoView.seekTo(seekBarProgress);
                    seekBar.setProgress(emVideoView.getCurrentPosition());

                    if (is_paused) {
                        is_paused = false;
                        emVideoView.pause();
                        progressView.setVisibility(View.GONE);
                    } else {
                        updateProgressBar();
                    }
                }
                else{

                    if (playerModel.getPlayPos() >= emVideoView.getDuration() / 1000) {
                        played_length = 0;
                    }
                    video_completed = false;
                    if (progressView != null) {
                        ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.VISIBLE);
                        center_play_pause.setVisibility(View.GONE);
                        latest_center_play_pause.setVisibility(View.GONE);
                    }

                    try {

                        //video log
                        if (content_types_id == LIVE_STREAM) {
                            if (SubTitlePath.size() > 0) {

                                Util.DefaultSubtitle =SubTitleName.get(0).trim();

                                CheckSubTitleParsingType("1");
                                subtitleDisplayHandler = new Handler();
                                subsFetchTask = new SubtitleProcessingTask("1");
                                subsFetchTask.execute();
                            } else {
                                CallVideoLog(ASYNC_VODEOLOG_DETAILS);
                            }

                            PreviousUsedDataByApp(false);
                            /**ad **/
                            if (playerModel.getAdNetworkId() == 3){
                                requestAds(playerModel.getChannel_id());
                            }
                            /**ad **/
                            emVideoView.start();

                            updateProgressBar();
                        } else {
                            startTimer();

                            if (played_length > 0) {
                                ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
                                Util.call_finish_at_onUserLeaveHint = false;

                                Intent resumeIntent = new Intent(PlayerActivity.this, ResumePopupActivity.class);
                                startActivityForResult(resumeIntent, RESUME_VIDEO_REQUEST_CODE);
                            } else {

                                PreviousUsedDataByApp(false);
                                /**ad **/
                                if (playerModel.getAdNetworkId() == 3){
                                    requestAds(playerModel.getChannel_id());

                                }
                                emVideoView.start();
                                seekBar.setProgress(emVideoView.getCurrentPosition());
                                updateProgressBar();

                                if (SubTitlePath.size() > 0) {

                                    Util.DefaultSubtitle =SubTitleName.get(0).trim();


                                    CheckSubTitleParsingType("1");
                                    subtitleDisplayHandler = new Handler();
                                    subsFetchTask = new SubtitleProcessingTask("1");
                                    subsFetchTask.execute();
                                } else {
                                    CallVideoLog(ASYNC_VODEOLOG_DETAILS);

                                }


                            }

                        }
                    } catch (Exception e) {
                    }
                }

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCalled();

            }
        });


//commented by me
        //  emVideoView.setVideoURI(Uri.parse(playerModel.getVideoUrl()));
        try {
            /*
             * Initialize the Wasabi Runtime (necessary only once for each
			 * instantiation of the application)
			 *
			 * ** Note: Set Runtime Properties as needed for your environment
			 */
            Runtime.initialize(getDir("wasabi", MODE_PRIVATE).getAbsolutePath());

            Log.v("BIBHU1","lib path=="+getDir("wasabi", MODE_PRIVATE).getAbsolutePath());

            /*
             * Personalize the application (acquire DRM keys). PlayerActivity.this is only
			 * necessary once each time the application is freshly installed
			 *
			 * ** Note: personalize() is a blocking call and may take long
			 * enough to complete to trigger ANR (Application Not Responding)
			 * errors. In a production application PlayerActivity.this should be called in a
			 * background thread.
			 */
            if (!Runtime.isPersonalized())
                Runtime.personalize();

        } catch (NullPointerException e) {
            //onBackPressed();
            backCalled();
            return;
        } catch (ErrorCodeException e) {
            // Consult WasabiErrors.txt for resolution of the error codes
            //onBackPressed();
            backCalled();
            return;
        }

        try {
            EnumSet<PlaylistProxy.Flags> flags = EnumSet.noneOf(PlaylistProxy.Flags.class);
            playerProxy = new PlaylistProxy(flags, PlayerActivity.this, new Handler());
            playerProxy.start();
        } catch (ErrorCodeException e) {
            // Consult WasabiErrors.txt for resolution of the error codes
            //  onBackPressed();
            backCalled();
            return;
        }



        	/*
         * create a playlist proxy url and pass it to the native player
		 */
        try {
            /*
             * Note that the MediaSourceType must be adapted to the stream type
			 * (DASH or HLS). Similarly,
			 * the MediaSourceParams need to be set according to the media type
			 * if MediaSourceType is SINGLE_FILE
			 */

            ContentTypes2 contentType = ContentTypes2.DASH;
            PlaylistProxy.MediaSourceParams params = new PlaylistProxy.MediaSourceParams();
            params.sourceContentType = contentType
                    .getMediaSourceParamsContentType();
            /*
			 * if the content has separate audio tracks (eg languages) you may
			 * select one using MediaSourceParams, eg params.language="es";
			 */
            String contentTypeValue = contentType.toString();
            if (playerModel.getVideoUrl().contains(".mpd")) {
                String url = playerProxy.makeUrl(playerModel.getVideoUrl(), PlaylistProxy.MediaSourceType.valueOf((contentTypeValue == "MP4" || contentTypeValue == "HLS" || contentTypeValue == "DASH") ? contentTypeValue : "SINGLE_FILE"), params);
                Log.v("BIBHU1","lib path url=="+url);
                emVideoView.setVideoURI(Uri.parse(url));

            } else {
                emVideoView.setVideoURI(Uri.parse(playerModel.getVideoUrl()));

            }


        } catch (ErrorCodeException e) {
            // Consult WasabiErrors.txt for resolution of the error codes
            //  onBackPressed();
            backCalled();
            return;
        } catch (IllegalArgumentException e) {
            // onBackPressed();
            backCalled();
            e.printStackTrace();
        } catch (SecurityException e) {
            // onBackPressed();
            backCalled();
            e.printStackTrace();
        } catch (IllegalStateException e) {
            //  onBackPressed();
            backCalled();
            e.printStackTrace();
        }


        /*****Offline*****/

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(PlayerActivity.this)) {
                    final Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                    try {
                        Util.call_finish_at_onUserLeaveHint = false;
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE);
                    } catch (ActivityNotFoundException e) {
                    }
                } else {

                    if (isDrm) {
                        // This is applicable for DRM content.

                        List_Of_Resolution_Format.clear();
                        List_Of_FileSize.clear();
                        List_Of_Resolution_Url.clear();
                        List_Of_Resolution_Url_Used_For_Download.clear();


                        asynWithdrm = new AsynWithdrm();
                        asynWithdrm.executeOnExecutor(threadPoolExecutor);
                    } else {
                        // This is applicable for NON-DRM contnet.// have to change


                        List_Of_Resolution_Format.clear();
                        List_Of_FileSize.clear();
                        List_Of_Resolution_Url.clear();
                        List_Of_Resolution_Url_Used_For_Download.clear();



                        if (playerModel.getNonDrmDownloadUrlList().size() > 0) {
                            for (int i = 0; i < playerModel.getNonDrmDownloadUrlList().size(); i++) {
                                List_Of_Resolution_Url.add(playerModel.getNonDrmDownloadUrlList().get(i));
                                List_Of_Resolution_Url_Used_For_Download.add(playerModel.getNonDrmDownloadUrlList().get(i));
                                List_Of_Resolution_Format.add(playerModel.getNonDrmDownloadFormatList().get(i));
                            }

                            /*Collections.reverse(List_Of_Resolution_Format);
                            Collections.reverse(List_Of_Resolution_Url);
                            Collections.reverse(List_Of_Resolution_Url_Used_For_Download);*/

                            pDialog_for_gettig_filesize = new ProgressBarHandler(PlayerActivity.this);
                            pDialog_for_gettig_filesize.show();

                            new DetectDownloadingFileSize().execute();
                        } else {
                            new DownloadFileFromURL().execute(playerModel.getVideoUrl());
                        }
                    }


                    if ((playerModel.getOfflineSubtitleUrl().size() > 0) && (playerModel.getOfflineSubtitleUrl().size() == playerModel.getOfflineSubtitleLanguage().size())) {
                        Download_SubTitle(playerModel.getOfflineSubtitleUrl().get(0));
                    }
                }

            }
        });


        percentg.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PlayerActivity.this, R.style.MyAlertDialogStyle);
                                            dlgAlert.setTitle(Util.getTextofLanguage(PlayerActivity.this, Util.STOP_SAVING_THIS_VIDEO, Util.DEFAULT_STOP_SAVING_THIS_VIDEO));
                                            dlgAlert.setMessage(Util.getTextofLanguage(PlayerActivity.this, Util.YOUR_VIDEO_WONT_BE_SAVED, Util.DEFAULT_YOUR_VIDEO_WONT_BE_SAVED));
                                            dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this, Util.BTN_KEEP, Util.DEFAULT_BTN_KEEP), null);
                                            dlgAlert.setCancelable(false);
                                            dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this, Util.BTN_KEEP, Util.DEFAULT_BTN_KEEP),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();

                                                        }
                                                    });
                                            dlgAlert.setNegativeButton(Util.getTextofLanguage(PlayerActivity.this, Util.BTN_DISCARD, Util.DEFAULT_BTN_DISCARD), null);
                                            dlgAlert.setCancelable(false);
                                            dlgAlert.setNegativeButton(Util.getTextofLanguage(PlayerActivity.this, Util.BTN_DISCARD, Util.DEFAULT_BTN_DISCARD),
                                                    new DialogInterface.OnClickListener() {
                                                        public void onClick(DialogInterface dialog, int id) {
                                                            dialog.cancel();
                                                            downloading = false;
                                                            audio = dbHelper.getContact(playerModel.getStreamUniqueId() + emailIdStr);

                                                            if (audio != null) {


                                                                String k = String.valueOf(audio.getDOWNLOADID());

                                                                downloadManager.remove(audio.getDOWNLOADID());
                                                                dbHelper.deleteRecord(audio);

                                                                SQLiteDatabase DB = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
                                                                String query = "DELETE FROM " + DBHelper.DOWNLOAD_CONTENT_INFO + " WHERE download_contnet_id = '" + enqueue + "'";
                                                                DB.execSQL(query);

                                                            }


                                                            exoplayerdownloadhandler.post(new Runnable() {
                                                                @Override
                                                                public void run() {


                                                                    Progress.setProgress((int) 0);
                                                                    //percentg.setText(0+"%");
                                                                    percentg.setVisibility(View.GONE);
                                                                    download.setVisibility(View.VISIBLE);


                                                                }
                                                            });

                                                            Toast.makeText(getApplicationContext(), Util.getTextofLanguage(PlayerActivity.this, Util.DOWNLOAD_CANCELLED, Util.DEFAULT_DOWNLOAD_CANCELLED), Toast.LENGTH_SHORT).show();

                                                        }
                                                    });

                                            dlgAlert.create().show();

                                        }
                                    }
        );

        /*****Offline*****/
    }


    @Override
    public void onErrorNotification(int i, String s) {

    }

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        initializeTimerTask();
        timer.schedule(timerTask, 1000, 1000); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {

            Log.v(TAG, "=======================================stoptimertask caled=================================");

            timer.cancel();
            timer = null;
        }

    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                threadHandler.post(new Runnable() {
                    public void run() {
                        if (emVideoView != null) {

                            //  Log.v(TAG,"===================****====================initializeTimerTask caled===============**==================");


                            int currentPositionStr = millisecondsToString(emVideoView.getCurrentPosition());
                            playerPosition = currentPositionStr;
                            if(content_types_id == LIVE_STREAM)
                            {

                                Log.v("BIBHU19","*************entered***********************="+emVideoView.getCurrentPosition());

                                try {
                                    if((emVideoView.getCurrentPosition()/1000)==0)
                                        return;

                                    if(((emVideoView.getCurrentPosition()/1000) % 60) == 0){
                                        CallVideoLog(ASYNC_FF_VODEOLOG_DETAILS);
                                          Log.v("BIBHU19","************************************=");

                                    }
                                }catch (Exception e){}



                            }else
                            {

                                if (isFastForward == true) {
                                    isFastForward = false;

                                    log_temp_id = "0";


                                    int duration = emVideoView.getDuration() / 1000;
                                    if (currentPositionStr > 0 && currentPositionStr == duration) {
                                        watchStatus = "complete";
                                        CallVideoLog(ASYNC_FF_VODEOLOG_DETAILS);
                                    } else {
                                        watchStatus = "halfplay";
                                        CallVideoLog(ASYNC_FF_VODEOLOG_DETAILS);
                                    }

                                } else if (isFastForward == false && currentPositionStr >0) {

                                    playerPreviousPosition = 0;

                                    int duration = emVideoView.getDuration() / 1000;
                                    if (currentPositionStr > 0 && currentPositionStr == duration) {

                                        watchStatus = "complete";
                                        CallVideoLog(ASYNC_VODEOLOG_DETAILS);

                                    } else if (currentPositionStr > 0 && currentPositionStr % 60 == 0) {

                                        watchStatus = "halfplay";
                                        CallVideoLog(ASYNC_VODEOLOG_DETAILS);

                                    }
                                }
                            }

                        }
                        //get the current timeStamp
                    }
                });
            }
        };
    }


    private int millisecondsToString(int milliseconds) {
        // int seconds = (int) (milliseconds / 1000) % 60 ;
        int seconds = (int) (milliseconds / 1000);

        return seconds;
    }

    @Override
    public void onOrientationChange(int orientation) {


        if (orientation == 90) {

            Util.player_description = false;
            Util.landscape = false;

            compressed = false;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            player_layout.setLayoutParams(params);
            compress_expand.setImageResource(R.drawable.ic_media_fullscreen_shrink);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            });
            hideSystemUI();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
            //current_time.setVisibility(View.GONE);
        } else if (orientation == 270) {
            Util.player_description = false;
            Util.landscape = true;

            compressed = false;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            player_layout.setLayoutParams(params);
            compress_expand.setImageResource(R.drawable.ic_media_fullscreen_shrink);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            });
            hideSystemUI();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            //current_time.setVisibility(View.GONE);

            // Do some landscape stuff
        } else if (orientation == 180) {

            Util.player_description = true;

            LinearLayout.LayoutParams params1 = null;
            if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                if (PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);

                } else {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);
                }
            } else {
                if (PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);

                } else {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);
                }
            }
            player_layout.setLayoutParams(params1);
            compressed = true;
            compress_expand.setImageResource(R.drawable.ic_media_fullscreen_stretch);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            });
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            showSystemUI();

            //current_time.setVisibility(View.GONE);

        } else if (orientation == 0) {

            Util.player_description = true;
            LinearLayout.LayoutParams params1 = null;
            if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)) {
                if (PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);

                } else {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 45) / 100);
                }
            } else {
                if (PlayerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);

                } else {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (screenHeight * 40) / 100);
                }
            }
            player_layout.setLayoutParams(params1);
            compressed = true;
            compress_expand.setImageResource(R.drawable.ic_media_fullscreen_stretch);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }
            });
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            showSystemUI();

            //current_time.setVisibility(View.GONE);
        }

        current_time_position_timer();

    }


    private class AsynGetIpAddress extends AsyncTask<Void, Void, Void> {
        String responseStr;


        @Override
        protected Void doInBackground(Void... params) {

            try {

                // Execute HTTP Post Request
                try {
                    URL myurl = new URL(APIUrlConstant.IP_ADDRESS_URL);
                    HttpsURLConnection con = (HttpsURLConnection) myurl.openConnection();
                    InputStream ins = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                    }

                    in.close();


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    ipAddressStr = "";

                } catch (UnsupportedEncodingException e) {

                    ipAddressStr = "";

                } catch (IOException e) {
                    ipAddressStr = "";

                }
                if (responseStr != null) {
                    Object json = new JSONTokener(responseStr).nextValue();
                    if (json instanceof JSONObject) {
                        ipAddressStr = ((JSONObject) json).getString("ip");

                    }
                }

            } catch (Exception e) {
                ipAddressStr = "";
            }
            return null;
        }


        protected void onPostExecute(Void result) {

            if (responseStr == null) {
                ipAddressStr = "";
            }
            return;
        }

        protected void onPreExecute() {

        }
    }


    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 1000);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {

            if (emVideoView.getCurrentPosition() % 2 == 0)
                BufferBandWidth();

            seekBarProgress = emVideoView.getCurrentPosition();
            current_played_length = emVideoView.getCurrentPosition();
            seekBar.setProgress(emVideoView.getCurrentPosition());
            seekBar.setMax(emVideoView.getDuration());
            Calcute_Currenttime_With_TotalTime();
            mHandler.postDelayed(this, 1000);

            if (content_types_id != LIVE_STREAM) {
                showCurrentTime();
            }

            current_matching_time = emVideoView.getCurrentPosition();

            if ((previous_matching_time == current_matching_time) && (current_matching_time < emVideoView.getDuration())) {
                ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.VISIBLE);

                primary_ll.setVisibility(View.GONE);
                last_ll.setVisibility(View.GONE);
                center_play_pause.setVisibility(View.GONE);
                latest_center_play_pause.setVisibility(View.GONE);
                current_time.setVisibility(View.GONE);
                subtitle_change_btn.setVisibility(View.GONE);
                mediaRouteButton.setVisibility(View.INVISIBLE);
            }

            else if(content_types_id == LIVE_STREAM && (previous_matching_time == current_matching_time)){
                ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.VISIBLE);

                primary_ll.setVisibility(View.GONE);
                last_ll.setVisibility(View.GONE);
                center_play_pause.setVisibility(View.GONE);
                latest_center_play_pause.setVisibility(View.GONE);
                current_time.setVisibility(View.GONE);
                subtitle_change_btn.setVisibility(View.GONE);
                mediaRouteButton.setVisibility(View.INVISIBLE);
            }
            else {

                if (content_types_id == LIVE_STREAM) {

                } else {
                    if (current_matching_time >= emVideoView.getDuration()) {
                        mHandler.removeCallbacks(updateTimeTask);

                        CallBufferLog();
                        seekBar.setProgress(0);
                        current_time.setText("00:00:00");
                        total_time.setText("00:00:00");
                        previous_matching_time = 0;
                        current_matching_time = 0;
                        video_completed = true;

                        /**ad **/
                        if (playerModel.getAdNetworkId() == 3) {
                            if (mAdsLoader != null) {
                                mAdsLoader.contentComplete();
                            }
                        } else {
                            backCalled();
                        }
                        /**ad **/
                    }
                }


                previous_matching_time = current_matching_time;
                ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);

                /**ad **/

                if (playerModel.getMidRoll() == 1) {
                    if (adDetails != null && adDetails.length > 0) {
                        for (int i = 0; i < adDetails.length; i++) {

                            if ((int) (TimeUnit.MILLISECONDS.toSeconds(emVideoView.getCurrentPosition())) > 0 && ((int) (TimeUnit.MILLISECONDS.toSeconds(emVideoView.getCurrentPosition())) == Integer.parseInt(adDetails[i]))) {

                                if (Util.checkNetwork(PlayerActivity.this)) {
                                    Util.call_finish_at_onUserLeaveHint = false;
                                    Util.hide_pause = true;
                                    ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
                                    latest_center_play_pause.setVisibility(View.VISIBLE);


                                    if (emVideoView.isPlaying()) {
                                        emVideoView.pause();
                                        latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                                        center_play_pause.setImageResource(R.drawable.ic_media_play);
                                        mHandler.removeCallbacks(updateTimeTask);

                                    }

                                    if (center_pause_paly_timer_is_running) {
                                        center_pause_paly_timer.cancel();
                                        center_pause_paly_timer_is_running = false;
                                        Log.v("BIBHU11", "CastAndCrewActivity End_Timer cancel called");


                                        subtitle_change_btn.setVisibility(View.GONE);
                                        primary_ll.setVisibility(View.GONE);
                                        last_ll.setVisibility(View.GONE);
                                        center_play_pause.setVisibility(View.GONE);
                                        current_time.setVisibility(View.GONE);
                                    }


                                    /**ad **/

                                    Intent adIntent = new Intent(PlayerActivity.this, AdPlayerActivity.class);
                                    adIntent.putExtra("fromAd", "fromAd");
                                    adIntent.putExtra("PlayerModel", playerModel);
                                    adIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                    startActivityForResult(adIntent, ADD_REQUEST_CODE);
                                    /**ad **/

                                } else {
                                    Toast.makeText(getApplicationContext(), Util.getTextofLanguage(PlayerActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                                }
                                break;
                            }
                        }
                    }
                }

                /**ad **/
            }

        }
    };

    public void Calcute_Currenttime_With_TotalTime() {
        TotalTime = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(emVideoView.getDuration()),
                TimeUnit.MILLISECONDS.toMinutes(emVideoView.getDuration()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(emVideoView.getDuration())),
                TimeUnit.MILLISECONDS.toSeconds(emVideoView.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(emVideoView.getDuration())));

        Current_Time = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(emVideoView.getCurrentPosition()),
                TimeUnit.MILLISECONDS.toMinutes(emVideoView.getCurrentPosition()) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(emVideoView.getCurrentPosition())),
                TimeUnit.MILLISECONDS.toSeconds(emVideoView.getCurrentPosition()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(emVideoView.getCurrentPosition())));

        total_time.setText(TotalTime);
        current_time.setText(Current_Time);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (asynGetIpAddress != null) {
            asynGetIpAddress.cancel(true);
        }

        if (progressView != null && progressView.isShown()) {
            progressView = null;
        }
        if (timer != null) {
            stoptimertask();
            timer = null;
        }

        if (video_completed == false) {

            CallVideoLog(ASYNC_RESUME_VODEOLOG_DETAILS);
            return;
        }
        mHandler.removeCallbacks(updateTimeTask);
        if (emVideoView != null) {
            emVideoView.release();
        }
        finish();
        overridePendingTransition(0, 0);
    }

    public void backCalled() {

        if (asynGetIpAddress != null) {
            asynGetIpAddress.cancel(true);
        }

        if (progressView != null && progressView.isShown()) {
            progressView = null;
        }
        if (timer != null) {
            stoptimertask();
            timer = null;
        }
        CallVideoLog(ASYNC_RESUME_VODEOLOG_DETAILS);
        return;
      /*  if (video_completed == false){

          CallVideoLog(ASYNC_RESUME_VODEOLOG_DETAILS);
            return;
        }*//*else{
            watchStatus = "com"
            CallVideoLog(ASYNC_VODEOLOG_DETAILS);
        }*//*
        mHandler.removeCallbacks(updateTimeTask);
        if (emVideoView!=null) {
            emVideoView.release();
        }
        finish();
        overridePendingTransition(0, 0);*/
    }

    /* public void onBackPressed() {
         super.onBackPressed();
         Log.v("SUBHA","HHVID"+videoLogId);
         if (asynGetIpAddress!=null){
             asynGetIpAddress.cancel(true);
         }
         if (asyncVideoLogDetails!=null){
             asyncVideoLogDetails.cancel(true);
         }
         if (asyncFFVideoLogDetails!=null){
             asyncFFVideoLogDetails.cancel(true);
         }
         if (progressView!=null && progressView.isShown()){
             progressView = null;
         }
         if (timer!=null){
             stoptimertask();
             timer = null;
         }
         mHandler.removeCallbacks(updateTimeTask);
         if (emVideoView!=null) {
             emVideoView.release();
         }
         finish();
         overridePendingTransition(0, 0);
     }*/
    @Override
    protected void onUserLeaveHint() {

        //if (played_length <= 0) {
        if (asynGetIpAddress != null) {
            asynGetIpAddress.cancel(true);
        }

        if (progressView != null && progressView.isShown()) {
            progressView = null;
        }
        if (timer != null && Util.call_finish_at_onUserLeaveHint) {
            stoptimertask();
            timer = null;
        }

        if ( playerModel.isstreaming_restricted() && Util.call_finish_at_onUserLeaveHint) {

            CallVideoLog(ASYNC_RESUME_VODEOLOG_DETAILS_HOME_CLICKED);

        }

        if (Util.call_finish_at_onUserLeaveHint) {

            Log.v("BIBHU6", "finish activity");

            Util.call_finish_at_onUserLeaveHint = true;

            mHandler.removeCallbacks(updateTimeTask);
            if (emVideoView != null) {
                emVideoView.release();
            }

            finish();
            overridePendingTransition(0, 0);
            super.onUserLeaveHint();
        }

    }

    /**
     * Responsible for play & pause the video .
     */
    public void Execute_Pause_Play() {

        /**
         * Responsible for play & pause the video in chromecast.
         */
        if (mCastSession != null && mCastSession.isConnected()) {
            if (remoteMediaClient.isPlaying()) {
                remoteMediaClient.pause();
                return;
            }
            if (remoteMediaClient.isPaused()) {
                remoteMediaClient.play();
                return;
            }
        }


        /**
         * Responsible for play & pause the video in player.
         */
        if (emVideoView.isPlaying()) {
            emVideoView.pause();
            latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
            center_play_pause.setImageResource(R.drawable.ic_media_play);
            mHandler.removeCallbacks(updateTimeTask);
        } else {
            if (video_completed) {

                if (content_types_id != LIVE_STREAM) {
                    backCalled();
                }

            } else {
                PreviousUsedDataByApp(false);
                emVideoView.start();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);
                mHandler.removeCallbacks(updateTimeTask);
                updateProgressBar();
            }

        }
    }



    /**
     * Handling visiblity or hiding player control during touching on player.
     */
    public void Start_Timer() {

        End_Timer();
        center_pause_paly_timer = new Timer();
        center_pause_paly_timer_is_running = true;
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                //perform your action here

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        current_time.setVisibility(View.GONE);
                        center_play_pause.setVisibility(View.GONE);
                        latest_center_play_pause.setVisibility(View.GONE);
                        End_Timer();
                    }
                });
            }
        };
        center_pause_paly_timer.schedule(timerTaskObj, 2000, 2000);
    }

    public void End_Timer() {
        if (center_pause_paly_timer_is_running) {
            center_pause_paly_timer.cancel();
            center_pause_paly_timer_is_running = false;

            subtitle_change_btn.setVisibility(View.GONE);
            mediaRouteButton.setVisibility(View.INVISIBLE);

            primary_ll.setVisibility(View.GONE);
            last_ll.setVisibility(View.GONE);
            center_play_pause.setVisibility(View.GONE);
            latest_center_play_pause.setVisibility(View.GONE);
            current_time.setVisibility(View.GONE);
        }

    }


    /**
     * Cancelling Start_Timer.
     */
    public void Instant_End_Timer() {
        if (center_pause_paly_timer_is_running) {
            center_pause_paly_timer.cancel();
            center_pause_paly_timer_is_running = false;
        }
    }

    public void showCurrentTime() {
        current_time.setText(Current_Time);
        current_time_position_timer();
    }


    public void current_time_position_timer() {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content_types_id != LIVE_STREAM) {

                            current_time.setText(Current_Time);
                            double pourcent = seekBar.getProgress() / (double) seekBar.getMax();
                            int offset = seekBar.getThumbOffset();
                            int seekWidth = seekBar.getWidth();
                            int val = (int) Math.round(pourcent * (seekWidth - 2 * offset));
                            int labelWidth = current_time.getWidth();
                            current_time.setX(offset + seekBar.getX() + val
                                    - Math.round(pourcent * offset)
                                    - Math.round(pourcent * labelWidth / 2));
                            timer.cancel();
                        }
                    }
                });
            }
        }, 0, 100);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            Util.call_finish_at_onUserLeaveHint = true;

            if (isDrm) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(PlayerActivity.this)) {
                    List_Of_Resolution_Format.clear();
                    List_Of_FileSize.clear();
                    List_Of_Resolution_Url.clear();
                    List_Of_Resolution_Url_Used_For_Download.clear();


                    asynWithdrm = new AsynWithdrm();
                    asynWithdrm.executeOnExecutor(threadPoolExecutor);

                    if ((playerModel.getOfflineSubtitleUrl().size() > 0) && (playerModel.getOfflineSubtitleUrl().size() == playerModel.getOfflineSubtitleLanguage().size())) {
                        Download_SubTitle(playerModel.getOfflineSubtitleUrl().get(0));
                    }
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(PlayerActivity.this)) {
                    {
                        List_Of_Resolution_Format.clear();
                        List_Of_FileSize.clear();
                        List_Of_Resolution_Url.clear();
                        List_Of_Resolution_Url_Used_For_Download.clear();


                        if (playerModel.getNonDrmDownloadUrlList().size() > 0) {
                            for (int i = 0; i < playerModel.getNonDrmDownloadUrlList().size(); i++) {
                                List_Of_Resolution_Url.add(playerModel.getNonDrmDownloadUrlList().get(i));
                                List_Of_Resolution_Url_Used_For_Download.add(playerModel.getNonDrmDownloadUrlList().get(i));
                                List_Of_Resolution_Format.add(playerModel.getNonDrmDownloadFormatList().get(i));
                            }

                            /*Collections.reverse(List_Of_Resolution_Format);
                            Collections.reverse(List_Of_Resolution_Url);
                            Collections.reverse(List_Of_Resolution_Url_Used_For_Download);*/

                            pDialog_for_gettig_filesize = new ProgressBarHandler(PlayerActivity.this);
                            pDialog_for_gettig_filesize.show();

                            new DetectDownloadingFileSize().execute();
                        } else {
                            new DownloadFileFromURL().execute(playerModel.getVideoUrl());
                        }


                        if ((playerModel.getOfflineSubtitleUrl().size() > 0) && (playerModel.getOfflineSubtitleUrl().size() == playerModel.getOfflineSubtitleLanguage().size())) {
                            Download_SubTitle(playerModel.getOfflineSubtitleUrl().get(0));
                        }

                    }
                }
            }


        }



        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_REQUEST_CODE) {
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                latest_center_play_pause.setVisibility(View.GONE);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);

                Util.call_finish_at_onUserLeaveHint = true;
                watchStatus = "halfplay";
                emVideoView.start();
                seekBar.setProgress(emVideoView.getCurrentPosition());
                updateProgressBar();


            }
            if (requestCode == SECOND_ADD_REQUEST_CODE) {
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                latest_center_play_pause.setVisibility(View.GONE);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);
                Util.call_finish_at_onUserLeaveHint = true;
                watchStatus = "start";
                emVideoView.start();
                seekBar.setProgress(emVideoView.getCurrentPosition());
                updateProgressBar();

            }



            if (requestCode == RESUME_VIDEO_REQUEST_CODE) {

                Util.call_finish_at_onUserLeaveHint = true;
                if (SubTitlePath.size() > 0) {
                    Util.DefaultSubtitle =SubTitleName.get(0).trim();
                }

                if (data.getStringExtra("yes").equals("1002")) {

                    watchStatus = "halfplay";
                    playerPosition = playerModel.getPlayPos();
                    player_start_time = playerPosition;
                    PreviousUsedDataByApp(false);
                    emVideoView.start();
                    emVideoView.seekTo(played_length);
                    seekBar.setProgress(played_length);
                    updateProgressBar();

                } else {
                    if (playerModel.getPreRoll() == 1 && playerModel.getAdNetworkId() == 1) {
                        if (Util.checkNetwork(PlayerActivity.this)) {
                            Util.call_finish_at_onUserLeaveHint = false;
                            Util.hide_pause = true;
                            ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
                            latest_center_play_pause.setVisibility(View.VISIBLE);


                            if (emVideoView.isPlaying()) {
                                emVideoView.pause();
                                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                                center_play_pause.setImageResource(R.drawable.ic_media_play);
                                mHandler.removeCallbacks(updateTimeTask);

                            }

                            if (center_pause_paly_timer_is_running) {
                                center_pause_paly_timer.cancel();
                                center_pause_paly_timer_is_running = false;
                                Log.v("BIBHU11", "CastAndCrewActivity End_Timer cancel called");


                                subtitle_change_btn.setVisibility(View.GONE);
                                primary_ll.setVisibility(View.GONE);
                                last_ll.setVisibility(View.GONE);
                                center_play_pause.setVisibility(View.GONE);
                                current_time.setVisibility(View.GONE);
                            }
                            /**ad **/

                            Intent adIntent = new Intent(PlayerActivity.this, AdPlayerActivity.class);
                            adIntent.putExtra("fromAd", "fromAd");
                            adIntent.putExtra("PlayerModel", playerModel);
                            adIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            startActivityForResult(adIntent, SECOND_ADD_REQUEST_CODE);
                            /**ad **/
                        } else {
                            Toast.makeText(getApplicationContext(), Util.getTextofLanguage(PlayerActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        emVideoView.start();
                        seekBar.setProgress(emVideoView.getCurrentPosition());
                        updateProgressBar();
                    }
                }
                if (SubTitlePath.size() > 0) {
                    CheckSubTitleParsingType("1");
                    subtitleDisplayHandler = new Handler();
                    subsFetchTask = new SubtitleProcessingTask("1");
                    subsFetchTask.execute();
                } else {
                    CallVideoLog(ASYNC_VODEOLOG_DETAILS);
                }
            }
            if (requestCode == SUBTITLE_REQUEST_CODE) {
                if(data.getStringExtra("type").equals("resolution"))
                {
                        mHandler.removeCallbacks(updateTimeTask);
                        if (!data.getStringExtra("position").equals("nothing")) {

                            if (!emVideoView.isPlaying()) {
                                is_paused = true;
                            }
                            change_resolution = true;
                            ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.VISIBLE);
                            emVideoView.setVideoURI(Uri.parse(ResolutionUrl.get(Integer.parseInt(data.getStringExtra("position")))));
                    }

                }else
                {
                    if (mCastSession != null && mCastSession.isConnected()) {
                        Util.call_finish_at_onUserLeaveHint = false;
                    }

                    if (!data.getStringExtra("position").equals("nothing")) {

                        if (data.getStringExtra("position").equals("0")) {
                            if (subtitleDisplayHandler != null)
                                subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
                            subtitleText.setText("");
                            active_track_index = "";


                            /**
                             * check chromecast is connected or not , if connected then remove the active track id
                             */

                            if (mCastSession != null && mCastSession.isConnected()) {

                                subtitleText.setText("");
                                remoteMediaClient.setActiveMediaTracks(new long[]{}).setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
                                    @Override
                                    public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                                        if (!mediaChannelResult.getStatus().isSuccess()) {
                                            Log.v("SUBHA", "Failed with status code:" +
                                                    mediaChannelResult.getStatus().getStatusCode());
                                            Toast.makeText(getApplicationContext(), "failed to off subtitle", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }


                        } else {
                            try {
                                CheckSubTitleParsingType(data.getStringExtra("position"));

                                subtitleDisplayHandler = new Handler();
                                subsFetchTask = new SubtitleProcessingTask(data.getStringExtra("position"));
                                subsFetchTask.execute();

                                active_track_index = (Integer.parseInt(data.getStringExtra("position")) - 1) + "";
                                int id = Integer.parseInt(active_track_index);

                                Log.v(TAG, " trackid===========" + id);

                                /**
                                 * check chromecast is connected or not , if connected then remove the active track id
                                 */

                                if (mCastSession != null && mCastSession.isConnected()) {
                                    subtitleText.setText("");
                                    remoteMediaClient.setActiveMediaTracks(new long[]{id}).setResultCallback(new ResultCallback<RemoteMediaClient.MediaChannelResult>() {
                                        @Override
                                        public void onResult(@NonNull RemoteMediaClient.MediaChannelResult mediaChannelResult) {
                                            if (!mediaChannelResult.getStatus().isSuccess()) {
                                                Log.v("SUBHA", "Failed with status code:" +
                                                        mediaChannelResult.getStatus().getStatusCode());
                                                Toast.makeText(getApplicationContext(), "failed to set subtitle", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }


                            } catch (Exception e) {
                            }

                        }

                    }
                }

            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (MovableTimer != null)
            MovableTimer.cancel();

        try{
            mCastContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
        }catch (Exception e){}

        Util.app_is_in_player_context = false;
        Util.hide_pause = false;


        if ( playerModel.isstreaming_restricted() && Util.Call_API_For_Close_Streming) {

            Util.Call_API_For_Close_Streming = false;
            if (!video_completed_at_chromecast) {
                CallVideoLog(ASYNC_RESUME_VODEOLOG_DETAILS);
            }
        }


        //to destroy the registerrecever
        // TODO Auto-generated method stub

        try {
            if (SelectedUrl != null)
                unregisterReceiver(SelectedUrl);
        } catch (Exception e) {

        }
    }

    /**
     *   Added Later For Subtitle Feature.
     */

    public class SubtitleProcessingTask extends AsyncTask<Void, Void, Void> {


        String Subtitle_Path = "";

        public SubtitleProcessingTask(String path) {

            Subtitle_Path = SubTitlePath.get((Integer.parseInt(path) - 1));
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // int count;
            try {

                File myFile = new File(Subtitle_Path);
                InputStream fIn = new FileInputStream(String.valueOf(myFile));

                if (callWithoutCaption) {
                    Log.v("BIBHU1", "SubTitlePath==============callWithoutCaption");
                    FormatSRT_WithoutCaption formatSRT = new FormatSRT_WithoutCaption();
                    srt = formatSRT.parseFile("sample", fIn);
                } else {
                    Log.v("BIBHU1", "SubTitlePath==============callWithCaption");

                    FormatSRT formatSRT = new FormatSRT();
                    srt = formatSRT.parseFile("sample", fIn);
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != srt) {
                subtitleText.setText("");
                subtitleDisplayHandler.post(subtitleProcessesor);
            }

            CallVideoLog(ASYNC_VODEOLOG_DETAILS);

            super.onPostExecute(result);
        }
    }

    public void onTimedText(Caption text) {
        if (text == null) {
            subtitleText.setVisibility(View.INVISIBLE);
            return;
        }

        Typeface videoGenreTextViewTypeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_regular));
        subtitleText.setTypeface(videoGenreTextViewTypeface);
        subtitleText.setText(Html.fromHtml(text.content));
        subtitleText.setVisibility(View.VISIBLE);

    }

    @Override
    public void finish() {
        cleanUp();
        super.finish();
    }

    private void cleanUp() {
        if (subtitleDisplayHandler != null) {
            subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
        }

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        castButtonClicked = false;
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (castButtonClicked) {
            handleCastAndCrew();
            castButtonClicked = false;
        }
    }



        @Override
    protected void onPause() {

        if(CheckAvailabilityOfChromecast!=null)
            CheckAvailabilityOfChromecast.cancel();

        Log.v("PINTU", "onPause called");
        super.onPause();
    }

    private Runnable subtitleProcessesor = new Runnable() {

        @Override
        public void run() {
            if (emVideoView != null && emVideoView.isPlaying()) {
                int currentPos = emVideoView.getCurrentPosition();
                Collection<Caption> subtitles = srt.captions.values();
                for (Caption caption : subtitles) {
                    if (currentPos >= caption.start.mseconds
                            && currentPos <= caption.end.mseconds) {
                        onTimedText(caption);
                        break;
                    } else if (currentPos > caption.end.mseconds) {
                        onTimedText(null);
                    }
                }
            }
            subtitleDisplayHandler.postDelayed(this, 100);
        }
    };

    public void CheckSubTitleParsingType(String path) {

        String Subtitle_Path = SubTitlePath.get((Integer.parseInt(path) - 1));


        callWithoutCaption = true;

        File myFile = new File(Subtitle_Path);
        BufferedReader test_br = null;
        InputStream stream = null;
        InputStreamReader in = null;
        try {
            stream = new FileInputStream(String.valueOf(myFile));
            in = new InputStreamReader(stream);
            test_br = new BufferedReader(in);

        } catch (Exception e) {
            e.printStackTrace();
        }

        int testinglinecounter = 1;
        int captionNumber = 1;


        String TestingLine = null;
        try {
            TestingLine = test_br.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (testinglinecounter < 6) {
            try {

                if (Integer.parseInt(TestingLine.toString().trim()) == captionNumber) {
                    callWithoutCaption = false;
                    testinglinecounter = 6;
                }
            } catch (Exception e) {
                try {
                    TestingLine = test_br.readLine();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
                testinglinecounter++;
            }
        }
    }

    private void hideSystemUI() {

        try{
            story.setText("");
            // Set the IMMERSIVE flag.
            // Set the content to appear under the system bars so that the content
            // doesn't resize when the system bars hide and show.
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                            | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }catch(Exception e){}


    }

    private void showSystemUI() {

        try{
            story.setText(playerModel.getVideoStory());
            ResizableCustomView.doResizeTextView(PlayerActivity.this, story, MAX_LINES, Util.getTextofLanguage(PlayerActivity.this,Util.VIEW_MORE,Util.DEFAULT_VIEW_MORE), true);

            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE

            );
        }catch(Exception e){}

    }


    /**
     * Added for Bandwidth Log .
     */


    public void BufferBandWidth() {
        DataAsynTask dataAsynTask = new DataAsynTask();
        dataAsynTask.execute();
    }

    private class DataAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {


            try {
                long total = 0;
                PackageManager pm = getPackageManager();
                List<PackageInfo> listPackages = pm.getInstalledPackages(0);
                for (PackageInfo pi : listPackages) {
                    String appName = (String) pi.applicationInfo.loadLabel(pm);
                    if (appName != null && appName.trim().equals(playerModel.getAppName())) {
                        int uid = pi.applicationInfo.uid;
                        total = (TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid)) / 1024;

                        CurrentUsedData = total - PreviousUsedData;
                        CurrentUsedData = CurrentUsedData - (DataUsedByDownloadContent() - PreviousUsedData_By_DownloadContent);
                    }
                }
            } catch (Exception e) {

            }

            return null;
        }
    }

    public void PreviousUsedDataByApp(boolean status) {

        try {

            long prev_data = 0;
            PackageManager pm = getPackageManager();
            List<PackageInfo> listPackages = pm.getInstalledPackages(0);
            for (PackageInfo pi : listPackages) {
                String appName = (String) pi.applicationInfo.loadLabel(pm);
                if (appName != null && appName.trim().equals(playerModel.getAppName())) {
                    int uid = pi.applicationInfo.uid;
                    prev_data = (TrafficStats.getUidRxBytes(uid) + TrafficStats.getUidTxBytes(uid)) / 1024;

                    if (status) {
                        PreviousUsedData = prev_data;
                    } else {
                        PreviousUsedData = ((prev_data - PreviousUsedData) - CurrentUsedData) + PreviousUsedData;
                    }
                }
            }

        } catch (Exception e) {
        }

    }

    public long DataUsedByDownloadContent() {

        try {

            SQLiteDatabase DB = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = DB.rawQuery("SELECT " + DBHelper.COLUMN_DOWNLOADID + " FROM " + DBHelper.TABLE_NAME + " ", null);
            int count = cursor.getCount();

            long Total = 0;

            if (count > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        DownloadManager downloadManager1 = (DownloadManager) PlayerActivity.this.getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Query download_id_query = new DownloadManager.Query();
                        download_id_query.setFilterById(Long.parseLong(cursor.getString(0).trim())); //filter by id which you have receieved when reqesting download from download manager
                        Cursor id_cursor = downloadManager1.query(download_id_query);


                        if (id_cursor != null && id_cursor.getCount() > 0) {
                            if (id_cursor.moveToFirst()) {
                                int columnIndex = id_cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                int status = id_cursor.getInt(columnIndex);

                                int sizeIndex = id_cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                                int downloadedIndex = id_cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                                long size = id_cursor.getInt(sizeIndex);
                                long downloaded = id_cursor.getInt(downloadedIndex);

                                Total = Total + downloaded / 1024;
                            }
                        }
                        Log.v("BIBHU11", "  TotalUsedData Download size============" + Total + "KB");

                    } while (cursor.moveToNext());
                }
                return Total;
            } else {
                return Total;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    private class AsynWithdrm extends AsyncTask<Void, Void, Void> {
        ProgressBarHandler pDialog;
        int responseCode;
        String responseStr;

        @Override
        protected Void doInBackground(Void... params) {


            String urlRouteList = rootUrl.trim() + Util.morlineBB.trim();

            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", authToken);
                httppost.addHeader("stream_unique_id", playerModel.getStreamUniqueId());

                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());


                } catch (IOException e) {
                    e.printStackTrace();
                }
                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);

                    responseCode = Integer.parseInt(myJson.optString("code"));
                }
                JSONObject mainJson = null;
                if (responseCode >= 0) {
                    if (responseCode == 200) {
                        Log.v("SUBHA", "" + responseCode);
                        mainJson = myJson.getJSONObject("data");

                        if ((mainJson.has("file")) && mainJson.getString("file").trim() != null && !mainJson.getString("file").trim().isEmpty() && !mainJson.getString("file").trim().equals("null") && !mainJson.getString("file").trim().matches("")) {
                            mlvfile = mainJson.getString("file");

                            Log.v("SUBHA", mlvfile);
                        } else {
                            mlvfile = Util.getTextofLanguage(PlayerActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
                        }

                        if ((mainJson.has("token")) && mainJson.getString("token").trim() != null && !mainJson.getString("token").trim().isEmpty() && !mainJson.getString("token").trim().equals("null") && !mainJson.getString("token").trim().matches("")) {
                            token = mainJson.getString("token");
                            Log.v("SUBHA", "token" + token);

                        } else {
                            token = Util.getTextofLanguage(PlayerActivity.this, Util.NO_DATA, Util.DEFAULT_NO_DATA);
                        }


                        if ((mainJson.has("multiple_resolution")) && mainJson.getString("multiple_resolution").trim() != null && !mainJson.getString("multiple_resolution").trim().isEmpty() && !mainJson.getString("multiple_resolution").trim().equals("null") && !mainJson.getString("multiple_resolution").trim().matches("")) {
                            JSONArray jsonArray = mainJson.optJSONArray("multiple_resolution");

                            for (int i = 0; i < jsonArray.length(); i++) {

                                if (jsonArray.getJSONObject(i).optString("resolution").trim().contains("BEST"))
                                    List_Of_Resolution_Format.add(jsonArray.getJSONObject(i).optString("resolution"));
                                else
                                    List_Of_Resolution_Format.add(jsonArray.getJSONObject(i).optString("resolution") + "p");

                                List_Of_Resolution_Url.add(jsonArray.getJSONObject(i).optString("url"));
                                List_Of_Resolution_Url_Used_For_Download.add(jsonArray.getJSONObject(i).optString("url"));

                                Log.v("BIBHU1", "resolution = " + jsonArray.getJSONObject(i).optString("resolution"));
                                Log.v("BIBHU1", "url = " + jsonArray.getJSONObject(i).optString("url"));
                            }

                            Collections.reverse(List_Of_Resolution_Format);
                            Collections.reverse(List_Of_Resolution_Url);
                            Collections.reverse(List_Of_Resolution_Url_Used_For_Download);

                        }
                        //=======================End====================//

                    }
                } else {
                    responseStr = "0";
                }

            } catch (Exception e) {
                responseCode = 0;
            }

            int count;
            InputStream is = new ByteArrayInputStream(token.getBytes());
            InputStream inputs = new BufferedInputStream(is, 8192);
            Log.v("SUBHA", "pathh" + token);
            File root = Environment.getExternalStorageDirectory();
            mediaStorageDir = new File(root + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/TOKEN", "");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("App", "failed to create directory");
                }
            }

            licensetoken = mediaStorageDir.getAbsolutePath() + "/" + System.currentTimeMillis() + ".xml";
            OutputStream output = null;
            try {
                output = new FileOutputStream(licensetoken);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.v("SUBHA", "pathh" + licensetoken);
            byte data[] = new byte[1024];

            long total = 0;

            try {
                while ((count = inputs.read(data)) != -1) {
                    total += count;
                    Log.v("SUBHA", "Lrngth" + total);

                    output.write(data, 0, count);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                output.flush();
                output.close();


                inputs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        protected void onPostExecute(Void result) {
            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                    pDialog = null;

                }
                Log.d("sanji", playerModel.getStreamUniqueId());


                //PlayerActivity.this portion is changed later because of multiple download option.

                if (List_Of_Resolution_Url.size() > 0) {
                    pDialog_for_gettig_filesize = new ProgressBarHandler(PlayerActivity.this);
                    pDialog_for_gettig_filesize.show();

                    new DetectDownloadingFileSize().execute();
                } else {
                    new DownloadFileFromURL().execute(mlvfile);

                }


            } catch (IllegalArgumentException ex) {
                Toast.makeText(PlayerActivity.this, Util.getTextofLanguage(PlayerActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

            }


        }

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(PlayerActivity.this);
            pDialog.show();


        }
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        ProgressBarHandler pDialog;
        int responseCode;
        String responseStr;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressBarHandler(PlayerActivity.this);
            pDialog.show();

        }

        /**
         * Downloading file in background thread
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(f_url[0]);
                HttpResponse execute = client.execute(httpGet);
                float size = calculateDownloadFileSize(execute);
                file_size = size;
                lengthfile = (int) size;


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            //===========

            return null;
        }


        @Override
        protected void onPostExecute(String file_url) {

            try {
                if (pDialog != null && pDialog.isShowing()) {
                    pDialog.hide();
                }


                String lengh = ""+file_size;

                if(lengh.toString().equals("0.0")){
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PlayerActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PlayerActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));
                    dlgAlert.setTitle(Util.getTextofLanguage(PlayerActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                    return;
                }

                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PlayerActivity.this, R.style.MyAlertDialogStyle);
                dlgAlert.setTitle(Util.getTextofLanguage(PlayerActivity.this, Util.WANT_TO_DOWNLOAD, Util.DEFAULT_WANT_TO_DOWNLOAD));
                dlgAlert.setMessage(playerModel.getVideoTitle() + " " + "(" + lengh + "MB)");
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this, Util.DOWNLOAD_BUTTON_TITLE, Util.DEFAULT_DOWNLOAD_BUTTON_TITLE), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this, Util.DOWNLOAD_BUTTON_TITLE, Util.DEFAULT_DOWNLOAD_BUTTON_TITLE),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                downloading = true;

                                int currentApiVersion = Build.VERSION.SDK_INT;
                                if (currentApiVersion >= Build.VERSION_CODES.M) {
                                    requestStoragePermission();
                                } else {
                                    downloadFile(true);
                                }

                            }
                        });
                dlgAlert.setNegativeButton(Util.getTextofLanguage(PlayerActivity.this, Util.CANCEL_BUTTON, Util.DEFAULT_CANCEL_BUTTON), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setNegativeButton(Util.getTextofLanguage(PlayerActivity.this, Util.CANCEL_BUTTON, Util.DEFAULT_CANCEL_BUTTON),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                            }
                        });
                dlgAlert.create().show();


            } catch (IllegalArgumentException ex) {
                Toast.makeText(PlayerActivity.this, Util.getTextofLanguage(PlayerActivity.this, Util.SIGN_OUT_ERROR, Util.DEFAULT_SIGN_OUT_ERROR), Toast.LENGTH_LONG).show();

            }


        }
    }

    public void checkDownLoadStatusFromDownloadManager1(final DownloadContentModel model, final boolean CallAccessPeriodApi) {

        if (model.getDOWNLOADID() != 0) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    downloading = true;
                    //  Util.downloadprogress=0;
                    int bytes_downloaded = 0;
                    int bytes_total = 0;
                    downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                    while (downloading) {


                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(model.getDOWNLOADID()); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = downloadManager.query(q);


                        if (cursor != null && cursor.getCount() > 0) {
                            if (cursor.moveToFirst()) {
                                int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                                int status = cursor.getInt(columnIndex);
                                if (status == DownloadManager.STATUS_SUCCESSFUL) {

                                    model.setDSTATUS(1);
                                    dbHelper.updateRecord(model);
                                    downloading = false;

                                    Intent intent = new Intent("NewVodeoAvailable");
                                    sendBroadcast(intent);

                                    SQLiteDatabase DB = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
                                    String query1 = "UPDATE " + DBHelper.DOWNLOAD_CONTENT_INFO + " SET download_status = '1'" +
                                            " WHERE email = '" + emailIdStr + "' AND download_contnet_id = '" + model.getDOWNLOADID() + "'";
                                    DB.execSQL(query1);

                                    if (isDrm && CallAccessPeriodApi) {
                                        try {
                                            String licenseAcquisitionToken = getActionTokenFromStorage(model.getToken());
                                            com.intertrust.wasabi.jni.Runtime.processServiceToken(licenseAcquisitionToken);

                                            EnumSet<PlaylistProxy.Flags> flags = EnumSet.noneOf(PlaylistProxy.Flags.class);
                                            playerProxy = new PlaylistProxy(flags, PlayerActivity.this, new Handler());
                                            playerProxy.start();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    if (CallAccessPeriodApi) {
                                        // Call API to get Access Period and Watch period of download content...
                                        new AsyncWatchAccessDetails().execute("" + model.getDOWNLOADID());
                                    }
                                    // Have to unComment


                                } else if (status == DownloadManager.STATUS_FAILED) {
                                    // 1. process for download fail.
                                    model.setDSTATUS(0);

                                    SQLiteDatabase DB = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
                                    String query1 = "UPDATE " + DBHelper.DOWNLOAD_CONTENT_INFO + " SET download_status = '0'" +
                                            " WHERE email = '" + emailIdStr + "' AND download_contnet_id = '" + model.getDOWNLOADID() + "'";
                                    DB.execSQL(query1);

                                } else if ((status == DownloadManager.STATUS_PAUSED) ||
                                        (status == DownloadManager.STATUS_RUNNING)) {
                                    model.setDSTATUS(2);

                                } else if (status == DownloadManager.STATUS_PENDING) {
                                    //Not handling now
                                }
                                int sizeIndex = cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
                                int downloadedIndex = cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
                                long size = cursor.getInt(sizeIndex);
                                long downloaded = cursor.getInt(downloadedIndex);
                                double progress = 0.0;
                                if (size != -1) progress = downloaded * 100.0 / size;
                                // At PlayerActivity.this point you have the progress as a percentage.
                                model.setProgress((int) progress);
                                //Util.downloadprogress=(int) progress;

                                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
//                                    downloading = false;
//                                    download_layout.setVisibility(View.GONE);
//                                    writefilepath();
//                                    String path=Environment.getExternalStorageDirectory() + "/WITHDRM/"+fname;
//                                    String fileNameWithOutExt = FilenameUtils.removeExtension(fname);
//                                    String path1 = Environment.getExternalStorageDirectory() + "/Android/data/"+getApplicationContext().getPackageName().trim()+"/WITHDRM/" + playerModel.getVideoTitle().trim() + "-1." + "mlv";
//                                    File file = new File(path1);
//                                    if (file != null && file.exists()) {
//                                        file.delete();
//                                    }

                                }


                            }
                        } else {
                            // model.setDSTATUS(3);
                        }


//

                        runOnUiThread(new Runnable() {
                            //
                            @Override
                            public void run() {


                                download.setVisibility(View.GONE);
                                percentg.setVisibility(View.VISIBLE);
                                Progress.setProgress(0);

                                Progress.setProgress((int) model.getProgress());
                                percentg.setText(model.getProgress() + "%");
//
                                if (model.getProgress() == 100) {

                                    //writefilepath();
//                                dbHelper.deleteRecord(audio);
                                    download_layout.setVisibility(View.GONE);
                                }

                            }
                        });

                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }


                }
            }).start();


        }

    }


    private void downloadFile(boolean singlefile) {

        DownloadManager.Request request;
        if (singlefile) {
            selected_download_format = 0;
            if (isDrm)
                request = new DownloadManager.Request(Uri.parse(mlvfile));
            else
                request = new DownloadManager.Request(Uri.parse(playerModel.getVideoUrl()));
        } else {
            if (isDrm)
                request = new DownloadManager.Request(Uri.parse(List_Of_Resolution_Url_Used_For_Download.get(selected_download_format)));
            else
                request = new DownloadManager.Request(Uri.parse(List_Of_Resolution_Url_Used_For_Download.get(selected_download_format)));
            selected_download_format = 0;
        }


        request.setTitle(playerModel.getVideoTitle());
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        String timestamp = "";
        if (isDrm) {
            //Get download file name
            fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(mlvfile);
            timestamp = System.currentTimeMillis() + ".mlv";
            //Save file to destination folder
            request.setDestinationInExternalPublicDir("Android/data/" + getApplicationContext().getPackageName().trim() + "/WITHDRM", timestamp);
        } else {
            //Get download file name
            fileExtenstion = MimeTypeMap.getFileExtensionFromUrl(playerModel.getVideoUrl());
            timestamp = System.currentTimeMillis() + ".exo";
            //Save file to destination folder
            request.setDestinationInExternalPublicDir("Android/data/" + getApplicationContext().getPackageName().trim() + "/WITHOUT_DRM", timestamp);

        }

        enqueue = downloadManager.enqueue(request);

        download.setVisibility(View.GONE);
        percentg.setVisibility(View.VISIBLE);
        Progress.setProgress(0);

        DownloadContentModel downloadContentModel = new DownloadContentModel();
        downloadContentModel.setMUVIID(playerModel.getVideoTitle()+"@@@"+playerModel.getStreamUniqueId());
        downloadContentModel.setDOWNLOADID((int) enqueue);
        downloadContentModel.setProgress(0);
        downloadContentModel.setUSERNAME(emailIdStr);
        downloadContentModel.setUniqueId(playerModel.getStreamUniqueId() + emailIdStr);
        downloadContentModel.setDSTATUS(2);
        downloadContentModel.setPoster(playerModel.getPosterImageId().trim());


        if (isDrm) {
            downloadContentModel.setToken(licensetoken);
            downloadContentModel.setPath(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/WITHDRM/" + timestamp);
        } else {
            downloadContentModel.setToken(fileExtenstion);
            downloadContentModel.setPath(Environment.getExternalStorageDirectory() + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/WITHOUT_DRM/" + timestamp);
        }

        downloadContentModel.setContentid(String.valueOf(playerModel.getContentTypesId()));
        downloadContentModel.setGenere(playerModel.getVideoGenre().trim());
        downloadContentModel.setMuviid(playerModel.getMovieUniqueId().trim());
        downloadContentModel.setDuration(playerModel.getVideoDuration().trim());

        if(isDrm)
            downloadContentModel.setDownloadContentType("1");
        else
            downloadContentModel.setDownloadContentType("0");


        dbHelper.insertRecord(downloadContentModel);




        audio = dbHelper.getContact(playerModel.getStreamUniqueId() + emailIdStr);
        if (audio != null) {
            if (audio.getUSERNAME().trim().equals(emailIdStr.trim())) {
                checkDownLoadStatusFromDownloadManager1(audio, true);
            }
        }


        SQLiteDatabase DB = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
        String query1 = "INSERT INTO " + DBHelper.DOWNLOAD_CONTENT_INFO + "(download_contnet_id,log_id,authtoken,email," +
                "ipaddress,movie_id,episode_id,device_type,download_status,server_sending_final_status) VALUES" +
                "('" + enqueue + "','0','" + authToken.trim() + "','" + emailIdStr.trim() + "','" + ipAddressStr + "'," +
                "'" + playerModel.getMovieUniqueId().trim() + "','" + playerModel.getStreamUniqueId().trim() + "'," +
                "'" + 2 + "','2','0')";

        DB.execSQL(query1);

        //---------------------- End ---------------------------------//

        // Send BroadCast to start sending Download content bandwidth .

        Intent intent = new Intent("BnadwidthLog");
        sendBroadcast(intent);

        //---------------------- End ---------------------------------//

        // Have to unComment

        // PlayerActivity.this code is only responsible for Access period and Watch Period feature on Download Contnet
        Cursor cursor = DB.rawQuery("SELECT * FROM " + DBHelper.WATCH_ACCESS_INFO + "" +
                " WHERE email = '" + emailIdStr.trim() + "' AND stream_unique_id = '" + playerModel.getStreamUniqueId() + "'", null);

        if (cursor.getCount() > 0) {
            String query = "UPDATE " + DBHelper.WATCH_ACCESS_INFO + " SET download_id = '" + enqueue + "' , " +
                    "stream_unique_id = '" + playerModel.getStreamUniqueId() + "',initial_played_time = '0'," +
                    "updated_server_current_time = '0' WHERE email = '" + emailIdStr.trim() + "' AND stream_unique_id = '" + playerModel.getStreamUniqueId() + "'";
            DB.execSQL(query);
            Log.v("BIBHU1234", "update called");

        } else {
            String query = "INSERT INTO " + DBHelper.WATCH_ACCESS_INFO + " (download_id , stream_unique_id , initial_played_time , updated_server_current_time,email) VALUES" +
                    " ('" + enqueue + "','" + playerModel.getStreamUniqueId() + "','0','0','" + emailIdStr.trim() + "')";
            DB.execSQL(query);
            Log.v("BIBHU1234", "insert called");

        }


        //=================================End=======================================================//


        // This code is responsible for resume watch feature in downloeded content.

        Cursor cursor1 = DB.rawQuery("SELECT * FROM "+DBHelper.RESUME_WATCH+" WHERE UniqueId = '"+playerModel.getStreamUniqueId()+ emailIdStr+"'", null);

        if(cursor1.getCount()>0)
        {
            String query = "UPDATE " + DBHelper.RESUME_WATCH+ " SET Flag='0' , PlayedDuration = '0',LatestMpdUrl = '',LicenceUrl=''  WHERE UniqueId = '"+playerModel.getStreamUniqueId()+ emailIdStr+"'";
            DB.execSQL(query);
            Log.v("BIBHU1234","resume watch update called");
        }
        else {
            String query = "INSERT INTO " + DBHelper.RESUME_WATCH + " (UniqueId , PlayedDuration,Flag,LicenceUrl,LatestMpdUrl) VALUES" +
                    " ('" + playerModel.getStreamUniqueId() + emailIdStr + "','0','0','','')";
            DB.execSQL(query);
            Log.v("BIBHU1234", "resume watch insert called");
            //=====================================End=======================================//

        }
    }

    private void requestStoragePermission() {
        if (ActivityCompat.checkSelfPermission(PlayerActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(PlayerActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_STORAGE);
        } else {

            if (List_Of_Resolution_Url_Used_For_Download.size() > 0) {
                downloadFile(false);
            } else {
                downloadFile(true);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

               /* if(List_Of_Resolution_Url_Used_For_Download.size()>0)
                {
                    downloadFile(false);
                }else
                {
                    downloadFile(true);
                }*/

            } else {
                Toast.makeText(PlayerActivity.this, Util.getTextofLanguage(PlayerActivity.this, Util.DOWNLOAD_INTERRUPTED, Util.DEFAULT_DOWNLOAD_INTERRUPTED), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void Download_SubTitle(String Url) {
        new DownloadFileFromURL_Offline().execute(Url);
    }

    class DownloadFileFromURL_Offline extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... f_url) {
            int count;


            try {
                URL url = new URL(f_url[0]);
                String str = f_url[0];
                filename = str.substring(str.lastIndexOf("/") + 1);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File root = Environment.getExternalStorageDirectory();
                mediaStorageDir1 = new File(root + "/Android/data/" + getApplicationContext().getPackageName().trim() + "/SubTitleList_Offline/", "");

                if (!mediaStorageDir1.exists()) {
                    if (!mediaStorageDir1.mkdirs()) {
                        Log.d("App", "failed to create directory");
                    }
                }

                Log.v("BIBHU3", "SubTitleName============ here called");

                SubtitleModel subtitleModel = new SubtitleModel();
                subtitleModel.setUID(playerModel.getStreamUniqueId() + emailIdStr);
                subtitleModel.setLanguage(playerModel.getOfflineSubtitleLanguage().get(0));
                String filename = mediaStorageDir1.getAbsolutePath() + "/" + System.currentTimeMillis() + ".vtt";
                subtitleModel.setPath(filename);

                Log.v("BIBHU3", "SubTitleName============" + filename);

                long rowId = dbHelper.insertRecordSubtittel(subtitleModel);
                Log.v("BIBHU3", "rowId============" + rowId + "sub id ::" + subtitleModel.getUID());

                playerModel.getOfflineSubtitleLanguage().remove(0);


                OutputStream output = new FileOutputStream(filename);

                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                Log.v("BIBHU3", "error===========" + e.toString());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
        }

        @Override
        protected void onPostExecute(String file_url) {

            playerModel.getOfflineSubtitleUrl().remove(0);
            if (playerModel.getOfflineSubtitleUrl().size() > 0) {
                Download_SubTitle(playerModel.getOfflineSubtitleUrl().get(0).trim());
            }

        }
    }

    /*****offline *****/


    // PlayerActivity.this added later for chromecast//
    private void setupCastListener() {
        mSessionManagerListener = new SessionManagerListener<CastSession>() {

            @Override
            public void onSessionEnded(CastSession session, int error) {

                onApplicationDisconnected();


                //mSessionManagerListener  = null;
                mCastSession = null;


                Util.call_finish_at_onUserLeaveHint = true;
                latest_center_play_pause.setEnabled(true);
                emVideoView.setEnabled(true);

                startTimer();
                PreviousUsedDataByApp(false);
                emVideoView.start();

                if (cast_disconnected_position != 0) {

                    Log.v("BIBHU2", "onSessionEnded===and video log called");

                    emVideoView.seekTo((int) cast_disconnected_position);
                    log_temp_id = "0";
                    player_start_time = millisecondsToString((int) cast_disconnected_position);
                    playerPosition = player_start_time;

                    // Call video log here

                    CallVideoLog(ASYNC_VODEOLOG_DETAILS);
                }

                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);
                latest_center_play_pause.setVisibility(View.GONE);
                mHandler.removeCallbacks(updateTimeTask);
                updateProgressBar();

            }


            @Override
            public void onSessionResumed(CastSession session, boolean wasSuspended) {
                onApplicationConnected(session);
                mCastSession = session;

                Log.v("BIBHU2", "onSessionResumed");
            }

            @Override
            public void onSessionResumeFailed(CastSession session, int error) {
                onApplicationDisconnected();

                Log.v("BIBHU2", "onSessionResumeFailed");
            }

            @Override
            public void onSessionStarted(CastSession session, String sessionId) {
                onApplicationConnected(session);
                mCastSession = session;
            }

            @Override
            public void onSessionStartFailed(CastSession session, int error) {
                onApplicationDisconnected();

                Log.v("BIBHU2", "onSessionStartFailed");
            }

            @Override
            public void onSessionStarting(CastSession session) {
                Log.v("BIBHU2", "onSessionStarting===cast connecting");
            }

            @Override
            public void onSessionEnding(CastSession session) {
                try {


                    cast_disconnected_position = session.getRemoteMediaClient().getApproximateStreamPosition();

                    if(isDrm){

                        DataUsedByChrmoeCast = Current_Sesion_DataUsedByChrmoeCast + DataUsedByChrmoeCast;
                        Current_Sesion_DataUsedByChrmoeCast = 0;
                    }

                    // PlayerActivity.this is done because , during cast ending receiver already closed the streaming restriction for PlayerActivity.this user , so we have to
                    // satrt a new streaming restriction at sender end.
                    restrict_stream_id = "0";

                    Log.v("BIBHU3", "onSessionEnding===================" + cast_disconnected_position);
                    Log.v("BIBHU3", "onSessionEnding DataUsedByChrmoeCast===================" + DataUsedByChrmoeCast);
                }
                catch (Exception e){

                }
            }

            @Override
            public void onSessionResuming(CastSession session, String sessionId) {
            }

            @Override
            public void onSessionSuspended(CastSession session, int reason) {

                Log.v("BIBHU3", "onSessionSuspended===cast disconnected");
            }

            private void onApplicationConnected(final CastSession castSession) {

                //================================================================================//

              /*  String x = "{'msg':'Reply from Bibhu Prasad'}";
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(x);
                }catch (Exception e){}

                castSession.sendMessage("urn:x-cast:muvi.mcrt.final",jsonObject.toString());*/

                try {
                    castSession.setMessageReceivedCallbacks("urn:x-cast:muvi.mcrt.final", new Cast.MessageReceivedCallback() {
                        @Override
                        public void onMessageReceived(CastDevice castDevice, String s, String s1) {
                            Log.v(TAG, "onMessageReceived Message from receiver=" + s1);


                            if (s1.contains("completed")) {
                                video_completed_at_chromecast = true;
                                Log.v(TAG, "video completed at chromecast");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                });
                            } else {
                                try {
                                    JSONObject jsonObject = new JSONObject(s1);
                                    videoLogId = jsonObject.optString("video_log_id");

                                    if(isDrm)
                                    {
                                        videoBufferLogId = jsonObject.optString("bandwidth_log_id");
                                        Current_Sesion_DataUsedByChrmoeCast = Long.parseLong(jsonObject.optString("bandwidth"));
                                        Log.v(TAG, "Current_Sesion_DataUsedByChrmoeCast=*****************=====" + Current_Sesion_DataUsedByChrmoeCast);
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }


                //================================================================================//

                // Call video log after caonnected with chromecast

                CallVideoLog(ASYNC_VODEOLOG_DETAILS);

                //====================END============================//


                //Will Add Some Data to send
                stoptimertask();
                Util.call_finish_at_onUserLeaveHint = false;
                Util.hide_pause = true;
                ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
                latest_center_play_pause.setVisibility(View.VISIBLE);
                subtitleText.setText("");
                emVideoView.setEnabled(false);
//                latest_center_play_pause.setEnabled(true);

                if (emVideoView.isPlaying()) {
                    emVideoView.pause();
                    latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                    center_play_pause.setImageResource(R.drawable.ic_media_play);
                    mHandler.removeCallbacks(updateTimeTask);
                }


                if (center_pause_paly_timer_is_running) {
                    center_pause_paly_timer.cancel();
                    center_pause_paly_timer_is_running = false;
                    Log.v("BIBHU11", "CastAndCrewActivity End_Timer cancel called");


                    last_ll.setVisibility(View.GONE);
                    center_play_pause.setVisibility(View.GONE);
                    current_time.setVisibility(View.GONE);
                }

                if (SubTitlePath.size() > 0)
                    subtitle_change_btn.setVisibility(View.VISIBLE);
                mediaRouteButton.setVisibility(View.VISIBLE);
                primary_ll.setVisibility(View.VISIBLE);


                //===================================================================================================//


                Log.v("BIBHU2", "cast connected");
                cast_disconnected_position = 0;

                mCastSession = castSession;
                mLocation = PlaybackLocation.REMOTE;
                if (null != mSelectedMedia) {

                    if (mPlaybackState == PlaybackState.PLAYING) {
                       /* mVideoView.pause();
                        loadRemoteMedia(mSeekbar.getProgress(), true);*/
                        return;
                    } else {
                        mPlaybackState = PlaybackState.IDLE;
                        updatePlaybackLocation(PlaybackLocation.REMOTE);
                    }
                }
                updatePlayButton(mPlaybackState);

                PlayUsingCsat();
            }

            private void onApplicationDisconnected() {
/*
                    mPlayCircle.setVisibility(View.GONE);
*/
                updatePlaybackLocation(PlaybackLocation.LOCAL);
                mPlaybackState = PlaybackState.IDLE;
                mLocation = PlaybackLocation.LOCAL;
                updatePlayButton(mPlaybackState);
                invalidateOptionsMenu();
            }
        };
    }

    private void updatePlayButton(PlaybackState state) {

        switch (state) {
            case PLAYING:
                break;
            case IDLE:
                if (mLocation == PlaybackLocation.LOCAL) {
                } else {
                }
                break;
            case PAUSED:

                break;
            case BUFFERING:

                break;
            default:
                break;
        }
    }

    private void updatePlaybackLocation(PlaybackLocation location) {
        mLocation = location;
        if (location == PlaybackLocation.LOCAL) {
            if (mPlaybackState == PlaybackState.PLAYING
                    || mPlaybackState == PlaybackState.BUFFERING) {
            } else {
            }
        } else {
        }
    }


    private void togglePlayback() {
        //stopControllersTimer();
        switch (mPlaybackState) {
            case PAUSED:
                switch (mLocation) {
                    case LOCAL:
                        Log.v("BIBHU2", "=============1");
                        break;

                    case REMOTE:
                        Log.v("BIBHU2", "=============2");

                        loadRemoteMedia(0, true);

                        break;
                    default:
                        break;
                }
                break;

            case PLAYING:
                Log.v("BIBHU2", "=============3");
                mPlaybackState = PlaybackState.PAUSED;
                break;

            case IDLE:
                switch (mLocation) {
                    case LOCAL:
                        Log.v("BIBHU2", "=============4");
                        break;
                    case REMOTE:
                        if (mCastSession != null && mCastSession.isConnected()) {
                            Log.v("BIBHU2", "=============5==+current_played_length==" + current_played_length);
                            loadRemoteMedia(current_played_length, true);
                        } else {
                        }
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
        updatePlayButton(mPlaybackState);
    }

    private void loadRemoteMedia(int position, boolean autoPlay) {

        if (mCastSession == null) {
            return;
        }
        remoteMediaClient = mCastSession.getRemoteMediaClient();


        if (remoteMediaClient == null) {
            return;
        }


        remoteMediaClient.addListener(new RemoteMediaClient.Listener() {

            @Override
            public void onStatusUpdated() {

//                Intent intent = new Intent(PlayerActivity.this, ExpandedControlsActivity.class);
//                startActivity(intent);
//                remoteMediaClient.removeListener(PlayerActivity.this);

                if (mCastSession != null && mCastSession.isConnected()) {
                    Log.v("BIBHU222", "======" + remoteMediaClient.isPlaying());

                    if (remoteMediaClient.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                                latest_center_play_pause.setVisibility(View.VISIBLE);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                                latest_center_play_pause.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                }
            }

            @Override
            public void onMetadataUpdated() {


            }

            @Override
            public void onQueueStatusUpdated() {
            }

            @Override
            public void onPreloadStatusUpdated() {
            }

            @Override
            public void onSendingRemoteMediaRequest() {
            }
        });


        remoteMediaClient.load(mSelectedMedia, autoPlay, position);


    }

    /***************
     * chromecast
     **********************/


    public void PlayUsingCsat() {


        MediaMetadata movieMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        movieMetadata.putString(MediaMetadata.KEY_SUBTITLE, playerModel.getVideoStory());
        movieMetadata.putString(MediaMetadata.KEY_TITLE, playerModel.getVideoTitle());
        movieMetadata.addImage(new WebImage(Uri.parse(playerModel.getPosterImageId())));
        movieMetadata.addImage(new WebImage(Uri.parse(playerModel.getPosterImageId())));


        String mediaContentType = "videos/mp4";
        if (playerModel.getVideoUrl().contains(".mpd")) {
            mediaContentType = "application/dash+xml";
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject();
                jsonObj.put("description", playerModel.getVideoTitle());
                jsonObj.put("licenseUrl", playerModel.getLicenseUrl());

                jsonObj.put("active_track_index", active_track_index);

                //  This Code Is Added For Video Log By Bibhu..

                jsonObj.put("authToken", authToken.trim());
                jsonObj.put("user_id", userIdStr.trim());
                jsonObj.put("ip_address", ipAddressStr.trim());
                jsonObj.put("movie_id", playerModel.getMovieUniqueId());
                jsonObj.put("episode_id", playerModel.getEpisode_id());


                jsonObj.put("watch_status", watchStatus);
                jsonObj.put("device_type", "2");
                jsonObj.put("log_id", videoLogId);

                if (playerModel.isstreaming_restricted()) {
                    jsonObj.put("restrict_stream_id", restrict_stream_id);
                    jsonObj.put("is_streaming_restriction", "1");
                    Log.v("BIBHU4", "restrict_stream_id============1");
                } else {
                    jsonObj.put("restrict_stream_id", restrict_stream_id);
                    jsonObj.put("is_streaming_restriction", "0");
                    Log.v("BIBHU4", "restrict_stream_id============0");
                }

                jsonObj.put("domain_name", rootUrl.trim().substring(0, rootUrl.trim().length() - 6));
                jsonObj.put("is_log", "1");

                // PlayerActivity.this code is changed according to new Video log //

                jsonObj.put("played_length", "0");
                jsonObj.put("log_temp_id", "0");
                jsonObj.put("resume_time", "" + (playerPosition));
                jsonObj.put("seek_status", "first_time");


                //=====================End===================//


                // PlayerActivity.this  Code Is Added For Drm BufferLog By Bibhu ...

                jsonObj.put("resolution", "BEST");
                jsonObj.put("start_time", String.valueOf(playerPosition));
                jsonObj.put("end_time", String.valueOf(playerPosition));
                jsonObj.put("log_unique_id", videoBufferLogUniqueId);
                jsonObj.put("bandwidth_log_id", videoBufferLogId);
                jsonObj.put("location", Location);
                jsonObj.put("video_type", "mped_dash");


                Log.v("BIBHU4", "drm_bandwidth_by_sender============" + ((CurrentUsedData + DataUsedByChrmoeCast) * 1024));
                Log.v("BIBHU4", "CurrentUsedData============" + CurrentUsedData);
                Log.v("BIBHU4", "DataUsedByChrmoeCast============" + DataUsedByChrmoeCast);

                jsonObj.put("drm_bandwidth_by_sender", "" + ((CurrentUsedData + DataUsedByChrmoeCast) * 1024));

                //====================End=====================//

            } catch (JSONException e) {
            }

            List tracks = new ArrayList();

            Log.v(TAG, "url size============" + playerModel.getChromecsatSubtitleUrl().size());
            if (playerModel.getChromecsatSubtitleUrl().size() > 0) {

                for (int i = 0; i < playerModel.getChromecsatSubtitleUrl().size(); i++) {

                    MediaTrack mediaTrack = new MediaTrack.Builder(i,
                            MediaTrack.TYPE_TEXT)
                            .setName(playerModel.getChromecsatSubtitleLanguage().get(i))
                            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                            .setContentId(playerModel.getChromecsatSubtitleUrl().get(i))
                            .setLanguage(playerModel.getChromecsatSubtitleLanguageCode().get(i))
                            .setContentType("text/vtt")
                            .build();

                    tracks.add(mediaTrack);
                }
            }


            mediaInfo = new MediaInfo.Builder(playerModel.getMpdVideoUrl().trim())
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType(mediaContentType)
                    .setMetadata(movieMetadata)
                    .setCustomData(jsonObj)
                    .setMediaTracks(tracks)
                    .build();
            mSelectedMedia = mediaInfo;


            togglePlayback();

        }

        else
        {
            mediaContentType = "videos/mp4";
            JSONObject jsonObj = null;
            try {
                jsonObj = new JSONObject();
                jsonObj.put("description", playerModel.getVideoTitle());
                jsonObj.put("active_track_index", active_track_index);

                //  This Code Is Added For Video Log By Bibhu..

                jsonObj.put("authToken", authToken.trim());
                jsonObj.put("user_id", userIdStr.trim());
                jsonObj.put("ip_address", ipAddressStr.trim());
                jsonObj.put("movie_id", playerModel.getMovieUniqueId());
                jsonObj.put("episode_id", playerModel.getEpisode_id());


                jsonObj.put("watch_status", watchStatus);
                jsonObj.put("device_type", "2");
                jsonObj.put("log_id", videoLogId);

                if (playerModel.isstreaming_restricted()) {
                    jsonObj.put("restrict_stream_id", restrict_stream_id);
                    jsonObj.put("is_streaming_restriction", "1");
                    Log.v("BIBHU4", "restrict_stream_id============1");
                } else {
                    jsonObj.put("restrict_stream_id", restrict_stream_id);
                    jsonObj.put("is_streaming_restriction", "0");
                    Log.v("BIBHU4", "restrict_stream_id============0");
                }

                jsonObj.put("domain_name", rootUrl.trim().substring(0, rootUrl.trim().length() - 6));
                jsonObj.put("is_log", "1");

                // PlayerActivity.this code is changed according to new Video log //

                jsonObj.put("played_length", "0");
                jsonObj.put("log_temp_id", "0");
                jsonObj.put("resume_time", "" + (playerPosition));
                jsonObj.put("seek_status", "first_time");


                //=====================End===================//

                // This  Code Is Added For Drm BufferLog By Bibhu ...

                jsonObj.put("resolution", "BEST");
                jsonObj.put("start_time", String.valueOf(playerPosition));
                jsonObj.put("end_time", String.valueOf(playerPosition));

                // This ia always "0" for Non DRM play and Cast.
                jsonObj.put("log_unique_id", "");
                jsonObj.put("bandwidth_log_id", "0");
                //====END========================//

                jsonObj.put("location", Location);
                jsonObj.put("video_type", "");


                //====================End=====================//
            } catch (JSONException e) {
            }

            List tracks = new ArrayList();

            Log.v(TAG, "url size============" + playerModel.getChromecsatSubtitleUrl().size());
            if (playerModel.getChromecsatSubtitleUrl().size() > 0) {

                for (int i = 0; i < playerModel.getChromecsatSubtitleUrl().size(); i++) {

                    MediaTrack mediaTrack = new MediaTrack.Builder(i,
                            MediaTrack.TYPE_TEXT)
                            .setName(playerModel.getChromecsatSubtitleLanguage().get(i))
                            .setSubtype(MediaTrack.SUBTYPE_SUBTITLES)
                            .setContentId(playerModel.getChromecsatSubtitleUrl().get(i))
                            .setLanguage(playerModel.getChromecsatSubtitleLanguageCode().get(i))
                            .setContentType("text/vtt")
                            .build();

                    tracks.add(mediaTrack);
                }
            }


            mediaInfo = new MediaInfo.Builder(playerModel.getVideoUrl().trim())
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType(mediaContentType)
                    .setMetadata(movieMetadata)
                    .setCustomData(jsonObj)
                    .setMediaTracks(tracks)
                    .build();
            mSelectedMedia = mediaInfo;


            togglePlayback();

        }
    }

    // PlayerActivity.this part is only applicable for multiple download optin feature

    class DetectDownloadingFileSize extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
        }

        /**
         * Downloading file in background thread
         */
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... f_url) {
            int count;

            try {

                DefaultHttpClient client = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(List_Of_Resolution_Url.get(0));
                HttpResponse execute = client.execute(httpGet);

               float size = calculateDownloadFileSize(execute);
                List_Of_FileSize.add("(" + size + " MB)");


            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {


            List_Of_Resolution_Url.remove(0);
            if (List_Of_Resolution_Url.size() > 0) {
                new DetectDownloadingFileSize().execute();
            } else {
                try {
                    if (pDialog_for_gettig_filesize != null && pDialog_for_gettig_filesize.isShowing()) {
                        pDialog_for_gettig_filesize.hide();
                    }
                } catch (IllegalArgumentException ex) {
                }

                // Show PopUp for Multiple Options for Download .

                if(List_Of_Resolution_Format.size()>0 && List_Of_FileSize.size()>0 && (List_Of_FileSize.size() == List_Of_Resolution_Format.size()))
                {
                    // Show PopUp for Multiple Options for Download .
                    ShowDownloadOptionPopUp();
                }else {
                    AlertDialog.Builder dlgAlert = new AlertDialog.Builder(PlayerActivity.this,R.style.MyAlertDialogStyle);
                    dlgAlert.setMessage(Util.getTextofLanguage(PlayerActivity.this,Util.SLOW_INTERNET_CONNECTION,Util.DEFAULT_SLOW_INTERNET_CONNECTION));
                    dlgAlert.setTitle(Util.getTextofLanguage(PlayerActivity.this,Util.SORRY,Util.DEFAULT_SORRY));
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK), null);
                    dlgAlert.setCancelable(false);
                    dlgAlert.setPositiveButton(Util.getTextofLanguage(PlayerActivity.this,Util.BUTTON_OK,Util.DEFAULT_BUTTON_OK),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    dlgAlert.create().show();
                }



            }

        }
    }

    public void ShowDownloadOptionPopUp() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlayerActivity.this, R.style.MyAlertDialogStyle);
        LayoutInflater inflater = (LayoutInflater) PlayerActivity.this.getSystemService(PlayerActivity.this.LAYOUT_INFLATER_SERVICE);

        View convertView = (View) inflater.inflate(R.layout.activity_sdk_download_popup, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle("");

        TextView title_text = (TextView) convertView.findViewById(R.id.title_text);
        ListView resolution_list = (ListView) convertView.findViewById(R.id.resolution_list);
        Button save = (Button) convertView.findViewById(R.id.save);
        Button cancel = (Button) convertView.findViewById(R.id.cancel);

        save.setText(Util.getTextofLanguage(PlayerActivity.this, Util.SAVE, Util.DEFAULT_SAVE));
        cancel.setText(Util.getTextofLanguage(PlayerActivity.this, Util.CANCEL_BUTTON, Util.DEFAULT_CANCEL_BUTTON));
        title_text.setText(Util.getTextofLanguage(PlayerActivity.this, Util.SAVE_OFFLINE_VIDEO, Util.DEFAULT_SAVE_OFFLINE_VIDEO));

        DownloadOptionAdapter adapter = new DownloadOptionAdapter(PlayerActivity.this, List_Of_FileSize, List_Of_Resolution_Format);
        resolution_list.setAdapter(adapter);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //download file here
                downloadFile(false);

                alert.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected_download_format = 0;
                alert.cancel();
            }
        });

        alert = alertDialog.show();
        alertDialog.setCancelable(false);
        alert.setCancelable(false);

        alert.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                selected_download_format = 0;
                // Toast.makeText(getApplicationContext(),"cancel",Toast.LENGTH_SHORT).show();
            }
        });

    }

    private BroadcastReceiver SelectedUrl = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String position = intent.getStringExtra("position");
            Log.v("BIBHU1", "Got position: " + position);
            selected_download_format = Integer.parseInt(position);

        }
    };

    //============================End=================================//


    // PlayerActivity.this AsyncTask is called to get Details of Watch Period and Access Period of download content.

    class AsyncWatchAccessDetails extends AsyncTask<String, String, String> {

        String responseStr;
        int statusCode = 0;
        String request_data = "";
        String log_id = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        protected String doInBackground(String... f_url) {

            Log.v("BIBHU11", "f_url[0]=======" + f_url[0]);

            SQLiteDatabase DB = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = DB.rawQuery("SELECT stream_unique_id FROM " + DBHelper.WATCH_ACCESS_INFO + " WHERE download_id = '" + f_url[0].trim() + "'", null);
            int count = cursor.getCount();
            String Stream_Id = "";

            if (count > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        Stream_Id = cursor.getString(0).trim();

                        Log.v("BIBHU3", "Stream_Id============" + Stream_Id);
                    } while (cursor.moveToNext());
                }
            }


            String urlRouteList = rootUrl.trim() + Util.GetOfflineViewRemainingTime.trim();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", authToken.trim());
                httppost.addHeader("stream_uniq_id", Stream_Id);
                httppost.addHeader("watch_remaining_time", "0");
                httppost.addHeader("device_id", Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                httppost.addHeader("user_id", userIdStr);
                httppost.addHeader("device_type", "2");
                httppost.addHeader("request_data", "");
                httppost.addHeader("lang_code", Util.getTextofLanguage(PlayerActivity.this, Util.SELECTED_LANGUAGE_CODE, Util.DEFAULT_SELECTED_LANGUAGE_CODE));


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                } catch (Exception e) {
                    statusCode = 0;
                    e.printStackTrace();
                }


                Log.v("BIBHU11", "response of GetOfflineViewRemainingTime in exoplayer=======" + responseStr);

                JSONObject myJson = null;
                if (responseStr != null) {
                    myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    request_data = myJson.optString("request_data");
                    log_id = myJson.optString("log_id");

                    Log.v("BIBHU11", "response of server_current_time in exoplayer=======" + myJson.optLong("created_date"));
                    Log.v("BIBHU11", "response of server_current_time in exoplayer=======" + myJson.optLong("access_expiry_time"));
                    Dwonload_Complete_Msg = "";
                    if (statusCode == 200) {

                        Dwonload_Complete_Msg = myJson.optString("download_complete_msg");

                        if (Dwonload_Complete_Msg.trim().equals(""))
                            Dwonload_Complete_Msg = "Your video has been downloaded successfully.";

                        SQLiteDatabase DB1 = PlayerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);


                        String query1 = "UPDATE " + DBHelper.WATCH_ACCESS_INFO + " SET server_current_time = '" + myJson.optLong("created_date") + "' ," +
                                "watch_period = '0',access_period = '" + myJson.optLong("access_expiry_time") + "' WHERE download_id = '" + f_url[0].trim() + "'";


//                        String query1 = "UPDATE "+DBHelper.WATCH_ACCESS_INFO+" SET server_current_time = '"+myJson.optLong("created_date")+"' ," +
//                                "watch_period = '0',access_period = '"+((myJson.optLong("created_date"))+300000)+"' WHERE download_id = '"+f_url[0].trim()+"'";


                        DB1.execSQL(query1);
                    }
                }
            } catch (Exception e) {
                statusCode = 0;
                Log.v("BIBHU11", "response of server_current_time in Exception=======" + e.toString());

            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            Log.v("BIBHU11", "response of onPostExecute called ======");
            Intent intent = new Intent(PlayerActivity.this, PopUpService.class);
            intent.putExtra("msg", Dwonload_Complete_Msg);
            PlayerActivity.this.startService(intent);
        }
    }

    private String getActionTokenFromStorage(String tokenFileName) {
        String token = null;
        byte[] readBuffer = new byte[1024];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = null;
        int bytesRead = 0;

        try {
            is = new FileInputStream(tokenFileName);
            while ((bytesRead = is.read(readBuffer)) != -1) {
                baos.write(readBuffer, 0, bytesRead);
            }
            baos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        token = new String(baos.toByteArray());
        return token;
    }

    /**
     * Request video ads from the given VAST ad tag.
     *
     * @param adTagUrl URL of the ad's VAST XML
     */
    private void requestAds(String adTagUrl) {
        AdDisplayContainer adDisplayContainer = mSdkFactory.createAdDisplayContainer();
        adDisplayContainer.setAdContainer(mAdUiContainer);

        // Create the ads request.
        AdsRequest request = mSdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setAdDisplayContainer(adDisplayContainer);
        request.setContentProgressProvider(new ContentProgressProvider() {
            @Override
            public VideoProgressUpdate getContentProgress() {
                if (mIsAdDisplayed || emVideoView == null || emVideoView.getDuration() <= 0) {
                    return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                }
                Log.v("SUBHA", "emVideoView.getCurrentPosition()" + emVideoView.getCurrentPosition());
                Log.v("SUBHA", "emVideoView.getDuration()" + emVideoView.getDuration());

               /* if (emVideoView.getCurrentPosition() >= emVideoView.getDuration()){
                    return new VideoProgressUpdate(emVideoView.getCurrentPosition(),
                            emVideoView.getDuration());
                }
*/
                return new VideoProgressUpdate(emVideoView.getCurrentPosition(),
                        emVideoView.getDuration());
            }
        });
       /* if (mAdsManager !=null){
            Log.v("SUBHA","ddT"+mAdsManager.getAdCuePoints());

        }*/
        Log.v("SUBHA", "ddT");

        // Request the ad. After the ad is loaded, onAdsManagerLoaded() will be called.
        mAdsLoader.requestAds(request);
    }

    @Override
    public void onAdEvent(AdEvent adEvent) {
        Log.v("SUBHA", "Event: " + adEvent.getType());

        // These are the suggested event types to handle. For full list of all ad event
        // types, see the documentation for AdEvent.AdEventType.
        switch (adEvent.getType()) {
            case LOADED:
                onClick1();
             /*   if (pDialog == null){
                    pDialog = new ProgressDialog(PlayerActivity.this);
                    pDialog.setMessage("loading");
                    pDialog.show();
                }*/

                // AdEventType.LOADED will be fired when ads are ready to be played.
                // AdsManager.start() begins ad playback. This method is ignored for VMAP or
                // ad rules playlists, as the SDK will automatically start executing the
                // playlist.
                mAdsManager.start();
                break;

            case STARTED:
             /*   if (pDialog != null){
                    Log.v("SUBHA","DISMISS");
                    pDialog.dismiss();
                }*/
                //  progressView.setVisibility(View.VISIBLE);
                Util.call_finish_at_onUserLeaveHint = false;
                emVideoView.pause();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                center_play_pause.setImageResource(R.drawable.ic_media_play);
                mHandler.removeCallbacks(updateTimeTask);
                onClick1();
             /*   final ProgressDialog finalPDialog = pDialog;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(finalPDialog !=null && finalPDialog.isShowing()) {
                            Log.v("SUBHA","DISMISS");

                            finalPDialog.dismiss();

                        }
                    }
                });*/


                break;

            case CONTENT_PAUSE_REQUESTED:
                // AdEventType.CONTENT_PAUSE_REQUESTED is fired immediately before a video
                // ad is played.
                mIsAdDisplayed = true;
                Util.call_finish_at_onUserLeaveHint = false;
                emVideoView.pause();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                center_play_pause.setImageResource(R.drawable.ic_media_play);
                mHandler.removeCallbacks(updateTimeTask);
                //  mVideoPlayer.pause();
                break;
            case CONTENT_RESUME_REQUESTED:
                // AdEventType.CONTENT_RESUME_REQUESTED is fired when the ad is completed
                // and you should start playing your content.
                mIsAdDisplayed = false;
                if (video_completed == true) {
                    backCalled();
                }
                Util.call_finish_at_onUserLeaveHint = true;
                watchStatus = "halfplay";
                // playerPosition = Util.dataModel.getPlayPos();
                emVideoView.start();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                latest_center_play_pause.setVisibility(View.GONE);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);
                seekBar.setProgress(emVideoView.getCurrentPosition());

              /*  emVideoView.seekTo(played_length);
                seekBar.setProgress(played_length);*/
                updateProgressBar();
                break;
            case ALL_ADS_COMPLETED:
                if (mAdsManager != null) {
                    mAdsManager.destroy();
                    mAdsManager = null;
                }
                if (video_completed == true) {
                    backCalled();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onAdError(AdErrorEvent adErrorEvent) {
        Log.e("subha", "Ad Error: " + adErrorEvent.getError().getMessage());
        Util.call_finish_at_onUserLeaveHint = true;
        watchStatus = "halfplay";
        // playerPosition = Util.dataModel.getPlayPos();
        emVideoView.start();
       /* emVideoView.seekTo(played_length);
        seekBar.setProgress(played_length);*/
        latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
        latest_center_play_pause.setVisibility(View.GONE);
        center_play_pause.setImageResource(R.drawable.ic_media_pause);
        seekBar.setProgress(emVideoView.getCurrentPosition());
        updateProgressBar();
    }

    public void onClick1() {
        if (mDialog == null) {
            mDialog = new ProgressBarHandler(PlayerActivity.this);
            mDialog.show();
        } else {
            mDialog.hide();

        }
    }


    // The following is changed later

    /**
     * Thi
     * @param logType
     */

    public void  CallVideoLog(int logType)
    {

        String keyList = "";
        String valueList = "";

        if(content_types_id != LIVE_STREAM) {

            if (current_matching_time >= emVideoView.getDuration()) {
                watchStatus = "complete";
            }
        }

        if(logType == ASYNC_RESUME_VODEOLOG_DETAILS){
            stopLogTimer = true;
        }

       /* if(playerModel.isstreaming_restricted()){
            keyList = "apiname,authToken,user_id,ip_address,movie_id,episode_id,watch_status,device_type,log_id,played_length,log_temp_id,resume_time,is_streaming_restriction,restrict_stream_id";
            valueList = Util.videoLogUrl.trim()+","+authToken+","+userIdStr+","+ipAddressStr+","+movieId.trim()+","+episodeId.trim()+","+watchStatus+","+"2"+","+videoLogId+","+(playerPosition - player_start_time)+","+log_temp_id+","+playerPosition+","+"1"+","+restrict_stream_id ;
        }else {
            keyList = "apiname,authToken,user_id,ip_address,movie_id,episode_id,watch_status,device_type,log_id,played_length,log_temp_id,resume_time";
            valueList = Util.videoLogUrl.trim()+","+authToken+","+userIdStr+","+ipAddressStr+","+movieId.trim()+","+episodeId.trim()+","+watchStatus+","+"2"+","+videoLogId+","+(playerPosition - player_start_time)+","+log_temp_id+","+playerPosition ;
        }
*/


        if (playerModel.isstreaming_restricted()) {

            if (logType == ASYNC_RESUME_VODEOLOG_DETAILS || logType == ASYNC_RESUME_VODEOLOG_DETAILS_HOME_CLICKED) {
                keyList = "apiname,authToken,user_id,ip_address,movie_id,episode_id,watch_status,device_type,log_id,played_length,log_temp_id,resume_time,is_streaming_restriction,restrict_stream_id,is_active_stream_closed";
                valueList = APIUrlConstant.VIDEO_LOGS_URL.trim() + "," + authToken.trim() + "," + userIdStr + "," + ipAddressStr + "," + movieId.trim() + "," + episodeId.trim() + "," + watchStatus + "," + "2" + "," + videoLogId + "," + (playerPosition - player_start_time) + "," + log_temp_id + "," + playerPosition + "," + "1" + "," + restrict_stream_id+","+"1";
            } else {
                keyList = "apiname,authToken,user_id,ip_address,movie_id,episode_id,watch_status,device_type,log_id,played_length,log_temp_id,resume_time,is_streaming_restriction,restrict_stream_id";
                valueList = APIUrlConstant.VIDEO_LOGS_URL.trim() + "," + authToken.trim() + "," + userIdStr + "," + ipAddressStr + "," + movieId.trim() + "," + episodeId.trim() + "," + watchStatus + "," + "2" + "," + videoLogId + "," + (playerPosition - player_start_time) + "," + log_temp_id + "," + playerPosition + "," + "1" + "," + restrict_stream_id;
            }
        } else {
            keyList = "apiname,authToken,user_id,ip_address,movie_id,episode_id,watch_status,device_type,log_id,played_length,log_temp_id,resume_time";
            valueList = APIUrlConstant.VIDEO_LOGS_URL.trim() + "," + authToken.trim() + "," + userIdStr + "," + ipAddressStr + "," + movieId.trim() + "," + episodeId.trim() + "," + watchStatus + "," + "2" + "," + videoLogId + "," + (playerPosition - player_start_time) + "," + log_temp_id + "," + playerPosition;
        }

        new WebApiController(PlayerActivity.this,keyList,valueList,""+logType,rootUrl).execute();

    }


    public void CallBufferLog()
    {
        String keyList = "";
        String valueList = "";
        String totalBandwidth = "";

        if (videoBufferLogUniqueId.equals("0")) {
            totalBandwidth = "0";
        } else {
            if (isDrm)
                totalBandwidth = "" + (CurrentUsedData + DataUsedByChrmoeCast);
            else
                totalBandwidth = "" + CurrentUsedData;
        }

        keyList = "apiname,authToken,user_id,ip_address,movie_id,episode_id,device_type,log_id,resolution,start_time,end_time,log_unique_id,location,video_type,totalBandwidth";
        valueList = APIUrlConstant.VIDEO_BUFFER_LOGS_URL.trim()+","+authToken+","+userIdStr+","+ipAddressStr+","+movieId.trim()+","+episodeId.trim()+","+"2"+","+videoBufferLogId+","+resolution.trim()+","+ String.valueOf(playerPosition)+","+ String.valueOf(playerPosition)+","+videoBufferLogUniqueId+","+Location+","+"mped_dash"+","+totalBandwidth;

        new WebApiController(PlayerActivity.this,keyList,valueList,""+ASYNC_BUFFERLOG_DETAILS,rootUrl).execute();

    }



    // These override methods are responsible to handle single controller responses..

    @Override
    public void onTaskpreExecute(String Requestdata) {

        if(Requestdata.equals(""+ASYNC_VODEOLOG_DETAILS) || Requestdata.equals(""+ASYNC_FF_VODEOLOG_DETAILS)
                || Requestdata.equals(""+ASYNC_RESUME_VODEOLOG_DETAILS))
        {
            stoptimertask();
        }

    }

    @Override
    public void onTaskPostExecute(String response, String requestId) {

        if(requestId.equals(""+ASYNC_VODEOLOG_DETAILS) || requestId.equals(""+ASYNC_FF_VODEOLOG_DETAILS)
                 || requestId.equals(""+ASYNC_RESUME_VODEOLOG_DETAILS) || requestId.equals(""+ASYNC_RESUME_VODEOLOG_DETAILS_HOME_CLICKED))
        {

            if (response != null && !response.trim().equals("")) {

                try{
                    JSONObject myJson = new JSONObject(response);
                    int statusCode = Integer.parseInt(myJson.optString("code"));
                    if (statusCode == 200) {
                        videoLogId = myJson.optString("log_id");
                        log_temp_id = myJson.optString("log_temp_id");
                        restrict_stream_id = myJson.optString("restrict_stream_id");
                    } else {
                        videoLogId = "0";
                        log_temp_id = "0";
                    }
                }catch(Exception e){
                    videoLogId = "0";
                    log_temp_id = "0";
                }
            }
            else{

                videoLogId = "0";
                log_temp_id = "0";
            }


            //#######################################//


           if(requestId.equals(""+ASYNC_VODEOLOG_DETAILS) || requestId.equals(""+ASYNC_FF_VODEOLOG_DETAILS))
           {
               // Call Bufferlog Here
               CallBufferLog();
               Log.v("BIBHU1", "CallBufferLog CALLED ");
           }

            //#######################################//

            if( requestId.equals(""+ASYNC_RESUME_VODEOLOG_DETAILS))
            {
                CallBufferLog();
                mHandler.removeCallbacks(updateTimeTask);
                if (emVideoView != null) {
                    emVideoView.release();
                }
                /***AD ***///
                if (video_completed == true) {
                    Log.v("SUBHA", "CALLED VIDEO COMPLETED");
                    /**SPOTX***/
                    if (playerModel.getAdNetworkId() == 1 && playerModel.getPostRoll() == 1) {

                        Intent adIntent = new Intent(PlayerActivity.this, AdPlayerActivity.class);
                        adIntent.putExtra("fromAd", "fromAd");
                        adIntent.putExtra("PlayerModel", playerModel);
                        startActivity(adIntent);
                    }
                }

                finish();
                overridePendingTransition(0, 0);
            }
        }

        //#######################################//

        // Following code is responsible for BufferLog Response

        if(requestId.equals(""+ASYNC_BUFFERLOG_DETAILS)) {
            if (response != null && !response.trim().equals("")) {
                try {
                    JSONObject myJson = new JSONObject(response);
                    int statusCode = Integer.parseInt(myJson.optString("code"));
                    if (statusCode == 200) {
                        videoBufferLogId = myJson.optString("log_id");
                        videoBufferLogUniqueId = myJson.optString("log_unique_id");
                        Location = myJson.optString("location");

                    } else {
                        videoBufferLogId = "0";
                        videoBufferLogUniqueId = "0";
                        Location = "0";
                    }
                } catch (Exception e) {
                    videoBufferLogId = "0";
                    videoBufferLogUniqueId = "0";
                    Location = "0";
                }
            } else {
                videoBufferLogId = "0";
                videoBufferLogUniqueId = "0";
                Location = "0";
            }

            if (!watchStatus.equals("complete") && !stopLogTimer)
                startTimer();

        }

        //********************************************************************************************************//


    }
   //===============================END=====================================================//




    // This code is responsible for change volume and brightness using swipe control ..

    OnSwipeTouchListener clickFrameSwipeListener = new OnSwipeTouchListener(){

        int startVolume;
        int maxVolume;
        int startBrightness;
        int maxBrightness;

        @Override
        public void onMove(Direction dir, float diff) {


            if(dir == Direction.LEFT || dir == Direction.RIGHT) {

                // Here we have to implement seek control in player using finger swiping.

            }else
            {
                if(initialX >= emVideoView.getWidth()/2 || mWindow==null) {
                    float diffVolume;
                    int finalVolume;

                    diffVolume = (float) maxVolume * diff / ((float) emVideoView.getHeight() / 2);
                    if (dir == Direction.DOWN) {
                        diffVolume = -diffVolume;
                    }
                    finalVolume = startVolume + (int) diffVolume;
                    if (finalVolume < 0)
                        finalVolume = 0;
                    else if (finalVolume > maxVolume)
                        finalVolume = maxVolume;

                    am.setStreamVolume(AudioManager.STREAM_MUSIC, finalVolume, 0);

                    volume_brightness_control.setImageResource(R.drawable.volume);
                    volume_bright_value.setText(""+finalVolume);
                    volume_brightness_control_layout.setVisibility(View.VISIBLE);
                    latest_center_play_pause.setVisibility(View.INVISIBLE);

                }
                else if(initialX < emVideoView.getWidth()/2){
                    float diffBrightness;
                    int finalBrightness;

                    diffBrightness = (float) maxBrightness * diff / ((float) emVideoView.getHeight() / 2);
                    if (dir == Direction.DOWN) {
                        diffBrightness = -diffBrightness;
                    }
                    finalBrightness = startBrightness + (int) diffBrightness;
                    if (finalBrightness < 0)
                        finalBrightness = 0;
                    else if (finalBrightness > maxBrightness)
                        finalBrightness = maxBrightness;

                    WindowManager.LayoutParams layout = mWindow.getAttributes();
                    layout.screenBrightness = (float)finalBrightness / 100;
                    mWindow.setAttributes(layout);

                    volume_brightness_control.setImageResource(R.drawable.brightness);
                    volume_bright_value.setText(""+finalBrightness);
                    volume_brightness_control_layout.setVisibility(View.VISIBLE);
                    latest_center_play_pause.setVisibility(View.INVISIBLE);


                /*PreferenceManager.getDefaultSharedPreferences(getContext()) .edit()
                        .putInt(BETTER_VIDEO_PLAYER_BRIGHTNESS, finalBrightness)
                        .apply();*/
                }
            }
        }

        @Override
        public void onClick() {

            if (mCastSession != null && mCastSession.isConnected() && (mediaRouteButton.getVisibility() == View.VISIBLE)) {
                return;
            }


            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    if (Util.hide_pause) {
                        Util.hide_pause = false;
                    }

                    if (((ProgressBar) findViewById(R.id.progress_view)).getVisibility() == View.VISIBLE) {
                        primary_ll.setVisibility(View.VISIBLE);
                        center_play_pause.setVisibility(View.GONE);
                        latest_center_play_pause.setVisibility(View.GONE);
                        current_time.setVisibility(View.GONE);
                        subtitle_change_btn.setVisibility(View.GONE);
                        mediaRouteButton.setVisibility(View.INVISIBLE);


                    } else {
                        if (primary_ll.getVisibility() == View.VISIBLE) {
                            primary_ll.setVisibility(View.GONE);
                            last_ll.setVisibility(View.GONE);
                            center_play_pause.setVisibility(View.GONE);
                            latest_center_play_pause.setVisibility(View.GONE);
                            current_time.setVisibility(View.GONE);
                            subtitle_change_btn.setVisibility(View.GONE);
                            mediaRouteButton.setVisibility(View.INVISIBLE);

                            End_Timer();
                        } else {
                            primary_ll.setVisibility(View.VISIBLE);

                            if(isDrm)
                            {
                                if (SubTitlePath.size() > 0) {
                                    subtitle_change_btn.setVisibility(View.VISIBLE);
                                }
                            }else {
                                if (SubTitlePath.size() > 0 || ResolutionUrl.size()>0) {
                                    subtitle_change_btn.setVisibility(View.VISIBLE);
                                }
                            }

                            // This is changed Later

                            if(mediaRouteButton.isEnabled()  && playerModel.getChromeCastEnable())
                            {
                                mediaRouteButton.setVisibility(View.VISIBLE);
                            }else
                            {
                                mediaRouteButton.setVisibility(View.GONE);
                            }


                            last_ll.setVisibility(View.VISIBLE);
                            center_play_pause.setVisibility(View.VISIBLE);
                            latest_center_play_pause.setVisibility(View.VISIBLE);
                            current_time.setVisibility(View.VISIBLE);
                            current_time.setVisibility(View.GONE);
                            showCurrentTime();
                            current_time.setVisibility(View.VISIBLE);
                            Start_Timer();
                        }

                    }


                }
            });

        }

        @Override
        public void onAfterMove() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    volume_brightness_control_layout.setVisibility(View.GONE);
                    if (primary_ll.getVisibility() == View.VISIBLE && ((ProgressBar) findViewById(R.id.progress_view)).getVisibility() != View.VISIBLE) {
                        latest_center_play_pause.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        @Override
        public void onBeforeMove(Direction dir) {

            if(dir == Direction.LEFT || dir == Direction.RIGHT) {
            }
            else{
                maxBrightness = 100;
                if(mWindow!=null) {
                    startBrightness = (int) (mWindow.getAttributes().screenBrightness * 100);
                }
                maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                startVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            }
        }
    };

    //=========================================End========================================//



    // This is added for the movable water mark //

    public void MoveWaterMark() {



        Rect rectf = new Rect();
        emVideoView.getLocalVisibleRect(rectf);
        int mainLayout_width = rectf.width() - 50;
        int mainLayout_height = rectf.height() - 120;


        // Child layout Lyout details

        Rect rectf1 = new Rect();
        linearLayout1.getLocalVisibleRect(rectf1);
        int childLayout_width = rectf1.width();
        int childLayout_height = rectf1.height();

        boolean show = true;

        while (show) {

            Random r = new Random();
            try {
                final int xLeft = r.nextInt(mainLayout_width - 10) + 10;


                final int min = 10;
                final int max = mainLayout_height;
                final int yUp = new Random().nextInt((max - min) + 1) + min;


                Log.v(TAG, "==========================================" + "\n");

                Log.v(TAG, "mainLayout_width  ===" + mainLayout_width);
                Log.v(TAG, "mainLayout_height  ===" + mainLayout_height);

                Log.v(TAG, "childLayout_width  ===" + childLayout_width);
                Log.v(TAG, "childLayout_height  ===" + childLayout_height);


                Log.v(TAG, "xLeft  ===" + xLeft);
                Log.v(TAG, "yUp  ===" + yUp);

                Log.v(TAG, "width addition  ===" + (childLayout_width + xLeft));
                Log.v(TAG, "height addition   ===" + (childLayout_height + yUp));

                if ((mainLayout_width > (childLayout_width + xLeft)) && (mainLayout_height > (childLayout_height + yUp))) {
                    show = false;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        linearLayout1.setX(xLeft);
                        linearLayout1.setY(yUp);
                    }
                });
            }catch (Exception e){}

        }
    }


    /**
     * This method is applicable to handle cast and crew click
     */
    public void handleCastAndCrew(){
        if (Util.checkNetwork(PlayerActivity.this)) {

            Util.hide_pause = true;
            ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
            latest_center_play_pause.setVisibility(View.VISIBLE);

            if (emVideoView.isPlaying()) {
                emVideoView.pause();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                center_play_pause.setImageResource(R.drawable.ic_media_play);
                mHandler.removeCallbacks(updateTimeTask);
            }


            if (center_pause_paly_timer_is_running) {
                center_pause_paly_timer.cancel();
                center_pause_paly_timer_is_running = false;

                subtitle_change_btn.setVisibility(View.GONE);
                primary_ll.setVisibility(View.GONE);
                last_ll.setVisibility(View.GONE);
                center_play_pause.setVisibility(View.GONE);
                current_time.setVisibility(View.GONE);
            }

        } else {
            Toast.makeText(getApplicationContext(), Util.getTextofLanguage(PlayerActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
        }
    }

    private float calculateDownloadFileSize( HttpResponse execute){

        float size = 0.0f;
        try{
            size = (Float.parseFloat("" + execute.getEntity().getContentLength()) / 1024) / 1024;
            DecimalFormat decimalFormat = new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
            String formatString = decimalFormat.format(size);
            size = Float.valueOf(formatString);

        }catch (Exception e ){}

        return size;
    }

}
