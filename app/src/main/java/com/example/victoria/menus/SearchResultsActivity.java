package com.example.victoria.menus;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by weiying on 2017/2/16.
 */

public class SearchResultsActivity extends Activity {
    private SQLiteDatabase db;

    private ArrayList<String> data = new ArrayList<String>();
    private ListView search_results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_results);

        MySQLHelper helper = new MySQLHelper(this, "Menu", null, 1);
        db = helper.getWritableDatabase();

        search_results = (ListView) findViewById(R.id.search_results_listview);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data);
        search_results.setAdapter(adapter);
        readMaterials();
    }

    private void readMaterials(){
        Intent intent = getIntent();
        ArrayList<String> selected_foods = intent.getStringArrayListExtra("selected_foods");
        for(int i = 0; i < selected_foods.size(); i ++){
            Cursor cursor = db.query(MySQLHelper.FOOD, null, "name = ?",  new String[]{selected_foods.get(i)}, null, null, null);
            if(cursor.getCount() != 1){
                Log.e("search", "more than one food with the same name!!");
            }
            if(cursor.moveToFirst()){
                do{
                    String materials = cursor.getString(cursor.getColumnIndex("materials"));
                    String[] materials_array = materials.split(",") ;
                    for(String x:materials_array){
                        if(data.contains(x)){
                            continue;
                        }else{
                            data.add(x);
                        }
                    }
                }while(cursor.moveToNext());
            }
            cursor.close();
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
