package com.release.muvisdk.player.activity;

import android.content.Context;

import com.release.muvisdk.player.utils.Util;


/**
 * Created by MUVI on 10-05-2017.
 */

public class TranslatedLanguage {
    Context context;

    public TranslatedLanguage(Context context) {
        this.context = context;
    }

    public String getNoInternetConnection() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.NO_INTERNET_CONNECTION, Util.DEFAULT_NO_INTERNET_CONNECTION);
    }

    public String getNoContent() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.NO_CONTENT, Util.DEFAULT_NO_CONTENT);
    }

    public String getNoData() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.NO_DATA, Util.DEFAULT_NO_DATA);
    }

    public String getButtonOk() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.BUTTON_OK, Util.DEFAULT_BUTTON_OK);
    }


    public String getYes() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.YES, Util.DEFAULT_YES);
    }

    public String getNo() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.NO, Util.DEFAULT_NO);
    }


    public String getMyLibrary() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.MY_LIBRARY, Util.DEFAULT_MY_LIBRARY);
    }

    public String getHome() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.HOME, Util.DEFAULT_HOME);
    }


    public String getCancelButton() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.CANCEL_BUTTON, Util.DEAFULT_CANCEL_BUTTON);
    }


    public String getResumeMsg() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.RESUME_MESSAGE, Util.DEFAULT_RESUME_MESSAGE);
    }

    public String getContinuebtn() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.CONTINUE_BUTTON, Util.DEAFULT_CONTINUE_BUTTON);
    }

    public String getCancelBtn() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.CANCEL_BUTTON, Util.DEAFULT_CANCEL_BUTTON);
    }

    public String getDownloadBtnTitle() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.DOWNLOAD_BUTTON_TITLE, Util.DOWNLOAD_BUTTON_TITLE);
    }

    public String getDownloadInterrupted() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.DOWNLOAD_INTERRUPTED, Util.DEFAULT_DOWNLOAD_INTERRUPTED);
    }

    public String getDownloadCompleted() {

        SaveData saveData = new SaveData(context, Util.LANGUAGE_SHARED_PRE);
        return saveData.getData(Util.DOWNLOAD_COMPLETED, Util.DEFAULT_DOWNLOAD_COMPLETED);
    }


}
