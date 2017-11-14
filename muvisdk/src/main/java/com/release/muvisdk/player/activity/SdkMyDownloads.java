package com.release.muvisdk.player.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.google.android.gms.cast.MediaInfo;
import com.release.muvi.muvisdk.R;
import com.release.muvisdk.api.APIUrlConstant;
import com.release.muvisdk.player.adapter.MyDownloadAdapter;

import com.release.muvisdk.player.model.DownloadContentModel;
import com.release.muvisdk.player.model.DownloadModel;
import com.release.muvisdk.player.utils.DBHelper;
import com.release.muvisdk.player.utils.ProgressBarHandler;
import com.release.muvisdk.player.utils.Util;


import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static com.release.muvisdk.player.utils.Constants.CHECK_USER_ID_MSG;
import static com.release.muvisdk.player.utils.Constants.DOMAIN_NAME_MSG;
import static com.release.muvisdk.player.utils.Constants.VALID_AUTHTOKEN;
import static com.release.muvisdk.player.utils.Constants.VALID_EMAIL;
import static com.release.muvisdk.player.utils.DBHelper.TABLE_NAME_SUBTITLE_LUIMERE;
import static java.lang.Thread.sleep;


public class SdkMyDownloads extends AppCompatActivity {


    String download_id_from_watch_access_table = "";
    ListView list;
    TextView noDataTextView;
    RelativeLayout nodata;
    MyDownloadAdapter adapter;
    String emailIdStr = "";
    DBHelper dbHelper;
    ArrayList<DownloadContentModel> download;
    ProgressBarHandler pDialog;
    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();

    ArrayList<String> Chromecast_Subtitle_Url = new ArrayList<String>();
    ArrayList<String> Chromecast_Subtitle_Language_Name = new ArrayList<String>();
    ArrayList<String> Chromecast_Subtitle_Code = new ArrayList<String>();


    int Played_Length = 0;
    String watch_status = "start";

    String PlayedLength = "0";
    String ipAddressStr = "";
    String MpdVideoUrl = "";
    String licenseUrl = "";
    String SubtitleUrl = "";
    String SubtitleLanguage = "";
    String SubtitleCode = "";
    String user_Id = "";
    String rootUrl = "";

    /*chromecast-------------------------------------*/
    View view;

    private static final int MAX_LINES = 3;


    public enum PlaybackLocation {
        LOCAL,
        REMOTE
    }

    /**
     * List of various states that we can be in
     */
    public enum PlaybackState {
        PLAYING, PAUSED, BUFFERING, IDLE
    }

    private PlaybackLocation mLocation;
    private PlaybackState mPlaybackState;
    private final float mAspectRatio = 72f / 128;
    private AQuery mAquery;
    private MediaInfo mSelectedMedia;
    String authToken = "";


    int corePoolSize = 60;
    int maximumPoolSize = 80;
    int keepAliveTime = 10;
    BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>(maximumPoolSize);
    Executor threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, TimeUnit.SECONDS, workQueue);
    int Position = 0;
    public static ProgressBarHandler progressBarHandler;

    DownloadModel downloadModel  = new DownloadModel();


    MediaInfo mediaInfo;
    /*chromecast-------------------------------------*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sdk_mydownload);


        /**
         * Customizing Toolbar.
         */

        Toolbar mActionBarToolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mActionBarToolbar);
        mActionBarToolbar.setTitle(Util.getTextofLanguage(SdkMyDownloads.this,Util.MY_DOWNLOAD,Util.DEFAULT_MY_DOWNLOAD));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));

        list= (ListView)findViewById(R.id.listView);
        nodata= (RelativeLayout) findViewById(R.id.noData);
        noDataTextView= (TextView) findViewById(R.id.noDataTextView);


        /**
         * Initialising database for future use.
         */
        dbHelper=new DBHelper(SdkMyDownloads.this);
        dbHelper.getWritableDatabase();


        /**
         * Collecting data form download model.
         */
        downloadModel = (DownloadModel) getIntent().getSerializableExtra("DownloadModel");
        user_Id = downloadModel.getUserId();
        authToken = downloadModel.getAuthToken();
        emailIdStr = downloadModel.getEmail();
