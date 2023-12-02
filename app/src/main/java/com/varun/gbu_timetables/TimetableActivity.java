package com.varun.gbu_timetables;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.varun.gbu_timetables.data.model.TimeTableBasic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.util.HashSet;

public class TimetableActivity extends AppCompatActivity {

    private final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 123;
    HashSet<TimeTableBasic> existing_data;
    SharedPreferences prefs;
    Gson gson = new Gson();
    String existing_TAG = "favourites";
    String json;
    Type favourites_type = new TypeToken<HashSet<TimeTableBasic>>() {
    }.getType();
    SharedPreferences.Editor editor;
    String message = "";
    private TimeTableBasic myclass;
    private final String myclass_TAG = "myclass";
    private Boolean isFABOpen = false;
    private FloatingActionButton fab_main, fab_fav, fab_myclass, fab_share;
    private int saved_theme;

    public static File SaveBitmapasPNG(Bitmap bitmap, String dir) {
        OutputStream os = null;
        try {
            File file = new File(dir, "image" + System.currentTimeMillis() + ".png");
            os = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            bitmap.recycle();
            return file;
        } catch (IOException e) {

            bitmap.recycle();
            Log.e("combineImages", "problem combining images", e);
            return null;
        }
    }

    private void reload() {
        json = prefs.getString(existing_TAG, null);
        existing_data = gson.fromJson(json, favourites_type);
        if (existing_data == null)
            existing_data = new HashSet<>();
    }

    public TimeTableBasic getCurrentBasic() {
        String Title = getIntent().getExtras().getString("Timetable_title");
        String Type = getIntent().getExtras().getString("Type");
        Long Id = null;
        if (Type.equals("Section")) {
            Id = getIntent().getExtras().getLong("Section_id");
        } else if (Type.equals("Faculty")) {
            Id = getIntent().getExtras().getLong("Faculty_id");
        }

        TimeTableBasic info = new TimeTableBasic();
        info.setTitle(Title);
        info.setId(Id);
        info.setType(Type);
        return info;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        setTheme(saved_theme);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_timetable);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        final String stored_mode = prefs.getString(getString(R.string.pref_tt_display_type_key), "0");

