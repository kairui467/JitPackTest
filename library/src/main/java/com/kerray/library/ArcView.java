package com.kerray.library;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * write this view for draw line,use it easy.
 */
public class ArcView extends View {

    private static final PaintFlagsDrawFilter FILTER = new PaintFlagsDrawFilter(0,
      Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

    private static final String TAG = ArcView.class.getSimpleName();
    private Context mContest;

    private Paint mPaint = new Paint();
    private Path mPath;

    private float mBorderWidth;
    private int mWidthHalf;

    private int mRaduis;
    private int mImageResId = android.R.drawable.ic_lock_idle_alarm;

    private String mText = "";

    private int measuredWidth;
    private int measuredHeight;

    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;
    private int mDirection = DIRECTION_UP;

    /** 顶部角度 */
    private final int UP_ANGLE = 270;
    /** 底部角度 */
    private final int DOWN_ANGLE = 90;


    /**
     * 构造函数
     * @param context 上下文
     */
    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContest = context.getApplicationContext();
        this.mRaduis = getScreenWidth(context) / 2;
        this.mBorderWidth = (mRaduis * 0.1f);
        init();
    }

    /**
     * 构造函数
     * @param context      上下文
     * @param pRaduis      半径
     * @param pBorderWidth 边框宽度
     */
    public ArcView(Context context, int pRaduis, Float pBorderWidth) {
        super(context);
        mContest = context.getApplicationContext();
        this.mRaduis = pRaduis;
        this.mBorderWidth = pBorderWidth;
        init();
    }

