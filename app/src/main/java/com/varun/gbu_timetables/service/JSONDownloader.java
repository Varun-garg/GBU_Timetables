package com.varun.gbu_timetables.service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class JSONDownloader extends AsyncTask<Void, Void, String> {

    Context c;
    String jsonURL;
    ListView lv;

    ProgressDialog pd;

    public JSONDownloader(Context c, String jsonURL, ListView lv) {
        this.c = c;
        this.jsonURL = jsonURL;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(c);
        pd.setTitle("Download JSON");
        pd.setMessage("Downloading...Please wait");
        pd.show();
    }

    @Override
    protected String doInBackground(Void... voids) {
        return this.download();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        super.onPostExecute(jsonData);

        pd.dismiss();
        if (jsonData.startsWith("Error")) {
            Toast.makeText(c, jsonData, Toast.LENGTH_SHORT).show();
        } else {
            //PARSE
            new JSONParser(c, jsonData, lv).execute();

        }

    }

    private String download() {
        Object connection = Connector.connect(jsonURL);
        if (connection.toString().startsWith("Error")) {
            return connection.toString();
        }

        try {
            //ESTABLISH CONNECTION
            HttpURLConnection con = (HttpURLConnection) connection;
            if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                //GET INPUT FROM STREAM
                InputStream is = new BufferedInputStream(con.getInputStream());
                BufferedReader br = new BufferedReader(new InputStreamReader(is));

                String line;
                StringBuffer jsonData = new StringBuffer();

                //READ
                while ((line = br.readLine()) != null) {
                    jsonData.append(line + "n");
                }

                //CLOSE RESOURCES
                br.close();
                is.close();

                //RETURN JSON
                return jsonData.toString();

            } else {
                return "Error " + con.getResponseMessage();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error " + e.getMessage();
        }
    }
}
