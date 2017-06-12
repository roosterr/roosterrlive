package com.roosterr;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.roosterr.adapters.HomeListItemRecyclerViewAdapter;
import com.roosterr.adapters.ShareListItemRecyclerViewAdapter;
import com.roosterr.dummy.DummyContent;
import com.roosterr.dummy.ShareContent;
import com.roosterr.ui.DividerItemDecoration;
import com.google.common.io.LineReader;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.fabric.sdk.android.*;
import io.fabric.sdk.android.BuildConfig;

import com.roosterr.util.IabBroadcastReceiver;
import com.roosterr.util.IabBroadcastReceiver.IabBroadcastListener;
import com.roosterr.util.IabHelper;
import com.roosterr.util.IabHelper.IabAsyncInProgressException;
import com.roosterr.util.IabResult;
import com.roosterr.util.Inventory;
import com.roosterr.util.Purchase;

import java.util.ArrayList;
import java.util.List;
import com.google.android.gms.ads.MobileAds;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String AzureAppUrl = "https://roosterr.azurewebsites.net";
    public static final String PLUS_PREFS_NAME = "GooglePlusLikeClicked";
    private MobileServiceClient mClient;
    private LinearLayout mPlusOneButtonView;
    //Ad Mob Integration Start
    //private static final String ADMOB_APP_ID = "ca-app-pub-6214469096466976~3355925043";
    private static final String ADMOB_APP_ID = "ca-app-pub-6214469096466976~3355925043";
    /**
     * ad unit configured to serve app install and content ads
     */
    private static final String NATIVE_AD_UNIT_ID = "ca-app-pub-6214469096466976/4832658248";
    //private static final String NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    //End
    public static final String EXTRA_DEMO_TYPE = "EXTRA_DEMO_TYPE";
    public static final int EXTRA_DEMO_TYPE_ALARM = 123;
    public static final int EXTRA_DEMO_TYPE_CALL = 456;
    public static AlertDialog.Builder progressDialogBuilder;

    //View progressOverlay;
    String globalCountryCode;
    String globalPhoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        final String phoneNumber = getIntent().getStringExtra("userInfo");
        final String cCode = getIntent().getStringExtra("cCode");
        globalCountryCode=cCode;
        globalPhoneNumber=phoneNumber;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        progressDialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppTheme_AlertDialog);
        if (!(phoneNumber == null || phoneNumber == "")) {
            try {
                this.mClient = new MobileServiceClient(AzureAppUrl, (Context) this);
                App_Users item = new App_Users();
                item.country_code = cCode;
                item.is_android_user = "1";
                item.is_ios_user = "0";
                item.phone = phoneNumber;
                item.status = "1";
                final String language = Locale.getDefault().getLanguage();
                getAppMessage(language, cCode);
                final Users user = Users.getUser(HomeActivity.this);

                if (user == null) {
                    animateView("show",HomeActivity.this,this.getResources().getString(R.string.loading_label));//progressOverlay, View.VISIBLE, 0.5f, 200
                    //animateView(progressOverlay, View.VISIBLE, 0.5f, 200);

                }

                this.mClient.getTable(App_Users.class).insert(item,new TableOperationCallback<App_Users>(){
                    public void onCompleted(App_Users entity, Exception exception, ServiceFilterResponse response) {
                        if (exception == null) {
                            Users.createUser(HomeActivity.this,cCode + phoneNumber,"0","0","0");
                            Users.getAzureUserID(HomeActivity.this,cCode,phoneNumber);
                            getChecklist(language,cCode);
                            createWeeklyReminder();
                        }
                        else{

                            if (user != null) {
                                Users.getAzureUserID(HomeActivity.this,cCode,phoneNumber);
                                animateView("hide",HomeActivity.this,"");

                                if(user.is_ready.equals(0)){
                                    animateView("show",HomeActivity.this,HomeActivity.this.getResources().getString(R.string.loading_label));
                                    getChecklist(language,cCode);
                                    createWeeklyReminder();
                                }

                                //progressOverlay,View.GONE,0.5f,200
                                //getChecklist(language,cCode);
                            } else {
                                Users.createUser(HomeActivity.this,cCode + phoneNumber,"0","0","0");
                                Users.getAzureUserID(HomeActivity.this,cCode,phoneNumber);
                                getChecklist(language,cCode);
                                createWeeklyReminder();
                            }
                        }
                    }
                });

            } catch (Exception e) {

            }
        }
        setSupportActionBar(toolbar);
        int hours = new Date().getHours();



        String display = "";
        if (hours >= 18) {
            display = this.getResources().getString(R.string.eve_label);
        } else if (hours > 12) {
            display = this.getResources().getString(R.string.aftr_label);
        } else {
            display = this.getResources().getString(R.string.good_morning);
        }


        TextView greeting = (TextView) findViewById(R.id.greeting);
        greeting.setText(display);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayShowTitleEnabled(false);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder groupDialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppTheme_AlertDialog);
                groupDialogBuilder.setView(R.layout.dialog_group);
                groupDialogBuilder.setPositiveButton(HomeActivity.this.getResources().getString(R.string.create_label), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        HomeActivity.this.createGroup(((EditText) ((Dialog) dialog).findViewById(R.id.txtGroupName)).getText().toString());
                    }
                });//second parameter used for onclicklistener
                groupDialogBuilder.setNegativeButton(HomeActivity.this.getResources().getString(R.string.cancel), null);
                groupDialogBuilder.show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Initialize the Mobile Ads SDK.
        //MobileAds.initialize(this, ADMOB_APP_ID);

        final List<Group> list = Group.getGroups(getApplicationContext());
        final List<Group> mInitialList;
        SharedPreferences.Editor editor = getSharedPreferences(DBHelper.GROUP_TABLE, 0).edit();

        StringBuilder grpLst = new StringBuilder();
        for (Group grp : list) {
            grpLst.append(grp._id + "|" + grp._group_name + ",");
        }
        String allGroups = grpLst.toString();
        if (allGroups != "") {
            allGroups = allGroups.substring(0, allGroups.length() - 1);
        }
        editor.putString("groups", allGroups);
        editor.commit();

        final String both_products =  getSharedPreferences("Purchase_Type", 0).getString("both", null);
        final String no_ads =  getSharedPreferences("Purchase_Type", 0).getString("no_ads", null);
        final String go_pro = getSharedPreferences("Purchase_Type",0).getString("go_pro",null);

        HomeListItemRecyclerViewAdapter adapter;
        if(both_products.equals("1") || no_ads.equals("1")) {
            mInitialList=list;
            adapter = new HomeListItemRecyclerViewAdapter(HomeActivity.this, list);
        }
        else {
            mInitialList=new ArrayList<>();
            double itemCount = list.size();
            for (int i = 0; i < itemCount; i++) {
                if ((i % 4 == 0) && (i > 0)) {
                    mInitialList.add(null);
                    mInitialList.add(list.get(i));

                } else {
                    mInitialList.add(list.get(i));
                }
            }

            adapter = new HomeListItemRecyclerViewAdapter(HomeActivity.this, mInitialList);
        }


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(HomeActivity.this, LinearLayoutManager.VERTICAL));

        ItemTouchHelper.Callback ithCallback = new ItemTouchHelper.Callback() {
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {



                try {
                    int targetPosition = target.getAdapterPosition();
                    int srcPosition = viewHolder.getAdapterPosition();

                    //if(target instanceof HomeListItemRecyclerViewAdapter.AddViewHolder ||target instanceof HomeListItemRecyclerViewAdapter.AddInstallViewHolder) {
                    if(target instanceof HomeListItemRecyclerViewAdapter.AddExpressViewHolder) {
                        if(srcPosition < targetPosition)
                            targetPosition=targetPosition+1;
                        else
                            targetPosition=targetPosition-1;
                    }
                    /*
                    Group targetItem = list.get(targetPosition);
                    list.remove(targetPosition);
                    list.add(targetPosition,list.get(srcPosition));
                    list.add(srcPosition,targetItem);
                    */
                    if(both_products.equals("1") || no_ads.equals("1")) {
                        Collections.swap(list, srcPosition, targetPosition);
                    }
                    else{

                        Collections.swap(mInitialList, srcPosition, targetPosition);

                    }
                    recyclerView.getAdapter().notifyItemMoved(srcPosition, targetPosition);
                    //recyclerView.getAdapter().notifyItemMoved(targetPosition,srcPosition);
                    recyclerView.getAdapter().notifyItemChanged(targetPosition);
                    recyclerView.getAdapter().notifyItemChanged(srcPosition);
                    return true;
                }
                catch (Exception ex){
                    return true;
                }

            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }

            @Override
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

                //if (viewHolder instanceof HomeListItemRecyclerViewAdapter.AddViewHolder) return 0;
                //if (viewHolder instanceof HomeListItemRecyclerViewAdapter.AddInstallViewHolder) return 0;
                if (viewHolder instanceof HomeListItemRecyclerViewAdapter.AddExpressViewHolder) return 0;

                return makeFlag(ItemTouchHelper.ACTION_STATE_DRAG,
                        ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END);
            }

        };
        ItemTouchHelper ith = new ItemTouchHelper(ithCallback);
        ith.attachToRecyclerView(recyclerView);

        ImageButton roosterButton = (ImageButton) findViewById(R.id.buttonRooster);
        ImageView imageView = (ImageView)findViewById(R.id.imageRipple);

        //if(both_products.equals("1") || go_pro.equals("1")) {
            roosterButton.setImageResource(R.drawable.ic_falcon_icon_2);
            roosterButton.setColorFilter(R.color.iconGrey);

            imageView.setVisibility(View.GONE);

            roosterButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(HomeActivity.this, FalconActivity.class));
                }
            });
        //} else {
        //    imageView.setVisibility(View.VISIBLE);

        //    roosterButton.setImageResource(R.drawable.ic_rooster_24dp);
        //    roosterButton.setOnClickListener(new View.OnClickListener() {
        //        @Override
        //        public void onClick(View v) {
        //            showShareDialog();
        //        }
        //   });

        //    Animation pulse = AnimationUtils.loadAnimation(this, R.anim.pulse);
        //    imageView.startAnimation(pulse);
        //}

        // Restore preferences
        mPlusOneButtonView = (LinearLayout) findViewById(R.id.plus_one_view);

        SharedPreferences settings = getSharedPreferences(PLUS_PREFS_NAME, 0);
        boolean clicked = settings.getBoolean("visible", false);
        if (clicked) {
            mPlusOneButtonView.setVisibility(View.INVISIBLE);
        }

        ImageView mPlusOneButton = (ImageView) findViewById(R.id.plus_one_button);
        mPlusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences settings = getSharedPreferences(PLUS_PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("visible", true);
                editor.commit();
                mPlusOneButtonView.setVisibility(View.INVISIBLE);
            }
        });

        Button upgradeButton = (Button) navigationView.getHeaderView(0).findViewById(R.id.upgrade);

        if(both_products.equals("1") || (no_ads.equals("1") && go_pro.equals("1"))) {
            upgradeButton.setText(R.string.purchased_label);
        }
        else{
            upgradeButton.setText(R.string.upgrade_label);
        }
        upgradeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                String userId =  getSharedPreferences("AzureUser", 0).getString("azureuser", null);
                String Phone = getSharedPreferences("AzureUser", 0).getString("phone_number", null);
                startActivity(new Intent(HomeActivity.this, UpgradeActivity.class)
                        .putExtra("UserID",userId)
                        .putExtra("Phone",Phone)
                );
            }
        });

        ImageButton mSentReminderButton = (ImageButton) findViewById(R.id.buttonSend);
        mSentReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, SentRemindersActivity.class));
            }
        });
    }
    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this,"Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            //super.onBackPressed();
            //return;
            if(doubleBackToExitPressedOnce){
                doExit();
            }
            else {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                }
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            showShareDialog();
        } else if (id == R.id.nav_rate) {
            AlertDialog.Builder rateDialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppTheme_AlertDialog);
            rateDialogBuilder.setView(R.layout.dialog_rate);
            rateDialogBuilder.setPositiveButton(R.string.ratenow_label, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    launchMarket();
                }
            });
            rateDialogBuilder.setNegativeButton(R.string.cancel, null);
            rateDialogBuilder.show();
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(HomeActivity.this, AboutActivity.class));
        } else if (id == R.id.nav_feedback) {
            startActivity(new Intent(HomeActivity.this, FeedbackActivity.class));
        } else if (id == R.id.nav_falcon) {
            startActivity(new Intent(HomeActivity.this, FalconActivity.class));
        } else if (id == R.id.nav_backup) {
            startActivity(new Intent(HomeActivity.this, BackupActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showShareDialog() {
        ShareListItemRecyclerViewAdapter shareAdapter = new ShareListItemRecyclerViewAdapter(ShareContent.ITEMS, HomeActivity.this);

        AlertDialog.Builder shareDialogBuilder = new AlertDialog.Builder(HomeActivity.this, R.style.AppTheme_AlertDialogButtonless);
        shareDialogBuilder.setTitle(getString(R.string.share_title));
        shareDialogBuilder.setAdapter(shareAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Handle click
            }
        }).create().show();

    }

    private void createGroup(String groupName) {
        new JavaScriptInterface(getApplicationContext()).createGroup(groupName);
        Toast.makeText(getApplicationContext(), HomeActivity.this.getResources().getString(R.string.group_created_label)+" "+ groupName, Toast.LENGTH_LONG).show();
        finish();
        startActivity(getIntent());
    }

    private void getGroups(String languageCode, String countryCode) {
        try {

            //TextView txtProgress =(TextView) findViewById(R.id.progressText);
            //txtProgress.setText("Fetching Groups...");
            animateView("show",HomeActivity.this,this.getResources().getString(R.string.fetch_group_label));
            this.mClient = new MobileServiceClient(AzureAppUrl, (Context) this);
            //field("country_code").eq(countryCode).and().
            this.mClient.getTable(reminder_group_template.class).where().field("language_code").eq(languageCode).orderBy("sort_order", QueryOrder.Ascending)
                    .execute(new TableQueryCallback<reminder_group_template>(){
                        public void onCompleted(List<reminder_group_template> result, int count, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                String group_name = "";
                                String group_name_id = "";
                                if (result.size() > 0) {
                                    for (reminder_group_template group : result) {
                                        if (group.is_user_group != null && group.is_user_group.equals("1")) {
                                            final String phone = getSharedPreferences("AzureUser", 0).getString("phone_number", null);
                                            if (group.group_profile != null && !group.group_profile.equals(""))
                                                group_name = group_name + group.reminder_group_name + "`+" + phone + "`" + group.group_profile + ",";
                                            else
                                                group_name = group_name + group.reminder_group_name + "`+" + phone + "`0,";
                                        } else {
                                            if (group.group_profile != null && !group.group_profile.equals(""))
                                                group_name = group_name + group.reminder_group_name + "`0`" + group.group_profile + ",";
                                            else
                                                group_name = group_name + group.reminder_group_name + "`0`0,";
                                        }
                                        group_name_id = group_name_id + group.id + "|" + group.reminder_group_name + ",";
                                    }
                                }
                                else{
                                    getGroups("en","1");
                                    return;
                                }
                                if (group_name != "") {
                                    group_name = group_name.substring(0, group_name.length() - 1);
                                    group_name_id = group_name_id.substring(0, group_name_id.length() - 1);
                                    //Toast.makeText(HomeActivity.this.getApplicationContext(), "Group Fetched " + group_name, Toast.LENGTH_LONG).show();
                                    getGroupMessages(group_name_id, group_name);
                                }
                            }
                        }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void getGroupMessages(final String group_name_id, String group_name_coll) {
        try {

            //TextView txtProgress =(TextView) findViewById(R.id.progressText);
            //txtProgress.setText("Fetching Messages...");
            animateView("show",HomeActivity.this,this.getResources().getString(R.string.fetch_msg_label));
            new JavaScriptInterface(getApplicationContext()).createMessage("", group_name_coll);
            this.mClient = new MobileServiceClient(AzureAppUrl, (Context) this);
            this.mClient.getTable(reminder_group_message_template.class).top(1000).execute(new TableQueryCallback<reminder_group_message_template>() {
                @Override
                public void onCompleted(List<reminder_group_message_template> result, int count, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        String message_date = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(Calendar.getInstance().getTime());

                        String message_time = "";
                        String message = "";
                        String frequency = "";
                        String group = "";
                        String data_val = "1-1";
                        String message_list = "";
                        String group_id = "";
                        String msg_type="";
                        String msg_trigger="";
                        String sys_msg_date="";
                        String msg_active="1";
                        String msg_demo="0";
                        String demo_min="0";
                        for (reminder_group_message_template msgs : result) {
                            message_time = msgs.message_time;
                            message = msgs.message;
                            frequency = msgs.frequency;
                            msg_type=msgs.msg_type;
                            msg_trigger=msgs.msg_trigger;
                            msg_active=msgs.message_active;
                            sys_msg_date= msgs.message_date;
                            msg_demo=msgs.is_demo;
                            demo_min=msgs.demo_minutes;
                            if(sys_msg_date!=null && !sys_msg_date.equals("")){
                                Calendar now = Calendar.getInstance();   // Gets the current date and time
                                int year = now.get(Calendar.YEAR);

                                sys_msg_date= sys_msg_date+"/"+Integer.toString(year);//"06/27/2007";
                                DateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH);
                                Date startDate;
                                try {
                                    startDate = df.parse(sys_msg_date);
                                    message_date = df.format(startDate);
                                } catch (ParseException e) {
                                   //e.printStackTrace();
                                }
                            }
                            if(msg_active==null){
                                msg_active="1";
                            }
                            if(msg_demo!=null && msg_demo.equals("1")){
                                frequency = "0";
                                message_date = new SimpleDateFormat("dd/MM/yyyy",Locale.ENGLISH).format(Calendar.getInstance().getTime());
                                //create Calendar instance
                                Calendar now = Calendar.getInstance();
                                //add minutes to current date using Calendar.add method
                                now.add(Calendar.MINUTE,Integer.parseInt(demo_min));
                                message_time = now.get(Calendar.HOUR_OF_DAY)
                                        + ":"
                                        + now.get(Calendar.MINUTE);
                                frequency = "0";
                            }

                            message_list =
                                    message_list + group + "|" + message + "|" + message_date + "|" + message_time + "|" + frequency + "|-99999" + "|" + data_val + "|" +
                                            msgs.reminder_group_id + "|" + msg_type + "|" + msg_trigger+ "|" +msg_active + "`";
                        }
                        if (message_list != "") {
                            new JavaScriptInterface(HomeActivity.this).createMessagesForGroups(message_list.substring(0, message_list.length() - 1), group_name_id);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
    private void getAppMessage(String languageCode, String countryCode) {
        try {
            this.mClient = new MobileServiceClient(AzureAppUrl, (Context) this);
            this.mClient.getTable(social_sharing_config.class).where().field("country_code").eq(countryCode).and().field("language_code").eq(languageCode).
                    execute(new TableQueryCallback<social_sharing_config>() {
                public void onCompleted(List<social_sharing_config> result, int count, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        String playstore_url = "";
                        String message = "";
                        for (social_sharing_config socialShare : result) {
                            playstore_url = socialShare.playstore_url;
                            message = socialShare.message;
                            SharedPreferences.Editor sfsocial = HomeActivity.this.getSharedPreferences("Social", 0).edit();
                            sfsocial.putString("social", message + "|" + playstore_url);
                            sfsocial.commit();
                        }
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getChecklist(final String languageCode, final String countryCode){
        try {


            //TextView txtProgress =(TextView) findViewById(R.id.progressText);
            //txtProgress.setText("Fetching Categories...");
            animateView("show",HomeActivity.this,this.getResources().getString(R.string.fetch_cat_label));
            this.mClient = new MobileServiceClient(AzureAppUrl, (Context) this);
            this.mClient.getTable(reminder_checklist.class).where().field("country_code").eq(countryCode).and().field("language_code").eq(languageCode)
                    .execute(new TableQueryCallback<reminder_checklist>(){
                        public void onCompleted(List<reminder_checklist> result, int count, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {
                                String checklist_name = "";
                                String checklist_id = "";
                                for (reminder_checklist checklist : result) {
                                    checklist_name = checklist_name + checklist.checklist_name + ",";
                                    checklist_id = checklist_id + checklist.id + "|" + checklist.checklist_name + ",";
                                }
                                if (checklist_name != "") {
                                    checklist_name = checklist_name.substring(0, checklist_name.length() - 1);
                                    checklist_id = checklist_id.substring(0, checklist_id.length() - 1);
                                    //Toast.makeText(HomeActivity.this.getApplicationContext(), "Categories Fetched " + checklist_name, Toast.LENGTH_LONG).show();
                                    getChecklistItems(checklist_id, checklist_name,languageCode,countryCode);
                                }
                                else{
                                    getGroups(languageCode, countryCode);
                                }
                            }
                            else{
                                getGroups(languageCode, countryCode);
                            }
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void getChecklistItems(final String checklist_name_id,String checklist_name_coll,final String languageCode,final String countryCode){
        try {

            //TextView txtProgress =(TextView) findViewById(R.id.progressText);
            //txtProgress.setText("Fetching Category Items...");
            animateView("show",HomeActivity.this,this.getResources().getString(R.string.fetch_cat_item_label));
            new JavaScriptInterface(getApplicationContext()).createChecklist(checklist_name_coll);
            this.mClient = new MobileServiceClient(AzureAppUrl, (Context) this);
            this.mClient.getTable(reminder_checklist_items.class).top(1000).execute(new TableQueryCallback<reminder_checklist_items>() {
                @Override
                public void onCompleted(List<reminder_checklist_items> result, int count, Exception exception, ServiceFilterResponse response) {
                    if (exception == null) {
                        String checklist = "";
                        String item_list = "";
                        String checklist_id = "";
                        String item="";
                        for (reminder_checklist_items items : result) {
                            item = items.item_name;
                            item_list = item_list + checklist + "|" + item + "|" + items.checklist_id + "`";
                        }
                        if (item_list != "") {
                            new JavaScriptInterface(HomeActivity.this).createItemsForChecklists(item_list.substring(0, item_list.length() - 1), checklist_name_id);
                            getGroups(languageCode, countryCode);
                        }
                        else{
                            getGroups(languageCode, countryCode);
                        }
                    }
                    else{
                        getGroups(languageCode, countryCode);
                    }
                }
            });
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,HomeActivity.this.getResources().getString(R.string.market_app_err_label), Toast.LENGTH_LONG).show();
        }
    }

    public static void animateView(String status,Context context,String Message) {
        /*final View view, final int toVisibility, float toAlpha, int duration
        boolean show = toVisibility == View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
        */
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_progress, null);
        TextView txtProgress =(TextView) view.findViewById(R.id.progressText);
        txtProgress.setText(Message);
        progressDialogBuilder.setView(view);

        AlertDialog ad = progressDialogBuilder.create();
        if(status.equals("show"))
            ad.show();
        else
            ad.dismiss();
    }

    private void createWeeklyReminder(){
        new JavaScriptInterface(HomeActivity.this).createWeeklyReminder();
    }

    /**
     * Exit the app if user select yes.
     */
    private void doExit() {
        /*
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                HomeActivity.this);

        alertDialog.setPositiveButton(R.string.yes_label, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });

        alertDialog.setNegativeButton(R.string.no_label, null);

        alertDialog.setMessage(R.string.exit_label);
        alertDialog.setTitle(R.string.app_name);
        alertDialog.show();
        */
        finishAffinity();
    }
}
