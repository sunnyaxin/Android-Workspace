package com.stydydemo.sunny.networkhttptest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    EditText mEditUrl;
    Button mBtnConnect;
    TextView mTxtShow;
    TextView txtProfileImage, txtAvatar, txtNick, txtUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String stringUrl = mEditUrl.getText().toString();
                if(checkNetworkConnection()){
                    new DownloadWebpageTask().execute(stringUrl);
                }else{
                    mTxtShow.setText("No network connection available");
                }
            }
        });
    }

    //1、Check the Network Connection
    public boolean checkNetworkConnection(){
        ConnectivityManager connMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }

    //2、perform network operations on a separate thread
    class DownloadWebpageTask extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... params) {
            try {
                return downloadUrl(params[0]);
            }catch (IOException e){
                return "Unable to retrive web page. URL may be invalid!";
            }
        }
        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            mTxtShow.setText(s);
            try{
                parseJsonObjec(s);
            }catch (JSONException e){

            }
        }
    }

    //3、connect and download data
    private  String downloadUrl(String myurl) throws IOException{
        InputStream is = null;

        try{
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("Network", "The response is: "+ response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;
        }
        // Makes sure that the InputStream is closed after the app is
        // finished using it.
        finally {
            if(is != null){
                is.close();
            }
        }
    }

    //4、Convert the InputStream to a String
    public String readIt(InputStream stream) throws IOException{
        Reader reader = null;
        int len = 500;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buff = new char[len];
        int readSize;
        StringBuffer sb = new StringBuffer();
        while( -1 != (readSize = reader.read(buff))){
            if(readSize > len){
                readSize = len;
            }
            sb.append(buff, 0, readSize);
        }
        return sb.toString();
    }

    //5、json parse
    public void parseJsonObjec(String jsonStr) throws JSONException{
        JSONObject jsonObject = new JSONObject(jsonStr);
        String profileimage = jsonObject.getString("profile-image");
        String avatar = jsonObject.getString("avatar");
        String nick = jsonObject.getString("nick");
        String username = jsonObject.getString("username");

        txtUserName.setText(username);
        txtNick.setText(nick);
        txtProfileImage.setText(profileimage);
        txtAvatar.setText(avatar);
    }

    public void initView(){
        mEditUrl = (EditText)findViewById(R.id.edittext_network);
        mBtnConnect = (Button)findViewById(R.id.btn_connect);
        mTxtShow = (TextView)findViewById(R.id.textview_show_network);
        txtAvatar = (TextView)findViewById(R.id.avatar);
        txtNick = (TextView)findViewById(R.id.nick);
        txtProfileImage = (TextView)findViewById(R.id.profile_image);
        txtUserName = (TextView)findViewById(R.id.username);
    }

}
