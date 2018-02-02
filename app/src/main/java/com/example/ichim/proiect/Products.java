package com.example.ichim.proiect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

class ViewHolder {
    TextView name;
    TextView category;
    TextView price;
}

class CustomProductAdapter extends BaseAdapter
{

    private static ArrayList<Product> products;
    private LayoutInflater productsInflater;

    public CustomProductAdapter(Context context, ArrayList<Product> results) {
        products = results;
        this.productsInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return products.size();
    }

    @Override
    public Object getItem(int position) {
        return products.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = productsInflater.inflate(R.layout.custom_product_list,null);
            holder = new ViewHolder();
            holder.name = (TextView)convertView.findViewById(R.id.productName);
            holder.category = (TextView)convertView.findViewById(R.id.productCategory);
            holder.price = (TextView)convertView.findViewById(R.id.productPrice);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.name.setText(products.get(position).getName());
        holder.category.setText(products.get(position).getCategory().toString());
        holder.price.setText(String.valueOf(products.get(position).getPrice()));
        return convertView;
    }
}

public class Products extends AppCompatActivity {

    ArrayList<Product> products=new ArrayList<Product>();
    ListView lv = null;
    CustomProductAdapter adapter;
    double value=0;
    int editedProductIndex;
    boolean hasContextMenu=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView)findViewById(R.id.product_list);

        Bundle extras = getIntent().getExtras();
        if(extras != null && extras.getBoolean("editable")==false) {
            products = extras.getParcelableArrayList("products");

            EditText title=(EditText)findViewById(R.id.listTitle);
            title.setText(extras.getString("title"));
            title.setEnabled(false);

            TextView valueT=(TextView)findViewById(R.id.value);
            valueT.setText(String.valueOf(extras.getDouble("value")));

            Button save=(Button)findViewById(R.id.save);
            save.setVisibility(View.GONE);
        }
        else {
            TextView view = new TextView(this);
            view.setText("~Add a product~");
            view.setGravity(Gravity.CENTER);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent newActivity = new Intent(Products.this, AddProduct.class);
                    startActivityForResult(newActivity, 0);
                }
            });
            lv.addFooterView(view, null, true);
            registerForContextMenu(lv);

            if(extras!=null && extras.getBoolean("editable")==true){
                products = extras.getParcelableArrayList("products");

                EditText title=(EditText)findViewById(R.id.listTitle);
                title.setText(extras.getString("title"));

                TextView valueT=(TextView)findViewById(R.id.value);
                valueT.setText(String.valueOf(extras.getDouble("value")));

                value=extras.getDouble("value");
            }
        }
        if(extras != null && extras.getBoolean("fromJSON")==true){
            hasContextMenu=false;

            ((EditText)findViewById(R.id.listTitle)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.valueLabel)).setVisibility(View.GONE);
            ((TextView)findViewById(R.id.value)).setVisibility(View.GONE);
            ((Button)findViewById(R.id.save)).setVisibility(View.GONE);

            AsyncProcessing asyncProcessing=new AsyncProcessing();
            asyncProcessing.execute("http://json-generator.appspot.com/api/json/get/cgbAqbcyIy?indent=2");
        }



        if(products!=null) {
            adapter = new CustomProductAdapter(this, products);
            lv.setAdapter(adapter);
        }

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!hasContextMenu){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("product", products.get(position));
                    setResult(Activity.RESULT_OK, returnIntent);

                    finish();
                }
            }
        });

    }

    public static ArrayList<Product> GetProductsFromJSON(String urlS){
        ArrayList<Product> productsList=new ArrayList<Product>();
        HttpURLConnection connection=null;
        try {
            URL urlC = new URL(urlS);
            connection = (HttpURLConnection) urlC.openConnection();
            InputStream inputStream = connection.getInputStream();

            JsonReader reader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            reader.beginArray();
            while (reader.hasNext()) {
                Product product=new Product();
                reader.beginObject();
                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if (name.equals("category")) {
                        product.setCategory(Category.valueOf(reader.nextString()));
                    } else if (name.equals("price")) {
                        product.setPrice(reader.nextInt());
                    } else if (name.equals("name")) {
                       product.setName(reader.nextString());
                    }
                }
                reader.endObject();
                productsList.add(product);
            }
        }catch (Exception ex){
            Log.d("Products",ex.getMessage());
        }
        return productsList;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch(item.getItemId()) {
            case R.id.delete_item:
                value-=products.get(info.position).getPrice();
                ((TextView)findViewById(R.id.value)).setText(String.valueOf(value));
                products.remove(info.position);
                adapter.notifyDataSetChanged();
                return true;
            case R.id.edit_item:
                Intent newActivity = new Intent(Products.this, AddProduct.class);
                editedProductIndex=info.position;
                newActivity.putExtra("name",products.get(info.position).getName());
                newActivity.putExtra("category",products.get(info.position).getCategory());
                newActivity.putExtra("price",products.get(info.position).getPrice());
                startActivityForResult(newActivity, 1);
                return true;
        }
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.product_list && hasContextMenu==true) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.context_menu, menu);
        }
    }

    public void Save(View view){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("title",((TextView)findViewById(R.id.listTitle)).getText().toString());
        returnIntent.putExtra("products", products);
        returnIntent.putExtra("value",value);
        setResult(Activity.RESULT_OK, returnIntent);

        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                Product product = data.getParcelableExtra("product");
                products.add(product);
                adapter.notifyDataSetChanged();
                value+=product.getPrice();
                ((TextView)findViewById(R.id.value)).setText(String.valueOf(value));
            }
        }

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Product product = data.getParcelableExtra("product");
                value=value-products.get(editedProductIndex).getPrice()+product.getPrice();
                ((TextView)findViewById(R.id.value)).setText(String.valueOf(value));
                products.get(editedProductIndex).setCategory(product.getCategory());
                products.get(editedProductIndex).setName(product.getName());
                products.get(editedProductIndex).setPrice(product.getPrice());
                adapter.notifyDataSetChanged();
            }
        }
    }

    class AsyncProcessing extends AsyncTask<String,Void,ArrayList<Product>> {

        @Override
        protected ArrayList<Product> doInBackground(String... params) {
            return Products.GetProductsFromJSON(params[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<Product> products) {
            Products.this.products=products;
            adapter = new CustomProductAdapter(Products.this, products);
            lv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

}
