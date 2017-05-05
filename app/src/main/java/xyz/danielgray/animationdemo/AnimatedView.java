package xyz.danielgray.animationdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

public class AnimatedView extends SurfaceView implements SurfaceHolder.Callback {

    private int viewWidth;
    private int viewHeight;
    private long startNanoTime;

    private SurfaceHolder canvasHolder;

    public AnimatedView( Context context, AttributeSet attrs ) {
        super( context, attrs );


        canvasHolder = getHolder();
        canvasHolder.addCallback( this );

        setDrawingCacheEnabled( false );

        startNanoTime = System.nanoTime();
    }

    @Override
    protected void onSizeChanged( int xNew, int yNew, int xOld, int yOld ) {
        super.onSizeChanged( xNew, yNew, xOld, yOld );

        viewWidth = xNew;
        viewHeight = yNew;
    }

    @Override
    public void surfaceCreated( SurfaceHolder holder ) {
        Log.e("AnimatewView", "SURFACE CREATED!!");
        Thread animator = new Thread( new Animator() );
        animator.start();
    }

    @Override
    public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
        Log.e("AnimatewView", "SURFACE CHANGED!!");
        viewWidth = width;
        viewHeight = height;
    }

    @Override
    public void surfaceDestroyed( SurfaceHolder holder ) {
        Log.e("AnimatewView", "SURFACE DESTROYED!!");
    }

    private class Animator implements Runnable {
        @Override
        public void run() {
            long nanosPerCycle = 400000000L;

            Paint colorPaint = new Paint( 0 );
            colorPaint.setColor( Color.RED);
            colorPaint.setStyle( Paint.Style.FILL_AND_STROKE );
            Paint blackPaint = new Paint( 0 );
            blackPaint.setColor( Color.BLACK );
            blackPaint.setStyle( Paint.Style.FILL_AND_STROKE );

            Rect colorRect = new Rect();
            Rect blackRect = new Rect();
            // run for 300 seconds
            while ( System.nanoTime() - startNanoTime < 30*1000000000L ) {
                long currentNanoTimeDelta = System.nanoTime() - startNanoTime;

                long nanosWithinCycle = currentNanoTimeDelta % nanosPerCycle;

                long nanosInHalfCycle = nanosPerCycle / 2;
                int position;
                // grow during the first half, get smaller during the second half
                if ( nanosWithinCycle < nanosInHalfCycle ) {
                    position = (int) ( nanosWithinCycle * (long) viewWidth / nanosInHalfCycle );
                } else {
                    position = (int) ( ( nanosPerCycle - nanosWithinCycle ) * (long) viewWidth / nanosInHalfCycle );
                }
                    Canvas canvasToDrawOn = canvasHolder.lockCanvas();
                    colorRect.set( position - 1, 0, position, viewHeight );
                    blackRect.set( 0, 0, viewWidth, viewHeight );

                    canvasToDrawOn.drawRect( blackRect, blackPaint );
                    canvasToDrawOn.drawRect( colorRect, colorPaint );

                    canvasHolder.unlockCanvasAndPost( canvasToDrawOn );
            }
        }
    }
}
