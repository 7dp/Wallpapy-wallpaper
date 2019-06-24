package id.radityo.wallpapy.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import id.radityo.wallpapy.R;

import static id.radityo.wallpapy.Activities.MyIntroActivity.FIRST_LAUNCH_KEY;
import static id.radityo.wallpapy.Utils.Cons.TAG;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(SplashScreenActivity.this);
                boolean isFirstLaunch = preferences.getBoolean(FIRST_LAUNCH_KEY, true);
                if (isFirstLaunch) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: firstLaunch");
                            Intent intent = new Intent(SplashScreenActivity.this, MyIntroActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e(TAG, "run: secondLaunch");
                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });

        thread.start();
    }
}
