/*
 ** Copyright (C) 2022 KunoiSayami
 **
 ** This program is free software: you can redistribute it and/or modify
 ** it under the terms of the GNU Affero General Public License as published by
 ** the Free Software Foundation, either version 3 of the License, or
 ** any later version.
 **
 ** This program is distributed in the hope that it will be useful,
 ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 ** GNU Affero General Public License for more details.
 **
 ** You should have received a copy of the GNU Affero General Public License
 ** along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package dev.leanhe.android.singlescreenimage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FullScreen extends AppCompatActivity {

    private static final String TAG = "FullScreenActivity";

    ImageView realImage;
    Button btnConfigure;
    private float default_brightness;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_full_screen);

        realImage = findViewById(R.id.imageView);
        btnConfigure = findViewById(R.id.btnConfigure);
        btnConfigure.setOnClickListener( v -> startActivity(new Intent(this, SettingsActivity.class)));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String imgUri = preferences.getString(MainActivity.IMAGE_KEY, null);
        Log.d(TAG, "onCreate: uri: " + imgUri);
        if (imgUri != null) {
            try {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                // path to /data/data/yourapp/app_data/imageDir
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File file = new File(directory, "default.jpg");
                FileInputStream inputStream = new FileInputStream(file);

                Bitmap bm = BitmapFactory.decodeStream(inputStream);
                realImage.setImageBitmap(bm);
                inputStream.close();
            } catch (FileNotFoundException e) {
                Toast.makeText(getApplicationContext(), R.string.file_not_found, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.io_error, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onCreate: Got io error", e);
            }
        }
    }

    // https://stackoverflow.com/a/43650650
    @Override
    protected void onResume() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("brightControl", false)) {
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            default_brightness = layout.screenBrightness;
            layout.screenBrightness = 1F;
            getWindow().setAttributes(layout);
        }
        super.onResume();
    }

    @Override
    protected void onPause() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.getBoolean("brightControl", false)) {
            WindowManager.LayoutParams layout = getWindow().getAttributes();
            layout.screenBrightness = default_brightness;
            getWindow().setAttributes(layout);
        }
        super.onPause();
    }

}