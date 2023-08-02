package com.example.towncenterstore.general;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class CornersTransformation extends BitmapTransformation {
    private static final String ID = "com.example.app.RoundedCornersTransformation";
    private static final byte[] ID_BYTES = ID.getBytes();
    private float radius;

    public CornersTransformation(Context context, float radius) {
        super();
        this.radius = radius;
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap transformedBitmap = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888);

        if (transformedBitmap == null) {
            transformedBitmap = Bitmap.createBitmap(outWidth, outHeight, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(transformedBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(null);
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);

        RectF rect = new RectF(0, 0, outWidth, outHeight);
        Path path = new Path();
        path.addRoundRect(rect, radius, radius, Path.Direction.CW);
        canvas.clipPath(path);
        canvas.drawBitmap(toTransform, 0, 0, paint);

        return transformedBitmap;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CornersTransformation && ((CornersTransformation) obj).radius == radius;
    }

    @Override
    public int hashCode() {
        return ID.hashCode() + (int) (radius * 100);
    }

    @Override
    public void updateDiskCacheKey(MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
        messageDigest.update(ByteBuffer.allocate(4).putFloat(radius).array());
    }
}
