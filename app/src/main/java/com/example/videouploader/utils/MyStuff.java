package com.example.videouploader.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.StrictMode;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.ClickableSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.UnderlineSpan;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import static com.example.videouploader.models.Models.Media.IMAGE_SUR;
import static com.example.videouploader.models.Models.Media.VIDEO;
import static com.example.videouploader.models.Models.Media.VIDEO_SUR;

public interface MyStuff {
    String HY = "-";

    static String getMediaID(String type, String uid) {
        if (type.equals(VIDEO)) {
            return uid.concat(HY).concat(Calendar.getInstance().getTime().toString()).concat(HY).concat(VIDEO_SUR);
        } else {
            return uid.concat(HY).concat(Calendar.getInstance().getTime().toString()).concat(HY).concat(IMAGE_SUR);
        }
    }

    //Editor
    @RequiresApi(api = Build.VERSION_CODES.P)
    static SpannableString bulletSpan(int color, String s) {
        SpannableString string = new SpannableString(s);
        BulletSpan bulletSpan = new BulletSpan(40, color, 20);
        string.setSpan(bulletSpan, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return string;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    static void addBulletToNextLine(int color, String s) {
        SpannableString string = new SpannableString(s);
        BulletSpan bulletSpan = new BulletSpan(40, color, 20);
        //  string.setSpan(bulletSpan,);
    }

    static SpannableStringBuilder underlineString(String s) {
        SpannableStringBuilder underlined = new SpannableStringBuilder(s);
        underlined.setSpan(new UnderlineSpan(), 0, s.length(), 0);
        return underlined;
    }

    static SpannableString strikeString(String s) {
        SpannableString stricken = new SpannableString(s);
        stricken.setSpan(new StrikethroughSpan(), 0, s.length(), 0);
        return stricken;
    }

    static SpannableString boldString(String s) {
        SpannableString boldness = new SpannableString(s);
        boldness.setSpan(new StyleSpan(Typeface.BOLD), 0, s.length(), 0);
        return boldness;
    }

    static SpannableString italicString(String s) {
        SpannableString mamamia = new SpannableString(s);
        mamamia.setSpan(new StyleSpan(Typeface.ITALIC), 0, s.length(), 0);
        return mamamia;
    }

    static SpannableString boldItalicString(String s) {
        SpannableString boldnessMamamia = new SpannableString(s);
        boldnessMamamia.setSpan(new StyleSpan(Typeface.BOLD_ITALIC), 0, s.length(), 0);
        return boldnessMamamia;
    }

    static SpannableString subscriptString(String s) {
        SpannableString sub = new SpannableString(s);
        sub.setSpan(new SubscriptSpan(), 0, s.length(), 0);
        return sub;
    }

    static SpannableString specificSubscriptString(String s, int begin, int end) {
        SpannableString sup = new SpannableString(s);
        sup.setSpan(new SubscriptSpan(), begin, end, 0);
        return sup;
    }

    static SpannableString superscriptString(String s) {
        SpannableString sup = new SpannableString(s);
        sup.setSpan(new SuperscriptSpan(), 0, s.length(), 0);
        return sup;
    }

    static SpannableString specificSuperscriptString(String s, int begin, int end) {
        SpannableString sup = new SpannableString(s);
        sup.setSpan(new SuperscriptSpan(), begin, end, 0);
        return sup;
    }

    static SpannableString urlString(String s) {
        SpannableString urlSpan = new SpannableString(s);
        urlSpan.setSpan(new SuperscriptSpan(), 0, s.length(), 0);
        return urlSpan;
    }

    static SpannableString clickString(String s) {
        SpannableString clickSpan = new SpannableString(s);
        clickSpan.setSpan(new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {

            }
        }, 0, s.length(), 0);
        return clickSpan;
    }

    static Bitmap drawableToBitmap(Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    static Bitmap getBitmapFromURL(String src) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @SuppressLint("NewApi")
    static Bitmap blurRenderScript(Bitmap smallBitmap, int radius, Activity activity) {

        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


        Bitmap bitmap = Bitmap.createBitmap(smallBitmap.getWidth(), smallBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(activity);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius); // radius must be 0 < r <= 25
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }

    static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];

        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());

        //Create a Bitmap of the appropriate format.
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

        //Set RGB pixels.
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}
