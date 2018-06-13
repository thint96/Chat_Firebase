package fsi.studymyselft.nguyenthanhthi.chatapp.other;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by thanhthi on 06/06/2018.
 */

public class DrawableHelper {

    private Context context;
    private int color;
    private int customColor;
    private String colorString;
    private Drawable drawable;
    private Drawable wrappedDrawable;

    public DrawableHelper(Context context) {
        this.context = context;
    }

    public static DrawableHelper withContext(Context context) {
        return new DrawableHelper(context);
    }

    public DrawableHelper withDrawable(int drawableRes) {
        drawable = ContextCompat.getDrawable(context, drawableRes);
        return this;
    }

    public DrawableHelper withDrawable(Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    public DrawableHelper withColor(int colorRes) {
        color = ContextCompat.getColor(context, colorRes);
        return this;
    }

    public DrawableHelper customColor(String color) {
        colorString = color;
        return this;
    }

    public DrawableHelper customTint() {
        if (drawable == null) {
            throw new NullPointerException("drawable is null");
        }

        if (colorString.isEmpty()) {
            throw new IllegalStateException("colorString is null");
        }

        wrappedDrawable = drawable.mutate();
        wrappedDrawable = DrawableCompat.wrap(wrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, Color.parseColor(colorString));
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN);

        return this;
    }

    public DrawableHelper tint() {
        if (drawable == null) {
            throw new NullPointerException("You must report drawable resources using method withDrawable()");
        }

        if (color == 0) {
            throw new IllegalStateException("You must report drawable resources using method withColor()");
        }

        wrappedDrawable = drawable.mutate();
        wrappedDrawable = DrawableCompat.wrap(wrappedDrawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        DrawableCompat.setTintMode(wrappedDrawable, PorterDuff.Mode.SRC_IN);

        return this;
    }

    public void applyToBackground(View view) {
        if (wrappedDrawable == null) {
            throw new NullPointerException("You must call method tint()");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(wrappedDrawable);
        } else {
            view.setBackgroundDrawable(wrappedDrawable);
        }
    }

    public void applyTo(ImageView imageView) {
        if (wrappedDrawable == null) {
            throw new NullPointerException("You must call method tint()");
        }
        imageView.setImageDrawable(wrappedDrawable);
    }

    public void applyTo(MenuItem menuItem) {
        if (wrappedDrawable == null) {
            throw new NullPointerException("You must call method tint()");
        }
        menuItem.setIcon(wrappedDrawable);
    }

    public Drawable get() {
        if (wrappedDrawable == null) {
            throw new NullPointerException("You must call method tint()");
        }
        return wrappedDrawable;
    }
}
