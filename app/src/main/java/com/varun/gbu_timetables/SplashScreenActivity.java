package com.varun.gbu_timetables;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class SplashScreenActivity extends AppCompatActivity {

    int set_theme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int saved_theme = Utility.ThemeTools.getThemeId(getApplicationContext());
        set_theme = R.style.AppTheme;
        if (set_theme != saved_theme)
            setTheme(saved_theme);
        set_theme = saved_theme;


        setContentView(R.layout.splash_screen);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );
        final ImageView imageView = findViewById(R.id.image_view);
        final LinearLayout imageView_ll = findViewById(R.id.image_view_ll);

        ViewTreeObserver vto = imageView_ll.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = imageView_ll.getViewTreeObserver();

                int height = imageView_ll.getHeight();
                int width = imageView_ll.getWidth();

                if (width < height)
                    height = width;
                else
                    width = height;

                Log.d("SPLASH", height + " " + width);
                BitmapFactory.Options opts = new BitmapFactory.Options();
                opts.inPurgeable = true;
                opts.inScaled = true;
                Bitmap preview_bitmap = ((BitmapDrawable) Utility.ThemeTools.getGBU_remastered(getApplicationContext())).getBitmap();
                Bitmap bmp = Bitmap.createScaledBitmap(preview_bitmap, width, height, true);
                imageView.setImageBitmap(bmp);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }

        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                Bundle Extras = getIntent().getExtras();
                if (Extras != null)
                    intent.putExtras(Extras);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                getApplicationContext().startActivity(intent);
                finish();
            }
        }, 2777L);

    }
}
