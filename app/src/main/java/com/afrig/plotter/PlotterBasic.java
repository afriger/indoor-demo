package com.afrig.plotter;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

//import android.support.annotation.Nullable;
public class PlotterBasic extends View
{
    private static final String tag = "Plotter";
    public static final String ttt = "TRA-CE";
    private final int DEFAULT_AXIS_WIDTH = 2;
    private final int DEFAULT_COORDINATE_TEXT_SIZE = 16;
    private final int DEFAULT_AXIS_COLOR = Color.BLACK;
    private final int DEFAULT_LINE_COLOR = Color.GREEN;
    private final int DEFAULT_MAX_XY = 300;
    private final int DEFAULT_UNIT_LENGTH = 50;
    private final int DEFAULT_PLOTTER_POINT_RADIUS = 11;
    private final int DEFAULT_PLOTER_POINT_COLOR = DEFAULT_AXIS_COLOR;
    private int mLineColor = DEFAULT_LINE_COLOR;
    private int mAxisColor = DEFAULT_AXIS_COLOR; // axis color
    private int nAxisWidth = DEFAULT_AXIS_WIDTH;
    private int mCoordinateTextSize = DEFAULT_COORDINATE_TEXT_SIZE; // the text size of text beside axis
    //change these for other size arrowheads
    private final double RADIUS = 32;
    private final double ANGLE = 20;
    private int xMax = DEFAULT_MAX_XY;
    private int yMax = DEFAULT_MAX_XY;
    private float mUnitLength = DEFAULT_UNIT_LENGTH;
    private int mWidth;
    private int mHeight;
    private PointF mOriginPoint;
    private PointF mLeftPoint;
    private PointF mRightPoint;
    private PointF mTopPoint;
    private PointF mBottomPoint;
    private Paint mPointPaint;
    private Paint mAxisPaint;
    private Paint mLinePaint;
    private Paint mCirclePaint;
    private Paint mgAxisPaint;
    private Paint mPointer;
    private List<AnchorPoint> mPoints = new ArrayList<>();
    private List<AnchorPoint> mCircles = new ArrayList<>();
    private List<Vector> mLines = new ArrayList<>();
    private final GestureDetector mGestureDetector;
    private boolean mSavePoints = false;

    public PlotterBasic(Context context)
    {
        super(context);
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
        init(context);
    }

    public PlotterBasic(Context context,/* @Nullable*/ AttributeSet attrs)
    {
        super(context, attrs);
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
        init(context);
    }

