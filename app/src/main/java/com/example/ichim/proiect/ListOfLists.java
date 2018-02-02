package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

class ListViewHolder {
    TextView title;
    TextView value;
    RatingBar rating;
}

class CustomListOfListsAdapter extends BaseAdapter
{

    private static ArrayList<ProductList> lists;
    private LayoutInflater listsInflater;

    public CustomListOfListsAdapter(Context context, ArrayList<ProductList> results) {
        lists = results;
        this.listsInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return lists.size();
    }

    @Override
    public Object getItem(int position) {
        return lists.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ListViewHolder holder;
        if(convertView == null)
        {
            convertView = listsInflater.inflate(R.layout.custom_list_of_lists,null);
            holder = new ListViewHolder();
            holder.title = (TextView)convertView.findViewById(R.id.listTitle);
            holder.value = (TextView)convertView.findViewById(R.id.listValue);
            holder.rating = (RatingBar)convertView.findViewById(R.id.ratingBar);
            holder.rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    MainActivity.productLists.get(position).setRating(rating);
                }
            });
            convertView.setTag(holder);
        }
        else
            holder = (ListViewHolder)convertView.getTag();

        holder.title.setText(lists.get(position).getTitle());
        holder.value.setText(String.valueOf(lists.get(position).getValue()));
        holder.rating.setRating(lists.get(position).getRating());
        return convertView;
    }
}

public class ListOfLists extends AppCompatActivity {

    ListView lv = null;
    ArrayList<ProductList> lists;
    int editableListIndex;
    CustomListOfListsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_lists);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView)findViewById(R.id.listOfLists);
        Bundle extras = getIntent().getExtras();
        lists=extras.getParcelableArrayList("lists");
        adapter=new CustomListOfListsAdapter(this, lists);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent newActivity = new Intent(ListOfLists.this, Products.class);
                newActivity.putExtra("editable",false);
                newActivity.putExtra("fromJSON",false);
                newActivity.putParcelableArrayListExtra("products",lists.get(position).getList());
                newActivity.putExtra("title",lists.get(position).getTitle());
                newActivity.putExtra("value",lists.get(position).getValue());
                startActivity(newActivity);
            }
        });

        registerForContextMenu(lv);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete_item:
                lists.remove(info.position);
                adapter.notifyDataSetChanged();
                MainActivity.productLists.remove(info.position);
                return true;
            case R.id.edit_item:
                Intent newActivity = new Intent(ListOfLists.this, Products.class);
                newActivity.putExtra("editable",true);
                newActivity.putExtra("fromJSON",false);
                newActivity.putParcelableArrayListExtra("products",lists.get(info.position).getList());
                newActivity.putExtra("title",lists.get(info.position).getTitle());
                newActivity.putExtra("value",lists.get(info.position).getValue());
                editableListIndex=info.position;
                startActivityForResult(newActivity,0);
                return true;
            case R.id.choose_item:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("productList", lists.get(info.position));
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listOfLists) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                ProductList list=new ProductList();
                list.setTitle(data.getStringExtra("title"));
                ArrayList<Product> prod=data.getParcelableArrayListExtra("products");
                list.setList(prod);
                list.setValue(data.getDoubleExtra("value", 0));

                lists.get(editableListIndex).setTitle(list.getTitle());
                lists.get(editableListIndex).setList(list.getList());
                lists.get(editableListIndex).setValue(list.getValue());

                adapter.notifyDataSetChanged();

                MainActivity.productLists.get(editableListIndex).setTitle(list.getTitle());
                MainActivity.productLists.get(editableListIndex).setList(list.getList());
                MainActivity.productLists.get(editableListIndex).setValue(list.getValue());
            }
        }
    }
}
