package app.orosoft.safaricom;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_PHONE_CALL = 777;
    SharedPreferences prefs;

    public static boolean change = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        loadLocale();
        themefy();

        setContentView(R.layout.activity_main);

        /////////////////////////////////////////////////////////////////// BANNER AD
        MobileAds.initialize(this, initializationStatus -> { });
        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        /////////////////////////////////////////////////////////////////// BANNER AD


    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.recreate();
    }

    // make a call
    public void call(String ussd) {
        try {
            if (ActivityCompat.checkSelfPermission(
                    getApplicationContext(), Manifest.permission.CALL_PHONE)
                    != PackageManager.PERMISSION_GRANTED ) {
                String[] perms = {
                        Manifest.permission.CALL_PHONE,
                        Manifest.permission.READ_CONTACTS,
                };
                ActivityCompat.requestPermissions(this, perms, REQUEST_PHONE_CALL );
                Toast.makeText(this, getString(R.string.grant_perms), Toast.LENGTH_LONG).show();
            }
            else{
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + Uri.encode( ussd )) );
                startActivity( intent );
            }
        }
        catch (Exception ignored){ }

    }
    public void recharge(View view){
        DialogRecharge.display(getSupportFragmentManager());
    }
    public void transfer(View view){
        DialogTransfer.display(getSupportFragmentManager());
    }
    public void callMeBack(View view){
        DialogCallmeBack.display(getSupportFragmentManager());
    }
    public void checkBalance(View view){
        call( "*704#" );
    }
    public void gebeta(View view){
        call( "*777#" );
    }
    public void info(View view){
        startActivity(new Intent(MainActivity.this, AboutActivity.class));
    }
    public void settings(View view){
        startActivity(new Intent(MainActivity.this, SettingsActivity.class));
    }
    public void loadLocale() {
        String lang = prefs.getString("language", "English");
        String mlang = "en";
        if (lang.equals("Amharic")) mlang = "am";
        else if (lang.equals("Afaan Oromoo")) mlang = "om";
        else mlang = "en";
        Locale myLocale = new Locale(mlang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
    }
//    public void themefy() {
//        boolean darkTheme = prefs.getBoolean("dark_theme_enabled", false);
//        if ( darkTheme ) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
//        }
//        else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
//        }
//    }
    public void themefy() {
        boolean darkTheme = prefs.getBoolean("dark_theme_enabled", false);
        if ( darkTheme ) {
            setTheme( R.style.DarkTheme );
        }
        else {
            setTheme( R.style.MainTheme );
        }
    }

}







