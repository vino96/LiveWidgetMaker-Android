package com.example.k014c1298.livewidgetmaker;


import android.graphics.Color;

import java.io.Serializable;

/**
 * Created by k014c1298 on 2018/01/17.
 */

public class Zukei implements Serializable {
    public String type;
    public int x;
    public int y;
    public int width;
    public int height;
    public int layer;


    public String ImagePath;

    public String comment;

    public String figuretype;

    //アニメーション用パラメータ
    public String animename;

    public double animealpha;

    public int animespeed;

    public int animex,animey;

    public int animerotate;

    public int animewidth;

    public int animeheight;

    public String String;

    public boolean actiontrigger;

    public int color;
    public  Zukei(String type,int x,int y,int width,int height){
        this.type   = type ;
        this.x      = x ;
        this.y      = y ;
        this.width  = width ;
        this.height = height ;
    }


}
