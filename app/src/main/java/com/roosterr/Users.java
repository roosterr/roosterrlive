package com.roosterr;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.widget.Toast;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.http.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.table.TableOperationCallback;
import com.microsoft.windowsazure.mobileservices.table.TableQueryCallback;
import com.microsoft.windowsazure.mobileservices.table.query.QueryOrder;

import java.util.List;

public class Users {
    public String phoneNumber;

    public String no_ads;
    public String go_pro;
    public String both;
    public Integer is_ready;

    private static final String AzureAppUrl = "https://roosterr.azurewebsites.net";
    private static MobileServiceClient mClient;

    public Users(String user_number) {
        this.phoneNumber = user_number;
    }
    public Users(String user_number,String noads,String gopro,String both_product) {
        this.phoneNumber = user_number;
        this.go_pro=gopro;
        this.no_ads=noads;
        this.both=both_product;
    }
    public Users(String user_number,String noads,String gopro,String both_product,Integer ready) {
        this.phoneNumber = user_number;
        this.go_pro=gopro;
        this.no_ads=noads;
        this.both=both_product;
        this.is_ready=ready;
    }
    public static void createUser(Context context, String user_number,String no_ads,String go_pro,String both) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        dbController.createUser(user_number,no_ads,go_pro,both);
    }

    public static Users getUser(Context context) {
        SQLController dbController = new SQLController(context);
        dbController.open();
        Cursor cursor = dbController.getUser();
        Users user = null;
        while (!cursor.isAfterLast()) {
            user = new Users(
                    cursor.getString(cursor.getColumnIndex(DBHelper.USER_NUMBER)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.NO_ADS)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.GO_PRO)),
                    cursor.getString(cursor.getColumnIndex(DBHelper.BOTH_PRODUCTS)),
                    cursor.getInt(cursor.getColumnIndex(DBHelper.IS_READY))
                    );
            cursor.moveToNext();
        }
        return user;
    }

    public static void getAzureUserID(final Context context,final String countryCode, final String phoneNumber){
        try {
            mClient = new MobileServiceClient(AzureAppUrl, context);
            mClient.getTable(App_Users.class).where().field("country_code").eq(countryCode).and().field("phone").eq(phoneNumber)
                    .execute(new TableQueryCallback<App_Users>(){
                        public void onCompleted(List<App_Users> result, int count, Exception exception, ServiceFilterResponse response) {
                            if (exception == null) {

                                for (App_Users user : result) {

                                    SharedPreferences.Editor sfAzureUser = context.getSharedPreferences("AzureUser", 0).edit();
                                    sfAzureUser.putString("azureuser", user.id);
                                    sfAzureUser.putString("phone_number", countryCode+phoneNumber);
                                    sfAzureUser.commit();

                                    SharedPreferences.Editor purchaseType = context.getSharedPreferences("Purchase_Type", 0).edit();
                                    if(user.no_ads==null)
                                        user.no_ads="0";
                                    if(user.go_pro==null)
                                        user.go_pro="0";
                                    if(user.both==null)
                                        user.both="0";

                                    purchaseType.putString("no_ads", user.no_ads);
                                    purchaseType.putString("go_pro", user.go_pro);
                                    purchaseType.putString("both", user.both);

                                    purchaseType.commit();

                                }
                            }
                        }
                    });
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static void updatePurchase(Context context,String phone, String no_ads, String go_pro, String both) {
        if(!no_ads.equals("0") || !go_pro.equals("0") || !both.equals("0")) {
            SQLController dbController = new SQLController(context);
            dbController.open();
            dbController.updatePurchase(phone, no_ads, go_pro, both);
        }
    }


    public static void updateAzurePurchase(Context context, String userID, final String noads, final String gopro, final String both_product) {
        if(!noads.equals("0") || !gopro.equals("0") || !both_product.equals("0")) {
            try {
                mClient = new MobileServiceClient(AzureAppUrl, context);
                mClient.getTable(App_Users.class).where().field("id").eq(userID)
                        .execute(new TableQueryCallback<App_Users>() {
                            public void onCompleted(List<App_Users> result, int count, Exception exception, ServiceFilterResponse response) {
                                if (exception == null) {
                                    for (App_Users user : result) {
                                        user.both = both_product;
                                        user.no_ads = noads;
                                        user.go_pro = gopro;

                                        mClient.getTable(App_Users.class).update(user, new TableOperationCallback<App_Users>() {
                                            public void onCompleted(App_Users entity, Exception exception, ServiceFilterResponse response) {
                                                if (exception == null) {

                                                }
                                            }
                                        });
                                    }
                                }
                            }
                        });
            } catch (Exception ex) {
                Toast.makeText(context, ex.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void resetDB(Context context) {
        SQLController db = new SQLController(context);
        db.open();
        db.resetDB();

    }

}
