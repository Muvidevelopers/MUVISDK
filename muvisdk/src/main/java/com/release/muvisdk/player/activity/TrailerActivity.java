package com.release.muvisdk.player.activity;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import com.release.muvi.muvisdk.R;
import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.player.model.Player;
import com.release.muvisdk.player.utils.DBHelper;
import com.release.muvisdk.player.utils.ResizableCustomView;
import com.release.muvisdk.player.utils.SensorOrientationChangeNotifier;
import com.release.muvisdk.player.utils.Util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
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
import static com.release.muvisdk.player.utils.Constants.APP_NAME_MSG;
import static com.release.muvisdk.player.utils.Constants.CHECK_USER_ID_MSG;
import static com.release.muvisdk.player.utils.Constants.LIVE_STREAM;
import static com.release.muvisdk.player.utils.Constants.VALID_AUTHTOKEN;
import static com.release.muvisdk.player.utils.Constants.VALID_EMAIL;
import static com.release.muvisdk.player.utils.Util.DEFAULT_NO_INTERNET_CONNECTION;
import static com.release.muvisdk.player.utils.Util.DEFAULT_VIEW_MORE;
import static com.release.muvisdk.player.utils.Util.VIEW_MORE;


public class TrailerActivity extends AppCompatActivity implements SensorOrientationChangeNotifier.Listener  {


    String emailIdStr = "";
    String userIdStr = "";
    String movieId = "";
    String episodeId = "0";
    String videoLogId = "0";
    String watchStatus = "start";
    String Current_Time = "", TotalTime = "";
    String videoBufferLogId = "0";
    String videoBufferLogUniqueId = "0";
    String Location = "0";
    String resolution = "BEST";
    String ipAddressStr = "";
    String authTokenStr="";
    String rootUrl="";
    String log_temp_id = "0";


    int playerStartPosition = 0;
    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    int playerPosition = 0;
    int playerPreviousPosition = 0;
    int player_layout_height,player_layout_width;
    int screenWidth,screenHeight;
    int seek_label_pos = 0;
    int content_types_id = 0;
    int player_start_time = 0;
    int player_end_time = 0;
    private static final int MAX_LINES = 3;


    long PreviousUsedData = 0;
    long CurrentUsedData = 0;
    long previous_matching_time = 0;
    long current_matching_time = 0;


    boolean compressed = true;
    boolean censor_layout = true;
    boolean center_pause_paly_timer_is_running = false;
    boolean video_completed = false;
    public  boolean isFastForward = false;


    Timer timer;
    Timer  center_pause_paly_timer;
    TimerTask timerTask;
    private Handler threadHandler = new Handler();
    private Handler mHandler = new Handler();


    TextView ipAddressTextView;
    TextView emailAddressTextView;
    TextView dateTextView;
    TextView current_time, total_time;
    TextView videoTitle,GenreTextView,videoDurationTextView,videoCensorRatingTextView;
    TextView videoCensorRatingTextView1,videoReleaseDateTextView,videoStoryTextView,videoCastCrewTitleTextView;


    ImageButton latest_center_play_pause;
    ImageButton  back, center_play_pause;
    ImageView compress_expand;


    AsyncVideoLogDetails asyncVideoLogDetails;
    AsyncFFVideoLogDetails asyncFFVideoLogDetails;
    AsynGetIpAddress asynGetIpAddress;


    SeekBar seekBar;
    ProgressBar progressView;
    LinearLayout primary_ll, last_ll;
    LinearLayout linearLayout1;
    RelativeLayout player_layout;
    private EMVideoView emVideoView;
    Player playerModel;


    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    SharedPreferences pref;



    @Override
    protected void onResume() {
        super.onResume();
        SensorOrientationChangeNotifier.getInstance(TrailerActivity.this).addListener(this);
        AsynGetIpAddress asynGetIpAddress = new AsynGetIpAddress();
        asynGetIpAddress.executeOnExecutor(threadPoolExecutor);
    }

