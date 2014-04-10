package nl.frankkie.bronyquotes;

import android.app.ListActivity;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by FrankkieNL on 17-11-13.
 */
public class PonyActivity extends ListActivity {

    Util.Pony myPony;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initUI();

        String ponyName;
        ponyName = getIntent().getStringExtra("pony");
        if (ponyName == null || ponyName.equals("")) {
            Toast.makeText(this,"404 PonyName not found", Toast.LENGTH_LONG).show();
            finish();
        }

        //get Pony
        for (Util.Pony pony : Util.ponies){
            if (pony.name.equalsIgnoreCase(ponyName)){
                myPony = pony;
            }
        }
        if (myPony == null){
            //404
            Toast.makeText(this,"404 Pony not found", Toast.LENGTH_LONG).show();
            finish();
        }

        initPonySounds();
    }

    public void initPonySounds() {
        if (Util.ponies == null || Util.ponies.size() == 0) {
            //Load when needed
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
            return myPony.sounds.size();
        }

        @Override
        public Object getItem(int position) {
            return myPony.sounds.get(position);
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
            final Util.Sound sound = (Util.Sound) getItem(position);
            TextView text = (TextView) convertView.findViewById(R.id.list_item_text);
            text.setText(sound.text);
            ImageView image = (ImageView) convertView.findViewById(R.id.list_item_image);
            try {
                //https://xjaphx.wordpress.com/2011/10/02/store-and-use-files-in-assets/
                // get input stream
                InputStream ims = getAssets().open(myPony.name + "/pony.png");
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
                    playSound(sound);
                }
            });

            return convertView;
        }
    }

    public void playSound(Util.Sound s){
        final MediaPlayer mediaPlayer = new MediaPlayer();
        try {
            String uriString = "file:///android_assets/" + myPony.name + "/" + s.file;
            //mediaPlayer.setDataSource(this, Uri.parse(uriString));
            //http://stackoverflow.com/questions/7199610/android-mediaplayer-is-playing-the-wrong-mp3-file-in-assets-directory
            AssetFileDescriptor descriptor = getAssets().openFd(myPony.name + "/" + s.file);
            mediaPlayer.setDataSource(descriptor.getFileDescriptor(), descriptor.getStartOffset(), descriptor.getLength());
            descriptor.close();

            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mediaPlayer.release();
                    //mediaPlayer = null; //eh jwz xD
                }
            });
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
