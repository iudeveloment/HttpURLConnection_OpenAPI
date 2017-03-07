package com.kitkat.android.httpurlconnection_openapi;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Http {
    public interface Callback {
        public Context getContext();
        public String getUrl();
        public void call(String jsonString);
    }

    public void getWebData(final Callback callback) {
        String urlString = callback.getUrl();

        if(!urlString.startsWith("http://"))
            urlString = "http://" + urlString;

        new AsyncTask<String, Void, String>() {
            ProgressDialog dialog;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new ProgressDialog(callback.getContext());
                dialog.setMessage("HttpURLConnection..");
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                dialog.show();
            }

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);

                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                    httpURLConnection.setRequestMethod("GET");

                    int responseCode = httpURLConnection.getResponseCode();

                    if(responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));

                        StringBuilder jsonString = new StringBuilder();
                        String dataLine = "";

                        while((dataLine = br.readLine()) != null) {
                            jsonString.append(dataLine);
                        }

                        return jsonString.toString();

                    } else {
                        Log.e("HttpURLConnection", "Error Code : " + responseCode);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                dialog.dismiss();
                callback.call(s);
            }
        }.execute(urlString);
    }
}
