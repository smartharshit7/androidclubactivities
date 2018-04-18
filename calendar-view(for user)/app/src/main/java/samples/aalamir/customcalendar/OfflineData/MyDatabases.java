package samples.aalamir.customcalendar.OfflineData;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;

import samples.aalamir.customcalendar.MainActivity;
import samples.aalamir.customcalendar.OnlineData.DataModel;
import samples.aalamir.customcalendar.R;

/**
 * Created by Shreyansh on 3/14/2018.
 */

public class MyDatabases extends ActionBarActivity {
    static MyDatabases instance;
    public static ProgressDialog dialog;
    ArrayList<DataModel> MyCalendarsList;
    public static OfflineTablesAdapter offlineTablesAdapter;
    public RecyclerView recyclerView;
    FeedReaderDbHelper mDbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_databases);

        instance = this;

        mDbHelper = new FeedReaderDbHelper(this);

        //RECYCLER VIEW
        recyclerView = (RecyclerView)findViewById(R.id.offline_tables_recycler_view);
        MyCalendarsList = new ArrayList<>();

        // FILL MyCalendarsList with the names of the table in the database.
        myTableList();

        //SET ADAPTER
        offlineTablesAdapter = new OfflineTablesAdapter(MyCalendarsList);
        RecyclerView.LayoutManager mLayoutmanager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutmanager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(offlineTablesAdapter);

    }

    public static MyDatabases getInstance(){return instance;}

    public void deleteTable(String tablename){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+tablename);
        //REFRESH LIST
        MyCalendarsList.clear();
        myTableList();
        //SET ADAPTER
        offlineTablesAdapter = new OfflineTablesAdapter(MyCalendarsList);
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(offlineTablesAdapter);

        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.updateCalendarAgain();
    }

    private void myTableList(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                //Toast.makeText(this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                if(!"android_metadata".equals(c.getString(0))) {
                    MyCalendarsList.add(new DataModel(c.getString(0)));
                }
                c.moveToNext();
            }
        }

        c.close();
    }
}