//        rootUrl = downloadModel.getDomainName();
        rootUrl = APIUrlConstant.BASE_URl;


        /**
         * Authenticating collected data from download model .
         */
        if((rootUrl ==null) || rootUrl.equals("")){
            Toast.makeText(SdkMyDownloads.this,DOMAIN_NAME_MSG, Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        if((user_Id ==null) || user_Id.equals("")){
            Toast.makeText(SdkMyDownloads.this,CHECK_USER_ID_MSG, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if((authToken ==null) || authToken.equals("")){
            Toast.makeText(SdkMyDownloads.this,VALID_AUTHTOKEN, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if((emailIdStr ==null) || emailIdStr.equals("")){
            Toast.makeText(SdkMyDownloads.this,VALID_EMAIL, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        /**
         * Following receiver is responsible to reload the download content list , when a new video is downloaded .
         */
        registerReceiver(UpadateDownloadList, new IntentFilter("NewVodeoAvailable"));


        /**
         * Fetching downloaded content info depending on user.
         */
        download=dbHelper.getContactt(emailIdStr,1);
        if(download.size()>0) {
            adapter = new MyDownloadAdapter(SdkMyDownloads.this , download);
            list.setAdapter(adapter);
        }else {
            nodata.setVisibility(View.VISIBLE);
            noDataTextView.setText(Util.getTextofLanguage(SdkMyDownloads.this,Util.NO_CONTENT,Util.DEFAULT_NO_CONTENT));
        }


        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Position = position;
                SubtitleUrl = "";
                MoveToOfflinePlayer();
            }
        });
    }

    public void visible(){
        if(download.size()>0) {
            adapter = new MyDownloadAdapter(SdkMyDownloads.this , download);
            list.setAdapter(adapter);

        }else {
            nodata.setVisibility(View.VISIBLE);
            noDataTextView.setText(Util.getTextofLanguage(SdkMyDownloads.this,Util.NO_CONTENT,Util.DEFAULT_NO_CONTENT));
        }
    }


    public void ShowRestrictionMsg(String msg) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(SdkMyDownloads.this, R.style.MyAlertDialogStyle);

        dlgAlert.setMessage(msg);
        dlgAlert.setTitle(Util.getTextofLanguage(SdkMyDownloads.this, Util.SORRY, Util.DEFAULT_SORRY));
        dlgAlert.setCancelable(false);
        dlgAlert.setPositiveButton(Util.getTextofLanguage(SdkMyDownloads.this, Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        dlgAlert.create().show();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(UpadateDownloadList);
    }


    private BroadcastReceiver UpadateDownloadList = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.v("BIBHU1","Onreceive called");

            download=dbHelper.getContactt(emailIdStr,1);
            if(download.size()>0) {
                adapter = new MyDownloadAdapter(SdkMyDownloads.this,download);
                list.setAdapter(adapter);
                nodata.setVisibility(View.GONE);
            }
        }
    };


    public void MoveToOfflinePlayer() {
        SubTitleName.clear();
        SubTitlePath.clear();

        SQLiteDatabase DB = SdkMyDownloads.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
        Cursor cursor = DB.rawQuery("SELECT LANGUAGE,PATH FROM "+TABLE_NAME_SUBTITLE_LUIMERE+" WHERE UID = '" + download.get(Position).getUniqueId() + "'", null);
        int count = cursor.getCount();

        /**
         * Getting subtitle info form data base for offline player .
         */

        if (count > 0) {
            if (cursor.moveToFirst()) {
                do {
                    SubTitleName.add(cursor.getString(0).trim());
                    SubTitlePath.add(cursor.getString(1).trim());
                } while (cursor.moveToNext());
            }
        }


        if (downloadModel.getRestrictionStatus()){
            if (!CheckAccessPeriodOfDpwnloadContent()) {
                return;
            }
    }

        /**
         * This is applicable for resume watch feature on downloaed content .
         */

        Cursor cursor11 = DB.rawQuery("SELECT * FROM "+DBHelper.RESUME_WATCH+" WHERE UniqueId = '"+download.get(Position).getUniqueId()+"'", null);
        int count11 = cursor11.getCount();

        if (count11 > 0) {
            if (cursor11.moveToFirst()) {
                do {
                    PlayedLength = cursor11.getString(1).trim();
                } while (cursor11.moveToNext());
            }
        }




        pDialog = new ProgressBarHandler(SdkMyDownloads.this);
        pDialog.show();

        new Thread(new Runnable(){
            public void run(){

                final String pathh=download.get(Position).getPath();

                final String data[] = download.get(Position).getMUVIID().trim().split("@@@");
                final String titles=data[0];
                final String gen=download.get(Position).getGenere();
                final String tok=download.get(Position).getToken();
                final String contentid=download.get(Position).getContentid();
                final String muviid=download.get(Position).getMuviid();
                final String poster=download.get(Position).getPoster();
                final String vidduration=download.get(Position).getDuration();
                final String filename=pathh.substring(pathh.lastIndexOf("/") + 1);


                try {
                    sleep(1200);

                    runOnUiThread(new Runnable() {
                        //
                        @Override
                        public void run() {

                            Intent in=new Intent(SdkMyDownloads.this,MarlinBroadbandExample.class);
                            Log.v("SUBHA", "PATH" + pathh);


                            in.putExtra("rootUrl", rootUrl);
                            in.putExtra("authToken", authToken);
                            in.putExtra("SubTitleName", SubTitleName);
                            in.putExtra("SubTitlePath", SubTitlePath);
                            in.putExtra("FILE", pathh);
                            in.putExtra("Title", titles);
                            in.putExtra("TOK", tok);
                            in.putExtra("poster", poster);
                            in.putExtra("contid", contentid);
                            in.putExtra("gen", gen);
                            in.putExtra("muvid", muviid);
                            in.putExtra("vid", vidduration);
                            in.putExtra("FNAME", filename);
                            in.putExtra("download_id_from_watch_access_table", download_id_from_watch_access_table);
                            in.putExtra("PlayedLength", PlayedLength);
                            in.putExtra("UniqueId",""+download.get(Position).getUniqueId());
                            in.putExtra("streamId",data[1]);
                            in.putExtra("download_content_type",""+download.get(Position).getDownloadContentType());
                            in.putExtra("user_id",user_Id);
                            in.putExtra("email",emailIdStr);
                            in.putExtra("Chromecast_Subtitle_Url",Chromecast_Subtitle_Url);
                            in.putExtra("Chromecast_Subtitle_Language_Name",Chromecast_Subtitle_Language_Name);
                            in.putExtra("Chromecast_Subtitle_Code",Chromecast_Subtitle_Code);
                            startActivity(in);

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    if (pDialog != null && pDialog.isShowing()) {
                                        pDialog.hide();
                                        pDialog = null;
                                    }
                                }
                            });
                        }
                    });


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    public boolean CheckAccessPeriodOfDpwnloadContent() {

        /**
         *  following block is responsible for restriction on download content .....
         */

        SQLiteDatabase DB = SdkMyDownloads.this.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);

        long server_current_time = 0;
        long watch_period = -1;
        long access_period = -1;
        long initial_played_time = 0;
        long updated_server_current_time = 0;

        Cursor cursor1 = DB.rawQuery("SELECT server_current_time , watch_period , access_period , initial_played_time , updated_server_current_time,download_id FROM " + DBHelper.WATCH_ACCESS_INFO + " WHERE download_id = '" + download.get(Position).getDOWNLOADID() + "'", null);
        int count1 = cursor1.getCount();

        if (count1 > 0) {
            if (cursor1.moveToFirst()) {
                do {
                    server_current_time = cursor1.getLong(0);
                    watch_period = cursor1.getLong(1);
                    access_period = cursor1.getLong(2);
                    initial_played_time = cursor1.getLong(3);
                    updated_server_current_time = cursor1.getLong(4);
                    download_id_from_watch_access_table = cursor1.getString(5);


                    Log.v("BIBHU3", "server_current_time============" + server_current_time);
                    Log.v("BIBHU3", "watch_period============" + watch_period);
                    Log.v("BIBHU3", "access_period============" + access_period);
                    Log.v("BIBHU3", "initial_played_time============" + initial_played_time);
                    Log.v("BIBHU3", "updated_server_current_time============" + updated_server_current_time);
                    Log.v("BIBHU3", "download_id_from_watch_access_table============" + download_id_from_watch_access_table);

                } while (cursor1.moveToNext());
            }
        } else {
            return false;
        }

        if (initial_played_time == 0) {
            if (((server_current_time < System.currentTimeMillis()) && (access_period > System.currentTimeMillis())) || (access_period == -1)) {
                String Qry = "UPDATE " + DBHelper.WATCH_ACCESS_INFO + " SET initial_played_time = '" + System.currentTimeMillis() + "'" +
                        " WHERE download_id = '" + download.get(Position).getDOWNLOADID() + "' ";

                DB.execSQL(Qry);
                return true;
            } else {
                ShowRestrictionMsg("You don't have access to play this video.");
                return false;
            }
        } else {
            if (updated_server_current_time < System.currentTimeMillis() || access_period == -1) {
                if (access_period == -1 || (System.currentTimeMillis() < access_period)) // && (((System.currentTimeMillis() - initial_played_time) < watch_period)) || watch_period == -1)
                {
                    String Qry = "UPDATE " + DBHelper.WATCH_ACCESS_INFO + " SET updated_server_current_time = '" + System.currentTimeMillis() + "'" +
                            " WHERE download_id = '" + download.get(Position).getDOWNLOADID() + "' ";
                    DB.execSQL(Qry);
                    return true;
                } else {
                    ShowRestrictionMsg("You don't have access to play this video.");
                    return false;
                }
            } else {
                ShowRestrictionMsg("You don't have access to play this video.");
                return false;
            }
        }

    }

}



