package bob.sun.bender.theme;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import bob.sun.bender.utils.AppConstants;
import bob.sun.bender.utils.UserDefaults;

/**
 * Created by bob.sun on 17/05/2017.
 */

public class ThemeManager {

    private static ThemeManager instance;
    private volatile Theme currentTheme = null;
    private Context context;
    private Gson gson;

    private ThemeManager(Context ctx) {
        context = ctx;
        gson = new Gson();
        copyThemesIfNeeded();
    }

    public static ThemeManager getInstance(Context context) {
        if (instance == null)
            instance = new ThemeManager(context);

        return instance;
    }

    private void copyThemesIfNeeded() {
        String themeDirInExt = Environment.getExternalStorageDirectory().getAbsolutePath();
        themeDirInExt += AppConstants.themeFolder;
        File themeDir = new File(themeDirInExt);
        if (themeDir.exists()) {
            return;
        }
        themeDir.mkdirs();

        AssetManager am = context.getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        boolean copied = false;
        try {
            inputStream = am.open("themes.zip");
            String fileName = themeDirInExt + "builtin.zip";
            outputStream = new FileOutputStream(fileName);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            copied = true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (copied) {
            unzip();
        }

    }


    private void unzip() { //Oh, naughty~
        String folder = Environment.getExternalStorageDirectory().getAbsolutePath() + AppConstants.themeFolder;
        String zipFile = folder + "builtin.zip";
        try  {
            FileInputStream fin = new FileInputStream(zipFile);
            ZipInputStream zin = new ZipInputStream(fin);
            ZipEntry ze = null;
            while ((ze = zin.getNextEntry()) != null) {
                if(ze.isDirectory()) {
                    File f = new File(folder + ze.getName());
                    if(!f.isDirectory()) {
                        f.mkdirs();
                    }
                } else {
                    FileOutputStream fout = new FileOutputStream(folder + ze.getName());
                    BufferedOutputStream bufout = new BufferedOutputStream(fout);
                    byte[] buffer = new byte[1024];
                    int read = 0;
                    while ((read = zin.read(buffer)) != -1) {
                        bufout.write(buffer, 0, read);
                    }
                    bufout.close();
                    zin.closeEntry();
                    fout.close();
                }

            }
            zin.close();
            new File(zipFile).delete();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public @NonNull Theme loadThemeNamed(@NonNull String name) {
        if (name != null && "Default".equalsIgnoreCase(name)) {
            return Theme.defaultTheme();
        }
        if (!validateByName(name)) {
            UserDefaults.getStaticInstance(context).setTheme("Default");
            return Theme.defaultTheme();
        }
        Theme ret = null;
        File folder = new File(Environment.getExternalStorageDirectory() + AppConstants.themeFolder + name);
        if (!folder.exists()) {
            currentTheme = Theme.defaultTheme();
            return currentTheme;
        }
        File config = new File(Environment.getExternalStorageDirectory() + AppConstants.themeFolder + name + "/config.json");
        if (!config.exists()) {
            currentTheme = Theme.defaultTheme();
            return currentTheme;
        }
        JsonParser parser = new JsonParser();
        JsonReader reader = null;
        JsonObject json;
        try {
            reader = new JsonReader(new FileReader(config));
            json = parser.parse(reader).getAsJsonObject();
            ret = gson.fromJson(json, Theme.class);
            ret.setName(name);
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (ret == null) {
            ret = Theme.defaultTheme();
        } else {
            UserDefaults.getStaticInstance(context).setTheme(name);
        }
        currentTheme = ret;
        return ret;
    }

    public @NonNull Theme loadCurrentTheme() {
        String name = UserDefaults.getStaticInstance(context).getTheme();
        Theme ret;
        ret = loadThemeNamed(name);
        if (ret == null) {
            ret = Theme.defaultTheme();
        }
        currentTheme = ret;
        return ret;
    }

    public @NonNull Theme getCurrentTheme() {
        if (currentTheme == null) {
            currentTheme = loadCurrentTheme();
        }
        return currentTheme;
    }

    public boolean validateByName(String name) {
        boolean ret = true;
        File theme = new File(Environment.getExternalStorageDirectory() + AppConstants.themeFolder + '/' + name);
        if (!theme.isDirectory()) {
            return false;
        }
        String[] files = theme.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return "config.json".equalsIgnoreCase(name);
            }
        });
        if (files.length == 0) {
            return false;
        }
        return ret;
    }

    public @NonNull ArrayList<String> getAllThemes() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(Theme.defaultTheme().getName());
        File folder = new File(Environment.getExternalStorageDirectory() + AppConstants.themeFolder);
        String[] themes = folder.list();
        for (String t : themes) {
            if (validateByName(t)) {
                ret.add(t);
            }
        }
        return ret;
    }

}
