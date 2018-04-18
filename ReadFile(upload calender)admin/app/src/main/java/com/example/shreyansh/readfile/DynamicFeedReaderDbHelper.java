package com.example.shreyansh.readfile;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Shreyansh on 2/21/2018.
 */

public class DynamicFeedReaderDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "MyAcademicCalendar.db";
    private static String dynamicTableName ;

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + dynamicTableName + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_ONE + " VARCHAR(255)," +
                    FeedReaderContract.FeedEntry.COLUMN_TWO + " VARCHAR(255)," +
                    FeedReaderContract.FeedEntry.COLUMN_THREE + " VARCHAR(255))";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + dynamicTableName;

    public DynamicFeedReaderDbHelper(Context context,String tablename) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.dynamicTableName=tablename;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
