package dev.derecky.sany.editor.tools.translateapi;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.URLDecoder;

import static android.content.ContentValues.TAG;

public class TranslateAPI {

    private static final String API_BASE_URL = "https://translate.googleapis.com/translate_a/single";
    private static final String USER_AGENT = "Mozilla/5.0";

    private final String langFrom;
    private final String langTo;
    private final String word;
    private TranslateListener listener;

    public TranslateAPI(String langFrom, String langTo, String text) {
        this.langFrom = langFrom;
        this.langTo = langTo;
        this.word = text;
    }

    public void setTranslateListener(TranslateListener listener) {
        this.listener = listener;
    }

    @SuppressLint("StaticFieldLeak")
    public void execute() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try {
                    Uri.Builder builder = Uri.parse(API_BASE_URL).buildUpon()
                            .appendQueryParameter("client", "gtx")
                            .appendQueryParameter("sl", langFrom)
                            .appendQueryParameter("tl", langTo)
                            .appendQueryParameter("dt", "t")
                            .appendQueryParameter("q", URLEncoder.encode(word, "UTF-8"));
                    URL url = new URL(builder.toString());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestProperty("User-Agent", USER_AGENT);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    //return response.toString();
                    //return URLDecoder.decode(response.substring(response.indexOf("<span title=\"") + 13, response.indexOf("\">", response.indexOf("<span title=\"") + 13)), "UTF-8");
                    return URLDecoder.decode(response.toString(), "UTF-8");
                } catch (IOException e) {
                    Log.e(TAG, "Failed to execute translation API request", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String response) {
                if (response == null) {
                    listener.onFailure("Network error");
                    return;
                }
                try {
                    JSONArray main = new JSONArray(response);
                    JSONArray total = main.getJSONArray(0);
                    StringBuilder builder = new StringBuilder();
                    for (int i = 0; i < total.length(); i++) {
                        JSONArray currentLine = total.getJSONArray(i);
                        builder.append(currentLine.getString(0));
                    }
                    String translatedText = builder.toString();
                    if (translatedText.length() > 2) {
                        listener.onSuccess(translatedText);
                    } else {
                        listener.onFailure("Failed to translate the input text.");
                    }
                } catch (JSONException e) {
                    listener.onFailure(e.getLocalizedMessage());
                }
            }
        }.execute();
    }

    public interface TranslateListener {
        void onSuccess(String translatedText);

        void onFailure(String ErrorText);
    }
}