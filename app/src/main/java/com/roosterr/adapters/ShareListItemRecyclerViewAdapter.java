package com.roosterr.adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Build.VERSION;
import android.provider.Telephony.Sms;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.roosterr.R;
import com.roosterr.dummy.ShareContent.ShareItem;
import com.roosterr.dummy.ShareContent;

import java.util.List;

public class ShareListItemRecyclerViewAdapter implements ListAdapter {

    private final List<ShareItem> mValues;
    private final Context mContext;

    public ShareListItemRecyclerViewAdapter(List<ShareItem> items, Context context) {
        mValues = items;
        mContext = context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return mValues.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_item_share, parent, false);
        ImageView mIconView = (ImageView) rowView.findViewById(R.id.share_icon);
        TextView mNameView = (TextView) rowView.findViewById(R.id.share_name);
        //ShareContent.ShareItem mItem = mValues.get(position);
        mNameView.setText(mValues.get(position).name);
        //holder.mIconView.setImageResource(mValues.get(position).name);
        mIconView.setImageResource(mValues.get(position).image);
        mNameView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareListItemRecyclerViewAdapter shareListItemRecyclerViewAdapter = ShareListItemRecyclerViewAdapter.this;
                String str = "\\|";
                String[] content = mContext.getSharedPreferences("Social", 0).getString("social", null).split("\\|");
                String title = content[0];
                String body = content[0] + " " + content[1];
                switch (position) {
                    case 0:
                        Intent email = new Intent("android.intent.action.SEND");
                        str = "android.intent.extra.EMAIL";
                        email.putExtra(Intent.EXTRA_EMAIL, new String[]{"yourfriend@somedomain.com"});
                        email.putExtra("android.intent.extra.SUBJECT", "Subject");
                        email.putExtra("android.intent.extra.TEXT", body);
                        email.setType("message/rfc822");
                        ShareListItemRecyclerViewAdapter.this.mContext.startActivity(Intent.createChooser(email, "Choose an Email client :"));
                        break;
                    case 1:
                        String whatsAppMessage = body;
                        Intent sendIntent = new Intent();
                        sendIntent.setAction("android.intent.action.SEND");
                        sendIntent.putExtra("android.intent.extra.TEXT", whatsAppMessage);
                        sendIntent.setType("text/plain");
                        sendIntent.setPackage("com.whatsapp");
                        ShareListItemRecyclerViewAdapter.this.mContext.startActivity(sendIntent);
                        break;
                    case 2:
                        Intent fintent = new Intent("android.intent.action.VIEW", Uri.parse("http://www.facebook.com/sharer/sharer.php?u=" + content[1]));
                        ShareListItemRecyclerViewAdapter.this.mContext.startActivity(fintent);
                        break;
                    case 3:
                        Intent tintent = new Intent("android.intent.action.VIEW", Uri.parse("http://twitter.com/share?text=" + title + "&url=" + content[1]));
                        ShareListItemRecyclerViewAdapter.this.mContext.startActivity(tintent);
                        break;
                    case 4:
                        try {
                            Intent smsIntent;
                            if (VERSION.SDK_INT >= 19) {
                                String defaultSmsPackageName = Sms.getDefaultSmsPackage(ShareListItemRecyclerViewAdapter.this.mContext);
                                smsIntent = new Intent("android.intent.action.SEND");
                                smsIntent.setType("text/plain");
                                smsIntent.putExtra("android.intent.extra.TEXT", body);
                                if (defaultSmsPackageName != null) {
                                    smsIntent.setPackage(defaultSmsPackageName);
                                }
                                ShareListItemRecyclerViewAdapter.this.mContext.startActivity(smsIntent);
                                return;
                            }
                            smsIntent = new Intent("android.intent.action.VIEW");
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            smsIntent.putExtra("sms_body", body);
                            ShareListItemRecyclerViewAdapter.this.mContext.startActivity(smsIntent);
                        } catch (Exception e) {
                            Toast.makeText(mContext, "app not available",Toast.LENGTH_SHORT).show();
                        }
                        break;
                    default:
                }
            }
        });//.setOnClickListener(new C03961(position));
        return rowView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return mValues.isEmpty();
    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }
}
