package bob.sun.bender.theme;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import bob.sun.bender.utils.AppConstants;

/**
 * Created by bob.sun on 17/05/2017.
 */

public class ThemeManager {

    private static ThemeManager instance;
    private Context context;

    private ThemeManager(Context ctx) {
        context = ctx;
    }

    public static ThemeManager getInstance(Context context) {
        if (instance == null)
            instance = new ThemeManager(context);

        return instance;
    }

    private void copyThemesFromAssets() {
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
        String zipFile = folder + "themes.zip";
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
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}
