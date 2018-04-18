package com.example.shreyansh.readfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.shreyansh.readfile.FeedReaderContract.FeedEntry.TABLE_NAME;

/**
 * Created by Shreyansh on 2/14/2018.
 */

public class JSONTransmitter extends AsyncTask<String, JSONObject, Void> {
    String url = "https://vitccandroidclubcalendar.000webhostapp.com/uploadFile.php/";
    OutputStream os;
    Context context;

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.showPDialog();
    }

    @Override
    protected void onPostExecute(Void res){
        MainActivity mainActivity = MainActivity.getInstance();
        mainActivity.dismissPDialog();
    }


    @Override
    protected Void doInBackground(String... data) {
        String json = data[0];
       // HttpClient client = new DefaultHttpClient();
       // HttpConnectionParams.setConnectionTimeout(client.getParams(), 100000);
      //  JSONObject jsonResponse = null;
       // HttpPost post = new HttpPost(url);
       // try {
        //    StringEntity se = new StringEntity("json="+json.toString());
         //   Log.d("TAG_NAME_okk ", json.toString() );
         //   post.addHeader("content-type", "application/x-www-form-urlencoded");
         //   post.setEntity(se);

         //   HttpResponse response;
         //   response = client.execute(post);
         //   String resFromServer = org.apache.http.util.EntityUtils.toString(response.getEntity());

          //  jsonResponse=new JSONObject(resFromServer);
         //   Log.i("Response from server", jsonResponse.getString("msg"));
       // } catch (Exception e) { e.printStackTrace();}

        HttpURLConnection urlConnection;
        try{
            //create connection
            URL urlTORequest = new URL(url);
            urlConnection = (HttpURLConnection)urlTORequest.openConnection();
            urlConnection.setConnectTimeout(100000);

            //handle POST parameters
            if(json!=null){
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(json.getBytes().length);
                urlConnection.setRequestProperty("Content-Type",
                       "application/x-www-form-urlencoded");
                urlConnection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                urlConnection.setRequestProperty("X-Requested-With", "XMLHttpRequest");
               //open
                urlConnection.connect();
                //send the post out
               // PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                //out.print(json);
               // out.close();
                //setup send
                os = new BufferedOutputStream(urlConnection.getOutputStream());
                os.write(json.getBytes());
                //clean up
                os.flush();
            }
        }catch (MalformedURLException e){
            //handle invalid url
        }catch (IOException e){
            //handle I/O
        }

        return null;
    }






}
