package com.release.muvisdk.player.activity;

public class CastCrew {
    public interface AppInterface {
        public void getCastCrewDetails(String movieId);
    }

    static AppInterface myapp = null;
    public static void registerApp(AppInterface appinterface) {
        myapp = appinterface;
    }

    public static void startCastCrewActivity(String movieId){
        myapp.getCastCrewDetails(movieId);
    }
}