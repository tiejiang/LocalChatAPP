package com.ty.winchat.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.ThumbnailUtils;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.ty.winchat.WinChatApplication;

/**
 * Created by yinyu-tiejiang on 17-12-13.
 */

public class LocalVideoView extends SurfaceView implements SurfaceHolder.Callback {

    Bitmap bitmap;
//    public static int height= WinChatApplication.height-200-50;
//    public static int width=(int) (WinChatApplication.height*0.75);
    public static int height= WinChatApplication.height;
    public static int width=(int) (WinChatApplication.width);
    Matrix matrix=new Matrix();
    private SurfaceHolder surfaceHolder;
    private Canvas mCanvas;
    private boolean isDrawing;

    public LocalVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.setFocusable(true);
        this.setFocusableInTouchMode(true);
//		getHolder().addCallback(this);
        surfaceHolder = getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);

    }

    public LocalVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        isDrawing = true;
        new Thread(new DrawRunnable()).start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        isDrawing = false;
    }

    class DrawRunnable implements Runnable {

        @Override
        public void run() {

            while (isDrawing){
                drawAimLogo();
                try {
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawAimLogo(){

        mCanvas = this.surfaceHolder.lockCanvas();
        if (mCanvas != null){
            synchronized (surfaceHolder){
                try{
//                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);//绘制透明色
//                    mCanvas.drawBitmap(bitmap, width, height, null);
//                    mCanvas.drawBitmap(ThumbnailUtils.extractThumbnail(bitmap,540,600),0,0,null);
                    mCanvas.drawBitmap(ThumbnailUtils.extractThumbnail(bitmap,width,height),0,0,null);
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }
        }

        if (mCanvas != null){
            surfaceHolder.unlockCanvasAndPost(mCanvas);
        }

    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        invalidate();
    }
}
