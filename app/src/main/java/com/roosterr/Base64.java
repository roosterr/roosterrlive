package com.roosterr;

import io.fabric.sdk.android.services.settings.SettingsJsonConstants;

public class Base64 {
    private Base64() {
    }

    public static final String encode(byte[] d) {
        if (d == null) {
            return null;
        }
        int idx;
        byte[] data = new byte[(d.length + 2)];
        System.arraycopy(d, 0, data, 0, d.length);
        byte[] dest = new byte[((data.length / 3) * 4)];
        int sidx = 0;
        int didx = 0;
        while (sidx < d.length) {
            dest[didx] = (byte) ((data[sidx] >>> 2) & 63);
            dest[didx + 1] = (byte) (((data[sidx + 1] >>> 4) & 15) | ((data[sidx] << 4) & 63));
            dest[didx + 2] = (byte) (((data[sidx + 2] >>> 6) & 3) | ((data[sidx + 1] << 2) & 63));
            dest[didx + 3] = (byte) (data[sidx + 2] & 63);
            sidx += 3;
            didx += 4;
        }
        for (idx = 0; idx < dest.length; idx++) {
            if (dest[idx] < 26) {
                dest[idx] = (byte) (dest[idx] + 65);
            } else if (dest[idx] < 52) {
                dest[idx] = (byte) ((dest[idx] + 97) - 26);
            } else if (dest[idx] < 62) {
                dest[idx] = (byte) ((dest[idx] + 48) - 52);
            } else if (dest[idx] < 63) {
                dest[idx] = (byte) 43;
            } else {
                dest[idx] = (byte) 47;
            }
        }
        for (idx = dest.length - 1; idx > (d.length * 4) / 3; idx--) {
            dest[idx] = (byte) 61;
        }
        return new String(dest);
    }

    public static final String encode(String s) {
        return encode(s.getBytes());
    }

    public static final byte[] decode(String str) {
        if (str == null) {
            return null;
        }
        return decode(str.getBytes());
    }

    public static final byte[] decode(byte[] data) {
        int tail = data.length;
        while (data[tail - 1] == (byte) 61) {
            tail--;
        }
        byte[] dest = new byte[(tail - (data.length / 4))];
        int idx = 0;
        while (idx < data.length) {
            if (data[idx] == (byte) 61) {
                data[idx] = (byte) 0;
            } else if (data[idx] == 47) {
                data[idx] = (byte) 63;
            } else if (data[idx] == 43) {
                data[idx] = (byte) 62;
            } else if (data[idx] >= 48 && data[idx] <= 57) {
                data[idx] = (byte) (data[idx] + 4);
            } else if (data[idx] >= 97 && data[idx] <= 122) {
                data[idx] = (byte) (data[idx] - 71);
            } else if (data[idx] >= 65 && data[idx] <= 90) {
                data[idx] = (byte) (data[idx] - 65);
            }
            idx++;
        }
        int sidx = 0;
        int didx = 0;
        while (didx < dest.length - 2) {
            dest[didx] = (byte) (((data[sidx] << 2) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) | ((data[sidx + 1] >>> 4) & 3));
            dest[didx + 1] = (byte) (((data[sidx + 1] << 4) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) | ((data[sidx + 2] >>> 2) & 15));
            dest[didx + 2] = (byte) (((data[sidx + 2] << 6) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) | (data[sidx + 3] & 63));
            sidx += 4;
            didx += 3;
        }
        if (didx < dest.length) {
            dest[didx] = (byte) (((data[sidx] << 2) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) | ((data[sidx + 1] >>> 4) & 3));
        }
        didx++;
        if (didx < dest.length) {
            dest[didx] = (byte) (((data[sidx + 1] << 4) & SettingsJsonConstants.SETTINGS_IDENTIFIER_MASK_DEFAULT) | ((data[sidx + 2] >>> 2) & 15));
        }
        return dest;
    }

    public static final void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: Base64 string");
            System.exit(0);
        }
        try {
            String e = encode(args[0].getBytes());
            String d = new String(decode(e));
            System.out.println("Input   = '" + args[0] + "'");
            System.out.println("Encoded = '" + e + "'");
            System.out.println("Decoded = '" + d + "'");
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
