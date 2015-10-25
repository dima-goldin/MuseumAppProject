package com.nantmobile.dgoldin.demomuseumapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class SQLUtils extends SQLiteOpenHelper
{
    public static final String DB_NAME = "data.db";
    public static final int DB_VERSION = 19;
    public static final String TABLE = "museum_art_links";

    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_URL_DESCRIPTION = "url_d";
    public static final String COL_URL_VIDEO = "url_v";
    public static final String COL_PIC = "pic";
    public static final String COL_DATE = "date";


    public SQLUtils(Context context)
    {
        this(context, null, null, 0);
    }


    public SQLUtils(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d("DB", "onCreate");

        String stmnt = "create table " + TABLE + "(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT,url_d TEXT, url_v TEXT, pic TEXT, date DATETIME DEFAULT CURRENT_TIMESTAMP );";

        db.execSQL(stmnt);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        Log.d("DB", "onUpgrade from " + oldVersion + "to " + newVersion);

        String stmnt = "DROP TABLE IF EXISTS " + TABLE;

        db.execSQL(stmnt);
        onCreate(db);



        //db.insertData("Baldassare Castiglione", "https://en.wikipedia.org/wiki/Baldassare_Castiglione", R.drawable.baldassare1);
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(COL_NAME, "Baldassare Castiglione");
//        contentValues.put(COL_URL_DESCRIPTION, "https://en.wikipedia.org/wiki/Portrait_of_Baldassare_Castiglione");
//        contentValues.put(COL_URL_VIDEO, "https://www.youtube.com/watch?v=SIERsiojL8E");
//        contentValues.put(COL_PIC, R.drawable.baldassare1);
//        db.insert(TABLE, null, contentValues);
//
//
//        contentValues.put(COL_NAME, "Mona Lisa");
//        contentValues.put(COL_URL_DESCRIPTION, "https://en.wikipedia.org/wiki/Mona_Lisa");
//        contentValues.put(COL_URL_VIDEO, "https://www.youtube.com/watch?v=IitbJszd1kM");
//        contentValues.put(COL_PIC,R.drawable.monalisa1);
//        db.insert(TABLE, null, contentValues);
//
//        contentValues.put(COL_NAME, "Anne, Loenardo Da Vinci");
//        contentValues.put(COL_URL_DESCRIPTION, "https://en.wikipedia.org/wiki/The_Virgin_and_Child_with_St._Anne_(Leonardo)");
//        contentValues.put(COL_URL_VIDEO, "https://www.youtube.com/watch?v=L4F2lgyi7FA");
//        contentValues.put(COL_PIC,R.drawable.anneleonardo);
//        db.insert(TABLE, null, contentValues);
    }

    public Long insertData(String name, String url_d, String url_v, String pic)
    {
        Log.d("DB", "onInsert");
        Entity entity = getEntityByName(name);

        if(entity != null)
        {
            deleteData(name);
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_NAME, name);
        contentValues.put(COL_URL_DESCRIPTION, url_d);
        contentValues.put(COL_URL_VIDEO, url_v);
        contentValues.put(COL_PIC,pic);
        Long id = db.insert(TABLE, null, contentValues);
        db.close();
        return id;

    }

    private void updateDate(String name)
    {
    }


    public int updateData(String name, String url)
    {
        Log.d("DB", "onUpdate");

        //placeHolder for later implementation
        return 0;
    }


    public void deleteData(String name)
    {
        Log.d("DB", "onDelete");

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE, COL_NAME + "=?", new String[]{name});
        db.close();
    }

    public ArrayList<Entity> getAllData()
    {
        Log.d("DB", "onGetAll");
        ArrayList<Entity> entities = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE + " ORDER BY datetime(" + COL_DATE + ") DESC ;", null);

        if(!cursor.isAfterLast())
            while (cursor.moveToNext())
            {
                try{
                    Entity entity = cursorToEntity(cursor);
                    entities.add(entity);
                }catch(Exception e){//this generic but you can control another types of exception
                    e.printStackTrace();
                    Log.d("Error: ", e.getMessage());
                }

            }

        cursor.close();
       // db.close();
        return entities;
    }

    private Entity cursorToEntity(Cursor cursor) throws ParseException
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss"); // TODO
        Date parsedDate = dateFormat.parse(cursor.getString(5));
        Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

        Entity entity = new Entity(cursor.getInt(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),timestamp);
        return entity;
    }


    public Entity getEntityByName(String name)
    {
        SQLiteDatabase db = getReadableDatabase();
        String stmnt = "SELECT * FROM " + TABLE + " WHERE " + COL_NAME +"=\""+name+"\";";
        Cursor cursor = db.rawQuery(stmnt, null);
        try
        {
            if(cursor.isAfterLast())
            {

                return null;
            }
            cursor.moveToFirst();
            Entity entity = cursorToEntity(cursor);
            return entity;
        } catch (ParseException e)
        {
            e.printStackTrace();
            return null;
        } finally
        {
            cursor.close();
            db.close();
        }
    }



}
