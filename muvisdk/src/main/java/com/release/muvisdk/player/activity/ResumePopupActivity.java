package com.release.muvisdk.player.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.release.muvi.muvisdk.R;
import com.release.muvisdk.player.utils.SensorOrientationChangeNotifier;
import com.release.muvisdk.player.utils.Util;


public class ResumePopupActivity extends Activity implements SensorOrientationChangeNotifier.Listener {
    TranslatedLanguage translatedLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_activity_resume_playing);
        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.mainlayout);

       /* Window window = ResumePopupActivity.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(Color.parseColor("#00ff00"));*/

        if(Util.player_description)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            if (!Util.landscape ) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                //current_time.setVisibility(View.GONE);
            } else
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }


        LinearLayout popupLayout = (LinearLayout) findViewById(R.id.popupLayout);
        Button yesButton = (Button) findViewById(R.id.yesButton);
        Button cancelButton = (Button) findViewById(R.id.cancelButton);
        TextView resumeTitleTextView = (TextView) findViewById(R.id.resumeTitleTextView);
        translatedLanguage=new TranslatedLanguage(ResumePopupActivity.this);
        resumeTitleTextView.setText(translatedLanguage.getResumeMsg());
        yesButton.setText(translatedLanguage.getContinuebtn());
        cancelButton.setText(translatedLanguage.getCancelBtn());

        Animation topTobottom = AnimationUtils.loadAnimation(this, R.anim.top_bottom);
        popupLayout.startAnimation(topTobottom);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("yes", "1002");
                setResult(RESULT_OK, playerIntent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playerIntent = new Intent();
                playerIntent.putExtra("yes", "1003");
                setResult(RESULT_OK, playerIntent);
                finish();
            }
        });
        mainLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SensorOrientationChangeNotifier.getInstance(ResumePopupActivity.this).addListener(this);
    }

    @Override
    public void onOrientationChange(int orientation) {



        if(Util.player_description)
        {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        else
        {
            if (orientation == 90) {
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                //current_time.setVisibility(View.GONE);
            } else if (orientation == 270) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                //current_time.setVisibility(View.GONE);

                // Do some landscape stuff
            } else if (orientation == 180) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //current_time.setVisibility(View.GONE);

            } else if (orientation == 0) {

                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                //current_time.setVisibility(View.GONE);
            }

        }

    }
}
