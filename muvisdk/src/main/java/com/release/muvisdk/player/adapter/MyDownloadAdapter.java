package com.release.muvisdk.player.adapter;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.release.muvi.muvisdk.R;
import com.release.muvisdk.player.activity.SdkMyDownloads;
import com.release.muvisdk.player.model.DownloadContentModel;
import com.release.muvisdk.player.utils.DBHelper;
import com.release.muvisdk.player.utils.Util;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Muvi on 1/16/2017.
 */
public class MyDownloadAdapter extends BaseAdapter {
    SdkMyDownloads activity;
    ArrayList<DownloadContentModel> downloadModel;
    DownloadContentModel audio;
    DBHelper dbHelper;
    DownloadManager downloadManager;
    public MyDownloadAdapter(SdkMyDownloads activity , ArrayList<DownloadContentModel> downloadModel) {
        this.activity = activity;
        this.downloadModel = downloadModel;
        dbHelper = new DBHelper(activity);
        downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);

    }

    @Override
    public int getCount() {
        return downloadModel.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v;
        LayoutInflater inflater = activity.getLayoutInflater();
        v = inflater.inflate(R.layout.custom_offlist, null);
        TextView title = (TextView) v.findViewById(R.id.textView);
        TextView realise_date = (TextView) v.findViewById(R.id.textView2);
        TextView genre = (TextView) v.findViewById(R.id.textView3);
        TextView duration = (TextView) v.findViewById(R.id.textView4);
        ImageView image = (ImageView) v.findViewById(R.id.imageView);
        ImageView image1 = (ImageView) v.findViewById(R.id.imageView1);


        Log.v("BIBHU11","downloadModel.get(position).getPoster()=="+downloadModel.get(position).getPoster());

//
        Picasso.with(activity)
                .load(downloadModel.get(position).getPoster())
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .into(image);
        String data[] = downloadModel.get(position).getMUVIID().trim().split("@@@");
        title.setText(data[0]);
        realise_date.setText("");
        if(!downloadModel.get(position).getGenere().equals(""))
            genre.setText(downloadModel.get(position).getGenere());
        else
            genre.setVisibility(View.GONE);
        String dd = downloadModel.get(position).getDuration();
        Log.v("SUBHA", dd);
        duration.setText(dd);

        image1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder dlgAlert = new AlertDialog.Builder(activity, R.style.MyAlertDialogStyle);
                dlgAlert.setTitle(Util.getTextofLanguage(activity, Util.WANT_TO_DELETE, Util.DEFAULT_WANT_TO_DELETE));
                dlgAlert.setPositiveButton(Util.getTextofLanguage(activity, Util.DELETE_BTN, Util.DEFAULT_DELETE_BTN), null);
                dlgAlert.setCancelable(false);
                dlgAlert.setPositiveButton(Util.getTextofLanguage(activity, Util.DELETE_BTN, Util.DEFAULT_DELETE_BTN),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();

                                DownloadManager downloadManager = (DownloadManager) activity.getSystemService(DOWNLOAD_SERVICE);
                                downloadManager.remove(downloadModel.get(position).getDOWNLOADID());

                                String path1 = downloadModel.get(position).getPath().trim();
                                File file = new File(path1);
                                if (file != null && file.exists()) {
                                    file.delete();
                                }


                                SQLiteDatabase DB = activity.openOrCreateDatabase(DBHelper.DATABASE_NAME, MODE_PRIVATE, null);
                                Cursor cursor = DB.rawQuery("SELECT LANGUAGE,PATH FROM SUBTITLE_LUIMERE WHERE UID = '"+downloadModel.get(position).getUniqueId()+"'", null);
                                int count = cursor.getCount();

                                if(count>0)
                                {
                                    String Query = "DELETE FROM "+DBHelper.TABLE_NAME_SUBTITLE_LUIMERE+" WHERE UID  = '"+downloadModel.get(position).getUniqueId()+"'";
                                    DB.execSQL(Query);
                                }

                                Cursor cursor1 = DB.rawQuery("SELECT * FROM "+DBHelper.RESUME_WATCH+" WHERE UniqueId = '"+downloadModel.get(position).getUniqueId()+"'", null);
                                int count1 = cursor.getCount();

                                if(count1>0)
                                {
                                    String Query = "DELETE FROM "+DBHelper.RESUME_WATCH+" WHERE UniqueId  = '"+downloadModel.get(position).getUniqueId()+"'";
                                    DB.execSQL(Query);
                                    Log.v("BIBHU11","Resume watch record deleted");
                                }


                                audio = dbHelper.getContact(downloadModel.get(position).getUniqueId().trim());
                                downloadModel.remove(position);
                                notifyDataSetChanged();
                                activity.visible();

                                if (audio != null) {
                                        dbHelper.deleteRecord(audio);
                                }

                            }
                        });
                dlgAlert.setNegativeButton(Util.getTextofLanguage(activity,Util.CANCEL_BUTTON,Util.DEFAULT_CANCEL_BUTTON),null);
                dlgAlert.setCancelable(false);
                dlgAlert.setNegativeButton(Util.getTextofLanguage(activity,Util.CANCEL_BUTTON,Util.DEFAULT_CANCEL_BUTTON),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                dlgAlert.create().show();
            }


        });

        return v;
    }


}
