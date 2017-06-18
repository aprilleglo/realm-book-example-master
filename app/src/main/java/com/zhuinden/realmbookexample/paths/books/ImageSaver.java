package com.zhuinden.realmbookexample.paths.books;

/**
 * Created by aprillebestglover on 6/17/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.*;


/**
 * Created by Ilya Gazman on 3/6/2016.
 */
public class ImageSaver {

    private String directoryName = "images";
    private String fileName = "image.png";
    private Context context;
    private boolean external;

    public  String errorString1;
    public  File BloisDataDir;

    public ImageSaver(Context context) {
        this.context = context;
    }

    public ImageSaver setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public ImageSaver setExternal(boolean external) {
        this.external = external;
        return this;
    }

    public ImageSaver setDirectoryName(String directoryName) {


        if (directoryName == "user") {
            if (isExternalStorageWritable()) {
                BloisDataDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), "userImages");
                if (!BloisDataDir.mkdirs()) {
                    Log.e("TravellerLog :: ", "Directory not created" + BloisDataDir.toString());
                }
                if (!BloisDataDir.exists()) {
                    errorString1 = BloisDataDir.toString();
                    Log.e("TravellerLog :: ", errorString1);
                } else {

                    Log.e("TravellerLog :: ", "it exists we are here userImages in imagerSaver");

                }

            }
        } else {
            if (isExternalStorageWritable()) {
                BloisDataDir = new File(directoryName);
                if (!BloisDataDir.mkdirs()) {
                    Log.e("TravellerLog :: ", "Directory not created");
                }
                if (!BloisDataDir.exists()) {
                    errorString1 = BloisDataDir.toString();
                    Log.e("TravellerLog :: ", errorString1);
                } else {

                    Log.e("TravellerLog :: ", "it exists we are here in soundStuff else imagerSaver" + errorString1);

                }


            }
        }
        this.directoryName = directoryName;
        return this;
    }

    public void save(Bitmap bitmapImage) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(createFile());
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @NonNull
    private File createFile() {
        File directory;
        if(external){
            directory = getAlbumStorageDir(directoryName);
        }
        else {
            directory = context.getDir(directoryName, Context.MODE_PRIVATE);
        }

        return new File(directory, fileName);
    }

    private File getAlbumStorageDir(String albumName) {
        File file = new File(albumName);
        if (file.exists()) {
            Log.e("ImageSaver", "Directory exists in getAlbumStorageDir " + file.toString());
        }
        if (!file.mkdirs()) {
            Log.e("ImageSaver", "Directory not created in get getAlbumStorageDir");
        }
        return file;
    }

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public Bitmap load() {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(createFile());
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}