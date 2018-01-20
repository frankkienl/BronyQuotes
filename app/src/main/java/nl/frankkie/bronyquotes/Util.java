package nl.frankkie.bronyquotes;

import android.content.Context;
import android.os.AsyncTask;
import au.com.bytecode.opencsv.CSVReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by FrankkieNL on 17-11-13.
 */
public class Util {

    public static ArrayList<Pony> ponies = new ArrayList<Pony>();

    public static class MyInitPoniesTask extends AsyncTask<Void, Void, Void> {

        Context context;
        Runnable callback;

        public MyInitPoniesTask(Context context, Runnable callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //Get Ponies in Assets
            try {
                String[] list = context.getAssets().list("");
                for (final String s : list) {
                    if (s.equals("images") || s.equals("kioskmode") || s.equals("sounds") || s.equals("webkit")) {
                        continue;
                    }
                    Pony pony = new Pony(s);
                    //read sound files
                    InputStream inputStream = context.getAssets().open(s + "/pony.ini");
                    CSVReader reader = new CSVReader(new InputStreamReader(inputStream));
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (nextLine[0].equalsIgnoreCase("speak")) {
                            if (stringContainsIgnoreCase(nextLine[1], "Soundboard")) {
                                //yay
                                Sound sound = new Sound();
                                sound.text = nextLine[2];
                                sound.file = nextLine[3].substring(2, nextLine[3].indexOf(".mp3")) + ".mp3";
                                pony.sounds.add(sound);
                            }
                        }
                    }
                    //
                    ponies.add(pony);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            callback.run();
        }
    }

    public static class Pony {
        String name;
        ArrayList<Sound> sounds = new ArrayList<Sound>();

        public Pony(String s) {
            this.name = s;
        }
    }

    public static class Sound {
        String text;
        String file;
    }

    public static boolean stringContainsIgnoreCase(String s1, String s2) {
        return s1.toLowerCase().contains(s2.toLowerCase());
    }
}
