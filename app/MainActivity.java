package com.example.k014c1298.livewidgetmaker;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;


//saveボタンで何処かに保存して、その後貼り付けまでする。
//graficsをpictureで保存はできるが設置したものはどうやって保存する？

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FileSelectionDialog.OnFileSelectListener{

    // 初期フォルダ
    private String				m_strInitialDir	= "/sdcard/Documents";
    public DrawerLayout drawer;
    //Zukeiクラスのリスト表示対応用
    private ListView list;
    public List<Zukei> zukeis = new ArrayList<Zukei>();
    public String fileName;
    //アクショントリガー判別用
    //こちらで紐づけた場合は紐づけた要素のレイヤ―のデータに紐づけること
    //wipのためコメントアウト
    public List<Zukei> actZukeis = new ArrayList<Zukei>();
    public int reflectSetingsZukeiLayer = 0;

    //外部アクティビティへ処理を委託する際に必要なもの
    private final int APP_SELECT_CODE = 1001;
    private final int SHORTCUT_SELECT_CODE = 1002;
    private final int URL_SELECT_CODE = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
/*
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
*/


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
            return true;
        }
        if (id == R.id.saveButton) {
            //保存ボタン
            //TODO json型への吐き出し

            Gson gson = new Gson();

            debugMessage(m_strInitialDir+"output.json");

            try (JsonWriter writer =
                         new JsonWriter(new BufferedWriter(new FileWriter(m_strInitialDir+"/output.json")))) {
                // ZukeiオブジェクトリストからJSONへの変換
                gson.toJson(zukeis, ArrayList.class, writer);
            } catch (IOException ex) {
                debugMessage(ex.getMessage());
            }


            //お試し部屋
            //surfaceViewの使用

            
            AnimationSurfaceView surfaceView = new AnimationSurfaceView(this,zukeis,fileName,this);
            setContentView(surfaceView);

        }
        return super.onOptionsItemSelected(item);
    }

    // ファイルが選択されたときに呼び出される関数

    public void onFileSelect( File file )
    {
        Toast.makeText( this, "File Selected : " + file.getPath(), Toast.LENGTH_SHORT ).show();
        m_strInitialDir = file.getParent();
    }

    public void debugMessage( String message )
    {
        Toast.makeText( this, "message : " + message, Toast.LENGTH_SHORT).show();
    }

    public void returnZukeiList(List<Zukei> value,String folderName){
        actZukeis.clear();
        list = (ListView) findViewById(R.id.objectList);
        this.zukeis = value;
        this.fileName = folderName;

        //zukeisに引き渡したリストのうち、アクショントリガーを保持しているものを別リスト(actZukeis)に保持
        for(int i = 0;i<zukeis.size();i++){
            if(zukeis.get(i).actiontrigger){
                actZukeis.add(zukeis.get(i));
                debugMessage(zukeis.get(i).type);
            }
        }
        //引き継いだ図形リストをMainActivityに表示
        ArrayAdapter<Zukei> adapter = new ArrayAdapter<Zukei>(this, R.layout.activity_app, actZukeis) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.activity_app, null);
                }

                TextView appLabel = (TextView) convertView.findViewById(R.id.item_app_label);
                appLabel.setText(zukeis.get(position).type);

                TextView appName = (TextView) convertView.findViewById(R.id.item_app_name);
                appName.setText(zukeis.get(position).type);
                return convertView;

            }
        };
        addClickListener();
        if(adapter!=null) {
            list.setAdapter(adapter);
        }
    }


    private  void addClickListener(){
        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                //TODO : 割り当てショートカットの呼び出し
                // (今は左から出てるけど、アクティビティ化して呼び出したのちに、
                // 引き継いだ値からどのオブジェクトに割り当てるかを判別、
                // 戻り値にzukeisリストの当該オブジェクトに挿入、保持
                // 待ち受け設定後に割り当て)
                //問題はアプリ用のショートカットを現在のリストからアクティビティに変更するやり方がわからない
                debugMessage("タッチイベント発生、イベント発生オブジェクトレイヤー:"+actZukeis.get(position).layer);
                reflectSetingsZukeiLayer = actZukeis.get(position).layer;
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.openDrawer(Gravity.LEFT);
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_apps) {
            Intent intent = new Intent(this, AppsListActivity.class);
            startActivityForResult(intent, APP_SELECT_CODE);
        } else if (id == R.id.nav_short) {
            Intent intent = new Intent(this, ShortActivity.class);
            startActivityForResult(intent, SHORTCUT_SELECT_CODE);
        } else if (id == R.id.nav_url) {
            Intent intent = new Intent(this, UrlActivity.class);
            startActivityForResult(intent, URL_SELECT_CODE);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //遷移先からのコールバックメソッド
    public void onActivityResult( int requestCode, int resultCode, Intent intent )
    {
    // startActivityForResult()の際に指定した識別コードとの比較
        //アプリケーションをアクションとした場合
        if( requestCode == APP_SELECT_CODE ){
            // 返却結果ステータスとの比較
            if( resultCode == Activity.RESULT_OK ){
                // 返却されてきたintentから値を取り出す
                String appName = intent.getStringExtra("appNameToString");
                for(int i = 0;i<zukeis.size();i++){
                    if(zukeis.get(i).layer == reflectSetingsZukeiLayer){
                        zukeis.get(i).actName = appName;
                    }
                }
            }
        }
        //URLをアクションとした場合
        if( requestCode == URL_SELECT_CODE ){
            // 返却結果ステータスとの比較
            if( resultCode == Activity.RESULT_OK ){
                // 返却されてきたintentから値を取り出す
                String URL = intent.getStringExtra("URLToString");
                for(int i = 0;i<zukeis.size();i++){
                    if(zukeis.get(i).layer == reflectSetingsZukeiLayer){
                        zukeis.get(i).actName = URL;
                    }
                }
            }
        }
        //ショートカットをアクションとした場合
        if( requestCode == SHORTCUT_SELECT_CODE ){
            // 返却結果ステータスとの比較
            if( resultCode == Activity.RESULT_OK ){
                // 返却されてきたintentから値を取り出す
                String ShortCut = intent.getStringExtra("ShortCutToString");
                for(int i = 0;i<zukeis.size();i++){
                    if(zukeis.get(i).layer == reflectSetingsZukeiLayer){
                        zukeis.get(i).actName = ShortCut;
                    }
                }
            }
        }
    }
}
