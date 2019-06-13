package id.radityo.wallpapy;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class BlurTransformation extends BitmapTransformation {
    private RenderScript render;
    private float radius;

    public BlurTransformation(Context context, float radius) {
        super();
        this.radius = radius;
        render = RenderScript.create(context);
    }

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool,
            @NonNull Bitmap toTransform,
            int outWidth,
            int outHeight) {

        Bitmap blurredBitmap = toTransform.copy(Bitmap.Config.RGB_565, true);

        Allocation input = Allocation.createFromBitmap(
                render,
                blurredBitmap,
                Allocation.MipmapControl.MIPMAP_FULL,
                Allocation.USAGE_SHARED);
        Allocation output = Allocation.createTyped(
                render,
                input.getType());

        ScriptIntrinsicBlur sib = ScriptIntrinsicBlur.create(render, Element.U8_4(render));

        sib.setInput(input);
        sib.setRadius(radius);
        sib.forEach(output);

        output.copyTo(blurredBitmap);

        sib.destroy();
        input.destroy();
        output.destroy();
        render.destroy();

        return blurredBitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update("blur transformation".getBytes());
    }
}
