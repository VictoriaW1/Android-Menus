package com.example.victoria.menus;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by weiying on 2017/2/16.
 */

public class AddFoodActivity extends Activity {
    private SQLiteDatabase db;

    private EditText category_name_edittext;
    private EditText name_edittext;
    private EditText materials_edittext;
    private Button confirm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_food);

        MySQLHelper helper = new MySQLHelper(this, "Menu", null, 1);
        db = helper.getWritableDatabase();

        category_name_edittext = (EditText) findViewById(R.id.food_category2_edittext);
        name_edittext = (EditText) findViewById(R.id.food_edittext);
        materials_edittext = (EditText) findViewById(R.id.food_material_edittext);
        confirm = (Button) findViewById(R.id.add_food_confirm_button);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFood(category_name_edittext.getText().toString(), name_edittext.getText().toString(), materials_edittext.getText().toString());

            }
        });
    }
    private void addFood(final String category, final String name, String materials){

        String[] names = {name};
        Cursor food_cursor = db.query(MySQLHelper.FOOD, null, "name=?", names, null, null, null);
        if(food_cursor.moveToFirst()){//If food exists,
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("警告");
            dialog.setMessage("您添加的食物已经存在，如果想要修改，请返回主页面进行修改操作！");
            //dialog.setCancelable(true);
            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    finish();
                }
            });
            dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which){
                    category_name_edittext.setText("");
                    name_edittext.setText("");
                    materials_edittext.setText("");
                }
            });
            dialog.show();
        }else{
            //insert food to FOOD
            ContentValues food_record = new ContentValues();
            food_record.put("name", name);
            food_record.put("parent", category);
            food_record.put("materials", materials);
            db.insert(MySQLHelper.FOOD, null, food_record);
            Toast.makeText(this, "菜名添加成功", Toast.LENGTH_SHORT).show();

            /////////////////////////////Whether category already existed in FOODCATEGORY/////////////////////////////////////////////
            String[] category_names = {category};
            Cursor category_cursor = db.query(MySQLHelper.FOODCATEGORY,null, "name=?", category_names, null, null ,null);
            if(!category_cursor.moveToFirst()) { //There is no category in FOODCATEGORY, insert
                ContentValues record = new ContentValues();
                record.put("name", category);
                record.put("foods", name + ",");
                db.insert(MySQLHelper.FOODCATEGORY, null, record);
            }else{
                //Update foods of corresponding category
                String foods = category_cursor.getString(category_cursor.getColumnIndex("foods"));
                ContentValues category_record = new ContentValues();
                category_record.put("foods", foods  + name + ",");
                db.update(MySQLHelper.FOODCATEGORY, category_record, "name=?", category_names);
            }
            category_cursor.close();
            finish();
        }
        food_cursor.close();

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
