package com.nantmobile.dgoldin.demomuseumapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.sql.Timestamp;

public class Entity
{
    private Integer id;
    private String name;
    private String url_d;
    private String url_v;
    private Bitmap pic;
    private Timestamp date;

    public Entity(String name, String url_d, String url_v, String pic, Timestamp date )
    {
        this.name = name;
        this.url_d = url_d;
        this.url_v = url_v;
        this.pic = stringToBitmap(pic);
        this.date = date;
    }

    private Bitmap stringToBitmap(String pic)
    {
        byte [] encodeByte= Base64.decode(pic, Base64.DEFAULT);
        Bitmap bitmap=BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        return bitmap;
    }

    public Entity(Integer id, String name, String url_d, String url_v, String pic, Timestamp date )
    {
        this(name,url_d,url_v,pic,date);
        this.id=id;
    }


    public Integer getId()
    {
        return id;
    }

    public void setId(Integer id)
    {
        this.id = id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getUrl_d()
    {
        return url_d;
    }

    public void setUrl_d(String url_d)
    {
        this.url_d = url_d;
    }

    public String getUrl_v()
    {
        return url_v;
    }

    public void setUrl_v(String url_v)
    {
        this.url_v = url_v;
    }

    public Bitmap getPic()
    {
        return pic;
    }

    public void setPic(Bitmap pic)
    {
        this.pic = pic;
    }

    public Timestamp getDate()
    {
        return date;
    }

    public void setDate(Timestamp date)
    {
        this.date = date;
    }

}


