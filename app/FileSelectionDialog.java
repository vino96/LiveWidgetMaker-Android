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
import com.google.gson.Gson;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class FileSelectionDialog
        implements OnItemClickListener {
    private Context m_parent;                // 呼び出し元
    private OnFileSelectListener m_listener;            // 結果受取先
    private AlertDialog m_dlg;                    // ダイアログ
    private FileInfoArrayAdapter m_fileinfoarrayadapter; // ファイル情報配列アダプタ
    private final static String TAG = FileSelectionDialog.class.getSimpleName(); //デバッグ用タグ
    private String getpath;
    // ダウンロードする画像ファイルや作成したZIPファイルを保存しておくディレクトリ
//    final String FILE_DIR_PATH = Environment.getExternalStorageDirectory() + "/archive/";
    public final String FILE_DIR_PATH = "/sdcard/test/Documents/";

    // コンストラクタ
    public FileSelectionDialog(Context context,
                               OnFileSelectListener listener) {
        m_parent = context;
        m_listener = (OnFileSelectListener) listener;
    }

    // ダイアログの作成と表示
    public void show(File fileDirectory) {
        // タイトル
        String strTitle = fileDirectory.getAbsolutePath();
        // リストビュー
        ListView listview = new ListView(m_parent);
        listview.setScrollingCacheEnabled(false);
        listview.setOnItemClickListener(this);
        // ファイルリスト
        File[] aFile = fileDirectory.listFiles();
        List<FileInfo> listFileInfo = new ArrayList<FileInfo>();
        if (null != aFile) {
            for (File fileTemp : aFile) {
                listFileInfo.add(new FileInfo(fileTemp.getName(), fileTemp));
            }
            Collections.sort(listFileInfo);
        }
        // 親フォルダに戻るパスの追加
        if (null != fileDirectory.getParent()) {
            listFileInfo.add(0, new FileInfo("..", new File(fileDirectory.getParent())));
        }
        m_fileinfoarrayadapter = new FileInfoArrayAdapter(m_parent, listFileInfo);
        listview.setAdapter(m_fileinfoarrayadapter);

        Builder builder = new AlertDialog.Builder(m_parent);
        builder.setTitle(strTitle);
        builder.setPositiveButton("Cancel", null);
        builder.setView(listview);
        m_dlg = builder.show();
    }

    // ListView内の項目をクリックしたときの処理
    public void onItemClick(AdapterView<?> l,
                            View v,
                            int position,
                            long id) {
        if (null != m_dlg) {
            m_dlg.dismiss();
            m_dlg = null;
        }

        FileInfo fileinfo = m_fileinfoarrayadapter.getItem(position);

        if (true == fileinfo.getFile().isDirectory()) {
            show(fileinfo.getFile());
        } else {
            // ファイルが選ばれた：リスナーのハンドラを呼び出す
            List<Zukei> zukeis = new ArrayList<Zukei>();
            extract(fileinfo.getFile());
            String fileNameWithOutExtension = getFileName(fileinfo.getFile().getName());
            //textの読み出し開始
            try {
                File file = new File(FILE_DIR_PATH + fileNameWithOutExtension + "/text.txt");
                BufferedReader br = new BufferedReader(new FileReader(file));
                String str;
                int i = 0;
                Gson gson = new Gson();
                while ((str = br.readLine()) != null) {
                    str = str.replaceAll(",$","");
                    if(str.indexOf("type") != -1){
                        zukeis.add(gson.fromJson(str, Zukei.class));
                        i++;
                    }
                }
                br.close();
            } catch (FileNotFoundException e) {
                m_listener.debugMessage("NotFound");
                System.out.println(e);
            } catch (IOException e) {
                m_listener.debugMessage("IOE");
                System.out.println(e);
            } catch (Exception e){
                m_listener.debugMessage(e.getMessage() + "Exception");
            }
            //MainActivityにリストを引き渡す
            if(zukeis != null){m_listener.returnZukeiList(zukeis,fileNameWithOutExtension);}
        }
    }

    // 選択したファイルの情報を取り出すためのリスナーインターフェース
    public interface OnFileSelectListener {
        // ファイルが選択されたときに呼び出される関数
        //public void onFileSelect(File file);

        //引き渡されたstringをtoastで表示する
        public void debugMessage(String message);

        public void returnZukeiList(List<Zukei> zukeis,String fileName);
    }

    public void extract(File filename) {
        ZipInputStream in = null;
        BufferedOutputStream out = null;
        ZipEntry zipEntry = null;
        int len = 0;
        String fileNameWithOutExtension = getFileName(filename.getName());
        try {
            in = new ZipInputStream(new FileInputStream(filename.getPath()));
            // ZIPファイルに含まれるエントリに対して順にアクセス
            while ((zipEntry = in.getNextEntry()) != null) {
                String newFilePath = zipEntry.getName().replace("\\", "/");
                File newfile = new File(newFilePath);
                File dir = new File(FILE_DIR_PATH + fileNameWithOutExtension);
                    boolean t = dir.mkdirs();

                // 出力用ファイルストリームの生成
                out = new BufferedOutputStream(
                        new FileOutputStream(FILE_DIR_PATH + fileNameWithOutExtension + "/" + newfile.getName())
                );
                // エントリの内容を出力
                byte[] buffer = new byte[1024];
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }

                in.closeEntry();
                out.close();
                out = null;
                getpath = FILE_DIR_PATH + filename.getName();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            m_listener.debugMessage("NotFoundByExrtact");
        } catch (IOException e) {
            e.printStackTrace();
            m_listener.debugMessage("IOE");
        } catch (Exception e) {
            m_listener.debugMessage(e.getMessage()+" byExtract");
        }
    }

    public String getFileName(String filename) {
     //拡張子対応
        String tmp = filename;
        int index = tmp.lastIndexOf('.');
        String body, ext;
        if (index >= 0) { //拡張子がある場合
            body = tmp.substring(0, index);
            ext = tmp.substring(index + 1);
        } else { //拡張子がない場合
            body = tmp;
            ext = null;
        }
        String[] str = {body, ext};
        return str[0];

    }

}
