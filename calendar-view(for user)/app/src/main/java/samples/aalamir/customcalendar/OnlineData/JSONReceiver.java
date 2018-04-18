package samples.aalamir.customcalendar.OnlineData;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Shreyansh on 2/14/2018.
 */

public class JSONReceiver extends AsyncTask<Void, Void, String> {
    //URL to get JSON
    private static final String requrl = "https://vitccandroidclubcalendar.000webhostapp.com/downloadJson.php";


    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        OnlineDatabases onlineDatabases = OnlineDatabases.getInstance();
        onlineDatabases.showPDialog();
    }

    @Override
    protected void onPostExecute(String response){

        OnlineDatabases onlineDatabases = OnlineDatabases.getInstance();
        onlineDatabases.dismissPDialog();
        if(response==null)
            Toast.makeText(onlineDatabases,"NO Data Received.",Toast.LENGTH_SHORT).show();
        else{
            onlineDatabases.onlineTableList.clear();
            try{
                JSONObject mainObject = new JSONObject(response);
                JSONArray uniArray = mainObject.getJSONArray("object_name");
                JSONObject uniObject;
                for(int i=0;i<uniArray.length();i++){
                    uniObject = uniArray.getJSONObject(i);
                    String tablename = uniObject.getString("Tables_in_id4729693_mycalendar");
                   onlineDatabases.onlineTableList.add(new DataModel(tablename));
                }
                onlineDatabases.addToList(onlineDatabases.onlineTableList);


            }catch (JSONException e){
                e.printStackTrace();
            }
        }
            //onlineDatabases.txtDisplay.setText(response);
    }


    @Override
    protected String doInBackground(Void... voids) {
       String response = null;
       try{
           URL url = new URL(requrl);
           HttpURLConnection conn = (HttpURLConnection)url.openConnection();
           conn.setConnectTimeout(1000);
           conn.setRequestMethod("POST");
           //read the response
           InputStream in = new BufferedInputStream(conn.getInputStream());
           response = convertStreamToString(in);
       }catch (MalformedURLException e){

       }catch (IOException e){

       }catch (Exception e) {

       }
        return response;


    }

    private String convertStreamToString(InputStream is){
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try{
            while((line = reader.readLine())!=null){
                sb.append(line).append('\n');
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally {
            try{
                is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return sb.toString();
    }




}
