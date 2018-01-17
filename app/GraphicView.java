package com.example.k014c1298.livewidgetmaker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class GraphicView extends View {
    public GraphicView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // 座標系がわかるような罫線を引く
        Paint paint = new Paint();
        paint.setColor(Color.argb(75, 255, 255, 255));
        paint.setStrokeWidth(1);
        for (int y = 0; y < 800; y = y + 10) {
            canvas.drawLine(0, y, 479, y, paint);
        }
        for (int x = 0; x < 480; x = x + 10) {
            canvas.drawLine(x, 0, x, 799, paint);
        }

        // 線を書く
        paint.setColor(Color.RED);
        // だんだん大きくしていく。
        for (int i = 1; i <= 10; i++) {
            paint.setStrokeWidth(i);
            canvas.drawLine(i * 20 - 10, 10, i * 20 + 10, 10, paint);
        }

        paint.setColor(Color.rgb(0, 255, 0));
        paint.setStrokeWidth(10);
        canvas.drawLine(10, 40, 300, 70, paint);
        paint.setStrokeWidth(10);
        paint.setAntiAlias(true);
        canvas.drawLine(10, 60, 300, 90, paint);

        // 円を書く
        paint.setAntiAlias(false);
        paint.setColor(Color.BLUE);
        canvas.drawCircle(90, 150, 40, paint);
        paint.setColor(Color.RED);
        canvas.drawCircle(90, 150, 20, paint);
        // 中心店の確認用の線
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(1);
        // Y座標は150が中心
        canvas.drawLine(40, 150, 140, 150, paint);
        // X座標は90が中心
        canvas.drawLine(90, 100, 90, 200, paint);

        // 矩形を書く
        paint.setColor(Color.RED);
        paint.setStrokeWidth(1);
        canvas.drawRect(10, 200, 50, 240, paint);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);
        canvas.drawRect(10, 250, 50, 290, paint);

        // 多角形を書く
        paint.setColor(Color.RED);
        Path path = new Path();
        path.moveTo(100, 300);
        path.lineTo(10, 350);
        path.lineTo(80, 330);
        canvas.drawPath(path, paint);

        // 文字を書く
        paint.setAntiAlias(false);
        paint.setColor(Color.rgb(255, 255, 0));
        paint.setTextSize(36);
        canvas.drawText("あいうえお", 10, 400, paint);
        paint.setAntiAlias(true);
        canvas.drawText("あいうえお", 10, 450, paint);

        // Pathに沿って文字を書く
        paint.setColor(Color.RED);
        path = new Path();
        path.moveTo(50, 500);
        path.lineTo(250, 500);
        path.lineTo(250, 600);
        path.lineTo(50, 600);
        canvas.drawTextOnPath("あいうえおかきくけこさしすせそ", path, 0, 0, paint);
        paint.setColor(Color.GREEN);
    }
}