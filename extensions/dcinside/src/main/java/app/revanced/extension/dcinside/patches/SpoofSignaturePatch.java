package app.revanced.extension.dcinside.patches;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Base64;

import org.lsposed.hiddenapibypass.HiddenApiBypass;

import java.lang.reflect.Field;
import java.util.Map;

//
// Adapted from:
// https://github.com/L-JINBIN/ApkSignatureKillerEx/blob/3e6a8dc7de1b108dc70647f62bc499d7b68852b2/killer/src/main/java/bin/mt/signature/KillerApplication.java
//
public class SpoofSignaturePatch extends Application {
    static {
        String packageName = "com.dcinside.app.android";
        String certificateData = "MIIFiTCCA3GgAwIBAgIVAK/29kxj016lyXYxZeM/LGWGIuWIMA0GCSqGSIb3DQEB" +
                "CwUAMHQxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQH" +
                "Ew1Nb3VudGFpbiBWaWV3MRQwEgYDVQQKEwtHb29nbGUgSW5jLjEQMA4GA1UECxMH" +
                "QW5kcm9pZDEQMA4GA1UEAxMHQW5kcm9pZDAgFw0yMjA0MTkwMjUzNDZaGA8yMDUy" +
                "MDQxOTAyNTM0NlowdDELMAkGA1UEBhMCVVMxEzARBgNVBAgTCkNhbGlmb3JuaWEx" +
                "FjAUBgNVBAcTDU1vdW50YWluIFZpZXcxFDASBgNVBAoTC0dvb2dsZSBJbmMuMRAw" +
                "DgYDVQQLEwdBbmRyb2lkMRAwDgYDVQQDEwdBbmRyb2lkMIICIjANBgkqhkiG9w0B" +
                "AQEFAAOCAg8AMIICCgKCAgEArb5fyIyJ1WY9yr6X4927GIS8pS8c5WgsLo5ZYGC7" +
                "JuKPN8N8z0mRHDsbJtckQqWqN/zmIssbks+0GzluFvFmxXsIPbThzQ6S9QpzMNyU" +
                "MvkNiaVQ6jjufbLdu/1s9UuXpUVkwknw4aO72PPfJ9ZAuthsaxgVAG/MkAz4GH+y" +
                "fZBWk3rGIDFF073/smk99cHNmVUL8bQ56zGUztcLAlj5x1CaF/y5xXYK1oABe5h1" +
                "DRuWmRkLmeZbVBFD529MMwRjKi4ujGwg5IT4hvA47BZfvCATA46q8ZajTW7BtRqp" +
                "rZI7f9EXaBY9n3rB5Ka/S0UsXCT/Rrq9+ZFWU46DMKaRfu1cx03wlVM5nTuOyAg9" +
                "YbliSf3L8w/7ezjwfmi3H+Ge8EnLl8+ynWheltvDF4IKVDP99Y9zQC8qhDhPn/w7" +
                "hsyCgmPmSwrdgbilI22TY8jLGRxQS8IZIQZ30F132ilgrHg/UJOc6dHOG++RgeEE" +
                "NtXIvh3kZ9upuK1dfd6y92m0hn1fixPHEVTCLx9OyAuW0blY+fh8kkZIRWhGSx15" +
                "tt94O8HO1xGhDDZn3bWICLTYWVTwUqTvs20zqTxZELp+yY+h8UIbG9Cnj5TU39hV" +
                "iClwzn5ZmwWGOGp+vmyzb1mk+unEhrpBA7MZRKnoIqBe4tZdqQcMHol+/itRvXFI" +
                "rP8CAwEAAaMQMA4wDAYDVR0TBAUwAwEB/zANBgkqhkiG9w0BAQsFAAOCAgEAYNU7" +
                "UsoSIFDgaSQQGplgum2SEBAK/eJXZhDw5qYDayug7DK6iFIyj68hVqgFzsD8LTnN" +
                "AG0xOyJ7lJ+6qCMSk8fzt5wofMJU18EobwkbfhnYVJ9e4RRnjuo1SfGD2pEQGaIo" +
                "aV4kNLLik+6UTeHZOVBtuGvgwcrJZUm23mZH7KitRHkuiP06kamo1U8EsQ26LM44" +
                "WgbY49YbM/n8WZB2pW4vTzZ0tnj8OS0CjFTWdVraFiEwHZCUeCoHK7/tIFBQvPEW" +
                "xWhsDsJYKgOyvqwiclqr3VBvaUBWLImF8p1g961z4/DpQffVe36U/m4Qy3o/1KLk" +
                "0EA56E+94OrX4ksjg7jd6vUwix5pmQS7TleWOGvu0C2Mc9xOWhnKIpZxecDgZQmi" +
                "cWhX3J8H5m/DR+Lik7URiCT0hWQtnruaJcNeEQLL3CzXP4lsWIqFE1QkNi1p5qtf" +
                "SGiTwMFPSL4ArC9YsuDMHNhDGhJ1l6bmU0bvXBRrxlz3uszWfkc121VdlreLa1nz" +
                "Ss2mYvVPhzx+yD0eeuRzJEUOTzzzDU8fotTYwsAdS7AUjXHEEklEPZ6M4OrmzjAf" +
                "2T9SRYjUHQcts9w0H9u3WnrJF2OQnHer/Z5LmTGi+/MU84MFnoONvQJoVGaQepIK" +
                "hHXTC3/WixFCEwmrGNtkTGohCbAje2Ry5gSXrYY=";

        Signature fakeSignature = new Signature(Base64.decode(certificateData, Base64.DEFAULT));
        Parcelable.Creator<PackageInfo> originalCreator = PackageInfo.CREATOR;
        Parcelable.Creator<PackageInfo> creator = new Parcelable.Creator<>() {
            @Override
            @SuppressWarnings("deprecation")
            public PackageInfo createFromParcel(Parcel source) {
                PackageInfo packageInfo = originalCreator.createFromParcel(source);
                if (packageInfo.packageName.equals(packageName)) {
                    if (packageInfo.signatures != null && packageInfo.signatures.length > 0) {
                        packageInfo.signatures[0] = fakeSignature;
                    }
                    if (packageInfo.signingInfo != null) {
                        Signature[] signaturesArray = packageInfo.signingInfo.getApkContentsSigners();
                        if (signaturesArray != null && signaturesArray.length > 0) {
                            signaturesArray[0] = fakeSignature;
                        }
                    }
                }
                return packageInfo;
            }

            @Override
            public PackageInfo[] newArray(int size) {
                return originalCreator.newArray(size);
            }
        };
        try {
            getFieldAccessible(PackageInfo.class, "CREATOR").set(null, creator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        HiddenApiBypass.addHiddenApiExemptions(
                "Landroid/os/Parcel;",
                "Landroid/content/pm",
                "Landroid/app"
        );

        try {
            Object cache = getFieldAccessible(PackageManager.class, "sPackageInfoCache").get(null);
            if (cache != null) {
                cache.getClass().getMethod("clear").invoke(cache);
            }
        } catch (Throwable ignored) {
        }
        try {
            Map<?, ?> mCreators = (Map<?, ?>) getFieldAccessible(Parcel.class, "mCreators").get(null);
            if (mCreators != null) {
                mCreators.clear();
            }
        } catch (Throwable ignored) {
        }
        try {
            Map<?, ?> sPairedCreators = (Map<?, ?>) getFieldAccessible(Parcel.class, "sPairedCreators").get(null);
            if (sPairedCreators != null) {
                sPairedCreators.clear();
            }
        } catch (Throwable ignored) {
        }
    }

    private static Field getFieldAccessible(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
            while (true) {
                clazz = clazz.getSuperclass();
                if (clazz == null || clazz.equals(Object.class)) {
                    break;
                }
                try {
                    Field field = clazz.getDeclaredField(fieldName);
                    field.setAccessible(true);
                    return field;
                } catch (NoSuchFieldException ignored) {
                }
            }
            throw e;
        }
    }
}