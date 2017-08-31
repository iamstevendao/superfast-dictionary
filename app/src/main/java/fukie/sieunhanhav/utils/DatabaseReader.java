package fukie.sieunhanhav.utils;

import android.content.Context;
import android.content.res.AssetManager;

import com.snappydb.DB;
import com.snappydb.DBFactory;
import com.snappydb.SnappydbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Fukie on 09/03/2016.
 */
public class DatabaseReader {
    private DB dbVoca;

    private DatabaseReader(Context context, String dbName, boolean isFirsttime) {
        try {
            this.dbVoca = DBFactory.open(context, dbName);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
        if(isFirsttime) {
            putJsonFile("vietanh.json", context);
            putJsonFile("anhviet.json", context);
            putJsonFile("tag.json", context);
            putJsonFile("idioms.json", context);
        }
    }

    private void putJsonFile(String filePath, Context context) {
        String line;
        String[] reString;
        String key;
        String value;
        try {
            AssetManager asset = context.getAssets();
            BufferedReader bf;
            bf = new BufferedReader(
                    new InputStreamReader(asset.open(filePath)));
            while ((line = bf.readLine()) != null) {
                if (line.length() == 1)
                    continue;
                reString = line.split(":", 2);
                if (reString.length > 1) {
                    key = reString[0].substring(1, reString[0].length() - 1);
                    if (reString[1].length() > 2) {
                        if(reString[1].charAt(reString[1].length() - 1) == ',')
                            value = reString[1].substring(2, reString[1].length() - 2);
                        else {
                            value = reString[1].substring(2, reString[1].length() - 1);
                        }
                    }
                    else
                        value = "";
                    set(key, value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DatabaseReader load(Context context, String dbName, boolean isFirsttime) {
        return new DatabaseReader(context, dbName, isFirsttime);
    }

    public void set(String key, String value) {
        try {
            dbVoca.put(key, value);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public String get(String key) {
        try {
             return dbVoca.get(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String[] findKeys(String key) {
        try {
            return dbVoca.findKeys(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean exists(String key) {
        try {
            return dbVoca.exists(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int countKeys(String key) {
        try {
            return dbVoca.countKeys(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void close() {
        try {
            dbVoca.close();
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }

    public void delete(String key){
        try {
            dbVoca.del(key);
        } catch (SnappydbException e) {
            e.printStackTrace();
        }
    }
}
