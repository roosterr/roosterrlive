package com.roosterr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.*;
import io.fabric.sdk.android.BuildConfig;

/**
 * A login screen that offers login via phone number using Digits.
 */
public class LoginActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "umqVdNp559amgyv5UtJYMQrzk";
    private static final String TWITTER_SECRET = "yesgvVI9LnbsUsFpImxrvt0AjLMKcQAEV6bmnaapdTJXW4Fz0n";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        //Users.resetDB(this);
        String phoneNumber="7388905555";
        String countryCode="91";
        SharedPreferences.Editor purchaseType = LoginActivity.this.getSharedPreferences("Purchase_Type", 0).edit();

        purchaseType.putString("no_ads", "0");
        purchaseType.putString("go_pro", "0");
        purchaseType.putString("both", "1");

        purchaseType.commit();

        Toast.makeText(getApplicationContext(), "Authentication successful for " + phoneNumber, Toast.LENGTH_LONG).show();
        //startActivity();
        Intent launchNextActivity;
        launchNextActivity = new Intent(this, HomeActivity.class).putExtra("userInfo", String.valueOf(phoneNumber)).putExtra("cCode", String.valueOf(countryCode));
        //new Intent(this, HomeActivity.class).putExtra("userInfo", String.valueOf(phoneNumber)).putExtra("cCode", String.valueOf(countryCode))
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchNextActivity.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(launchNextActivity);

        finish();
        return;
        */


        Users user = Users.getUser(this);
        if (user != null) {
            String phoneNumber ="+"+ user.phoneNumber;
            String[] rl = getResources().getStringArray(R.array.CountryCodes);
            String countryCode = "";
            for (String split : rl) {
                String[] g = split.split(",");
                if (phoneNumber.startsWith("+" + g[0].trim())) {
                    countryCode = "+" + g[0].trim();
                    break;
                }
            }
            phoneNumber = phoneNumber.replace(countryCode, "");
            countryCode = countryCode.replace("+", "");

            SharedPreferences.Editor purchaseType = LoginActivity.this.getSharedPreferences("Purchase_Type", 0).edit();

            purchaseType.putString("no_ads", user.no_ads);
            purchaseType.putString("go_pro", user.go_pro);
            purchaseType.putString("both", user.both);

            purchaseType.commit();

            Toast.makeText(getApplicationContext(), LoginActivity.this.getResources().getString(R.string.authentication_success)+" "+ phoneNumber, Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, HomeActivity.class).putExtra("userInfo", String.valueOf(phoneNumber)).putExtra("cCode", String.valueOf(countryCode)));
            finish();
            return;
        }

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);

        Digits.Builder digitsBuilder = new Digits.Builder().withTheme(R.style.AppTheme);
        Fabric.with(this, new TwitterCore(authConfig), digitsBuilder.build());

        setContentView(R.layout.activity_login_sms);

        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, String phoneNumber) {
                // TODO: associate the session userID with your user model

                String[] rl = LoginActivity.this.getResources().getStringArray(R.array.CountryCodes);
                String countryCode = "";
                for (String split : rl) {
                    String[] g = split.split(",");
                    if (phoneNumber.startsWith("+" + g[0].trim())) {
                        countryCode = "+" + g[0].trim();
                        break;
                    }
                    else if(phoneNumber.startsWith(g[0].trim())){
                        countryCode = "+" + g[0].trim();
                        break;
                    }
                }

                phoneNumber = phoneNumber.replace(countryCode, "");
                countryCode = countryCode.replace("+", "");

                SharedPreferences.Editor purchaseType = LoginActivity.this.getSharedPreferences("Purchase_Type", 0).edit();

                purchaseType.putString("no_ads", "0");
                purchaseType.putString("go_pro", "0");
                purchaseType.putString("both", "0");

                purchaseType.commit();

                Toast.makeText(LoginActivity.this.getApplicationContext(), LoginActivity.this.getResources().getString(R.string.authentication_success)+" "+ phoneNumber, Toast.LENGTH_LONG).show();
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, HomeActivity.class).putExtra("userInfo", String.valueOf(phoneNumber)).putExtra("cCode", String.valueOf(countryCode)));
                LoginActivity.this.finish();

            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });
        digitsButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));

    }
}

