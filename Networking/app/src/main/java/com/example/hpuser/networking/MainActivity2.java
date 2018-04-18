package com.example.hpuser.networking;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity2 extends AppCompatActivity {

    TextView st;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        st = (TextView) findViewById(R.id.st);

        new MainActivity2.SendPostRequest().execute();
    }


    public class SendPostRequest extends AsyncTask<String, String, String> {

        String us = getIntent().getStringExtra("reg").toString();
        String pas = getIntent().getStringExtra("mac").toString();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            st.setText("Loading..");
        }

        @Override
        protected String doInBackground(String... strings) {
            try
            {
                URL url = new URL("https://android-club-project.herokuapp.com/upload_details?reg_no="+us+"&"+"mac="+pas);
                HttpURLConnection x = (HttpURLConnection) url.openConnection();
                x.connect();

                InputStream stream = x.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

                String line = "";
                StringBuffer buffer = new StringBuffer( );
                while((line=reader.readLine())!=null)
                {
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "Check Your Internet Connection";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            st.setText(s);
        }
    }

}
