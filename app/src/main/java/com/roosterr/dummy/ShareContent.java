package com.roosterr.dummy;

import com.roosterr.R;

import java.util.ArrayList;
import java.util.List;


public class ShareContent {

    public static final List<ShareItem> ITEMS = new ArrayList<ShareItem>();
    private static final String[] NAMES = new String[]{"Email", "WhatsApp", "Facebook", "Twitter", "SMS Text"};
    private static final int[] ICONS = new int[]{R.drawable.ic_email, R.mipmap.ic_whatsapp, R.mipmap.ic_facebook, R.mipmap.ic_twitter, R.drawable.ic_phone_android};

    static {
        // Add some sample items.
        for (int i = 0; i < NAMES.length; i++) {
            ITEMS.add(createShareItem(i));
        }
    }

    private static ShareItem createShareItem(int position) {
        return new ShareItem(String.valueOf(position), NAMES[position], ICONS[position]);
    }

    /**
     * A dummy item representing a piece of name.
     */
    public static class ShareItem {
        public final String id;
        public final String name;
        public final int image;

        public ShareItem(String id, String name, int icon) {
            this.id = id;
            this.name = name;
            this.image = icon;
        }

        @Override
        public String toString() {
            return name;
        }
    }
}
