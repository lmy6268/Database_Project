package com.example.storesale;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class DB {
    private String url=null;
    private String key=null;
    private Handler handler=null;
    public DB(String url,String key,Handler handler) {
        this.url=url;
        this.key=key;
        this.handler=handler;

    }


    public void connectDB() {
        try {
            URLConnection URLconnection;
            URL Url = new URL(url);
            URLconnection = Url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;

            InputStream stream = httpConnection.getErrorStream();
            if (stream == null) {
                stream = httpConnection.getInputStream();
            }

            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(stream, "utf-8"));

                StringBuilder response = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
                Bundle bun = new Bundle();

                bun.putString(key, response.toString()); //키값 과 value
                Message msg = handler.obtainMessage();
                msg.setData(bun);
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
                httpConnection.disconnect();
            }


        } catch (MalformedURLException malformedURLException) {
            malformedURLException.printStackTrace();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }

    }
}