    /**
     * This method is responsible for widjet initialization.
     */
    public void _init(){

        try{
            videoTitle = (TextView) findViewById(R.id.videoTitle);
            GenreTextView = (TextView) findViewById(R.id.GenreTextView);
            videoDurationTextView = (TextView) findViewById(R.id.videoDurationTextView);
            videoCensorRatingTextView = (TextView) findViewById(R.id.videoCensorRatingTextView);
            videoCensorRatingTextView1 = (TextView) findViewById(R.id.videoCensorRatingTextView1);
            videoReleaseDateTextView = (TextView) findViewById(R.id.videoReleaseDateTextView);
            videoStoryTextView = (TextView) findViewById(R.id.videoStoryTextView);
            videoCastCrewTitleTextView = (TextView) findViewById(R.id.videoCastCrewTitleTextView);
            ipAddressTextView = (TextView) findViewById(R.id.emailAddressTextView);
            emailAddressTextView = (TextView) findViewById(R.id.ipAddressTextView);
            dateTextView = (TextView) findViewById(R.id.dateTextView);
            current_time = (TextView) findViewById(R.id.current_time);
            total_time = (TextView) findViewById(R.id.total_time);
            ((ImageView) findViewById(R.id.subtitle_change_btn)).setVisibility(View.INVISIBLE);
            compress_expand = (ImageView) findViewById(R.id.compress_expand);
            back = (ImageButton) findViewById(R.id.back);
            center_play_pause = (ImageButton) findViewById(R.id.center_play_pause);
            latest_center_play_pause = (ImageButton) findViewById(R.id.latest_center_play_pause);
            emVideoView = (EMVideoView) findViewById(R.id.emVideoView);
            player_layout = (RelativeLayout) findViewById(R.id.player_layout);
            primary_ll = (LinearLayout) findViewById(R.id.primary_ll);
            last_ll = (LinearLayout) findViewById(R.id.last_ll);
            last_ll = (LinearLayout) findViewById(R.id.last_ll);
            linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
            seekBar = (SeekBar) findViewById(R.id.progress);
            progressView = (ProgressBar) findViewById(R.id.progress_view);
        }catch (Exception e){}

    }

