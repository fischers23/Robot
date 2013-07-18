package com.robot.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CVSliderSpeed extends SurfaceView implements SurfaceHolder.Callback {

	public CVSliderSpeed(Context context) {
		super(context);
	}

	public CVSliderSpeed(Context context, AttributeSet attrs) {
		super(context);
	}

    @Override
    public void onDraw (Canvas canvas) {
//        int dCenter = 40;     //Distance from center in px, NOT in dp
//        int centerX = (int)(getWidth()/2);
//        int centerY = (int)(getHeight()/2);
        
        

        String file = "@drawable/button_slider_vert";
        Bitmap bg = null;
        try {
            bg = BitmapFactory.decodeFile(file);
            canvas.setBitmap(bg);
        } catch (Exception e) {
            Log.d("MyGraphics", "setBitmap() failed according to debug");
        }
    }

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		setWillNotDraw(false); // Allows us to use invalidate() to call onDraw()
		postInvalidate();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
	}

}