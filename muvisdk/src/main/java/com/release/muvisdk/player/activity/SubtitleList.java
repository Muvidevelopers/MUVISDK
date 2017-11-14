package com.release.muvisdk.player.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.release.muvi.muvisdk.R;
import com.release.muvisdk.player.adapter.SubtitleAdapter;
import com.release.muvisdk.player.utils.Util;

import java.util.ArrayList;

public class SubtitleList extends Activity {

    ListView listView;
    ArrayList<String> subtitle_list = new ArrayList<>();
    SubtitleAdapter subtitleAdapter;
    LinearLayout total_layout;

    ArrayList<String> SubTitleName = new ArrayList<>();
    ArrayList<String> SubTitlePath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_activity_subtitle_list);
        listView = (ListView) findViewById(R.id.listView);
        total_layout = (LinearLayout) findViewById(R.id.total_layout);

        Util.call_finish_at_onUserLeaveHint = true;


        if (getIntent().getStringArrayListExtra("SubTitleName") != null) {
            SubTitleName = getIntent().getStringArrayListExtra("SubTitleName");
        } else {
            SubTitleName.clear();
        }

        if (getIntent().getStringArrayListExtra("SubTitlePath") != null) {
            SubTitlePath = getIntent().getStringArrayListExtra("SubTitlePath");
        } else {
            SubTitlePath.clear();
        }

        subtitle_list.add("Off");

        for(int i=0;i<SubTitleName.size();i++)
        {
            subtitle_list.add(SubTitleName.get(i));
        }

        subtitleAdapter = new SubtitleAdapter(SubtitleList.this,subtitle_list);
        listView.setAdapter(subtitleAdapter);

        Animation topTobottom = AnimationUtils.loadAnimation(this, R.anim.bottom_top);
        listView.startAnimation(topTobottom );


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0)
                {
                    Util.DefaultSubtitle ="Off";
                }
                else
                {

                    Util.DefaultSubtitle =SubTitleName.get(position-1).trim();
                }


                Intent playerIntent = new Intent();
                playerIntent.putExtra("position", ""+position);
                playerIntent.putExtra("type", "subtitle");
                setResult(RESULT_OK, playerIntent);
                finish();
            }
        });

        total_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("type", "subtitle");
                playerIntent.putExtra("position", "nothing");
                setResult(RESULT_OK, playerIntent);
                finish();
                return false;
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent playerIntent = new Intent();
        playerIntent.putExtra("type", "subtitle");
        playerIntent.putExtra("position", "nothing");
        setResult(RESULT_OK, playerIntent);
        finish();
    }
}
