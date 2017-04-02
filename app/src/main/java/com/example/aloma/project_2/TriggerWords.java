package com.example.aloma.project_2;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * Created by Aloma on 11/20/2016.
 */
public class TriggerWords extends Activity{
    ArrayAdapter<String> adapter;
    EditText editText;
    ArrayList<String> itemList;
    SQLiteAdapter db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trigger_words);
        db = new SQLiteAdapter(this);
        String[] items = {""};
        if(db.isTableExists("words", true)) {
            if (db.countWords() > 0) {
                Log.e("counting words........", String.valueOf(db.countWords()));
                itemList = db.getWords();
            } else {
                itemList = new ArrayList<String>(Arrays.asList(items));
            }
        }
        else{
            itemList = new ArrayList<String>(Arrays.asList(items));
        }
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtview, itemList);
        ListView listV = (ListView) findViewById(R.id.list_words);
        listV.setAdapter(adapter);
        editText = (EditText) findViewById(R.id.word);
        Button btAdd = (Button) findViewById(R.id.add_trigger_word);
        btAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newItem = editText.getText().toString();
                // add new item to arraylist
                db.addWord(newItem);
                itemList.add(newItem);
                // notify listview of data changed
                adapter.notifyDataSetChanged();
            }
        });
    }
}