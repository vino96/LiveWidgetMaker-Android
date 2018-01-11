package com.example.k014c1298.livewidgetmaker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MyAndy extends AppCompatActivity {
//画像を貼る
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DrawView view = new DrawView(getApplication());
        setContentView(view);
    }


}

class DrawView extends View {
    private Bitmap img;

    public DrawView(Context context) {
        super(context);
        Resources res = this.getContext().getResources();
        //以下をimg = BitmapFactory.decodeResource(res, path);にする。
        img = BitmapFactory.decodeResource(res, R.drawable.okabe);
    }

    public void onDraw(Canvas c){
        c.drawColor(Color.BLACK);

        //Paint fill_paint = new Paint();
        //c.drawBitmap(img, 50, 50, fill_paint);

        int w = img.getWidth();
        int h = img.getHeight();
        Rect str = new Rect(0, 0, w, h);
        //以下をパスで集めたデータにする
        Rect dst = new Rect(0, 0, w/2, h/2);
        c.drawBitmap(img,str ,dst, null);

    }
}