    public void _ApplyFont(){

        try{
            Typeface videoTitleface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts_regular));
            videoTitle.setTypeface(videoTitleface);
            Typeface GenreTextViewface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts));
            GenreTextView.setTypeface(GenreTextViewface);
            Typeface videoDurationTextViewface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts));
            videoDurationTextView.setTypeface(videoDurationTextViewface);
            Typeface videoCensorRatingTextViewface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts));
            videoCensorRatingTextView.setTypeface(videoCensorRatingTextViewface);
            Typeface videoCensorRatingTextView1face = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts));
            videoCensorRatingTextView1.setTypeface(videoCensorRatingTextView1face);
            Typeface videoReleaseDateTextViewface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts));
            videoReleaseDateTextView.setTypeface(videoReleaseDateTextViewface);
            Typeface videoStoryTextViewTypeface = Typeface.createFromAsset(getAssets(), getResources().getString(R.string.fonts_regular));
            videoStoryTextView.setTypeface(videoStoryTextViewTypeface);
            Typeface watchTrailerButtonTypeface = Typeface.createFromAsset(getAssets(),getResources().getString(R.string.fonts_regular));
            videoCastCrewTitleTextView.setTypeface(watchTrailerButtonTypeface);
            videoCastCrewTitleTextView.setText(Util.getTextofLanguage(TrailerActivity.this, Util.CAST_CREW_BUTTON_TITLE, Util.DEFAULT_CAST_CREW_BUTTON_TITLE));

        }catch (Exception e){}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_trailer_player);

        /**
         * Calling initialization method.
         */
        _init();

        /**
         * Calling font applying method.
         */
        _ApplyFont();



        player_layout_height = player_layout.getHeight();
        player_layout_width = player_layout.getWidth();

        ipAddressTextView.setVisibility(View.GONE);
        emailAddressTextView.setVisibility(View.GONE);
        dateTextView.setVisibility(View.GONE);

        /**
         * Retrieving screen height & width for future use.
         */

        Display display = getWindowManager().getDefaultDisplay();
        screenWidth = display.getWidth();
        screenHeight = display.getHeight();

        rootUrl = APIUrlConstant.BASE_URl;

        /**
         * Getting how much bandwidth is consumed before .
         */
        PreviousUsedDataByApp(true);


        /**
         * Reciving data for plyer model before playing the video.
         */
        playerModel = (Player) getIntent().getSerializableExtra("PlayerModel");
        if (playerModel.getVideoUrl().matches("")){
            backCalled();
        }

        movieId = playerModel.getMovieUniqueId();
        episodeId = playerModel.getEpisode_id();
        content_types_id = playerModel.getContentTypesId();

        /**
         * Authenticating necessary inputs coming form player model to play the video. 
         */
        
        try{
            if (playerModel != null && playerModel.getAuthToken() != null && !playerModel.getAuthToken().trim().matches("")) {
                authTokenStr = playerModel.getAuthToken();
            } else {
                Toast.makeText(TrailerActivity.this, VALID_AUTHTOKEN, Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (playerModel != null && playerModel.getUserId() != null && !playerModel.getUserId().trim().matches("")) {
                userIdStr = playerModel.getUserId();
            }

            if (playerModel != null && playerModel.getEmailId() != null && !playerModel.getEmailId().trim().matches("")) {
                emailIdStr = playerModel.getEmailId();
            } else {
                Toast.makeText(TrailerActivity.this, VALID_EMAIL, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            if (playerModel != null && playerModel.getAppName() != null && !playerModel.getAppName().trim().matches("")) {
            } else {
                Toast.makeText(TrailerActivity.this, APP_NAME_MSG, Toast.LENGTH_LONG).show();
                finish();
                return;
            }
        }catch (Exception e){}


        /**
         * Setting data in trailer page coming form player model .
         */

        try {
            if (playerModel.getVideoTitle().trim() != null) {
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
                    videoCensorRatingTextView1.setText(Data[-1]);
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
            if (playerModel.getCensorRating().trim() != null && !playerModel.getCensorRating().trim().matches("")) {
                videoCensorRatingTextView.setVisibility(View.GONE);
                videoCensorRatingTextView1.setVisibility(View.GONE);
            }

            if (playerModel.getVideoReleaseDate().trim() != null && !playerModel.getVideoReleaseDate().trim().matches(""))

            {
                videoReleaseDateTextView.setText(playerModel.getVideoReleaseDate().trim());
                videoReleaseDateTextView.setVisibility(View.VISIBLE);
                censor_layout = false;
            } else {
                videoReleaseDateTextView.setVisibility(View.GONE);
            }

            if (censor_layout) {
                findViewById(R.id.durationratingLiearLayout).setVisibility(View.GONE);
            }

            if (playerModel.getVideoStory().trim() != null && !playerModel.getVideoStory().trim().matches(""))

            {
                videoStoryTextView.setText(playerModel.getVideoStory());
                videoStoryTextView.setVisibility(View.VISIBLE);
                ResizableCustomView.doResizeTextView(TrailerActivity.this, videoStoryTextView, MAX_LINES, Util.getTextofLanguage(TrailerActivity.this, Util.VIEW_MORE, Util.DEFAULT_VIEW_MORE), true);

            } else {
                videoStoryTextView.setVisibility(View.GONE);
            }

            if (playerModel.isCastCrew() == true) {
                videoCastCrewTitleTextView.setVisibility(View.VISIBLE);
            } else {
                videoCastCrewTitleTextView.setVisibility(View.GONE);
            }

        } catch (Exception e) { }



        videoCastCrewTitleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Util.checkNetwork(TrailerActivity.this)) {
                    //Will Add Some Data to send
                    Util.call_finish_at_onUserLeaveHint = false;
                    Util.hide_pause = true;
                    findViewById(R.id.progress_view).setVisibility(View.GONE);
                    latest_center_play_pause.setVisibility(View.VISIBLE);

                    if (emVideoView.isPlaying()) {
                        emVideoView.pause();
                        latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
                        center_play_pause.setImageResource(R.drawable.ic_media_play);
                        mHandler.removeCallbacks(updateTimeTask);
                    }

                    final Intent detailsIntent = new Intent(TrailerActivity.this, CastAndCrewActivity.class);
                    detailsIntent.putExtra("cast_movie_id", movieId.trim());
                    detailsIntent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(detailsIntent);
                } else {
                    Toast.makeText(getApplicationContext(),  Util.getTextofLanguage(TrailerActivity.this, Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION), Toast.LENGTH_LONG).show();
                }
            }
        });


    /**
     * This code is responsible for initial player size .
     */

        LinearLayout.LayoutParams params1 = null;
        if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
            if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
            }
            else
            {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
            }
        }
        else
        {
            if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
            {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
            }
            else
            {
                params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
            }
        }
        player_layout.setLayoutParams(params1);


        /**
         * Following code is applicable to handle seekbar during live stream video playing.
         */
        
        if (content_types_id== LIVE_STREAM){
            seekBar.setEnabled(false);
            seekBar.setProgress(0);
        }else{
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

                // Call New Video Log Api.
                asyncVideoLogDetails = new AsyncVideoLogDetails();
                asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
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

                // ============End=====================//

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

        emVideoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressView.getVisibility() == View.VISIBLE) {
                    primary_ll.setVisibility(View.VISIBLE);
                    center_play_pause.setVisibility(View.GONE);
                    latest_center_play_pause.setVisibility(View.GONE);
                    current_time.setVisibility(View.GONE);

                } else {
                    if (primary_ll.getVisibility() == View.VISIBLE) {
                        primary_ll.setVisibility(View.GONE);
                        last_ll.setVisibility(View.GONE);
                        center_play_pause.setVisibility(View.GONE);
                        latest_center_play_pause.setVisibility(View.GONE);
                        current_time.setVisibility(View.GONE);
                        End_Timer();
                    } else {
                        primary_ll.setVisibility(View.VISIBLE);
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


        /**
         * Following code is responsible to handle screen orientation by orientation change button.
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
                    hideSystemUI();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {

                    LinearLayout.LayoutParams params1 = null;
                    if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
                        if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
                        }
                        else
                        {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
                        }
                    }
                    else
                    {
                        if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                        {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
                        }
                        else
                        {
                            params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
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
                    showSystemUI();
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
                Execute_Pause_Play();
            }
        });

        back.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    back.setImageResource(R.drawable.ic_back);

                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                }
                return false;
            }
        });

        emVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                
                try {

                    video_completed = false;
                    progressView.setVisibility(View.VISIBLE);
                    center_play_pause.setVisibility(View.GONE);
                    latest_center_play_pause.setVisibility(View.GONE);
                    
                    if (content_types_id== LIVE_STREAM){
                        asyncVideoLogDetails = new AsyncVideoLogDetails();
                        asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);


                        emVideoView.start();
                        updateProgressBar();
                    }else{
                        startTimer();

                        asyncVideoLogDetails = new AsyncVideoLogDetails();
                        asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
                        emVideoView.start();
                        updateProgressBar();
                    }
                } catch (Exception e) {
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backCalled();
            }
        });


        /**
         * Setting trailer URL to play the video.
         */
        try{
            emVideoView.setVideoURI(Uri.parse(playerModel.getVideoUrl()));
        }catch(Exception e){}


    }


    public void backCalled(){
        
        try{
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

            if (video_completed == false){

                AsyncResumeVideoLogDetails  asyncResumeVideoLogDetails = new AsyncResumeVideoLogDetails();
                asyncResumeVideoLogDetails.executeOnExecutor(threadPoolExecutor);
                return;
            }
            mHandler.removeCallbacks(updateTimeTask);
            if (emVideoView!=null) {
                emVideoView.release();
            }
            finish();
            overridePendingTransition(0, 0);
        }catch (Exception e){}
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

                            int currentPositionStr = millisecondsToString(emVideoView.getCurrentPosition());
                            playerPosition = currentPositionStr;


                            if (isFastForward == true) {
                                isFastForward = false;
                                log_temp_id = "0";

                                int duration = emVideoView.getDuration() / 1000;
                                if (currentPositionStr > 0 && currentPositionStr == duration) {
                                    asyncFFVideoLogDetails = new AsyncFFVideoLogDetails();
                                    watchStatus = "complete";
                                    asyncFFVideoLogDetails.executeOnExecutor(threadPoolExecutor);
                                } else {
                                    asyncFFVideoLogDetails = new AsyncFFVideoLogDetails();
                                    watchStatus = "halfplay";
                                    asyncFFVideoLogDetails.executeOnExecutor(threadPoolExecutor);
                                }

                            } else if (isFastForward == false && currentPositionStr >= millisecondsToString(playerPreviousPosition)) {

                                playerPreviousPosition = 0;

                                int duration = emVideoView.getDuration() / 1000;
                                if (currentPositionStr > 0 && currentPositionStr == duration) {
                                    asyncVideoLogDetails = new AsyncVideoLogDetails();
                                    watchStatus = "complete";
                                    asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);
                                } else if (currentPositionStr > 0 && currentPositionStr % 60 == 0) {
                                    asyncVideoLogDetails = new AsyncVideoLogDetails();
                                    watchStatus = "halfplay";
                                    asyncVideoLogDetails.executeOnExecutor(threadPoolExecutor);

                                }
                            }
                        }
                        //get the current timeStamp
                    }
                });
            }
        };
    }

    private class AsynGetIpAddress extends AsyncTask<Void, Void, Void> {
        String responseStr;


        @Override
        protected Void doInBackground(Void... params) {

            try {

                // Execute HTTP Post Request
                try {
                    URL myurl = new URL(APIUrlConstant.IP_ADDRESS_URL);
                    HttpsURLConnection con = (HttpsURLConnection)myurl.openConnection();
                    InputStream ins = con.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);

                    String inputLine;

                    while ((inputLine = in.readLine()) != null)
                    {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                    }

                    in.close();


                } catch (org.apache.http.conn.ConnectTimeoutException e){
                    ipAddressStr = "";

                } catch (UnsupportedEncodingException e) {

                    ipAddressStr = "";

                }catch (IOException e) {
                    ipAddressStr = "";

                }
                if(responseStr!=null){
                    Object json = new JSONTokener(responseStr).nextValue();
                    if (json instanceof JSONObject){
                        ipAddressStr = ((JSONObject) json).getString("ip");
                    }
                }

            }
            catch (Exception e) {
                ipAddressStr = "";

            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if(responseStr == null){
                ipAddressStr = "";
            }
            return;
        }

        protected void onPreExecute() {

        }
    }

    private class AsyncVideoLogDetails extends AsyncTask<Void, Void, Void> {
        //  ProgressDialog pDialog;
        String responseStr;
        int statusCode = 0;

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = APIUrlConstant.getVideoLogsUrl();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", authTokenStr.trim());
                httppost.addHeader("user_id", userIdStr.trim());
                httppost.addHeader("ip_address", ipAddressStr.trim());
                httppost.addHeader("movie_id", movieId.trim());
                httppost.addHeader("episode_id", episodeId.trim());
                httppost.addHeader("watch_status", watchStatus);
                httppost.addHeader("device_type", "2");
                httppost.addHeader("content_type", "2");
                httppost.addHeader("log_id", videoLogId);

                Log.v("MUVI", "authToken=" + authTokenStr.trim());
                Log.v("MUVI", "user_id=" + userIdStr.trim());
                Log.v("MUVI", "ip_address=" + ipAddressStr.trim());
                Log.v("MUVI", "movie_id=" + movieId.trim());
                Log.v("MUVI", "episode_id=" + episodeId.trim());
                Log.v("MUVI", "played_length=" + String.valueOf(playerPosition));
                Log.v("MUVI", "watch_status=" + watchStatus);
                Log.v("MUVI", "device_type=" + "2");
                Log.v("MUVI", "log_id=" + videoLogId);



                // Following code is changed due to NewVideoLog API ;

                httppost.addHeader("played_length", "" + (playerPosition - player_start_time));
                httppost.addHeader("log_temp_id", log_temp_id);
                httppost.addHeader("resume_time", "" + (playerPosition));

                Log.v("MUVI", "player_start_time===*****************=========" + player_start_time);
                Log.v("MUVI", "playerPosition======***************8======" + playerPosition);


                Log.v("MUVI", "played_length============" + (playerPosition - player_start_time));
                Log.v("MUVI", "log_temp_id============" + log_temp_id);
                Log.v("MUVI", "resume_time============" + (playerPosition));
                Log.v("MUVI", "playerPosition============" + playerPosition);
                Log.v("MUVI", "log_id============" + videoLogId);

                Log.v("MUVI", "user_id============" + userIdStr.trim());
                Log.v("MUVI", "movieId.trim()============" + movieId.trim());
                Log.v("MUVI", "episodeId.trim()============" + episodeId.trim());
                Log.v("MUVI", "watchStatus============" + watchStatus);


                //===============End=============================//


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("MUVI", "responseStr of videolog============" + responseStr);


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoLogId = "0";
                        }
                    });

                } catch (Exception e) {
                    videoLogId = "0";
                    e.printStackTrace();
                }

                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    if (statusCode == 200) {
                        videoLogId = myJson.optString("log_id");
                        log_temp_id = myJson.optString("log_temp_id");


                    } else {
                        videoLogId = "0";
                        log_temp_id = "0";
                    }

                }

            } catch (Exception e) {
                videoLogId = "0";
                log_temp_id = "0";

            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if (responseStr == null) {
                videoLogId = "0";
                log_temp_id = "0";
            }

            AsyncVideoBufferLogDetails asyncVideoBufferLogDetails = new AsyncVideoBufferLogDetails();
            asyncVideoBufferLogDetails.executeOnExecutor(threadPoolExecutor);
        }

        @Override
        protected void onPreExecute() {
            stoptimertask();
        }
    }

    private class AsyncFFVideoLogDetails extends AsyncTask<Void, Void, Void> {
        String responseStr;
        int statusCode = 0;

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = APIUrlConstant.getVideoLogsUrl();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", authTokenStr.trim());
                httppost.addHeader("user_id", userIdStr);
                httppost.addHeader("ip_address", ipAddressStr.trim());
                httppost.addHeader("movie_id", movieId.trim());
                httppost.addHeader("episode_id", episodeId.trim());
                httppost.addHeader("content_type", "2");
                httppost.addHeader("watch_status", watchStatus);

                httppost.addHeader("device_type", "2");
                httppost.addHeader("log_id", videoLogId);


                // Following code is changed due to NewVideoLog API ;

                httppost.addHeader("played_length", "" + (playerPosition - player_start_time));
                httppost.addHeader("log_temp_id", log_temp_id);
                httppost.addHeader("resume_time", "" + (playerPosition));


                Log.v("BIBHU11", "played_length============" + (playerPosition - player_start_time));


                Log.v("BIBHU11", "log_temp_id============" + log_temp_id);
                Log.v("BIBHU11", "resume_time============" + (playerPosition));
                Log.v("BIBHU11", "log_id============" + videoLogId);

                //===============End=============================//

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("MUVI", "responseStr of responseStr ff ============" + responseStr);

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoLogId = "0";

                        }

                    });

                } catch (IOException e) {
                    videoLogId = "0";

                    e.printStackTrace();
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    if (statusCode == 200) {
                        videoLogId = myJson.optString("log_id");
                        log_temp_id = myJson.optString("log_temp_id");

//                        Log.v("MUVI", "responseStr of restrict_stream_id============" + restrict_stream_id);
                    } else {
                        videoLogId = "0";
                        log_temp_id = "0";
                    }

                }

            } catch (Exception e) {
                videoLogId = "0";
                log_temp_id = "0";

            }

            return null;
        }


        protected void onPostExecute(Void result) {
            if (responseStr == null) {
                videoLogId = "0";
                log_temp_id = "0";
            }
            AsyncVideoBufferLogDetails asyncVideoBufferLogDetails = new AsyncVideoBufferLogDetails();
            asyncVideoBufferLogDetails.executeOnExecutor(threadPoolExecutor);

        }

        @Override
        protected void onPreExecute() {
            // updateSeekBarThread.stop();
            stoptimertask();

        }


    }

    private class AsyncResumeVideoLogDetails extends AsyncTask<Void, Void, Void> {
        //  ProgressDialog pDialog;
        String responseStr;
        int statusCode = 0;
        String watchSt = "halfplay";

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = APIUrlConstant.getVideoLogsUrl();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", authTokenStr.trim());
                httppost.addHeader("user_id", userIdStr.trim());
                httppost.addHeader("ip_address", ipAddressStr.trim());
                httppost.addHeader("movie_id", movieId.trim());
                httppost.addHeader("episode_id", episodeId.trim());
                httppost.addHeader("content_type", "2");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (current_matching_time >= emVideoView.getDuration()) {
                            watchSt = "complete";
                        }

                    }

                });
                httppost.addHeader("watch_status", watchSt);


                // Following code is changed due to NewVideoLog API ;

                httppost.addHeader("played_length", "" + (playerPosition - player_start_time));
                httppost.addHeader("log_temp_id", log_temp_id);
                httppost.addHeader("resume_time", "" + (playerPosition));

                Log.v("BIBHU11", "played_length============" + (playerPosition - player_start_time));
                Log.v("BIBHU11", "log_temp_id============" + log_temp_id);
                Log.v("BIBHU11", "resume_time============" + (playerPosition));
                Log.v("BIBHU11", "log_id============" + videoLogId);

                //===============End=============================//


                httppost.addHeader("device_type", "2");
                httppost.addHeader("log_id", videoLogId);


                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());
                    Log.v("MUVI", "responseStr of responseStr============" + responseStr);


                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoLogId = "0";

                        }

                    });

                } catch (IOException e) {
                    videoLogId = "0";

                    e.printStackTrace();
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    if (statusCode == 200) {
                        videoLogId = myJson.optString("log_id");
                        log_temp_id = myJson.optString("log_temp_id");
                    } else {
                        videoLogId = "0";
                        log_temp_id = "0";
                    }

                }

            } catch (Exception e) {
                videoLogId = "0";
                log_temp_id = "0";

            }

            return null;
        }


        protected void onPostExecute(Void result) {
         /*   try {
                if (pDialog.isShowing())
                    pDialog.dismiss();
            } catch (IllegalArgumentException ex) {
                videoLogId = "0";
            }*/
            if (responseStr == null) {
                videoLogId = "0";
                log_temp_id = "0";

            }
            mHandler.removeCallbacks(updateTimeTask);
            if (emVideoView != null) {
                emVideoView.release();
            }

            finish();
            overridePendingTransition(0, 0);
            //startTimer();
            return;


        }

        @Override
        protected void onPreExecute() {
            stoptimertask();
        }
    }

    private class AsyncVideoBufferLogDetails extends AsyncTask<Void, Void, Void> {
        //  ProgressDialog pDialog;
        String responseStr;
        int statusCode = 0;

        @Override
        protected Void doInBackground(Void... params) {

            String urlRouteList = APIUrlConstant.getVideoBufferLogsUrl();
            try {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(urlRouteList);
                httppost.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=UTF-8");
                httppost.addHeader("authToken", authTokenStr.trim());
                httppost.addHeader("user_id", userIdStr);
                httppost.addHeader("ip_address", ipAddressStr.trim());
                httppost.addHeader("movie_id", movieId.trim());
                httppost.addHeader("episode_id", episodeId.trim());
                httppost.addHeader("content_type", "2");
                httppost.addHeader("device_type", "2");
                httppost.addHeader("log_id", videoBufferLogId);
                httppost.addHeader("resolution", resolution.trim());
                httppost.addHeader("start_time", String.valueOf(playerPosition));
                httppost.addHeader("end_time", String.valueOf(playerPosition));
                httppost.addHeader("log_unique_id", videoBufferLogUniqueId);
                httppost.addHeader("location", Location);
                httppost.addHeader("video_type", "mped_dash");

                if (videoBufferLogUniqueId.equals("0"))
                    httppost.addHeader("totalBandwidth", "0");
                else
                    httppost.addHeader("totalBandwidth", "" + CurrentUsedData);

                Log.v("MUVI", "Response of the bufferlog CurrentUsedData======#############=" + CurrentUsedData);

                // Execute HTTP Post Request
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    responseStr = EntityUtils.toString(response.getEntity());

                    Log.v("MUVI", "Response of the bufferlog =" + responseStr);

                } catch (org.apache.http.conn.ConnectTimeoutException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            videoBufferLogId = "0";
                            videoBufferLogUniqueId = "0";
                            Location = "0";
                        }
                    });

                } catch (IOException e) {
                    videoBufferLogId = "0";
                    videoBufferLogUniqueId = "0";
                    Location = "0";
                    e.printStackTrace();
                }
                if (responseStr != null) {
                    JSONObject myJson = new JSONObject(responseStr);
                    statusCode = Integer.parseInt(myJson.optString("code"));
                    if (statusCode == 200) {
                        videoBufferLogId = myJson.optString("log_id");
                        videoBufferLogUniqueId = myJson.optString("log_unique_id");
                        Location = myJson.optString("location");

                    } else {
                        videoBufferLogId = "0";
                        videoBufferLogUniqueId = "0";
                        Location = "0";
                    }
                }
            } catch (Exception e) {
                videoBufferLogId = "0";
                videoBufferLogUniqueId = "0";
                Location = "0";
            }

            return null;
        }


        protected void onPostExecute(Void result) {

            if (responseStr == null) {

                videoBufferLogId = "0";
                videoBufferLogUniqueId = "0";
                Location = "0";
            }
            if (!watchStatus.equals("complete"))
                startTimer();

            return;
        }

        @Override
        protected void onPreExecute() {

        }
    }

    private int millisecondsToString(int milliseconds)  {
        // int seconds = (int) (milliseconds / 1000) % 60 ;
        int seconds = (int) (milliseconds / 1000);

        return seconds;
    }


    /**
     * This method is responsible to handle screen orientation.
     * @param orientation
     */
    @Override
    public void onOrientationChange(int orientation) {

        if (orientation == 90){
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
        }
        else if (orientation == 270){


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
        } else if (orientation == 180){

            LinearLayout.LayoutParams params1 = null;
            if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
                if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
                }
                else
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
                }
            }
            else
            {
                if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
                }
                else
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
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
            showSystemUI();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        } else if (orientation == 0) {

            LinearLayout.LayoutParams params1 = null;
            if (((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_LARGE) || ((getResources().getConfiguration().screenLayout & SCREENLAYOUT_SIZE_MASK) == SCREENLAYOUT_SIZE_XLARGE)){
                if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
                }
                else
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*45)/100);
                }
            }
            else
            {
                if(TrailerActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
                }
                else
                {
                    params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,(screenHeight*40)/100);
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
            showSystemUI();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        current_time_position_timer();

    }


    private void updateProgressBar() {
        mHandler.postDelayed(updateTimeTask, 1000);
    }

    private Runnable updateTimeTask = new Runnable() {
        public void run() {

            if (emVideoView.getCurrentPosition() % 2 == 0)
                BufferBandWidth();


            seekBar.setProgress(emVideoView.getCurrentPosition());
            seekBar.setMax(emVideoView.getDuration());
            Calcute_Currenttime_With_TotalTime();
            mHandler.postDelayed(this, 1000);

            if (content_types_id!= LIVE_STREAM){
                seek_label_pos = (((seekBar.getRight() - seekBar.getLeft()) * seekBar.getProgress()) / seekBar.getMax()) + seekBar.getLeft();
            }

            current_matching_time = emVideoView.getCurrentPosition();


            if ((previous_matching_time == current_matching_time) && (current_matching_time < emVideoView.getDuration())) {
                progressView.setVisibility(View.VISIBLE);
                center_play_pause.setVisibility(View.GONE);
                latest_center_play_pause.setVisibility(View.GONE);
                previous_matching_time = current_matching_time;
            } else {
                if (content_types_id== LIVE_STREAM){
                }
                else
                {
                    if (current_matching_time >= emVideoView.getDuration()) {
                        mHandler.removeCallbacks(updateTimeTask);
                        seekBar.setProgress(0);
                        current_time.setText("00:00:00");
                        total_time.setText("00:00:00");
                        previous_matching_time = 0;
                        current_matching_time = 0;
                        video_completed = true;
                        backCalled();
                    }
                }

                previous_matching_time = current_matching_time;
                ((ProgressBar) findViewById(R.id.progress_view)).setVisibility(View.GONE);
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

    public void onBackPressed() {
        super.onBackPressed();

        try{
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
        }catch (Exception e){}
    }
    @Override
    protected void onUserLeaveHint()
    {
        try{

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
            super.onUserLeaveHint();
        }catch (Exception e){}

    }


    public void Execute_Pause_Play() {
        if (emVideoView.isPlaying()) {
            emVideoView.pause();
            latest_center_play_pause.setImageResource(R.drawable.center_ic_media_play);
            center_play_pause.setImageResource(R.drawable.ic_media_play);
            mHandler.removeCallbacks(updateTimeTask);
        } else {
            if (video_completed) {

                if (content_types_id!= LIVE_STREAM){
                    // onBackPressed();
                    backCalled();
                }

            } else {
                emVideoView.start();
                latest_center_play_pause.setImageResource(R.drawable.center_ic_media_pause);
                center_play_pause.setImageResource(R.drawable.ic_media_pause);
                mHandler.removeCallbacks(updateTimeTask);
                updateProgressBar();
            }

        }
    }

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

            primary_ll.setVisibility(View.GONE);
            last_ll.setVisibility(View.GONE);
            center_play_pause.setVisibility(View.GONE);
            latest_center_play_pause.setVisibility(View.GONE);
        }

    }

    public void Instant_End_Timer() {
        if (center_pause_paly_timer_is_running) {
            center_pause_paly_timer.cancel();
            center_pause_paly_timer_is_running = false;
        }

    }

    public void showCurrentTime ()
    {

        current_time.setText(Current_Time);
        current_time_position_timer();
    }

    public void current_time_position_timer()
    {
        final Timer timer  = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (content_types_id!= LIVE_STREAM){

                            seek_label_pos = (((seekBar.getRight() - seekBar.getLeft()) * seekBar.getProgress()) / seekBar.getMax()) + seekBar.getLeft();
                            current_time.setX(seek_label_pos - current_time.getWidth() / 2);
                            timer.cancel();
                        }
                    }
                });
            }
        },0,100);
    }

    private void hideSystemUI() {
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
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {

        if (playerModel.getVideoStory().trim() != null && !playerModel.getVideoStory().trim().matches("")){
            videoStoryTextView.setText(playerModel.getVideoStory());
            videoStoryTextView.setVisibility(View.VISIBLE);
            ResizableCustomView.doResizeTextView(TrailerActivity.this,videoStoryTextView, MAX_LINES, Util.getTextofLanguage(TrailerActivity.this, VIEW_MORE, DEFAULT_VIEW_MORE), true);

        } else {
            videoStoryTextView.setVisibility(View.GONE);
        }
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE

        );
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
                    PreviousUsedData = prev_data;
                }
            }

        } catch (Exception e) {

        }
    }


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
                        CurrentUsedData = CurrentUsedData - (DataUsedByDownloadContent());


                    }
                }
            } catch (Exception e) {

            }

            return null;
        }
    }


    public long DataUsedByDownloadContent() {

        try {

            SQLiteDatabase DB = TrailerActivity.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
            Cursor cursor = DB.rawQuery("SELECT " + DBHelper.COLUMN_DOWNLOADID + " FROM " + DBHelper.TABLE_NAME + " ", null);
            int count = cursor.getCount();

            long Total = 0;

            if (count > 0) {
                if (cursor.moveToFirst()) {
                    do {
                        DownloadManager downloadManager1 = (DownloadManager) TrailerActivity.this.getSystemService(DOWNLOAD_SERVICE);
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


}