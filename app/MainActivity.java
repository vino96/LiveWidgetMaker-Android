package com.example.k014c1298.livewidgetmaker;

import android.app.WallpaperManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;


//saveボタンで何処かに保存して、その後貼り付けまでする。
//graficsをpictureで保存はできるが設置したものはどうやって保存する？

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FileSelectionDialog.OnFileSelectListener{

    // 初期フォルダ
    private String				m_strInitialDir	= "/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.menu_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.addButton) {
            FileSelectionDialog dlg = new FileSelectionDialog( this, this );
            dlg.show( new File( m_strInitialDir ) );
            //取得したストリング型の文字列をjson型参照してオブジェクト変換

            //過去の俺の謎の遺産
            /*ObjectMapper mapper = new ObjectMapper();
            Object hoge = mapper.readValue(json, Hoge.class);
            */

            //テキストの内容によって、ifで分岐
            /*
            for(Object s: hoge){

            }

            if(="Figure"){

            }
            if(type="Image"){

            }
            if(type="TextArea"){

            }
            */

            return true;
        }
        if (id == R.id.saveButton) {
            //お試し部屋
            //surfaceViewの使用
            AnimationSurfaceView surfaceView = new AnimationSurfaceView(this);
            setContentView(surfaceView);

            //画像を表示
            /*
            Intent intent = new Intent(this, MyAndy.class);
            startActivityForResult(intent, 0);

            //GraphicViewを使用
            setContentView(new GraphicView(this));


            おそらくsetResource()に入れれば壁紙セットができそう
            どこに保存するかが問題ではある。
            WallpaperManager wpManager = WallpaperManager.getInstance(this);
            wpManager.setResource();
            サイズ変更
            Bitmap _bmp = BitmapFactory.decodeResource(getResources(),R.drawable.sample);
            Bitmap _newbmp = Bitmap.createScaledBitmap(_bmp, _setWidth, _setHeight, true);
            画面の大きさ取得、縦横反転？
            wpManager.getDesiredMinimumHeight();
            wpManager.getDesiredMinimumWidth();
            wpManager.setBitmap(_newbmp);
            */
        }
        return super.onOptionsItemSelected(item);
    }

    // ファイルが選択されたときに呼び出される関数

    public void onFileSelect( File file )
    {
        Toast.makeText( this, "File Selected : " + file.getPath(), Toast.LENGTH_SHORT ).show();
        m_strInitialDir = file.getParent();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_apps) {
            Intent intent = new Intent(this, AppsListActivity.class);
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_short) {
            Intent intent = new Intent(this, ShortActivity.class);
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_url) {
            Intent intent = new Intent(this, UrlActivity.class);
            startActivityForResult(intent, 0);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
