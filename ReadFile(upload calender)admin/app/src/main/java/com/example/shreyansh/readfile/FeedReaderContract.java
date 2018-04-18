package com.example.shreyansh.readfile;

import android.provider.BaseColumns;

/**
 * Created by Shreyansh on 2/13/2018.
 */

public class FeedReaderContract {
    //To prevent someone from accidentally instantiating the contract class,
    //make the constructor private.
    private FeedReaderContract(){}

    //Inner class that defines the table contents.
    public static class FeedEntry implements BaseColumns{
        public static final String TABLE_NAME="mycalendar";
        public static final String COLUMN_ONE="sno";
        public static final String COLUMN_TWO="date";
        public static final String COLUMN_THREE="activity";
    }
}
