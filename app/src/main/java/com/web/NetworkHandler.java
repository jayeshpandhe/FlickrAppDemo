package com.web;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * This class manages network transactions.
 */
public class NetworkHandler {
    private static String TAG = NetworkHandler.class.getSimpleName();

    public static String executeGETRequest(String requestURL) {
        Log.d(TAG, "Execute GET request. URL: " + requestURL);
        String response = null;
        InputStream in = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestURL);
            urlConnection = (HttpURLConnection) url.openConnection();
            in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader r = new BufferedReader(new InputStreamReader(in));
            response = readStream(r);
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e);
        } finally {
            if(in != null) {
                try {
                    in.close();
                } catch (Exception e) {}
            }
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        Log.d(TAG, "Response: " + response);
        return response;
    }

    private static String readStream(BufferedReader br) throws IOException {
        String response = null;
        if(br != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            response = sb.toString();
        }
        return response;
    }
}
