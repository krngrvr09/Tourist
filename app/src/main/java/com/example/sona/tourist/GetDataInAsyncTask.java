package com.example.sona.tourist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class GetDataInAsyncTask extends AsyncTask<String, String, String> {
    //String ActivityMain.ServerURL = "http://www.usebackpack.com";
    String url;
    String postData;
    String callback;
    String output;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected String doInBackground(String... strings) {
        try{
            Log.d("konsaurl", strings[0]);
            return getData(strings[0], strings[1]);
        }
        catch(ArrayIndexOutOfBoundsException e){
            // this happens when setting the adapter for fragmentViewPager
            return null;
        }
    }

    //        task.execute();
/*
     * this method id called by above asynctask to get data from server
     */
    protected String getData(String url, String postData) {

        URL urlObject;
        HttpURLConnection urlConn = null;
        String outputData = "";
        try {
            urlObject = new URL(url);
            urlConn = (HttpURLConnection) urlObject.openConnection();
//            urlConn.addRequestProperty("Authorization",
//                    "Token token=" + ActivityMain.api_key);
//
            urlConn.setDoOutput(true);

            if (postData != null) {
//				urlConn.setDoInput(true);
                OutputStreamWriter wr = new OutputStreamWriter(
                        urlConn.getOutputStream());
                wr.write(postData);
                wr.flush();
            }

            InputStream in = new BufferedInputStream(urlConn.getInputStream());
            BufferedReader buffin = new BufferedReader(
                    new InputStreamReader(in));
            StringBuilder responseStrBuilder = new StringBuilder();
            while ((outputData = buffin.readLine()) != null) {
                responseStrBuilder.append(outputData);
            }
            outputData = responseStrBuilder.toString();
            Log.d("Response code", urlConn.getResponseCode() + "    " + url);
        } catch (MalformedURLException e1) {

            e1.printStackTrace();
        } catch(ConnectException e){
            return "";

        } catch (IOException e) {

            e.printStackTrace();
        } finally{
            if (urlConn != null)
                urlConn.disconnect();
        }
        return outputData;

    }
}
