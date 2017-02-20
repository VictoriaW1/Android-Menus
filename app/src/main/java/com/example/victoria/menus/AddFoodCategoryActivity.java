package com.example.victoria.menus;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by weiying on 2017/2/16.
 */

public class AddFoodCategoryActivity extends Activity {
    private SQLiteDatabase db;
    private EditText category_name;
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food_category);
        MySQLHelper helper = new MySQLHelper(this, "Menu", null, 1);
        db = helper.getWritableDatabase();

        category_name = (EditText) findViewById(R.id.food_category_edittext);
        confirm = (Button) findViewById(R.id.add_food_category_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = category_name.getText().toString();
                addFoodCategory(name);
                finish();
            }
        });

    }
    private void addFoodCategory(String name){
        String[] names = {name};
        Cursor cursor = db.query("FOODCATEGORY", null, "name=?", names, null, null, null);
        if(cursor.moveToFirst()){
            String category = cursor.getString(cursor.getColumnIndex("name"));
            Log.e("add category", "category" + category);
            //Log.e("add category", "category" + name + "exists!");
            Toast.makeText(this, "菜品已经存在！", Toast.LENGTH_SHORT).show();
            category_name.setText("");
        }else{
            ContentValues record = new ContentValues();
            record.put("name", name);
            db.insert("FOODCATEGORY", null, record);
            Toast.makeText(this, "添加成功", Toast.LENGTH_SHORT).show();
            Log.e("add_category", "add " + name + "sucessfully!");
        }
        cursor.close();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
