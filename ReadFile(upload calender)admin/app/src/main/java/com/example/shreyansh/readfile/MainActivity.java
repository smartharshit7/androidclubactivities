package com.example.shreyansh.readfile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.example.shreyansh.readfile.FileUpload.FileMainActivity;
import com.example.shreyansh.readfile.FileUpload.FilePath;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import static com.example.shreyansh.readfile.FeedReaderContract.FeedEntry.TABLE_NAME;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    static MainActivity instance;
    FeedReaderDbHelper mDbHelper;
    DynamicFeedReaderDbHelper dynamicDbHelper;
    int number = 1;
    int start = 0;
    int flag = 0;
    int counter = -1;
    private static final int PICK_FILE_REQUEST = 1;
    private static final String TAG = FileMainActivity.class.getSimpleName();
    public static String path;
    private String selectedFilePath;
    ImageView ivAttachment;
    Button bUpload;
    Button cancelPreview;
    Button savePreview;
    TextView tv;
    public static ProgressDialog dialog;
    ArrayList<DataModel> tableNameList;
    private static TablesAdapter tablesAdapter;
    private RecyclerView recyclerView;

    public static MainActivity getInstance(){
        return instance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        instance = this;

        //RECYCLER VIEW
        recyclerView = findViewById(R.id.tables_recycler_view);
        tableNameList = new ArrayList<>();


        ivAttachment =  findViewById(R.id.ivAttachment);
        bUpload =  findViewById(R.id.b_upload);
        cancelPreview = findViewById(R.id.cancelpreview);
        savePreview = findViewById(R.id.savepreview);
        tv = findViewById(R.id.tv);
        tv.setMovementMethod(new ScrollingMovementMethod());
        ivAttachment.setOnClickListener(this);
        bUpload.setOnClickListener(this);

        path = this.getDatabasePath("MyAcademicCalendar.db").toString();

        mDbHelper = new FeedReaderDbHelper(MainActivity.this);
        //assetTODatabase();

        //VISIBILITY
        bUpload.setVisibility(View.GONE);
        tv.setVisibility(View.GONE);
        savePreview.setVisibility(View.GONE);
        cancelPreview.setVisibility(View.GONE);

        //Fill tablesNameList with the names of the table in the database
        myTableList();

        //SET ADAPTER
        tablesAdapter = new TablesAdapter(tableNameList);
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tablesAdapter);

       // composeJSONfromSQLite("scseacademiccalendar_winter1718v1_students");


    }

    private void myTableList(){
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                //Toast.makeText(this, "Table Name=> "+c.getString(0), Toast.LENGTH_LONG).show();
                if(!"android_metadata".equals(c.getString(0))) {
                    tableNameList.add(new DataModel(c.getString(0)));
                }
                c.moveToNext();
            }
        }

        c.close();
    }


    public void onClick(View v) {
        if(v== ivAttachment){

            //on attachment icon click
            showFileChooser();
        }
        if(v== bUpload){

            //on upload button Click
            if(selectedFilePath != null){
                dialog = ProgressDialog.show(MainActivity.this,"","Uploading File...",true);
                        //uploadFile(selectedFilePath);
                        uploadFile();

            }else{
                Toast.makeText(MainActivity.this,"Please choose a File First",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        //sets the select file to all types of files
        intent.setType("*/*");
        //allows to select data and return it
        intent.setAction(Intent.ACTION_GET_CONTENT);
        //starts new activity to select file and return data
        startActivityForResult(Intent.createChooser(intent,"Choose File to Upload.."),PICK_FILE_REQUEST);
    }

    private void uploadFile(){
        JSONTransmitter transmitter = new JSONTransmitter();
        //transmitter.execute(getResults().toString());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == PICK_FILE_REQUEST){
                if(data == null){
                    //no data present
                    return;
                }


                Uri selectedFileUri = data.getData();
                selectedFilePath = FilePath.getPath(this,selectedFileUri);
                Log.i(TAG,"Selected File Path:" + selectedFilePath);

                if(selectedFilePath != null && !selectedFilePath.equals("")){
                    File file = new File(selectedFilePath);
                    String fileName = file.getName();
                    String fileType = fileName.substring(fileName.lastIndexOf(".")+1);
                    if(fileType.equals("txt")){
                        externalStorageTODatabse(file);
                    }else {
                        Toast.makeText(this,"Unsupported Format",Toast.LENGTH_SHORT).show();
                    }
                   //tvFileName.setText(selectedFilePath);
                  //  Toast.makeText(MainActivity.this,fileType,Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this,"Cannot upload file to server",Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void externalStorageTODatabse(final File file){
        //Gets the data repository in write mode
       // SQLiteDatabase db = mDbHelper.getWritableDatabase();
       // db.delete(TABLE_NAME,null,null);


        String data = "";

        StringBuffer mbuffer = new StringBuffer();

        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            if (is != null) {
                try {
                    while ((data = reader.readLine()) != null) {
                        if (data.equals(""))
                            continue;
                        if (data.equals(number + ". ")) {
                            mbuffer.append("=====================================================\n");
                            number++;
                            start = 1;
                            flag = 1;
                        }

                        if(start==1)
                             mbuffer.append(data + "\n");

                    }
                    tv.setText(mbuffer);
                    //VISIBILITY
                    recyclerView.setVisibility(View.GONE);
                    tv.setVisibility(View.VISIBLE);
                    savePreview.setVisibility(View.VISIBLE);
                    cancelPreview.setVisibility(View.VISIBLE);

                    //If preview is not correct call cancel();
                    cancelPreview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //VISIBILITY
                            recyclerView.setVisibility(View.VISIBLE);
                            tv.setVisibility(View.GONE);
                            savePreview.setVisibility(View.GONE);
                            cancelPreview.setVisibility(View.GONE);
                            Toast.makeText(MainActivity.this,"Cancel this preview",Toast.LENGTH_SHORT).show();
                        }
                    });
                    //if preview is correct
                    savePreview.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            saveMyPreview(file);
                        }
                    });
                    number=1;
                    start = 0;
                    flag = 0;
                    counter = -1;
                    is.close();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (FileNotFoundException e){
            Toast.makeText(this,"FILE NOT FOUND",Toast.LENGTH_SHORT).show();
        }
    }

    private void saveMyPreview(File file){

        String fileName = file.getName();
        String tableName = fileName.substring(0,fileName.lastIndexOf("."));



        //Gets the data repository in write mode
      //  dynamicDbHelper = new DynamicFeedReaderDbHelper(MainActivity.this,file.getName());
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(SQL_DELETE_ENTRIES);

        String CREATE_TABLE_NEW_USER = "CREATE TABLE " + tableName + " (" +
                FeedReaderContract.FeedEntry.COLUMN_ONE + " VARCHAR(255)," +
                FeedReaderContract.FeedEntry.COLUMN_TWO + " VARCHAR(255)," +
                FeedReaderContract.FeedEntry.COLUMN_THREE + " VARCHAR(255))";
        db.execSQL(CREATE_TABLE_NEW_USER);


        Cursor cursor = db.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                db.delete(tableName,null,null);
            }
            cursor.close();
        }


        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        String data = "";

        StringBuffer mbuffer = new StringBuffer();

        try {
            InputStream is = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            if (is != null) {
                try {
                    while ((data = reader.readLine()) != null) {
                        if (data.equals(""))
                            continue;
                        if (data.equals(number + ". ")) {
                            //mbuffer.append("=====================================================\n");
                            number++;
                            start = 1;
                            flag = 1;
                        }
                        if (start == 1) {
                            if (flag == 1) {
                                counter = 0;
                            }
                            flag = 0;
                            if (counter == 0) {
                                values.put(FeedReaderContract.FeedEntry.COLUMN_ONE, data.toString());
                                // db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values1);
                                counter++;
                                continue;
                            }
                            if (counter == 1) {
                                values.put(FeedReaderContract.FeedEntry.COLUMN_TWO, data.toString());
                                //  db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values2);
                                counter++;
                                continue;
                            }
                            if (counter == 2) {
                                values.put(FeedReaderContract.FeedEntry.COLUMN_THREE, data.toString());
                                db.insert(tableName, null, values);
                            }
                            // mbuffer.append(data + "\n");

                        }
                    }
                    Toast.makeText(MainActivity.this,"preview Saved",Toast.LENGTH_LONG).show();

                    number = 1;
                    start = 0;
                    flag = 0;
                    counter = -1;
                    is.close();

                    //REFRESH LIST
                    tableNameList.clear();
                    myTableList();
                    //SET ADAPTER
                    tablesAdapter = new TablesAdapter(tableNameList);
                    RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(tablesAdapter);

                    //VISIBILITY
                    bUpload.setVisibility(View.GONE);
                    tv.setVisibility(View.GONE);
                    savePreview.setVisibility(View.GONE);
                    cancelPreview.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }

    }

    public void deleteTable(String tablename){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+tablename);
        //REFRESH LIST
        tableNameList.clear();
        myTableList();
        //SET ADAPTER
        tablesAdapter = new TablesAdapter(tableNameList);
        RecyclerView.LayoutManager  mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(tablesAdapter);
    }

    public void uploadTable(String tablename){

            Toast.makeText(MainActivity.this,"Uploading",Toast.LENGTH_SHORT).show();
            JSONTransmitter transmitter = new JSONTransmitter();
            transmitter.execute(getResults(tablename).toString());

    }

    public  void showPDialog(){
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setMessage("Uploading Data.Please wait.");
        dialog.setIndeterminate(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    public void dismissPDialog(){
        dialog.dismiss();
    }


    private void assetTODatabase(){

        //Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.delete(TABLE_NAME,null,null);

        //Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        String data = "";

        StringBuffer mbuffer = new StringBuffer();

        InputStream is = this.getResources().openRawResource(R.raw.scseacademiccalendar_winter1718v1_students);

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));

        if(is!=null){
            try{
                while((data=reader.readLine())!=null){
                    if(data.equals(""))
                        continue;
                    if(data.equals(number+". ")){
                        //mbuffer.append("=====================================================\n");
                        number++;
                        start = 1;
                        flag=1;
                    }
                    if(start==1) {
                        if(flag==1){
                            counter=0;
                        }
                        flag=0;
                        if(counter==0){
                            values.put(FeedReaderContract.FeedEntry.COLUMN_ONE,data.toString());
                            // db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values1);
                            counter++;
                            continue;
                        }
                        if(counter==1){
                            values.put(FeedReaderContract.FeedEntry.COLUMN_TWO,data.toString());
                            //  db.insert(FeedReaderContract.FeedEntry.TABLE_NAME, null, values2);
                            counter++;
                            continue;
                        }
                        if(counter==2){
                            values.put(FeedReaderContract.FeedEntry.COLUMN_THREE,data.toString());
                            db.insert(TABLE_NAME, null, values);
                        }
                        // mbuffer.append(data + "\n");

                    }
                }

                number=1;
                start = 0;
                flag = 0;
                counter = -1;

                is.close();

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    private JSONArray getResults(String tablename)
    {

        String myPath = this.getDatabasePath("MyAcademicCalendar.db").toString();// Set path to your database

        String myTable = tablename;//Set name of your table

//or you can use `context.getDatabasePath("my_db_test.db")`

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);


        String searchQuery = "SELECT  * FROM " + myTable;
        Cursor cursor = myDataBase.rawQuery(searchQuery, null );

        JSONArray resultSet     = new JSONArray();

        JSONObject rObject = new JSONObject();

        try {
            rObject.put("TableName", tablename);
            resultSet.put(rObject);
        }catch (JSONException e){
            e.printStackTrace();
        }





        if(cursor.moveToFirst()){
            try {
                do {

                    JSONObject rowObject = new JSONObject();


                    rowObject.put(cursor.getColumnName(0), cursor.getString(0));
                    rowObject.put(cursor.getColumnName(1), cursor.getString(1));
                    rowObject.put(cursor.getColumnName(2), cursor.getString(2));
                    resultSet.put(rowObject);

                    Log.d("TAG_NAME", cursor.getString(0));
                    Log.d("TAG_NAME", cursor.getString(1));
                    Log.d("TAG_NAME", cursor.getString(2));
                } while (cursor.moveToNext());
            }catch( Exception e )
            {
                Log.d("TAG_NAME", e.getMessage()  );
            }
        }

/**
        int totalColumn = cursor.getColumnCount();


        do{
            JSONObject rowObject = new JSONObject();
            for( int i=0 ;  i< totalColumn ; i++ )
            {
                if( cursor.getColumnName(i) != null )
                {
                    try
                    {
                        if( cursor.getString(i) != null )
                        {
                           // Log.d("TAG_NAME", cursor.getString(i) );
                            rowObject.put(cursor.getColumnName(i) ,  cursor.getString(i) );
                        }
                        else
                        {
                            rowObject.put( cursor.getColumnName(i) ,  "" );
                        }
                    }
                    catch( Exception e )
                    {
                        Log.d("TAG_NAME", e.getMessage()  );
                    }
                }
            }
            resultSet.put(rowObject);
        }while (cursor.moveToNext());

*/

        cursor.close();
        Log.d("TAG_NAME", resultSet.toString() );
        return resultSet;
    }

    private String composeJSONfromSQLite(String tablename){
        ArrayList<HashMap<String, String>> offlineList;
        offlineList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM "+tablename;
        String myPath = this.getDatabasePath("MyAcademicCalendar.db").toString();// Set path to your database
        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        Cursor cursor = myDataBase.rawQuery(selectQuery, null );
        if(cursor.moveToFirst()){
            do{
                HashMap<String, String> map = new HashMap<String, String>();
                Log.d("TAG_NAME", cursor.getString(0) );
                Log.d("TAG_NAME", cursor.getString(1) );
                Log.d("TAG_NAME", cursor.getString(2) );
                map.put(cursor.getColumnName(0),cursor.getString(0));
                map.put(cursor.getColumnName(1),cursor.getString(1));
                map.put(cursor.getColumnName(2),cursor.getString(2));
                offlineList.add(map);
            }while(cursor.moveToNext());
        }
        HashMap<String,ArrayList<HashMap<String, String>>> offlineMap = new HashMap<String, ArrayList<HashMap<String,String>>>();
        offlineMap.put("offline", offlineList);
        myDataBase.close();
        Gson gson = new GsonBuilder().create();
        //Use gson to serialize Array List to JSON
        return gson.toJson(offlineMap);

    }




}
