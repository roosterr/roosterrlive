package com.roosterr;

import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.roosterr.util.IabBroadcastReceiver;
import com.roosterr.util.IabBroadcastReceiver.IabBroadcastListener;
import com.roosterr.util.IabHelper;
import com.roosterr.util.IabHelper.IabAsyncInProgressException;
import com.roosterr.util.IabResult;
import com.roosterr.util.Inventory;
import com.roosterr.util.Purchase;
import com.roosterr.util.SkuDetails;


import java.util.ArrayList;
import java.util.List;

public class UpgradeActivity extends AppCompatActivity implements IabBroadcastListener{
    // In app Billing
    static final String TAG = "RemindKing";
    //Does the user have the no ads upgrade
    boolean mIsNoAds=false;
    //Does the user have the Go Pro upgrade
    boolean mIsGoPro=false;
    //Does the user have Both upgrade
    boolean mIsBoth = false;

    // SKUs for our products: the no ads,go pro,both (consumable)
    static final String SKU_NoAds = "no_ads";
    static final String SKU_GoPro = "pro";
    static final String SKU_BOTH = "both";
    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 10001;
    // The helper object
    IabHelper mHelper;

    // Provides purchase notification while this app is running
    IabBroadcastReceiver mBroadcastReceiver;
    // End
    String userID;
    String Phone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upgrade);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        userID = getIntent().getStringExtra("UserID");
        Phone = getIntent().getStringExtra("Phone");
        //IAB Starts
        String keyInit = getResources().getString(R.string.base_64_part_initial);
        String keyMiddle = getResources().getString(R.string.base_64_part_middle);
        String keyLast = getResources().getString(R.string.base_64_part_last);
        String base64EncodedPublicKey = keyInit + keyMiddle + keyLast;
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);
        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_label) + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(UpgradeActivity.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);
                List<String> moreSku=new ArrayList<String>();
                moreSku.add(SKU_NoAds);
                moreSku.add(SKU_BOTH);
                moreSku.add(SKU_GoPro);
                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(true,moreSku,null,mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_err_2));
                }
            }
        });

        Button noAdsButton = (Button) findViewById(R.id.button4);
        noAdsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade to No ads.");
                setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this.
         */
                String payload = userID;

                try {
                    mHelper.launchPurchaseFlow(UpgradeActivity.this, SKU_NoAds, RC_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (IabAsyncInProgressException e) {
                    complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_err_2));
                    setWaitScreen(false);
                }
            }
        });

        //Go Pro
        Button goProButton = (Button) findViewById(R.id.buttonGoPro);
        goProButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade to No ads.");
                setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this.
         */
                String payload = userID;

                try {
                    mHelper.launchPurchaseFlow(UpgradeActivity.this, SKU_GoPro, RC_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (IabAsyncInProgressException e) {
                    complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_err_2));
                    setWaitScreen(false);
                }
            }
        });
        //Both
        Button bothButton = (Button) findViewById(R.id.button3);
        bothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade to No ads.");
                setWaitScreen(true);

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this.
         */
                String payload = userID;

                try {
                    mHelper.launchPurchaseFlow(UpgradeActivity.this, SKU_BOTH, RC_REQUEST,
                            mPurchaseFinishedListener, payload);
                } catch (IabAsyncInProgressException e) {
                    complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_err_2));
                    setWaitScreen(false);
                }
            }
        });
    }
        @Override
        public void onBackPressed() {
            super.onBackPressed();
            //startActivity(new Intent(this, HomeActivity.class));
            //finish();
        }
        // Listener that's called when we finish querying the items and subscriptions we own
        IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                Log.d(TAG, "Query inventory finished.");

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Is it a failure?
                if (result.isFailure()) {
                    complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_inv) + result);
                    return;
                }

                Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

                // Do we have the premium upgrade no ads?
                Purchase premiumPurchaseNoAds = inventory.getPurchase(SKU_NoAds);
                mIsNoAds = (premiumPurchaseNoAds != null && verifyDeveloperPayload(premiumPurchaseNoAds));
                TextView no_ads_price =(TextView) findViewById(R.id.no_ads_price);
                //SkuDetails noadsDetails= inventory.getSkuDetails(SKU_NoAds);
                String no_price= inventory.getSkuDetails(SKU_NoAds).getPrice();
                no_ads_price.setText(no_price);
                Log.d(TAG, "User is " + (mIsNoAds ? "No Ads" : "NOT Purchased No Ads"));
                /*
                if(mIsNoAds) {
                    try {
                        mHelper.consumeAsync(premiumPurchaseNoAds, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {

                            }
                        });
                    } catch (Exception ex) {

                    }
                }
                */
                // Do we have the premium upgrade go pro?
                Purchase premiumPurchaseGoPro = inventory.getPurchase(SKU_GoPro);
                mIsGoPro = (premiumPurchaseGoPro != null && verifyDeveloperPayload(premiumPurchaseGoPro));
                TextView go_pro_price =(TextView) findViewById(R.id.go_pro_price);
                String go_price= inventory.getSkuDetails(SKU_GoPro).getPrice();
                go_pro_price.setText(go_price);
                Log.d(TAG, "User is " + (mIsGoPro ? "Go Pro" : "NOT Purchased Go Pro"));
                /*
                if(mIsGoPro) {
                    try {
                        mHelper.consumeAsync(premiumPurchaseGoPro, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {

                            }
                        });
                    } catch (Exception ex) {

                    }
                }
                */
                // Do we have the premium upgrade go pro?
                Purchase premiumPurchaseBoth = inventory.getPurchase(SKU_BOTH);
                mIsBoth = (premiumPurchaseBoth != null && verifyDeveloperPayload(premiumPurchaseBoth));
                TextView both_price =(TextView) findViewById(R.id.both_price);
                String both_prd_price= inventory.getSkuDetails(SKU_BOTH).getPrice();
                both_price.setText(both_prd_price);
                Log.d(TAG, "User is " + (mIsBoth ? "Both" : "NOT Purchased Both"));
                /*
                if(mIsBoth) {
                    try {
                        mHelper.consumeAsync(premiumPurchaseBoth, new IabHelper.OnConsumeFinishedListener() {
                            @Override
                            public void onConsumeFinished(Purchase purchase, IabResult result) {

                            }
                        });
                    } catch (Exception ex) {

                    }
                }
                */
                updateUi();
                setWaitScreen(false);
                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            }
        };
        @Override
        public void receivedBroadcast() {
            // Received a broadcast notification that the inventory of items has changed
            Log.d(TAG, "Received broadcast notification. Querying inventory.");
            try {
                mHelper.queryInventoryAsync(mGotInventoryListener);
            } catch (IabHelper.IabAsyncInProgressException e) {
                complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_err_2));
            }
        }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);
        if (mHelper == null) return;

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            // not handled, so handle it ourselves (here's where you'd
            // perform any handling of activity results not related to in-app
            // billing...
            super.onActivityResult(requestCode, resultCode, data);
        }
        else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }
    /** Verifies the developer payload of a purchase. */
    boolean verifyDeveloperPayload(Purchase p) {
        String payload = p.getDeveloperPayload();

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */
        //return true;

        if(payload.equals(userID))
            return true;
        else
            return false;

    }

    // Callback for when a purchase is finished
    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                complain("Error purchasing: " + result);
                setWaitScreen(false);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain(UpgradeActivity.this.getResources().getString(R.string.iab_err_purchase));
                setWaitScreen(false);
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_NoAds)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is no ads. Congratulating user.");
                alert(UpgradeActivity.this.getResources().getString(R.string.iab_thank_no_ads));
                mIsNoAds = true;
                updateUi();
                setWaitScreen(false);
            }
            if (purchase.getSku().equals(SKU_GoPro)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is no ads. Congratulating user.");
                alert(UpgradeActivity.this.getResources().getString(R.string.iab_thank_go_pro));
                mIsGoPro = true;
                updateUi();
                setWaitScreen(false);
            }
            if (purchase.getSku().equals(SKU_BOTH)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is no ads. Congratulating user.");
                alert(UpgradeActivity.this.getResources().getString(R.string.iab_thank_both));
                mIsBoth = true;
                updateUi();
                setWaitScreen(false);
            }
        }
    };

    // We're being destroyed. It's important to dispose of the helper here!
    @Override
    public void onDestroy() {
        super.onDestroy();

        // very important:
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }

        // very important:
        Log.d(TAG, "Destroying helper.");
        if (mHelper != null) {
            mHelper.disposeWhenFinished();
            mHelper = null;
        }
    }

    // updates UI to reflect model
    public void updateUi() {
        // update the car color to reflect premium status or lack thereof
        Button mButtonGoPro =(Button) findViewById(R.id.buttonGoPro);
        Button mButtonBoth =(Button) findViewById(R.id.button3);
        Button mButtonNoAds =(Button) findViewById(R.id.button4);

        Button mButtonGoProPurchased =(Button) findViewById(R.id.buttonGPurchased);
        Button mButtonBothPurchased =(Button) findViewById(R.id.buttonBothPurchased);
        Button mButtonNoAdsPurchased =(Button) findViewById(R.id.buttonNoAdsPurchased);
        //mIsGoPro=true;
        //mIsNoAds=true;
        if(mIsGoPro) {
            mButtonGoPro.setVisibility(View.GONE);
            mButtonGoProPurchased.setVisibility(View.VISIBLE);
        }
        else {
            mButtonGoPro.setVisibility(View.VISIBLE);
            mButtonGoProPurchased.setVisibility(View.GONE);
        }

        if(mIsNoAds) {
            mButtonNoAds.setVisibility(View.GONE);
            mButtonNoAdsPurchased.setVisibility(View.VISIBLE);
        }
        else {
            mButtonNoAds.setVisibility(View.VISIBLE);
            mButtonNoAdsPurchased.setVisibility(View.GONE);
        }

        if(mIsBoth) {
            mButtonBoth.setVisibility(View.GONE);
            mButtonGoPro.setVisibility(View.GONE);
            mButtonNoAds.setVisibility(View.GONE);

            mButtonGoProPurchased.setVisibility(View.VISIBLE);
            mButtonBothPurchased.setVisibility(View.VISIBLE);
            mButtonNoAdsPurchased.setVisibility(View.VISIBLE);
        }
        else
        {
            mButtonBoth.setVisibility(View.VISIBLE);
        }

        if(mIsNoAds && mIsGoPro){
            mButtonBoth.setVisibility(View.GONE);
            mButtonGoPro.setVisibility(View.GONE);
            mButtonNoAds.setVisibility(View.GONE);

            mButtonGoProPurchased.setVisibility(View.VISIBLE);
            mButtonBothPurchased.setVisibility(View.VISIBLE);
            mButtonNoAdsPurchased.setVisibility(View.VISIBLE);
        }
        saveData();
    }

    // Enables or disables the "please wait" screen.
    void setWaitScreen(boolean set) {
        findViewById(R.id.content_upgrade).setVisibility(set ? View.GONE : View.VISIBLE);
        findViewById(R.id.screen_wait).setVisibility(set ? View.VISIBLE : View.GONE);
    }

    void complain(String message) {
        Log.e(TAG, "**** RemindKing Error: " + message);
        alert(UpgradeActivity.this.getResources().getString(R.string.err_label) + message);
    }

    void alert(String message) {
        AlertDialog.Builder bld = new AlertDialog.Builder(this);
        bld.setMessage(message);
        bld.setNeutralButton(UpgradeActivity.this.getResources().getString(R.string.ok_label), null);
        Log.d(TAG, "Showing alert dialog: " + message);
        bld.create().show();
    }

    void saveData() {
        /*
         * WARNING: on a real application, we recommend you save data in a secure way to
         * prevent tampering. For simplicity in this sample, we simply store the data using a
         * SharedPreferences.
        */
        //save data to database and Azure
        String both="0";
        String no_ads="0";
        String go_pro="0";
        if(mIsBoth)
            both="1";
        if(mIsGoPro)
            go_pro="1";
        if(mIsNoAds)
            no_ads="1";


        SharedPreferences.Editor purchaseType = UpgradeActivity.this.getSharedPreferences("Purchase_Type", 0).edit();

        purchaseType.putString("no_ads", no_ads);
        purchaseType.putString("go_pro", go_pro);
        purchaseType.putString("both", both);

        purchaseType.commit();


        Users.updatePurchase(UpgradeActivity.this,Phone,no_ads,go_pro,both);
        Users.updateAzurePurchase(UpgradeActivity.this,userID,no_ads,go_pro,both);

    }
}
