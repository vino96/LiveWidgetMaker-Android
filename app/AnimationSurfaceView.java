package com.example.k014c1298.livewidgetmaker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AnimationSurfaceView extends SurfaceView
        implements Runnable, SurfaceHolder.Callback {
    public final String FILE_DIR_PATH = "/sdcard/test/Documents/";
    public final float SCREEN_SCALE = 2F;
    //public final float SCREEN_SCALE = 4.5F;
    public final int SCREEN_DEF = 230;
    //public final int SCREEN_DEF = 1150;
    static final long FPS = 20;
    static final long FRAME_TIME = 1000 / FPS;
    static final int BALL_R = 30;
    public PackageManager pm;
    SurfaceHolder surfaceHolder;
    Thread thread;
    int cx = BALL_R, cy = BALL_R;
    int screen_width, screen_height;
    public List<Zukei> zukeis;
    public String folderName;
    private Activity myActivity;

    public AnimationSurfaceView(Context context,List<Zukei> zukeis,String folderName, Activity pAct) {
        super(context);
        pm = context.getPackageManager();
        this.zukeis = zukeis;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        this.folderName = folderName;
        myActivity = pAct;
    }

    public AnimationSurfaceView(Context context,AttributeSet attrs) {
        super(context,attrs);
    }


    public AnimationSurfaceView(Context context,AttributeSet attrs,int defStyleAttr) {
        super(context,attrs,defStyleAttr);
    }

    public void setParameter(List<Zukei> zukeis,String folderName, Activity pAct){
        this.zukeis = zukeis;
        this.folderName = folderName;
        myActivity = pAct;
    }
    public void initialise(){

        Canvas canvas = null;
        Paint bgPaint = new Paint();

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        // Background
        bgPaint.setStyle(Style.FILL);
        bgPaint.setColor(Color.LTGRAY);


//        canvas = surfaceHolder.lockCanvas();
//      canvas.drawRect(
//                0, 0,
//                screen_width, screen_height,
//                bgPaint);
//        surfaceHolder.unlockCanvasAndPost(canvas);
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void run() {

        Canvas canvas = null;
        Paint bgPaint = new Paint();

        // Background
        bgPaint.setStyle(Style.FILL);
        bgPaint.setColor(Color.LTGRAY);


        canvas = surfaceHolder.lockCanvas();
        canvas.drawRect(
                0, 0,
                screen_width, screen_height,
                bgPaint
        );

        if(zukeis != null) {
            Collections.sort(zukeis,
                    new Comparator<Zukei>() {
                        public int compare(Zukei z1, Zukei z2) {
                            if (z1.layer < z2.layer) {
                                return 1;
                            } else if (z1.layer == z2.layer) {
                                return 0;
                            } else {
                                return -1;
                            }
                        }
                    });
            for (int i = 0; i < zukeis.size(); i++) {
                Paint paint = new Paint();
                switch (zukeis.get(i).type) {
                    case "Figure":
                        paint.setColor(zukeis.get(i).color);
                        switch (zukeis.get(i).figuretype) {
                            case "Circle":
                                paint.setColor(zukeis.get(i).color);
                                canvas.drawOval(
                                        zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF,
                                        zukeis.get(i).y * SCREEN_SCALE,
                                        zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF + zukeis.get(i).width * SCREEN_SCALE,
                                        zukeis.get(i).y * SCREEN_SCALE + zukeis.get(i).height * SCREEN_SCALE,
                                        paint
                                );
                                break;
                            case "Rectangle":
                                paint.setColor(zukeis.get(i).color);
                                canvas.drawRect(
                                        zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF,
                                        zukeis.get(i).y * SCREEN_SCALE,
                                        zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF + zukeis.get(i).width * SCREEN_SCALE,
                                        zukeis.get(i).y * SCREEN_SCALE + zukeis.get(i).height * SCREEN_SCALE,
                                        paint);
                                break;
                            default:
                                break;
                        }
                        break;
                    case "TextArea":
                        paint.setColor(Color.argb(0, 0, 0, 0));
                        canvas.drawText(zukeis.get(i).String, zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF, zukeis.get(i).y * SCREEN_SCALE, paint);
                        break;
                    case "Image":

                        Bitmap bm = BitmapFactory.decodeFile(FILE_DIR_PATH + folderName + "/" + getFileName(zukeis.get(i).ImagePath));
                        bm = Bitmap.createScaledBitmap(bm, zukeis.get(i).width * (int)SCREEN_SCALE, zukeis.get(i).height* (int)SCREEN_SCALE, false);
                        canvas.drawBitmap(bm, zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF, zukeis.get(i).y * SCREEN_SCALE, paint);
                        break;

                    default:

                        break;
                    }
                }
            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }

    @Override
    public void surfaceChanged(
            SurfaceHolder holder,
            int format,
            int width,
            int height) {
        screen_width = width;
        screen_height = height;
    }


    public boolean onTouchEvent(MotionEvent event)
    {
        //X軸の取得
        float pointX = event.getX();
        //Y軸の取得
        float pointY = event.getY();

        actionStart(pointX,pointY);
        return true;
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        thread = null;
    }

    public String getFileName(String filename) {

        String tmp = filename;
        int index = tmp.lastIndexOf('/');
        String body, ext;
        if (index >= 0) { //拡張子がある場合
            body = tmp.substring(0, index);
            ext = tmp.substring(index + 1);
        } else { //拡張子がない場合
            body = tmp;
            ext = null;
        }
        String[] str = {body, ext};
        return str[1];

    }

    public void actionStart(float x,float y){
        for(int i = 0;i<zukeis.size();i++){
            switch (zukeis.get(i).type){
                case "Figure" :
                    //X条件
                    if(zukeis.get(i).x*SCREEN_SCALE-SCREEN_DEF < x &&
                            zukeis.get(i).x*SCREEN_SCALE-SCREEN_DEF+zukeis.get(i).width*SCREEN_SCALE > x){
                        //y条件
                        if(zukeis.get(i).y*SCREEN_SCALE < y &&
                                zukeis.get(i).y*SCREEN_SCALE+zukeis.get(i).height*SCREEN_SCALE>y){
                            if(zukeis.get(i).actName!=null){
                            Intent intent = pm.getLaunchIntentForPackage(zukeis.get(i).actName);
                                if(intent != null) {
                                    myActivity.startActivity(intent);
                                }
                            }
                        }
                    }
                    break;
                case "TextArea" :
                    break;
                case "Image" :

                    break;

                default:
                    break;
            }
        }

    }

}

