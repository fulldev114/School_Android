package com.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

public class CircularImageView extends CSlinkImageView
{
    public static class RoundedDrawable extends Drawable
    {

        protected final BitmapShader bitmapShader;
        protected final RectF mBitmapRect;
        protected final RectF mRect = new RectF();
        protected final Paint paint = new Paint();

        public void draw(Canvas canvas)
        {
            canvas.drawCircle(mRect.width() / 2.0F, mRect.height() / 2.0F, mRect.width() / 2.0F, paint);
        }

        public int getOpacity()
        {
            return -3;
        }

        protected void onBoundsChange(Rect rect)
        {
            super.onBoundsChange(rect);
            mRect.set(0.0F, 0.0F, rect.width(), rect.height());
            Matrix matrix = new Matrix();
            matrix.setRectToRect(mBitmapRect, mRect, android.graphics.Matrix.ScaleToFit.FILL);
            bitmapShader.setLocalMatrix(matrix);
        }

        public void setAlpha(int i)
        {
            paint.setAlpha(i);
        }

        public void setColorFilter(ColorFilter colorfilter)
        {
            paint.setColorFilter(colorfilter);
        }

        public RoundedDrawable(Bitmap bitmap)
        {

            bitmapShader = new BitmapShader(bitmap, android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP);
            mBitmapRect = new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
            paint.setAntiAlias(true);
            paint.setShader(bitmapShader);
        }
    }


    public CircularImageView(Context context)
    {
        super(context);
    }

    public CircularImageView(Context context, AttributeSet attributeset)
    {
        super(context, attributeset);
    }

    public CircularImageView(Context context, AttributeSet attributeset, int i)
    {
        super(context, attributeset, i);
    }

    protected void onDraw(Canvas canvas) {
        try{
            super.onDraw(canvas);
        } catch(OutOfMemoryError ome) {
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    public void setBorderColor(int i)
    {
    }

    public void setBorderWidth(int i)
    {
    }

    public void setImageBitmap(Bitmap bitmap)
    {
        super.setImageDrawable(new RoundedDrawable(bitmap));
    }

    public void setImageDrawable(Drawable drawable)
    {
        if (drawable != null && (drawable instanceof RoundedDrawable))
        {
            super.setImageDrawable(drawable);
            return;
        }
        if (drawable != null && (drawable instanceof BitmapDrawable))
        {
            Bitmap bitmap = ((BitmapDrawable)drawable).getBitmap();
            if (bitmap == null || bitmap.isRecycled())
            {
                return;
            } else
            {
                super.setImageDrawable(new RoundedDrawable(bitmap));
                return;
            }
        } else
        {
            super.setImageDrawable(drawable);
            return;
        }
    }

    public void setImageResource(int i)
    {
        setImageDrawable(getResources().getDrawable(i));
    }

}