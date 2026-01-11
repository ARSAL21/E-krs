package network;

import android.os.AsyncTask;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class ApiClient {
    // Ganti dengan URL Laravel kamu
    private static final String BASE_URL = "http://192.168.1.10:9001";

    public interface Callback {
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }

    public static void post(String endpoint, Map<String, String> params, Callback callback) {
        new PostTask(endpoint, params, callback).execute();
    }

    private static class PostTask extends AsyncTask<Void, Void, JSONObject> {
        private String endpoint;
        private Map<String, String> params;
        private Callback callback;
        private String error = null;

        public PostTask(String endpoint, Map<String, String> params, Callback callback) {
            this.endpoint = endpoint;
            this.params = params;
            this.callback = callback;
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL(BASE_URL + endpoint);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                // Build JSON body
                JSONObject jsonBody = new JSONObject();
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    jsonBody.put(entry.getKey(), entry.getValue());
                }

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonBody.toString());
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                BufferedReader br;
                if (responseCode >= 200 && responseCode < 300) {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } else {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();

                return new JSONObject(sb.toString());

            } catch (Exception e) {
                e.printStackTrace();
                error = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            if (response != null) {
                callback.onSuccess(response);
            } else {
                callback.onError(error != null ? error : "Unknown error");
            }
        }
    }
}
