package nl.frankkie.bronyquotes;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends ListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initPonies();
    }

    public void initPonies() {
        if (Util.ponies == null || Util.ponies.size() == 0) {
            Util.MyInitPoniesTask task = new Util.MyInitPoniesTask(this, new Runnable() {
                @Override
                public void run() {
                    initListView();
                }
            });
            task.execute();
        } else {
            initListView();
        }
    }

    public void initUI() {
        setContentView(R.layout.activity_main);
    }

    public void initListView() {
        ListView listView = getListView();
        listView.setAdapter(new MyArrayAdapter());
    }

    public class MyArrayAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return Util.ponies.size();
        }

        @Override
        public Object getItem(int position) {
            return Util.ponies.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            if (convertView == null) {
                convertView = (ViewGroup) inflater.inflate(R.layout.list_item, parent, false);
            }
            final Util.Pony pony = (Util.Pony) getItem(position);
            TextView text = (TextView) convertView.findViewById(R.id.list_item_text);
            text.setText(pony.name);
            ImageView image = (ImageView) convertView.findViewById(R.id.list_item_image);
            try {
                //https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/
                // get input stream
                InputStream ims = getAssets().open(pony.name + "/pony.png");
                // load image as Drawable
                Drawable d = Drawable.createFromStream(ims, null);
                // set image to ImageView
                image.setImageDrawable(d);
            } catch (IOException ex) {
                //ignore and show default
                image.setImageResource(R.drawable.ic_launcher);
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent();
                    i.setClass(MainActivity.this, PonyActivity.class);
                    i.putExtra("pony", pony.name);
                    startActivity(i);
                }
            });

            return convertView;
        }
    }
}