    private void init() {
        this.mWidthHalf = (int) (mBorderWidth / 2);

        int size = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14, //
          mContest.getResources().getDisplayMetrics());
        mPaint.setTextSize(size);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setAntiAlias(true);
    }

    public ArcView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //***************************** 1.0 ************************************
        // 弧形的高 = 圆半径 - （斜边^2 - 邻边^2 ）
        /* int measureText = (int) mPaint.measureText(mText);
        measuredWidth = measureText + (int) mBorderWidth;
		measuredHeight = (int) (mRaduis - Math.sqrt(mRaduis * mRaduis - (measuredWidth / 2) * (measuredWidth / 2)));
		measuredHeight += (int) mBorderWidth;
		setMeasuredDimension(measuredWidth, measuredHeight);*/
        // setMeasuredDimension(400, 400);

        //***************************** 2.0 ************************************
        int measureText = (int) mPaint.measureText(mText);
        measureText += mBorderWidth;

        // 实际半径
        int radius = (int) (mRaduis - mBorderWidth);
        // 已知圆心角和半径，可求出弧形的拱高和宽度
        int arcLengthToAngle = arcLengthToAngle(measureText, radius);
        measuredHeight = (int) (mRaduis - (Math.cos(Math.toRadians(arcLengthToAngle / 2)) * mRaduis));
        measuredWidth = (int) (Math.sin(Math.toRadians(arcLengthToAngle / 2)) * mRaduis * 2);
        //Log.i(TAG, "measuredWidth: " + measuredWidth + ", mRaduis:" + mRaduis);

        measuredHeight += (int) mBorderWidth;
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mDirection == DIRECTION_UP) {
            //drawTopText(canvas);
            drawUpText(canvas);
        } else {
            drawDownText(canvas);
        }

    }

    private void drawUpText(Canvas canvas) {
        if (mPath == null) {
            mPath = new Path();
        }

        canvas.save();
        canvas.setDrawFilter(FILTER);
        canvas.translate(-mRaduis + measuredWidth / 2, 0);

        int measureText = (int) mPaint.measureText(mText);
        int arcLengthToAngle = arcLengthToAngle(measureText, mRaduis);
        //int arcLengthToAngle = arcLengthToAngle(measuredWidth, mRaduis);
        int arcLengthToAngleHalf = arcLengthToAngle / 2;

        int startAngle = UP_ANGLE - arcLengthToAngleHalf;
        // 画图片
        Point calcAnglePoint = calcAnglePoint(mRaduis, startAngle);
        Bitmap bitmap = drawableToBitamp(getResources().getDrawable(mImageResId, null));

        float scale = 1;
        if (bitmap.getWidth() > mBorderWidth) {
            scale = (float) mBorderWidth / bitmap.getWidth();
        }

        Matrix matrix = new Matrix();
        // 计算图片旋转角度
        double rotateAngle = ((double) (measuredHeight - mBorderWidth)) / ((double) measuredWidth / 2);
        float angle = (float) Math.toDegrees(Math.atan(rotateAngle));
        angle = startAngle - UP_ANGLE;
        Log.i(TAG, "startAngle:" + startAngle + "  angle ; " + angle);
        matrix.postRotate(angle, 0, bitmap.getHeight());
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // y 轴上移 1/3
        //calcAnglePoint.y -= mBorderWidth / 3;
        canvas.drawBitmap(newBitmap, calcAnglePoint.x, calcAnglePoint.y, mPaint);

        int bitmapAngle = arcLengthToAngle(mBorderWidth, mRaduis);
        // 画弧形文字,需要从图片后面开始画
        startAngle += bitmapAngle;

        canvas.rotate(startAngle, mRaduis, mRaduis);
        mPath.addCircle(mRaduis, mRaduis, mRaduis, Path.Direction.CW);

        canvas.drawPath(mPath, mPaint);
        canvas.drawTextOnPath(mText, mPath, 0, mPaint.getTextSize(), mPaint);

        canvas.restore();
    }

    private void drawDownText(Canvas canvas) {
        if (mPath == null)
            mPath = new Path();

        canvas.save();
        canvas.setDrawFilter(FILTER);
        canvas.translate(-mRaduis + measuredWidth / 2, -mRaduis * 2 + measuredHeight);

        int measureText = (int) mPaint.measureText(mText);
        int arcLengthToAngle = arcLengthToAngle(measureText, mRaduis);
        //int arcLengthToAngle = arcLengthToAngle(measuredWidth, mRaduis);
        int arcLengthToAngleHalf = arcLengthToAngle / 2;

        int startAngle = DOWN_ANGLE + arcLengthToAngleHalf;
        // 画图片
        Point calcAnglePoint = calcAnglePoint(mRaduis, startAngle);
        Bitmap bitmap = drawableToBitamp(getResources().getDrawable(mImageResId, null));

        float scale = 1;
        if (bitmap.getWidth() > mBorderWidth) {
            scale = (float) mBorderWidth / bitmap.getWidth();
        }

        Matrix matrix = new Matrix();
        // 计算图片旋转角度
        double rotateAngle = ((double) (measuredHeight - mBorderWidth)) / ((double) measuredWidth / 2);
        float angle = (float) Math.toDegrees(Math.atan(rotateAngle));

        angle = UP_ANGLE - startAngle;
        matrix.postRotate(angle, 0, bitmap.getHeight());
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        // y 轴上移 1/3
        calcAnglePoint.y -= mBorderWidth / 3;
        canvas.drawBitmap(newBitmap, calcAnglePoint.x, calcAnglePoint.y, mPaint);

        int bitmapAngle = arcLengthToAngle(mBorderWidth, mRaduis);
        // 画弧形文字,需要从图片后面开始画
        startAngle -= bitmapAngle;

        canvas.rotate(startAngle, mRaduis, mRaduis);
        mPath.addCircle(mRaduis, mRaduis, mRaduis, Path.Direction.CCW);

        canvas.drawPath(mPath, mPaint);
        canvas.drawTextOnPath(mText, mPath, 0, 0/*mPaint.getTextSize() / 2*/, mPaint);

        canvas.restore();
    }

    private void drawTopText(Canvas canvas) {
        if (mPath == null) {
            mPath = new Path();
        }

        canvas.save();
        canvas.setDrawFilter(FILTER);
        canvas.translate(-mRaduis + measuredWidth / 2, 0);

        int arcLengthToAngle = arcLengthToAngle(measuredWidth, mRaduis);
        int arcLengthToAngleHalf = arcLengthToAngle / 2;

        int startAngle = 270 - arcLengthToAngleHalf;
        // 画图片
        Point calcAnglePoint = calcAnglePoint(mRaduis, startAngle);
        Bitmap bitmap = drawableToBitamp(getResources().getDrawable(mImageResId, null));

        float scale = 1;
        if (bitmap.getWidth() > mBorderWidth) {
            scale = (float) mBorderWidth / bitmap.getWidth();
        }

        Matrix matrix = new Matrix();
        // 计算图片旋转角度
        double rotateAngle = ((double) (measuredHeight - mBorderWidth)) / ((double) measuredWidth / 2);
        float angle = (float) Math.toDegrees(Math.atan(rotateAngle));
        matrix.postRotate(-angle, 0, bitmap.getHeight());
        matrix.postScale(scale, scale);
        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        canvas.drawBitmap(newBitmap, calcAnglePoint.x, calcAnglePoint.y, mPaint);
        /*canvas.drawRect(calcAnglePoint.x, calcAnglePoint.y, //
                calcAnglePoint.x + mBorderWidth, calcAnglePoint.y + mBorderWidth, mPaint);*/

        int bitmapAngle = arcLengthToAngle(mBorderWidth, mRaduis);
        // 画弧形文字,需要从图片后面开始画
        startAngle += bitmapAngle;
        RectF rectF = new RectF(mWidthHalf, mWidthHalf, mRaduis * 2 - mWidthHalf, mRaduis * 2 - mWidthHalf);
        mPath.addArc(rectF, startAngle, arcLengthToAngle);
        //canvas.drawPath(mPath, mPaint);
        canvas.drawTextOnPath(mText, mPath, 0, mPaint.getTextSize() / 2, mPaint);

        canvas.restore();
    }

    /**
     * 弧长转角度
     * @param arcLength 弧长
     * @param radius    半径
     * @return 角度
     */
    private int arcLengthToAngle(float arcLength, int radius) {
        // 角度 = 弧度 * 180 / πr
        return (int) (arcLength * 180d / radius / Math.PI);
    }

    /**
     * 已知圆心，半径，角度，求圆上的点坐标
     * @param radius     半径
     * @param startAngle 角度
     * @return 求圆上的点坐标
     * @see 公式：
     * 圆点坐标：(x0,y0)
     * 则圆上任一点为坐标：（x1,y1）
     * x1 = x0 + 半径 * cos(角度 * 3.14 / 180)
     * y1 = y0 + 半径 * sin(角度 * 3.14 / 180)
     */
    private Point calcAnglePoint(int radius, double startAngle) {
        int x = (int) (radius + radius * Math.cos(startAngle * Math.PI / 180));
        int y = (int) (radius + radius * Math.sin(startAngle * Math.PI / 180));
        return new Point(x, y);
    }

    private Bitmap drawableToBitamp(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    public void setText(String text) {
        this.mText = text;
        invalidate();
    }

    public void setTextSize(float size) {
        this.mPaint.setTextSize(size);
        invalidate();
    }

    public void setImageResource(@DrawableRes int resId) {
        this.mImageResId = resId;
        invalidate();
    }

    public void setDirection(int pDirection) {
        this.mDirection = pDirection;
        invalidate();
    }

    /**
     * 获得屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }
}
