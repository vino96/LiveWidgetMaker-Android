package com.example.k014c1298.livewidgetmaker;

//選択してzipを解凍してtextファイルをグラフィックスに流す。（グラフィックス起動まで）
//現在の機能では/sdcard/test/にzipをおいた場合に限る。とりあえず現状維持で


import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FileSelectionDialog
        implements OnItemClickListener
{
    private Context					m_parent;				// 呼び出し元
    private OnFileSelectListener	m_listener;			// 結果受取先
    private AlertDialog				m_dlg;					// ダイアログ
    private FileInfoArrayAdapter	m_fileinfoarrayadapter; // ファイル情報配列アダプタ
    // ダウンロードする画像ファイルや作成したZIPファイルを保存しておくディレクトリ
//    final String FILE_DIR_PATH = Environment.getExternalStorageDirectory() + "/archive/";
    public final String FILE_DIR_PATH = "/sdcard/test/";

    // コンストラクタ
    public FileSelectionDialog( Context context,
                                OnFileSelectListener listener )
    {
        m_parent = context;
        m_listener = (OnFileSelectListener)listener;
    }

    // ダイアログの作成と表示
    public void show( File fileDirectory )
    {
        // タイトル
        String strTitle = fileDirectory.getAbsolutePath();

        // リストビュー
        ListView listview = new ListView( m_parent );
        listview.setScrollingCacheEnabled( false );
        listview.setOnItemClickListener( this );
        // ファイルリスト
        File[] aFile = fileDirectory.listFiles();
        List<FileInfo> listFileInfo = new ArrayList<FileInfo>();
        if( null != aFile )
        {
            for( File fileTemp : aFile )
            {
                listFileInfo.add( new FileInfo( fileTemp.getName(), fileTemp ) );
            }
            Collections.sort( listFileInfo );
        }
        // 親フォルダに戻るパスの追加
        if( null != fileDirectory.getParent() )
        {
            listFileInfo.add( 0, new FileInfo( "..", new File( fileDirectory.getParent() ) ) );
        }
        m_fileinfoarrayadapter = new FileInfoArrayAdapter( m_parent, listFileInfo );
        listview.setAdapter( m_fileinfoarrayadapter );

        Builder builder = new AlertDialog.Builder( m_parent );
        builder.setTitle( strTitle );
        builder.setPositiveButton( "Cancel", null );
        builder.setView( listview );
        m_dlg = builder.show();
    }

    // ListView内の項目をクリックしたときの処理
    public void onItemClick(	AdapterView<?> l,
                                View v,
                                int position,
                                long id )
    {
        if( null != m_dlg )
        {
            m_dlg.dismiss();
            m_dlg = null;
        }

        FileInfo fileinfo = m_fileinfoarrayadapter.getItem( position );

        if( true == fileinfo.getFile().isDirectory() )
        {
            show( fileinfo.getFile() );
        }
        else
        {
            // ファイルが選ばれた：リスナーのハンドラを呼び出す
            m_listener.onFileSelect( fileinfo.getFile() );
            //解凍してgraficsで表示まで 未達成
            extract(fileinfo.getFile());

            //textの読み出し開始
            try{
                File file = new File(FILE_DIR_PATH+"ddd/text.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));

                String str;
                Gson gson = new Gson();
                while((str = br.readLine()) != null){

                    User user = gson.fromJson("{\"email\":\"bob@jmail.com\",\"fullname\":\"Bob\"}", User.class);
                    System.out.println("User: " + user.email + " / " + user.fullname);
                }

                br.close();
            }catch(FileNotFoundException e){
                System.out.println(e);
            }catch(IOException e){
                System.out.println(e);
            }

        }
    }

    // 選択したファイルの情報を取り出すためのリスナーインターフェース
    public interface OnFileSelectListener
    {
        // ファイルが選択されたときに呼び出される関数
        public void onFileSelect( File file );
    }

    public void extract(File filename) {
        ZipInputStream in = null;
        BufferedOutputStream out = null;

        ZipEntry zipEntry = null;
        int len = 0;

        try {
            in = new ZipInputStream(new FileInputStream(filename));

            // ZIPファイルに含まれるエントリに対して順にアクセス
            while ((zipEntry = in.getNextEntry()) != null) {
                String newFilePath = zipEntry.getName().replace("\\","/");
                File newfile = new File(newFilePath);
                File dir = new File(FILE_DIR_PATH + newfile.getParent());
                boolean t = dir.mkdirs();

                // 出力用ファイルストリームの生成
                out = new BufferedOutputStream(
                        new FileOutputStream(FILE_DIR_PATH + newfile.getName())
                );

                // エントリの内容を出力
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                in.closeEntry();
                out.close();
                out = null;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
