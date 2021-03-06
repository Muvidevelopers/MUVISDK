package com.release.muvi.sdk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.release.muvisdk.api.apiController.GetAppHomeFeatureAsyncTask;
import com.release.muvisdk.api.apiModel.AppHomeFeatureInputModel;
import com.release.muvisdk.api.apiModel.AppHomeFeatureOutputModel;
import com.release.muvisdk.player.activity.PlayerActivity;
import com.release.muvisdk.player.model.Player;

import static com.release.muvisdk.player.utils.Util.DEFAULT_SELECTED_LANGUAGE_CODE;
import static com.release.muvisdk.player.utils.Util.SELECTED_LANGUAGE_CODE;

public class MainActivity extends AppCompatActivity implements GetAppHomeFeatureAsyncTask.AppHomeFeature{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppHomeFeatureInputModel appHomeFeatureInputModel = new AppHomeFeatureInputModel();

//        GetAppHomeFeatureAsyncTask getAppHomeFeatureAsyncTask = new GetAppHomeFeatureAsyncTask(appHomeFeatureInputModel, this,MainActivity.this);
//        getAppHomeFeatureAsyncTask.executeOnExecutor(threadPoolExecutor);

//        Player playerModel = new Player(MainActivity.this);
        Player playerModel = new Player();
        playerModel.setVideoUrl("ms3://ms3.test.expressplay.com:8443/hms/ms3/rights/?b=ABMABAADERwACURSTTQtTXV2aWk0ABAXqtqxZIeuiWUN9l9mbXB4AIDCbwk9Y-aGbq6OwM-9LMasc4yzQ8iuzJqVbdLipch4EQQUsaX1lHs-OYwgKBfsHnmIb0IME_Zh2bLw7mXvix5X2J3fIBvkmuIPbK-xIfEOBI34LOysPqEHnt3v1G6XETMziD81Yi29B5AKpsVIlpd_wzz4Kr6DtWZGE8bhnaoakgAAABQ4YkEXOKSeIspx0wDapcCGX3s9cw#https%3A%2F%2Fvimeoassets-singapore.s3-ap-southeast-1.amazonaws.com%2F4050%2FEncodedVideo%2Fuploads%2Fmovie_stream%2Ffull_movie%2F91003%2Fstream.mpd");
        playerModel.setUserId("151404");
        playerModel.setStreamUniqueId("cd222bdc2af51646483a4ae9271074b6");
        playerModel.setMovieUniqueId("b3acd62aaa10103ae0e9cea9f031ac54");
        playerModel.setEmailId("bb@gmail.com");
        playerModel.setAuthToken("25e74a5c88d19c4b57c8138bf47abdf7");
        playerModel.setAppName(getResources().getString(R.string.app_name));
        playerModel.setDomainName("https://www.muvi.com/rest/");

        playerModel.setWaterMark(true);
        playerModel.useIp(true);
        playerModel.useDate(true);

        Intent intent  = new Intent(MainActivity.this, PlayerActivity.class);
        intent.putExtra("PlayerModel",playerModel);
        startActivity(intent);
    }

    @Override
    public void AppHomeFeaturePreExecute() {

    }

    @Override
    public void AppHomeFeaturePostExecute(AppHomeFeatureOutputModel appHomeFeatureOutputModel, int code) {

    }
}
