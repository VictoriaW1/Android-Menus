package com.example.victoria.menus;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener{
    public static ArrayList<String> selected_food = new ArrayList<String>();
    public static ArrayList<String> selected_category = new ArrayList<String>();
    public static int count_of_selected;
    public static int getCount_of_selected_category;

    private SQLiteDatabase db;

    private Button add_food_category;
    private Button add_food;
    private Button refresh;
    private ListView food_category;
    private ListView food;
    private Button search;
    private Button delete;
    private Button modify;
    private TextView count;


    private ArrayList<String> food_category_data = new ArrayList<String>();
    private ArrayList<String> food_data = new ArrayList<String>();
    private FoodCategoryAdapter category_adapter;
    private FoodAdapter food_adapter;

    private static final int ADD_FOOD_CATEGORY_REQUEST_CODE = 1;
    private static final int ADD_FOOD_REQUEST_CODE = 2;
    private static final int MODIFY_REQUEST_CODE = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count_of_selected = 0;
        getCount_of_selected_category = 0;

        MySQLHelper helper = new MySQLHelper(this, "Menu", null, 1);
        db = helper.getWritableDatabase();

        /////////////////////ListView显示//////////////////////////////////////
        food = (ListView) findViewById(R.id.food_listview);
        food_adapter = new FoodAdapter(this, R.layout.embeded_listview_item, food_data);
        food.setAdapter(food_adapter);
        //Utility.setListViewHeightBasedOnChildren(food);
        readFood();

        food_category = (ListView) findViewById(R.id.food_category_listview);
        category_adapter  = new FoodCategoryAdapter(this, R.layout.item, food_category_data);
        food_category.setAdapter(category_adapter);
        food_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = food_category_data.get(position);
                food.setSelection(position);
            }
        });
        readFoodCategory();
        /*********************************************************************/

        ///////////////////////////TextView//////////////////////////////
        count = (TextView) findViewById(R.id.count_selected_textview);
        /***************************************************************/

        ////////////////////////////////////////按键////////////
        add_food_category = (Button) findViewById(R.id.add_food_category_button);
        add_food = (Button) findViewById(R.id.add_food_button);
        refresh = (Button) findViewById(R.id.refresh_button);
        search = (Button) findViewById(R.id.search_button);
        modify = (Button) findViewById(R.id.modify_button);
        delete = (Button) findViewById(R.id.delete_button);

        add_food_category.setBackgroundColor(Color.LTGRAY);
        add_food.setBackgroundColor(Color.LTGRAY);
        refresh.setBackgroundColor(Color.LTGRAY);
        search.setBackgroundColor(Color.LTGRAY);
        modify.setBackgroundColor(Color.LTGRAY);
        delete.setBackgroundColor(Color.LTGRAY);

        add_food_category.setOnClickListener(this);
        add_food.setOnClickListener(this);
        refresh.setOnClickListener(this);
        search.setOnClickListener(this);
        modify.setOnClickListener(this);
        delete.setOnClickListener(this);
        /***************************************************/
    }

    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.add_food_category_button:
                Intent intent1 = new Intent(MainActivity.this, AddFoodCategoryActivity.class);
                startActivityForResult(intent1, ADD_FOOD_CATEGORY_REQUEST_CODE);
                break;
            case R.id.add_food_button:
                Intent intent2 = new Intent(MainActivity.this, AddFoodActivity.class);
                startActivityForResult(intent2, ADD_FOOD_REQUEST_CODE);
                Log.e("add food", "after adding continuing...");
                break;
            case R.id.refresh_button:
                refresh();
                break;
            case R.id.search_button:
                if(selected_category.size() != 0){
                    Toast.makeText(this, "只能查询菜名", Toast.LENGTH_SHORT).show();
                }else {
                    if (selected_food.size() == 0) {
                        Log.e("search button", "no selected food");
                        Toast.makeText(this, "请先选择菜名", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent3 = new Intent(MainActivity.this, SearchResultsActivity.class);
                        intent3.putExtra("selected_foods", selected_food);
                        startActivity(intent3);
                        refresh();
                    }
                }
                break;
            case R.id.delete_button:
                delete();
                refresh();
                break;
            case R.id.modify_button:
                if(selected_category.size() != 0){
                    Toast.makeText(this, "只能修改菜名", Toast.LENGTH_SHORT).show();
                }else {
                    if (count_of_selected == 0) {
                        Toast.makeText(this, "请先选择", Toast.LENGTH_SHORT).show();
                    } else {
                        if (count_of_selected > 1) {
                            Toast.makeText(this, "只可以修改一个", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent4 = new Intent(MainActivity.this, ModifyFoodActivity.class);
                            intent4.putExtra("food", selected_food);
                            startActivityForResult(intent4, MODIFY_REQUEST_CODE);
                        }
                    }
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int request_code, int result_code, Intent data){
        switch (request_code){
            case ADD_FOOD_CATEGORY_REQUEST_CODE:
                Log.e("after add c", "refresh...");
                refresh();
                break;
            case ADD_FOOD_REQUEST_CODE:
                refresh();
                break;
            case MODIFY_REQUEST_CODE:
                refresh();
                break;
        }
    }
    @Override
    protected void onDestroy(){
        super.onDestroy();
        db.close();
    }

    private void readFoodCategory(){
        food_category_data.clear();
        String[] columns = {"name"};
        Cursor cursor = db.query(MySQLHelper.FOODCATEGORY, columns, null, null, null, null, null);
        if(cursor.moveToFirst()){
            do{
                String name = cursor.getString(cursor.getColumnIndex("name"));
                food_category_data.add(name);
            }while(cursor.moveToNext());
        }
        cursor.close();
    }
    private void readFood(){
        food_data.clear();
        for(String category:food_category_data){
            Cursor cursor = db.query(MySQLHelper.FOODCATEGORY, null, "name=?", new String[]{category}, null, null, null);
            if(cursor.getCount() != 1){
                Log.e("read from table", "more than one category with the same name:" + category);
            }else{
                cursor.moveToFirst();
                String foods = cursor.getString(cursor.getColumnIndex("foods"));
                food_data.add(foods);
            }
            cursor.close();
        }
    }

    /*
    After operation on tables, refresh display and reset selected data
     */
    private void refresh(){
        readFood();
        food_adapter.notifyDataSetChanged();
        food_adapter.refresh();
        count_of_selected = 0;
        selected_food.clear();
        count.setText("已选中" + count_of_selected + "个");

        readFoodCategory();
        category_adapter.notifyDataSetChanged();
        category_adapter.refresh();
        getCount_of_selected_category = 0;
        selected_category.clear();
    }

    /*
    Delete selected foods from table FOOD
     */
    private void delete() {

        if (selected_food.size() == 0 && selected_category.size() == 0) {
            Toast.makeText(this, "请先选择", Toast.LENGTH_SHORT).show();
        } else {
            //////////////////////////////////////////////////////////////////////////
            //when delete from Food, FoodCategory needed to be updated
            for (String food : selected_food) {
                Log.e("delete", food);
                String category_name;
                Cursor food_cursor = db.query(MySQLHelper.FOOD, null, "name=?", new String[]{food}, null, null, null);
                if (food_cursor.moveToFirst()) {
                    category_name = food_cursor.getString(food_cursor.getColumnIndex("parent"));
                    Cursor category_cursor = db.query(MySQLHelper.FOODCATEGORY, null, "name=?", new String[]{category_name}, null, null, null);
                    if (category_cursor.getCount() != 1) {
                        Log.e("delete", "more than one parent of food " + food);
                    }
                    if (category_cursor.moveToFirst()) {
                        String foods = category_cursor.getString(category_cursor.getColumnIndex("foods"));
                        String updated_foods = foods.replaceAll(food + ",", "");
                        ContentValues record = new ContentValues();
                        record.put("foods", updated_foods);
                        db.update(MySQLHelper.FOODCATEGORY, record, "name=?", new String[]{category_name});
                    } else {
                        Log.e("delete", "the selected food " + food + "' category doesn't exist in FOODCATEGORY!!!");
                    }
                } else {
                    Log.e("delete", "the selected food " + food + " doesn't exist in FOOD!!!");
                }
            }
            //delete records in FOOD
            for (String food : selected_food) {
                db.delete(MySQLHelper.FOOD, "name=?", new String[]{food});
            }
            /************************************************************************************************************/

            /////////////////////////////delete selected category/////////////////////////////////////////////////////////
            for(String category:selected_category){
                db.delete(MySQLHelper.FOODCATEGORY, "name=?", new String[]{category});
                db.delete(MySQLHelper.FOOD, "parent=?", new String[]{category});
            }
            /***********************************************************************************************************/


            Toast.makeText(this, "成功删除", Toast.LENGTH_LONG).show();
        }
    }

    class FoodAdapter extends ArrayAdapter<String> {
        private int resourceId;
        private ArrayList<String> data;
        private ListView embeded_list_view;

        public FoodAdapter(Context context, int textViewResourceId, ArrayList<String> objects){
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
            data = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final String embed_foods = getItem(position);//element in embeded_foods is like: "pizza,hamburger,"
            String[] foods_array;
            if(embed_foods != null) {
                foods_array = embed_foods.split(",");
            }else{
                foods_array = new String[]{};
            }
            ArrayList<String> foods_list = new ArrayList<String>();
            for(String food:foods_array){
                foods_list.add(food);
            }
            View view;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            }else{
                view = convertView;
            }
            embeded_list_view = (ListView) view.findViewById(R.id.embeded_listview);
            EmbededFoodAdapter embeded_adapter = new EmbededFoodAdapter(MainActivity.this, R.layout.item, foods_list);
            embeded_list_view.setAdapter(embeded_adapter);
            Utility.setListViewHeightBasedOnChildren(embeded_list_view);
            return view;
        }

        public void refresh(){
            for(int i=0; i<data.size();i++){
                getView(i, null, null);
            }
        }
    }



    class EmbededFoodAdapter extends ArrayAdapter<String> {
        private int resourceId;
        private ArrayList<String> data;
        private Button select_food_item;
        private TextView selected_food_textview;

        public EmbededFoodAdapter(Context context, int textViewResourceId, ArrayList<String> objects){
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
            data = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final String food = (String) getItem(position);
            Log.e("show", food);
            View view;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            }else{
                view = convertView;
            }

            selected_food_textview = (TextView) view.findViewById(R.id.food_item_textview);
            selected_food_textview.setText(food);

            select_food_item = (Button) view.findViewById(R.id.food_item_button);
            select_food_item.setText("+");
            select_food_item.setEnabled(true);
            select_food_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("select button", "click food" + food);
                    Button button = (Button) v;
                    button.setEnabled(false);
                    button.setText("已选择");
                    selected_food.add(food);
                    count_of_selected += 1;
                    count.setText("已选中" + count_of_selected + "个");
                }
            });
            return view;
        }
    }

    class FoodCategoryAdapter extends ArrayAdapter<String> {
        private int resourceId;
        private ArrayList<String> data;
        private Button select_category_item;
        private TextView selected_category_textview;

        public FoodCategoryAdapter(Context context, int textViewResourceId, ArrayList<String> objects){
            super(context, textViewResourceId, objects);
            resourceId = textViewResourceId;
            data = objects;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            final String category= (String) getItem(position);
            Log.e("show", category);
            View view;
            if(convertView == null){
                view = LayoutInflater.from(getContext()).inflate(resourceId, null);

            }else{
                view = convertView;
            }

            selected_category_textview = (TextView) view.findViewById(R.id.food_item_textview);
            selected_category_textview.setText(category);

            select_category_item = (Button) view.findViewById(R.id.food_item_button);
            select_category_item.setText("+");
            select_category_item.setEnabled(true);
            select_category_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("select button", "click food" + food);
                    Button button = (Button) v;
                    button.setEnabled(false);
                    button.setText("已选择");
                    selected_category.add(category);
                    getCount_of_selected_category += 1;
                    count.setText("已选中" + getCount_of_selected_category + "个");
                }
            });
            return view;
        }

        public void refresh(){
            for(int i=0; i<data.size();i++){
                getView(i, null, null);
            }
        }
    }
}
