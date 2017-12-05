package com.release.muvisdk.api;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by MUVI on 11/16/2017.
 */

public class Utils {

    //for handle http and https request

    public static String handleHttpAndHttpsRequest(URL url,String query,int status,String message){
        //InputStream ins=null;
        String responseStr = "";
        try {
            if(url.toString().contains("https")){
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                //ins = conn.getInputStream();
                int responseCode = conn.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    InputStream err = conn.getErrorStream();
                    InputStreamReader isr = new InputStreamReader(err);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                        Log.v("MUVISDK", "responseStr" + responseStr);

                    }
                    in.close();
                    err.close();
                } else {
                    InputStream ins = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                        Log.v("MUVISDK", "responseStr" + responseStr);

                    }
                    in.close();
                }

            }else{
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();
                int responseCode = conn.getResponseCode();
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    InputStream err = conn.getErrorStream();
                    InputStreamReader isr = new InputStreamReader(err);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                        Log.v("MUVISDK", "responseStr" + responseStr);

                    }
                    in.close();
                    err.close();
                } else {
                    InputStream ins = conn.getInputStream();
                    InputStreamReader isr = new InputStreamReader(ins);
                    BufferedReader in = new BufferedReader(isr);
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        System.out.println(inputLine);
                        responseStr = inputLine;
                        Log.v("MUVISDK", "responseStr" + responseStr);

                    }
                    in.close();
                }

            }
        }catch (org.apache.http.conn.ConnectTimeoutException e) {
            Log.v("MUVISDK", "org.apache.http.conn.ConnectTimeoutException e" + e.toString());

            status = 0;
            message = "";

        } catch (IOException e) {
            Log.v("MUVISDK", "IOException" + e.toString());

            status = 0;
            message = "";
        }

        return responseStr;
    }
}
