package com.roosterr.adapters;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.TextView;
import com.roosterr.Base64;
import com.roosterr.EditActivity;
import com.roosterr.R;
import com.roosterr.Group;

import java.util.ArrayList;
import java.util.List;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.NativeExpressAdView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import static java.lang.Math.ceil;
import static java.lang.Math.round;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Group}
 * TODO: Replace the implementation with code for your data type.
 */
public class HomeListItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int CONTENT_TYPE = 123;
    private static final int AD_TYPE = 456;
    private final List<Group> mValues;
    private final List<Group> mInitialList;
    private final Context mContext;

    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private AdLoader mInstallAdLoader;
    private String  mAdUnitId = "ca-app-pub-6214469096466976/4832658248";//"ca-app-pub-6214469096466976/4832658248";//
    private NativeContentAd mContentAd;
    private NativeAppInstallAd mAppInstallAd;
    private NativeExpressAdView mExpressAd;
    boolean isContentAd=true;


    public HomeListItemRecyclerViewAdapter(Context context, List<Group> items) {
        mInitialList =items;// new ArrayList<>();

        mContext = context;
        //double itemCount = items.size();
        //itemCount=itemCount+(ceil(itemCount/2));

        final String both_products =  context.getSharedPreferences("Purchase_Type", 0).getString("both", null);
        final String no_ads =  context.getSharedPreferences("Purchase_Type", 0).getString("no_ads", null);

        //if(both_products.equals("1") || no_ads.equals("1")) {
            mValues = items;
        //}
        //else {
        //    mValues = new ArrayList<>();
        //    int ads = 0;
        //    for (int i = 0; i < itemCount; i++) {
        //        if ((i % 4 == 0) && (i > 0)) {
        //            mValues.add(null);
        //            mValues.add(items.get(i));
        //            ads++;
        //        } else {
        //            mValues.add(items.get(i));
        //        }
        //    }
        //}

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = null;
        RecyclerView.ViewHolder vh = null;

        if (viewType == AD_TYPE) {
            //if(isContentAd) {
            //    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home_content_ad, parent, false);
            //    vh = new AddViewHolder(v);
            //}
            //else {
            //    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home_app_install_ad, parent, false);
            //    vh = new AddInstallViewHolder(v);
            //}

            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home_ad, parent, false);
            vh = new AddExpressViewHolder(v);
        }
        else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_home, parent, false);
            vh = new ContentViewHolder(v);
        }

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder,final int position) {
        try {
            if (getItemViewType(position) == AD_TYPE) {
                //if(isContentAd){
                //    isContentAd=false;
                //    getNativeContentAds((AddViewHolder) holder);
                //}
                //else{
                //    isContentAd=true;
                //    getNativeInstallAds((AddInstallViewHolder) holder);
                //}

                getExpressAds((AddExpressViewHolder) holder);

            } else {
                if (mValues.get(position) != null) {
                    Integer group_sms = Integer.valueOf(0);
                    Integer group_contacts = Integer.valueOf(0);
                    if (((Group) this.mValues.get(position))._group_sms != null) {
                        group_sms = ((Group) this.mValues.get(position))._group_sms;
                    }
                    if (((Group) this.mValues.get(position))._group_contacts != null) {
                        group_contacts = ((Group) this.mValues.get(position))._group_contacts;
                    }
                    ((ContentViewHolder) holder).mItem =(Group)  mValues.get(position);
                    ((ContentViewHolder) holder).mCallView.setText("0");
                    ((ContentViewHolder) holder).mTextView.setText(group_sms.toString());
                    ((ContentViewHolder) holder).mAlarmView.setText("0");
                    ((ContentViewHolder) holder).mGroupView.setText(group_contacts.toString());
                    ((ContentViewHolder) holder).mContentView.setText(((Group) this.mValues.get(position))._group_name);
                    if (((Group) this.mValues.get(position))._image != null) {
                        byte[] decodedString = Base64.decode(((Group) this.mValues.get(position))._image);
                        ((ContentViewHolder) holder).mImageView.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    }
                    ((ContentViewHolder) holder).mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // TODO Handle click
                            String group_id = ((Group) HomeListItemRecyclerViewAdapter.this.mValues.get(position))._id;
                            Editor sfsocial = HomeListItemRecyclerViewAdapter.this.mContext.getSharedPreferences("groupid", 0).edit();
                            sfsocial.putString("groupid", group_id);
                            sfsocial.commit();
                            HomeListItemRecyclerViewAdapter.this.mContext.startActivity(new Intent(HomeListItemRecyclerViewAdapter.this.mContext, EditActivity.class).putExtra("groupid", group_id));
                        }
                    });
                }

            }
        }
        catch (Exception ex){

        }

    }

    private void getNativeInstallAds(AddInstallViewHolder holder) {
        loadInstallAd(holder);
    }

    private void getNativeContentAds(AddViewHolder holder) {
        loadAd(holder);
    }
    public void getExpressAds(AddExpressViewHolder holder){
        loadExpressAd(holder);
    }
    public void loadExpressAd(final AddExpressViewHolder mViewHolder) {
        synchronized (mSyncObject) {

            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mExpressAd != null) {
                mViewHolder.popolateView();
                return;
            }

        }
    }

    public void loadAd(final AddViewHolder mViewHolder) {
        synchronized (mSyncObject) {


            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                return;
            }


            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mContentAd != null) {
                mViewHolder.popolateView(mContentAd);
                return;
            }

            NativeContentAd.OnContentAdLoadedListener contentAdListener =
                    new NativeContentAd.OnContentAdLoadedListener() {
                        public void onContentAdLoaded(NativeContentAd ad) {
                            mContentAd = ad;
                            mViewHolder.popolateView(mContentAd);
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(mContext, mAdUnitId)
                        .forContentAd(contentAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                mViewHolder.hideView();
                            }
                        }).build();
            }
            // permissions is already available, load ads.
            fetchAd();
        }
    }

    public void fetchAd(){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();

        AdRequest request = new AdRequest.Builder()
                .addTestDevice(deviceid)
                .build();
        mAdLoader.loadAd(request);
    }

    public void loadInstallAd(final AddInstallViewHolder mViewHolder) {
        synchronized (mSyncObject) {

            if ((mInstallAdLoader != null) && mInstallAdLoader.isLoading()) {
                return;
            }

            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mAppInstallAd != null) {
                mViewHolder.populateView(mAppInstallAd);
                return;
            }

            NativeAppInstallAd.OnAppInstallAdLoadedListener appInstallAdListener =
                    new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                            mAppInstallAd = ad;
                            mViewHolder.populateView(mAppInstallAd);
                        }
                    };

            if (mInstallAdLoader == null) {
                mInstallAdLoader = new AdLoader.Builder(mContext, mAdUnitId)
                        .forAppInstallAd(appInstallAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                mViewHolder.hideView();
                            }
                        }).build();
            }
                 // permissions is already available, load ads.
                fetchInstallAd();
        }
    }

    public void fetchInstallAd(){
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceid = tm.getDeviceId();

        AdRequest request = new AdRequest.Builder()
                .addTestDevice(deviceid)
                .build();
        mInstallAdLoader.loadAd(request);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    @Override
    public long getItemId(int position) {
        // here code for getting the right itemID,
        // i.e. return super.getItemId(mPosition);
        // where mPosition ist the Position in the Collection.
        return super.getItemId(position);
    }
    @Override
    public int getItemViewType(int position)
    {
        if (mValues.get(position)==null) {
            return AD_TYPE;
        }
        return CONTENT_TYPE;
    }

    public class ContentViewHolder extends RecyclerView.ViewHolder {

        public final TextView mAlarmView;
        public final TextView mCallView;
        public final TextView mContentView;
        public final TextView mGroupView;
        public final ImageView mImageView;
        public Group mItem;
        public final TextView mTextView;
        public final View mView;

        public ContentViewHolder(View view) {
            super(view);
            this.mView = view;
            this.mCallView = (TextView) view.findViewById(R.id.date);
            this.mTextView = (TextView) view.findViewById(R.id.time);
            this.mAlarmView = (TextView) view.findViewById(R.id.repeat);
            this.mGroupView = (TextView) view.findViewById(R.id.group);
            this.mImageView = (ImageView) view.findViewById(R.id.image);
            this.mContentView = (TextView) view.findViewById(R.id.reminder_name);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }

    public class AddViewHolder extends RecyclerView.ViewHolder {
        public final NativeContentAdView mAdView;
        public AddViewHolder(View view) {

            super(view);
            this.mAdView = (NativeContentAdView) view;
            mAdView.setHeadlineView(mAdView.findViewById(R.id.contentad_headline));
            mAdView.setImageView(mAdView.findViewById(R.id.contentad_image));
            mAdView.setCallToActionView(mAdView.findViewById(R.id.contentad_call_to_action));
            mAdView.setAdvertiserView(mAdView.findViewById(R.id.contentad_advertiser));
        }

        public void popolateView(NativeContentAd contentAd){
            ((TextView) mAdView.getHeadlineView()).setText(contentAd.getHeadline());
            ((TextView) mAdView.getCallToActionView()).setText(contentAd.getCallToAction());
            ((TextView) mAdView.getAdvertiserView()).setText(contentAd.getAdvertiser());

            List<NativeAd.Image> images = contentAd.getImages();

            if (images != null && images.size() > 0) {
                ((ImageView) mAdView.getImageView())
                        .setImageDrawable(images.get(0).getDrawable());
            }

            // assign native ad object to the native view and make visible
            mAdView.setNativeAd(contentAd);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.setBackgroundColor(Color.TRANSPARENT);
        }

        public void hideView() {
            mAdView.setVisibility(View.GONE);
        }
    }

    public class AddExpressViewHolder extends RecyclerView.ViewHolder{
        public final NativeExpressAdView mAdView;
        public AddExpressViewHolder(View view) {
            super(view);
            this.mAdView = (NativeExpressAdView) view;
            mExpressAd =(NativeExpressAdView) mAdView.findViewById(R.id.adExpressView);
        }

        public void popolateView(){
           // mAdView.loadAd(new AdRequest.Builder().build());
           // mAdView.setVisibility(View.VISIBLE);
           // mAdView.setBackgroundColor(Color.TRANSPARENT);
            mExpressAd.loadAd(new AdRequest.Builder().build());
        }

        public void hideView() {
            mExpressAd.setVisibility(View.GONE);
        }
    }
    public class AddInstallViewHolder extends RecyclerView.ViewHolder {
        public NativeAppInstallAdView mAdView;
        public AddInstallViewHolder(View view) {

            super(view);
            this.mAdView =(NativeAppInstallAdView) view;

            mAdView.setHeadlineView(mAdView.findViewById(R.id.appinstall_headline));
            mAdView.setCallToActionView(mAdView.findViewById(R.id.appinstall_call_to_action));
            mAdView.setIconView(mAdView.findViewById(R.id.appinstall_app_icon));
            mAdView.setPriceView(mAdView.findViewById(R.id.appinstall_price));
            mAdView.setStarRatingView(mAdView.findViewById(R.id.appinstall_stars));
        }
        public void populateView(NativeAppInstallAd appInstallAd) {
            ((TextView) mAdView.getHeadlineView()).setText(appInstallAd.getHeadline());
            ((TextView) mAdView.getPriceView()).setText(appInstallAd.getPrice());
            ((Button) mAdView.getCallToActionView()).setText(appInstallAd.getCallToAction());
            ((ImageView) mAdView.getIconView()).setImageDrawable(appInstallAd.getIcon().getDrawable());
            ((RatingBar) mAdView.getStarRatingView())
                    .setRating(appInstallAd.getStarRating().floatValue());

            // assign native ad object to the native view and make visible
            mAdView.setNativeAd(appInstallAd);
            mAdView.setVisibility(View.VISIBLE);
            mAdView.setBackgroundColor(Color.TRANSPARENT);
        }

        /**
         * Hides the {@link NativeAppInstallAdView} used to display the native ad.
         */

        public void hideView() {
            mAdView.setVisibility(View.GONE);
        }
    }
}