    public PlotterBasic(Context context, /*@Nullable*/ AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        mGestureDetector = new GestureDetector(context, new MyGestureListener());
        init(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (mGestureDetector.onTouchEvent(event)) return true;
        return true;// super.onTouchEvent(event);
    }

    public static class Vector
    {
        public final PointF a;
        public final PointF b;

        public Vector(PointF a, PointF b)
        {
            this.a = new PointF(a.x, a.y);
            this.b = new PointF(b.x, b.y);
        }
    }

    public void savePoints(boolean b)
    {
        mSavePoints = b;
    }

    public void SetPlotterSize(int xMax, int yMax, float UnitLength)
    {
        this.yMax = yMax;
        this.xMax = xMax;
        this.mUnitLength = UnitLength;
    }

    private void init(Context context)
    {
        mAxisPaint = new Paint();
        mAxisPaint.setStrokeWidth(nAxisWidth);
        mAxisPaint.setColor(mAxisColor);
        mAxisPaint.setAntiAlias(true);
        mAxisPaint.setStyle(Paint.Style.STROKE);
        mAxisPaint.setTextSize(mCoordinateTextSize);
        mLinePaint = new Paint();
        mLinePaint.setStrokeWidth(nAxisWidth * 2);
        mLinePaint.setColor(mLineColor);
        mLinePaint.setAntiAlias(true);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setTextSize(mCoordinateTextSize);
        mgAxisPaint = new Paint();
        mgAxisPaint.setColor(Color.LTGRAY);
        mgAxisPaint.setAntiAlias(true);
        mgAxisPaint.setStyle(Paint.Style.STROKE);
        mgAxisPaint.setTextSize(mCoordinateTextSize);
        mCirclePaint = new Paint();
        mCirclePaint.setColor(mAxisColor);
        mCirclePaint.setStyle(Paint.Style.STROKE);
        mPointPaint = new Paint();
        mPointPaint.setColor(DEFAULT_PLOTER_POINT_COLOR);
        mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPointPaint.setAntiAlias(true);
        mPointer = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPointer.setColor(Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        if (mOriginPoint == null)
        {
            mOriginPoint = new PointF();
            mOriginPoint.set(mWidth / 2f, mHeight / 2f);
            mLeftPoint = new PointF();
            mLeftPoint.set(0, mHeight / 2f);
            mRightPoint = new PointF();
            mRightPoint.set(mWidth, mHeight / 2f);
            mTopPoint = new PointF();
            mTopPoint.set(mWidth / 2f, 0);
            mBottomPoint = new PointF();
            mBottomPoint.set(mWidth / 2f, mHeight);
        }
        Log.e(ttt, "xmax: " + xMax + "; ymax: " + yMax + "; unitLength: " + mUnitLength);
        drawAxis(canvas);
        // draw circles
        for (int i = 0; i < mCircles.size(); ++i)
        {
            AnchorPoint c = mCircles.get(i);
            drawCirle(c, canvas);
        }
        // draw points
        for (int i = 0; i < mPoints.size(); ++i)
        {
            AnchorPoint point = mPoints.get(i);
            drawPoint(point, canvas);
        }
        //draw lines
        for (int i = 0; i < mLines.size(); ++i)
        {
            Vector v = mLines.get(i);
            drawLine(v.a, v.b, canvas);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom)
    {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();
        Log.e(ttt, "w: " + mWidth + "; h: " + mHeight);
    }

    public void addPoint(AnchorPoint point)
    {
        if (mPoints != null)
        {
            mPoints.add(point);
        }
    }

    public void addCirle(AnchorPoint c)
    {
        if (c != null)
        {
            mCircles.add(c);
        }
    }

    public void addLine(AnchorPoint a, AnchorPoint b)
    {
        addLine(a.getPoint(), b.getPoint());
    }

    public void addLine(PointF a, PointF b)
    {
        if (a != null && b != null)
        {
            Vector v = new Vector(a, b);
            mLines.add(v);
        }
    }

    public void reset(boolean bSavePoints)
    {
        if (mLines != null)
        {
            mLines.clear();
        }
        if (null != mPoints)
        {
            if (bSavePoints)
            {
                List<AnchorPoint> lp = SaveRedPoint();
                mPoints.clear();
                mPoints.addAll(lp);
            }
            else
            {
                mPoints.clear();
            }
        }
        if (mCircles != null)
        {
            mCircles.clear();
        }
        invalidate();
    }

    private List<AnchorPoint> SaveRedPoint()
    {
        List<AnchorPoint> lp = new ArrayList<>();
        for (AnchorPoint pp : mPoints)
        {
            if (pp.getPointColor() == Color.RED)
            {
                lp.add(pp);
            }
        }
        return lp;
    }

    private PointF convertLogicalPoint2Raw(PointF logical, float unitLength)
    {
        return convertLogicalPoint2Raw(logical.x, logical.y, unitLength, mOriginPoint);
    }

    private PointF convertLogicalPoint2Raw(float x, float y, float unitLength, PointF origin)
    {
        float rawX = origin.x + x * unitLength;
        float rawY = origin.y - y * unitLength;
        return new PointF(rawX, rawY);
    }

    private float c2raw(double x)
    {
        return (float) (mOriginPoint.x + x * mUnitLength);
    }

    private void drawPoint(AnchorPoint point, Canvas canvas)
    {
        PointF pointRaw = convertLogicalPoint2Raw(point.getPoint(), mUnitLength);
        if (point.getPointColor() != null)
        {
            mPointPaint.setColor(point.getPointColor());
        }
        int radius = point.getPointRadius() == null ? DEFAULT_PLOTTER_POINT_RADIUS : point.getPointRadius();
        canvas.drawCircle(pointRaw.x, pointRaw.y, radius, mPointPaint);
    }

    private void drawCirle(AnchorPoint p, Canvas canvas)
    {
        PointF pointRaw = convertLogicalPoint2Raw(p.getPoint(), mUnitLength);
        float radius = (float) p.r * mUnitLength;
        Path path = new Path();
        path.addCircle(pointRaw.x, pointRaw.y, radius, Path.Direction.CW);
        canvas.drawPath(path, mCirclePaint);
    }

    private void drawLine(PointF a, PointF b, Canvas canvas)
    {
        PointF start = convertLogicalPoint2Raw(a, mUnitLength);
        PointF end = convertLogicalPoint2Raw(b, mUnitLength);
        canvas.drawLine(start.x, start.y, end.x, end.y, mLinePaint);
        arrowHead(mLinePaint, canvas, start, end);
    }

    private void drawAxis(Canvas canvas)
    {
        // draw x axis
        canvas.drawLine(mLeftPoint.x, mLeftPoint.y, mRightPoint.x, mRightPoint.y, mAxisPaint);
        canvas.drawLine(mTopPoint.x, mTopPoint.y, mBottomPoint.x, mBottomPoint.y, mAxisPaint);
        for (int i = 0; i < xMax; ++i)
        {
            canvas.drawLine(mTopPoint.x + i * mUnitLength, mTopPoint.y, mBottomPoint.x + i * mUnitLength, mBottomPoint.y, mgAxisPaint);
            canvas.drawLine(mTopPoint.x - i * mUnitLength, mTopPoint.y, mBottomPoint.x - i * mUnitLength, mBottomPoint.y, mgAxisPaint);
        }
        for (int i = 0; i < yMax; ++i)
        {
            canvas.drawLine(mLeftPoint.x, mLeftPoint.y + i * mUnitLength, mRightPoint.x, mRightPoint.y + i * mUnitLength, mgAxisPaint);
            canvas.drawLine(mLeftPoint.x, mLeftPoint.y - i * mUnitLength, mRightPoint.x, mRightPoint.y - i * mUnitLength, mgAxisPaint);
        }
    }

    private void arrowHead(Paint paint, Canvas canvas, PointF start, PointF end)
    {
        double anglerad, lineangle;

        //some angle calculations
        anglerad = (Math.PI * ANGLE / 180.0f) / 2.0;
        lineangle = (Math.atan2(end.y - start.y, end.x - start.x));
        //tha triangle
        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(end.x, end.y);
        path.lineTo((float) (end.x - RADIUS * Math.cos(lineangle - anglerad)), (float) (end.y - RADIUS * Math.sin(lineangle - anglerad)));
        path.lineTo((float) (end.x - RADIUS * Math.cos(lineangle + anglerad)), (float) (end.y - RADIUS * Math.sin(lineangle + anglerad)));
        path.close();
        canvas.drawPath(path, paint);
    }

    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {
            scrollBy((int) distanceX, (int) distanceY);
            return true;
        }
    }
}//class Plotter
