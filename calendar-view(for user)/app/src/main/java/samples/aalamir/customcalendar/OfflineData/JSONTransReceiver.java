package samples.aalamir.customcalendar.OfflineData;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.net.ssl.HttpsURLConnection;

import samples.aalamir.customcalendar.MainActivity;
import samples.aalamir.customcalendar.OnlineData.OnlineDatabases;

import static android.content.ContentValues.TAG;

/**
 * Created by Shreyansh on 2/27/2018.
 */

public class JSONTransReceiver extends AsyncTask<String, Void, String>{

private static String tablename;

    @Override
    protected void onPreExecute(){

    }

    @Override
    protected void onPostExecute(String res){
        String jsonStr = res;
        OnlineDatabases onlineDatabases = OnlineDatabases.getInstance();
        Toast.makeText(onlineDatabases,"sending name "+tablename,Toast.LENGTH_SHORT).show();
        Log.d(TAG, "Response from url: " + jsonStr);
        if(jsonStr!=null){
            FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(onlineDatabases);
            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            String SQL_DELETE_ENTRIES =
                    "DROP TABLE IF EXISTS " + tablename;
            db.execSQL(SQL_DELETE_ENTRIES);

            String CREATE_TABLE_NEW_USER = "CREATE TABLE " + tablename + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_ONE + " VARCHAR(255)," +
                    FeedReaderContract.FeedEntry.COLUMN_TWO + " VARCHAR(255)," +
                    FeedReaderContract.FeedEntry.COLUMN_THREE + " VARCHAR(255))";
            db.execSQL(CREATE_TABLE_NEW_USER);



            db.delete(tablename,null,null);

            //create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            try{
                JSONArray rowData = new JSONArray(jsonStr);
                // looping through all rowData
                for(int i=0;i<rowData.length();i++){
                    JSONObject c = rowData.getJSONObject(i);

                    String sno = c.getString("sno");
                    String date = c.getString("date_");
                    String activity = c.getString("activity");

                    values.put(FeedReaderContract.FeedEntry.COLUMN_ONE,sno);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_TWO,date);
                    values.put(FeedReaderContract.FeedEntry.COLUMN_THREE,activity);

                    db.insert(tablename,null,values);
                }
            }catch (JSONException e){
                //Handle JSON Exception
            }
        }

        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.updateCalendarAgain();

    }


    @Override
    protected String doInBackground(String... data) {
        String json = data[0];
        tablename = json;
        HttpURLConnection conn;
        try{

            JSONObject postDataParams = new JSONObject();
            postDataParams.put("TableName",json);

            //create connection
            URL urlTORequest = new URL("https://vitccandroidclubcalendar.000webhostapp.com/downloadTable.php");
            conn = (HttpURLConnection)urlTORequest.openConnection();
            conn.setReadTimeout(15000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            DataOutputStream printOut;
            printOut = new DataOutputStream(conn.getOutputStream());
            printOut.writeBytes(URLEncoder.encode(json.toString(),"UTF-8"));
            printOut.flush();
            printOut.close();



            int responseCode = conn.getResponseCode();

            if(responseCode== HttpsURLConnection.HTTP_OK){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line = "";

                while((line = in.readLine())!= null){
                    sb.append(line);
                    break;
                }
                in.close();
                return sb.toString();
            }else {
                return new String("false : "+responseCode);
            }
        }catch (IOException e){
            e.printStackTrace();
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }
}
