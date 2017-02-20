package com.example.victoria.menus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by weiying on 2017/2/16.
 */

public class ModifyFoodActivity extends Activity {
    private SQLiteDatabase db;

    private EditText food_category;
    private EditText food;
    private EditText materials;
    private Button confirm;

    private String selected_food_category;
    private String selected_food_materials;
    private String[] selected_food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modify_food);

        MySQLHelper helper = new MySQLHelper(this, "Menu", null, 1);
        db = helper.getWritableDatabase();

        ///////////////////Get old information///////////////////////////////////////////
        Intent intent = getIntent();
        ArrayList<String> selected_foods = intent.getStringArrayListExtra("food");
        if(selected_foods.size() != 1){
            Log.e("modify", "more than one or zero selected food!!!");
        }
        selected_food = new String[] {selected_foods.get(0)};
        Cursor cursor = db.query(MySQLHelper.FOOD, null, "name=?", selected_food, null, null, null);
        if(cursor.moveToFirst()){
            selected_food_category = cursor.getString(cursor.getColumnIndex("parent"));
            selected_food_materials = cursor.getString(cursor.getColumnIndex("materials"));
        }else{
            Toast.makeText(this, "没有任何选中的", Toast.LENGTH_SHORT).show();
        }
        /******************************************************************************/

        food_category = (EditText) findViewById(R.id.modify_food_category_edittext);
        food = (EditText) findViewById(R.id.modify_food_edittext);
        materials = (EditText) findViewById(R.id.modify_food_material_edittext);
        food_category.setHint(selected_food_category);
        food.setHint(selected_food[0]);
        materials.setHint(selected_food_materials);

        confirm = (Button) findViewById(R.id.modify_food_confirm_button);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String new_name = food.getText().toString();
                String new_category =  food_category.getText().toString();
                String new_materials = materials.getText().toString();
                modify(selected_food[0], new_name, selected_food_category, new_category, selected_food_materials, new_materials);
                finish();
            }
        });
    }
    private void modify(String old_name, String new_name, String old_category, String new_category, String old_materials, String new_materials){

        /*if(new_name == "" || new_name == null){
            new_name = old_name;
        }
        if(new_category == "" || new_category == null){
            new_category = old_category;
        }
        if(new_materials == "" || new_materials == null){
            new_materials = old_materials;
        }*/
        if(new_category.length() == 0){
            new_category = old_category;
        }
        if(new_name.length() == 0){
            new_name = old_name;
        }
        if(new_materials.length() == 0){
            new_materials = old_materials;
        }
        Log.e("modify", "new_name:"+new_name);
        Log.e("modify","new_category:"+new_category);
        Log.e("modify", "new_materials:"+new_materials);
        //Update record in FOOD
        ContentValues food_record = new ContentValues();
        food_record.put("name", new_name);
        food_record.put("parent", new_category);
        food_record.put("materials", new_materials);
        db.update(MySQLHelper.FOOD, food_record, "name=?", new String[] {old_name});
        //Update old_category in FOODCATEGORY
        Cursor old_category_cursor = db.query(MySQLHelper.FOODCATEGORY, null, "name=?", new String[] {old_category} , null, null, null);
        if(old_category_cursor.moveToFirst()){//Whether new_category exists in FOODCATEGORY
            //Update foods
            String foods = old_category_cursor.getString(old_category_cursor.getColumnIndex("foods"));
            String updated_foods = foods.replaceAll(old_name+",", ""); //Delete old food name of this category record
            ContentValues record = new ContentValues();
            record.put("foods", updated_foods);
            db.update(MySQLHelper.FOODCATEGORY, record, "name=?", new String[] {old_category});
        }else{
            Log.e("modify food", "no old category in FOODCATEGORY");
        }
        old_category_cursor.close();

        //Update new_category in FOODCATEGORY
        Cursor new_category_cursor = db.query(MySQLHelper.FOODCATEGORY, null, "name=?", new String[] {new_category} , null, null, null);
        if(new_category_cursor.moveToFirst()){//Whether new_category exists in FOODCATEGORY
            //Update foods
            String foods = new_category_cursor.getString(new_category_cursor.getColumnIndex("foods"));
            String updated_foods = foods + new_name + ",";
            ContentValues record = new ContentValues();
            record.put("foods", updated_foods);
            db.update(MySQLHelper.FOODCATEGORY, record, "name=?", new String[] {new_category});
        }else{//Insert new category to FOODCATEGORY
            ContentValues record = new ContentValues();
            record.put("name", new_category);
            record.put("foods", new_name + ",");
            db.insert(MySQLHelper.FOODCATEGORY, null, record);
        }
        new_category_cursor.close();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }
}
