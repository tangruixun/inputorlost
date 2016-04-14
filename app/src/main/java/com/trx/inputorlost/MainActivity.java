package com.trx.inputorlost;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    //private SecretEditText secretEditText ;
    private EditText secretEditText ;

    private String idle_min; // 4 seconds after user stops typing

    private long last_text_edit = 0;
    private Handler h = new Handler();
    private String TAG = "--->";

    private SharedPreferences sharedPreferences;
    private boolean bTimerEnabled;
    private String strFontName;
    private String strFontSize;

    private final TextWatcher tw = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }
        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && s.length() > 0 ) {
                Log.i(TAG, "afterTextChanged");
                // dispatch after done typing (1 sec after)
                last_text_edit = System.currentTimeMillis();
                h.postDelayed(input_finish_checker, Long.parseLong(idle_min));
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
                secretEditText.addTextChangedListener(tw);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // secretEditText = new SecretEditText(this);
        secretEditText = (EditText) findViewById(R.id.secrettext);
        secretEditText.addTextChangedListener(tw);

        retriveLatestPref ();

    }

    private void retriveLatestPref() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        bTimerEnabled = sharedPreferences.getBoolean(getString(R.string.timer_enabled_key), true);
        idle_min = sharedPreferences.getString(getString(R.string.timer_list_key), "3000");
        strFontName = sharedPreferences.getString(getString(R.string.font_list_key), "0");
        strFontSize = sharedPreferences.getString(getString(R.string.font_size_list_key), "0");
    }

    private void fadeText(EditText et) {
        Log.i (TAG, "fadeText");
        //et.setDuration(INT_DISAPPEAR);     // set fade duration to 3 seconds
        //et.hide();
        et.setText("");
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
        } else if (id == R.id.action_about) {
            //Intent settingsIntent = new Intent(this, AboutActivity.class);
            //startActivity(settingsIntent);
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


        super.onResume();
    }
}
