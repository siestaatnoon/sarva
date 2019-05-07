package com.oscarrrweb.sarva.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.oscarrrweb.sarva.R;
import com.oscarrrweb.sarva.data.entity.TestEntity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import androidx.annotation.NonNull;

final public class FileUtils {

    public static byte[] getSampleImageBytesFromAndroidRes(@NonNull Context context) {
        Drawable drawable = context.getResources().getDrawable(R.drawable.ic_more_vert_black_24dp, context.getTheme());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }

    public static byte[] getSampleImageBytesFromTestResources() {
        ClassLoader loader = (new TestEntity()).getClass().getClassLoader();
        if (loader == null) {
            return null;
        }

        InputStream inputStream;
        try {
            inputStream = loader.getResourceAsStream("sample-image.png"); // loads from src/test/resources
        } catch (Exception e) {
            return null;
        }

        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        return outputStream.toByteArray();
    }
}
