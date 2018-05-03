package com.example.kp.botapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kp.botapp.Adapter.CustomAdapter;
import com.example.kp.botapp.Modeks.ChatModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String userQuery;
    boolean voiceBoom ;
    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    FloatingActionButton voiceBtn ;
    ListView listView;
    EditText editText;
    List<ChatModel> list_chat = new ArrayList<>();
    FloatingActionButton btn_send_message;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        List<ChatModel> models = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = findViewById(R.id.list_of_message);
        editText = findViewById(R.id.user_message);
        btn_send_message = findViewById(R.id.fab);
        voiceBtn = findViewById(R.id.voice);
        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                ChatModel model = new ChatModel(text,true, false); // user send message
                list_chat.add(model);
                voiceBoom = false;
             //   new RetrieveFeedback().execute(list_chat);
                  new RetrieveFeedback().execute(list_chat);
                //remove user message
                editText.setText("");
            }
        });

        voiceBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                voiceBoom = true;
                promptSpeechInput();
            }
        });

    }


    private void promptSpeechInput() {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Say Something");
        try {
            startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn\\'t support speech input",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case VOICE_RECOGNITION_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery=result.get(0);
                    ChatModel model = new ChatModel(userQuery,true, true); // user send message
                    list_chat.add(model);

                    new RetrieveFeedback().execute(list_chat);
                    //remove user message
                    editText.setText("lkjfdl;a");
                }
                break;
            }

        }
    }

    public String GetText(String query) throws UnsupportedEncodingException {

        String text = "";
        BufferedReader reader = null;

        // Send data
        try {

            // Defined URL  where to send data
            URL url = new URL("https://api.api.ai/v1/query?v=20150910");

            // Send POST data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer "+"8e47a88cb7d54c5d8ad3f9d7e1c7a243");
            conn.setRequestProperty("Content-Type", "application/json");

            //Create JSONObject here
            JSONObject jsonParam = new JSONObject();
            JSONArray queryArray = new JSONArray();
            queryArray.put(query);
            jsonParam.put("query", query);
//            jsonParam.put("name", "order a medium pizza");
            jsonParam.put("lang", "en");
            jsonParam.put("sessionId", "1234567890");


            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d("karma", "after conversion is " + jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d("karma", "json is " + jsonParam);

            // Get the server response

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;


            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line + "\n");
            }


            text = sb.toString();



            JSONObject object1 = new JSONObject(text);
            JSONObject object = object1.getJSONObject("result");
            JSONObject fulfillment = null;
            String speech = null;
//            if (object.has("fulfillment")) {
            fulfillment = object.getJSONObject("fulfillment");
         //   JSONObject another = fulfillment.getJSONArray("messages");
//                if (fulfillment.has("speech")) {
            speech = fulfillment.optString("speech");
            //String speech2 = .optString("")
//                }
//            }


            Log.d("karma ", "response is " + text);
            return speech;

        } catch (Exception ex) {
            Log.d("karma", "exception at last " + ex);
        } finally {
            try {

                reader.close();
            } catch (Exception ex) {
            }
        }

        return null;
    }



    public class RetrieveFeedback extends AsyncTask<List<ChatModel>, Boolean, String>{
        List<ChatModel> models;
        String s = null;



        @Override
        protected String doInBackground(List<ChatModel>[] lists) {

            models = lists[0];
            try {
                String userMessage;
                if(!models.get(0).isVoice()) {
                  userMessage =   editText.getText().toString();
                    s = GetText(userMessage);

                }
                else {
                    userMessage = userQuery;
                    s = GetText(userMessage);
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                Log.d("karma", "Exception occurred " + e);
            }

            return s;
        }

        @Override
        protected void onPostExecute(String s) {
          //  super.onPreExecute();
            if(s!=null) {
                ChatModel chatModel = new ChatModel(s, false, true); // get response from simsimi
                models.add(chatModel);
                CustomAdapter adapter = new CustomAdapter(models, getApplicationContext());
                listView.setAdapter(adapter);
            }
            else{
                ChatModel chatModel = new ChatModel("no response", false, true); // get response from simsimi
                models.add(chatModel);
                CustomAdapter adapter = new CustomAdapter(models, getApplicationContext());
                listView.setAdapter(adapter);
            }
        }
    }


}
