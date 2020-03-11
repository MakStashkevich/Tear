package com.makstashkevich.tear.tasks;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import com.makstashkevich.tear.activity.MainActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class VKPostsTask extends AsyncTask<String[], Void, String[]> {

    private Exception exception;

    @SuppressLint("StaticFieldLeak")
    private MainActivity parent;

    public VKPostsTask(MainActivity parent) {
        this.parent = parent;
    }

    @Override
    protected String[] doInBackground(String[]... main) {
        try {
            String GROUP = "167157112";
            String TOKEN = "1519b56b1519b56b1519b56be2157e3ede115191519b56b490fe770018fd4704c7a323f";
            String VERSION = "5.92";

            StringBuilder response;
            try {
                URL url = new URL("https://api.vk.com/method/wall.get?owner_id=-" + GROUP + "&count=1&access_token=" + TOKEN + "&v=" + VERSION);
                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
                String inputLine;
                response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
            } catch (IOException e) {
                Log.wtf("VKPostsTask", "Ошибка подключения к интернету: " + e);
                return null;
            }

            if (response != null) {
                JSONObject obj = new JSONObject(response.toString());
                JSONObject item = obj.getJSONObject("response").getJSONArray("items").getJSONObject(0);
                String id = item.getString("id");
                String text = item.getString("text");
                return new String[]{id, text, GROUP};
            } else return null;
        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(Void... progress) {
    }

    @Override
    protected void onPostExecute(String[] result) {
        Exception e = this.exception;
        if (e == null && result != null) parent.sendNotice(result);
    }
}
