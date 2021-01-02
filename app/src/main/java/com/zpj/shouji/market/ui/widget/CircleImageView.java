package com.zpj.shouji.market.ui.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

public class CircleImageView extends AppCompatImageView {


    private int outCircleWidth = 0;
    private int outCircleColor = Color.WHITE;

    private int viewWidth;
    private int viewHeight;

    private Bitmap image;
    private Paint  paintBorder;


    public CircleImageView(Context context) {
        super(context);
        setup(context, null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup(context, attrs);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup(context, attrs);
    }


    private void setup(Context context, AttributeSet attrs) {
        paintBorder = new Paint();
        paintBorder.setColor(outCircleColor);
        paintBorder.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = measureWidth(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);

        viewWidth = width - (outCircleWidth * 2);
        viewHeight = height - (outCircleWidth * 2);

        setMeasuredDimension(width, height);

    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        int size = MeasureSpec.getSize(heightMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = viewHeight;
        }
        return result;
    }

    private int measureWidth(int widthMeasureSpec) {
        int result = 0;
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);

        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = viewWidth;
        }


        return result;
    }

    public void setBorderColor(int borderColor) {
        if (paintBorder != null)
            paintBorder.setColor(borderColor);

        this.invalidate();
    }

    public void setBorderWidth(int outCircleWidth) {
        this.outCircleWidth = outCircleWidth;

        this.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        loadBitmap();

        if (image != null) {
            int min = Math.min(viewWidth, viewHeight);

            int circleCenter = min / 2;

            image = Bitmap.createScaledBitmap(image, min, min, false);

            canvas.drawCircle(circleCenter + outCircleWidth, circleCenter + outCircleWidth, circleCenter + outCircleWidth, paintBorder);

            canvas.drawBitmap(createCircleImage(image, min), outCircleWidth, outCircleWidth, null);
        }
    }

    private Bitmap createCircleImage(Bitmap source, int min) {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(min, min, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(min / 2f, min / 2f, min / 2f, paint);
        /**
         * 使用SRC_IN，参考上面的说明
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(source, 0, 0, paint);
        return target;


    }

    private void loadBitmap() {
        BitmapDrawable bitmapDrawable = (BitmapDrawable) this.getDrawable();

        if (bitmapDrawable != null) {
            image = bitmapDrawable.getBitmap();
        }
    }
}