        if (savedInstanceState == null) {

            if (stored_mode.equals("0"))
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TimetableFragmentPager())
                        .commit();
            else //1
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, new TimetableFragmentSinglePage())
                        .commit();
        }


        final TimeTableBasic info = getCurrentBasic();
        reload();

        final Drawable fav_yes = Utility.ThemeTools.FavouriteIcon.getFavYes(getApplicationContext());
        final Drawable fav_no = Utility.ThemeTools.FavouriteIcon.getFavNo(getApplicationContext());
        final Drawable share = Utility.ThemeTools.getShareIconDrawable(getApplicationContext());
        final Drawable myclass_yes = Utility.ThemeTools.MyClassIcon.getMyClassYes(getApplicationContext());
        final Drawable myclass_no = Utility.ThemeTools.MyClassIcon.getMyClassNo(getApplicationContext());

        fab_main = findViewById(R.id.fab_main);
        fab_fav = findViewById(R.id.fab_favr);
        fab_myclass = findViewById(R.id.fab_myclass);
        fab_share = findViewById(R.id.fab_share);

        fab_main.setImageDrawable(Utility.ThemeTools.FabIcon.getfabup(getApplicationContext()));

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFABOpen) {
                    showFABMenu();
                } else {
                    closeFABMenu();
                }
            }
        });

        if (existing_data.contains(info)) {
            fab_fav.setImageDrawable(fav_yes);
        } else {
            fab_fav.setImageDrawable(fav_no);
        }

        fab_fav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                TimeTableBasic info = getCurrentBasic();
                reload();

                if (existing_data.contains(info)) {
                    existing_data.remove(info);
                    message = "Removed from Favourites";
                    fab_fav.setImageDrawable(fav_no);
                } else {
                    existing_data.add(info);
                    message = "Added to Favourites";
                    fab_fav.setImageDrawable(fav_yes);
                }

                json = gson.toJson(existing_data);

                editor.putString(existing_TAG, json);
                editor.apply();

                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
            }
        });


        if (getCurrentBasic().equals(gson.fromJson(prefs.getString(myclass_TAG, ""), TimeTableBasic.class)))
            fab_myclass.setImageDrawable(myclass_yes);
        else
            fab_myclass.setImageDrawable(myclass_no);
        fab_myclass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final TimeTableBasic setmyclass;
                final TimeTableBasic currentBasic = getCurrentBasic();
                reload();
                String myclass_json = prefs.getString(myclass_TAG, "");
                if (myclass_json.length() > 1) {
                    setmyclass = gson.fromJson(myclass_json, TimeTableBasic.class);
                } else {
                    setmyclass = null;
                }


                if (setmyclass == null) {
                    myclass = currentBasic;
                    message = "MyClass set";
                    fab_myclass.setImageDrawable(myclass_yes);

                    json = gson.toJson(myclass);
                    editor.putString(myclass_TAG, json);
                    editor.apply();
                    Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();

                } else {
                    if (currentBasic.equals(setmyclass)) {
                        myclass = null;
                        message = "Current MyClass Removed";
                        fab_myclass.setImageDrawable(myclass_no);


                        json = gson.toJson(myclass);
                        editor.putString(myclass_TAG, json);
                        editor.apply();
                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                    } else {
                        //Replace existing class with current class,Prompt user to confirm the action
                        AlertDialog.Builder builder;
                        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //  builder = new AlertDialog.Builder(TimetableActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                        //} else {
                        builder = new AlertDialog.Builder(TimetableActivity.this);
                        //}
                        builder.setTitle("Replace Current MyClass")
                                .setMessage("There is an existing MyClass, This will replace current Myclass")
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        myclass = currentBasic;
                                        json = gson.toJson(myclass);
                                        message = "MyClass Replaced";
                                        fab_myclass.setImageDrawable(myclass_yes);
                                        editor.putString(myclass_TAG, json);
                                        editor.apply();
                                        Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // no need to do anything here
                                    }
                                })
                                //.setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }

                }

            }
        });
        fab_share.setImageDrawable(share);
        fab_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(TimetableActivity.this, Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_GRANTED) {
                    File png;
                    if (stored_mode.equals("0"))
                        png = SaveBitmapasPNG(BitmapfromView(findViewById(R.id.timetable_pager), null), Environment.getExternalStorageDirectory().getAbsolutePath());
                    else
//                       // png = SaveBitmapasPNG(BitmapfromView(findViewById(R.id.timetable_table), findViewById(R.id.timetable_faculty_data)), Environment.getExternalStorageDirectory().getAbsolutePath());
                        png = SaveBitmapasPNG(BitmapfromView(findViewById(R.id.timetable_table), null), Environment.getExternalStorageDirectory().getAbsolutePath());
                    if (png == null)
                        return;
                    try {
                        Uri imageURI = FileProvider.getUriForFile(TimetableActivity.this, TimetableActivity.this.getApplicationContext().getPackageName() + ".fileprovider", png);
                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                        sharingIntent.setType("image/*");
                        sharingIntent.putExtra(Intent.EXTRA_STREAM, imageURI);
                        startActivity(Intent.createChooser(sharingIntent, "Share image using"));
                    } catch (Exception e) {
                        Log.e("PNG_Share", "onClick: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    ActivityCompat.requestPermissions(TimetableActivity.this,
                            new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                            MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                }
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fab_share.performClick();
            } else {
                Toast.makeText(this, "Need permission to add background image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFABMenu() {
        isFABOpen = true;
        fab_fav.animate().translationY(-getResources().getDimension(R.dimen.fab_margin_1));
        fab_myclass.animate().translationY(-getResources().getDimension(R.dimen.fab_margin_2));
        fab_share.animate().translationY(-getResources().getDimension(R.dimen.fab_margin_3));
        fab_main.animate().rotation(180);
    }

    private void closeFABMenu() {
        isFABOpen = false;
        fab_fav.animate().translationY(0);
        fab_myclass.animate().translationY(0);
        fab_main.animate().rotation(0);
        fab_share.animate().translationY(0);
    }

    public Bitmap BitmapfromView(View view1, @Nullable View view2) {
        int height, width;
        if (view2 != null) {
            height = view1.getHeight() + view2.getHeight();
            width = Math.max(view1.getWidth(), view2.getWidth());
        } else {
            height = view1.getHeight();
            width = view1.getWidth();
        }
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Drawable Backgrounddrawable = view1.getBackground();
        if (Backgrounddrawable != null)
            Backgrounddrawable.draw(canvas);
        else {
            if (saved_theme == R.style.LightTheme)
                canvas.drawColor(getResources().getColor(R.color.main_activity_bg));
            else
                canvas.drawColor(getResources().getColor(R.color.main_activity_bg_dark));
        }
        view1.draw(canvas);
        if (view2 != null) {
            canvas.translate(0, view1.getHeight());
            view2.draw(canvas);
        }
        return bitmap;
    }

}
