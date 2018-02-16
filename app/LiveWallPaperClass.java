package com.example.k014c1298.livewidgetmaker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.support.annotation.RequiresApi;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by k014c1287 on 2018/02/08.
 */
public class LiveWallPaperClass extends WallpaperService {

    private final Handler mHandler = new Handler();

    // 初期フォルダ
    private String				m_strInitialDir	= "/sdcard/Documents/output.json";
    public final float SCREEN_SCALE = 4.5F;
    public final int SCREEN_DEF = 1150;
    static final long FPS = 20;
    static final long FRAME_TIME = 1000 / FPS;
    static final int BALL_R = 30;

    SurfaceHolder surfaceHolder;
    Thread thread;
    int cx = BALL_R, cy = BALL_R;
    int screen_width, screen_height;
    public List<Zukei> zukeis;
    public String folderName;
    private Activity myActivity;


    @Override
    public Engine onCreateEngine() {
        // 描画用の自作Engineクラス
        return new LiveEngine();
    }

    // 描画を行うEngineクラス
    public class LiveEngine extends Engine {
        // 画像
        private Bitmap image;

        //---移動関連-------------------
        //x
        private int x = 0;
        //y
        private int y = 0;
        //幅
        private int width;
        //高さ
        private int height;

        // 描画用のRunnable
        private final Runnable drawRunnable = new Runnable() {
            public void run() {
                drawFrame();
            }
        };

        //表示状態
        private boolean visible;

        //====== コンストラクタ ===========//
        public LiveEngine() {
            Gson gson = new Gson();

            Type listType = new TypeToken<List<Zukei>>(){}.getType();

            try (JsonReader reader =
                         new JsonReader(new BufferedReader(new FileReader(m_strInitialDir)))) {
                //TODO JSONからZukeisリストへの変換
                zukeis = gson.fromJson(reader,listType);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        //====== 生成時に呼び出される ===========//
        @Override
        public void onCreate(SurfaceHolder surfaceHolder) {
            super.onCreate(surfaceHolder);
        }

        //====== 破棄時に呼び出される ===========//
        @Override
        public void onDestroy() {
            super.onDestroy();
            mHandler.removeCallbacks(drawRunnable);
        }

        //====== 表示状態変更時に呼び出される ===========//
        @Override
        public void onVisibilityChanged(boolean visible) {
            this.visible = visible;

            if (visible) {
                drawFrame();
            } else {
                mHandler.removeCallbacks(drawRunnable);
            }
        }

        //////////////サーフェイス ////////////////////
        //======生成時 ===========//
        @Override
        public void onSurfaceCreated(SurfaceHolder surfaceHolder) {
            super.onSurfaceCreated(surfaceHolder);
        }

        //======変更時　===========//
        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format, int width , int height) {
            super.onSurfaceChanged(holder, format, width, height);

            this.width = width;
            this.height = height;

            drawFrame();
        }

        //======破棄時　===========//
        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);

            visible = false;

            mHandler.removeCallbacks(drawRunnable);
        }

        //////////////////////////////////////////////////

        //======オフセット変更時　===========//
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels) {
            drawFrame();
        }

        //======描画　===========//
        private void drawFrame() {

            final SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = null;

            try {
                Paint bgPaint = new Paint();

                // Background
                bgPaint.setStyle(Paint.Style.FILL);
                bgPaint.setColor(Color.LTGRAY);


                canvas = holder.lockCanvas();
                canvas.drawRect(
                        0, 0,
                        screen_width, screen_height,
                        bgPaint
                );

                if (zukeis != null) {
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
                                Bitmap bm = BitmapFactory.decodeFile(zukeis.get(i).ImagePath);
                                bm = Bitmap.createScaledBitmap(bm, zukeis.get(i).width * (int)SCREEN_SCALE, zukeis.get(i).height* (int)SCREEN_SCALE, false);
                                canvas.drawBitmap(bm, zukeis.get(i).x * SCREEN_SCALE - SCREEN_DEF, zukeis.get(i).y * SCREEN_SCALE, paint);
                                break;
                            default:
                                break;
                        }
                    }
                }
            }catch(Exception ex){
                ex.printStackTrace();
            }finally {
                holder.unlockCanvasAndPost(canvas);
            }
            // 次の描画をセット
            mHandler.removeCallbacks(drawRunnable);

            if (visible) {
                mHandler.postDelayed(drawRunnable, 60);
            }
        }

        //======タッチイベント　===========//
        @Override
        public void onTouchEvent(MotionEvent event) {
            //X軸の取得
            float pointX = event.getX();
            //Y軸の取得
            float pointY = event.getY();

            actionStart(pointX,pointY);
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
                                    PackageManager pm = getPackageManager();
                                    try{
                                    Intent intent = pm.getLaunchIntentForPackage(zukeis.get(i).actName);
                                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }catch(Exception e){
                                        e.printStackTrace();
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
}
