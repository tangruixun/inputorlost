package com.trx.inputorlost;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {
    //private SecretEditText secretEditText ;
    private Context context;
    private EditText secretEditText ;

    private String idle_min; // seconds after user stops typing
    private boolean bVibrate = true;

    private long last_text_edit = 0;
    private Handler h = new Handler();
    private String TAG = "--->";

    private SharedPreferences sharedPreferences;
    private boolean bTimerEnabled;
    private String strFontName;
    private String strFontSize;
    private AdView adView;

    private final TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            if (bTimerEnabled) {
                if (s != null && s.length() > 0 ) {
                    Log.i(TAG, "afterTextChanged");
                    // dispatch after done typing (1 sec after)
                    last_text_edit = System.currentTimeMillis();
                    h.postDelayed(input_finish_checker, Long.parseLong(idle_min));
                }
            }
        }
    };

    private Runnable input_finish_checker = new Runnable() {
        public void run() {
            if (System.currentTimeMillis() > (last_text_edit + Long.parseLong(idle_min) - 500)) {
                Log.i(TAG, "Runnable input_finish_checker");
                // user hasn't changed the EditText for longer than
                // the min delay (with half second buffer window)
                secretEditText.removeTextChangedListener(tw);
                fadeText (secretEditText);  // your queries
                if (bVibrate) {
                    Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    // Vibrate for 500 milliseconds
                    v.vibrate(500);
                }
                secretEditText.addTextChangedListener(tw);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        // secretEditText = new SecretEditText(this);
        secretEditText = (EditText) findViewById(R.id.secrettext);
        secretEditText.addTextChangedListener(tw);

        retriveLatestPref ();

        adView = (AdView) findViewById(R.id.adView);
        final AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    private void retriveLatestPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        bTimerEnabled = sharedPreferences.getBoolean(getString(R.string.timer_enabled_key), true);
        int milsec = sharedPreferences.getInt(getString(R.string.timer_key), 3);
        milsec *= 1000;
        bVibrate = sharedPreferences.getBoolean(getString(R.string.timer_vibrate_key), true);
        idle_min = String.valueOf(milsec);
        strFontName = sharedPreferences.getString(getString(R.string.font_list_key), "0");
        strFontSize = sharedPreferences.getString(getString(R.string.font_size_list_key), "0");

    }


    Animation textDisplayAnimationObject;
    Animation delayBetweenAnimations;
    Animation fadeOutAnimationObject;
    int fadeEffectDuration = 400;
    int delayDuration = 300;
    int displayFor = 300;

    private void fadeText(final EditText et) {
        Log.i (TAG, "fadeText");
        //et.setDuration(INT_DISAPPEAR);     // set fade duration to 3 seconds
        //et.hide();

        textDisplayAnimationObject = new AlphaAnimation(1f, 0.2f);
        textDisplayAnimationObject.setDuration(displayFor);

        delayBetweenAnimations = new AlphaAnimation(0.2f, 1f);
        delayBetweenAnimations.setDuration(delayDuration);

        fadeOutAnimationObject = new AlphaAnimation(1f, 0f);
        fadeOutAnimationObject.setDuration(fadeEffectDuration);

        textDisplayAnimationObject.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                et.startAnimation(delayBetweenAnimations);
            }
        });

        delayBetweenAnimations.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                et.setAlpha(0.45f);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                et.startAnimation(fadeOutAnimationObject);
            }
        });

        fadeOutAnimationObject.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                et.setAlpha(0.45f);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                et.setText("");
            }
        });

        et.startAnimation(textDisplayAnimationObject);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);

        return true;//return true so that the menu pop up is opened
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p/>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        } else if (id == R.id.action_share) {
            String shareText = secretEditText.getText().toString();
            shareText += "\n-- \nvia \"Input Or Lost\"";

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
            shareIntent.setType("text/plain");
            startActivity(shareIntent);
        } else if (id == R.id.action_about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        retriveLatestPref ();
        if (bTimerEnabled) {
            secretEditText.removeTextChangedListener(tw);
            secretEditText.addTextChangedListener(tw);
        } else {
            secretEditText.removeTextChangedListener(tw);
        }
        switch (Integer.valueOf(strFontName)) {
            case 0:
                secretEditText.setTypeface(Typeface.SANS_SERIF);
                break;
            case 1:
                secretEditText.setTypeface(Typeface.SERIF);
                break;
            case 2:
                secretEditText.setTypeface(Typeface.MONOSPACE);
                break;
            default:
                secretEditText.setTypeface(Typeface.DEFAULT);
        }

        switch (Integer.valueOf(strFontSize)) {
            case 0:
                secretEditText.setTextSize(8);
                break;
            case 1:
                secretEditText.setTextSize(12);
                break;
            case 2:
                secretEditText.setTextSize(16);
                break;
            case 3:
                secretEditText.setTextSize(20);
                break;
            case 4:
                secretEditText.setTextSize(24);
                break;
            case 5:
                secretEditText.setTextSize(28);
                break;
            case 6:
                secretEditText.setTextSize(32);
                break;
            default:
                secretEditText.setTextSize(20);
        }

        if (adView != null) {
            adView.resume();
        }
        super.onResume();
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (adView != null) {
            adView.pause();
        }
    }

    @Override
    protected void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }
}
