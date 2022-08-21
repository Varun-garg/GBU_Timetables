package com.varun.gbu_timetables.service;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class JSONParser extends AsyncTask<Void, Void, Boolean> {

    Context c;
    String jsonData;
    ListView lv;

    ProgressDialog pd;
    ArrayList<String> users = new ArrayList<>();
    ArrayList<String> noticelink = new ArrayList<>();

    public JSONParser(Context c, String jsonData, ListView lv) {
        //jsonData= jsonData.replaceAll("\\n","\n");
        Log.v("AWAS", "Response from url: " + jsonData);
        this.c = c;
        this.jsonData = jsonData;
        this.lv = lv;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(c);
        pd.setTitle("Parse JSON");
        pd.setMessage("Parsing...Please wait");
        pd.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        return this.parse();
    }

    @Override
    protected void onPostExecute(Boolean isParsed) {
        super.onPostExecute(isParsed);

        pd.dismiss();
        if (isParsed) {
            //BIND
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1, users);
            lv.setAdapter(adapter);

            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Toast.makeText(c, users.get(i), Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse(noticelink.get(i));
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    c.startActivity(intent);
                    // downloadFile(noticelink.get(i));
                }

                private void downloadFile(String url) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                    request.setTitle("Downloading...");  //set title for notification in status_bar
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);  //flag for if you want to show notification in status or not

                    //String nameOfFile = "YourFileName.pdf";    //if you want to give file_name manually

                    String nameOfFile = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url)); //fetching name of file and type from server

                    File f = new File(Environment.getExternalStorageDirectory() + "/" + "GBUTimetables");       // location, where to download file in external directory
                    if (!f.exists()) {
                        f.mkdirs();
                    }
                    request.setDestinationInExternalPublicDir("GBUTimetables", nameOfFile);
                    DownloadManager downloadManager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);
                }
            });

        } else {
            Toast.makeText(c, "Unable To Parse,Check Your Log output", Toast.LENGTH_SHORT).show();
        }

    }

    private Boolean parse() {
        try {
            JSONArray ja = new JSONArray(jsonData);
            JSONObject jo;

            users.clear();
            noticelink.clear();
            for (int i = 0; i < ja.length(); i++) {
                jo = ja.getJSONObject(i);

                String name = jo.getString("Title");
                String link = "https://www.gbu.ac.in/Admissions/DownloadFile?nName=" + jo.getString("Pdf");
                users.add(name);
                noticelink.add(link);
            }

            return true;

        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
}
