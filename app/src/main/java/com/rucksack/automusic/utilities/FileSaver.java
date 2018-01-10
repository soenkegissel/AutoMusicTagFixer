package com.rucksack.automusic.utilities;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by franco on 3/10/17.
 */

public final class FileSaver {
    public static final String SLASH = "/";
    public static final String AUTO_MUSIC_TAG_FIXER_FOLDER_NAME = "Covers";
    public static final String EXTENSION = "jpg";
    public static final String DOT = ".";
    public static final String NULL_DATA = "null data";
    public static final String NO_EXTERNAL_STORAGE_WRITABLE = "no external storage writable";
    public static final String INPUT_OUTPUT_ERROR = "i/o error";
    private static final String GENERIC_NAME = "Extracted_Cover";
    private static String sNewFileName = "";


    /**
     *
     * @param data Image data
     * @param title The title of song if exist
     * @param artist The artist of song if exist
     * @param album The album of song if exist
     * @return string absolute path where image was saved or
     *                  any other string representing the error.
     * @throws IOException
     */
    public static String saveImageFile(byte[] data, String title, String artist, String album) throws IOException {

        //No data to write
        if(data == null)
            return NULL_DATA;

        //External storage es not writable
        if(!isExternalStorageWritable()){
            Log.e("error", "no external storage writable");
            return NO_EXTERNAL_STORAGE_WRITABLE;
        }


        //Retrieve folder app, and if doesn't exist create it before
        File pathToFile = getAlbumStorageDir(AUTO_MUSIC_TAG_FIXER_FOLDER_NAME);

        //File object representing the new image file
        File imageFileCreated = null;
        try {
            imageFileCreated = createFile(pathToFile, title, artist, album);
        }
        catch (ParseException e){
            e.printStackTrace();
            return INPUT_OUTPUT_ERROR;
        }

        //Stream to write
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imageFileCreated);
            fos.write(data);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
            sNewFileName = "";
            if(fos != null){
                fos.close();
                fos = null;
            }
            return INPUT_OUTPUT_ERROR;
        }
        sNewFileName = "";
        return imageFileCreated.getAbsolutePath();

    }

    /**
     * Generates filename, appending the
     * current data and time to avoid repeat
     * file names
     * @param pathToFile Absolute path where will be saved the image
     * @param title The title of song if exist
     * @param artist The artist of song if exist
     * @param album The album of song if exist
     * @return File representing the image
     */
    private static File createFile(File pathToFile, String title, String artist, String album) throws ParseException {
        //Get and format date
        Date date = new Date();
        DateFormat now = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String newFileName = null;
        //Name of image maybe title, artist, album or filename provided from pathToFile
        if(!title.equals("")) {
            newFileName = title + "_" + now.format(date);
        }
        else if(!artist.equals("")) {
            newFileName = artist + "_"  + now.format(date);
        }
        else if(!album.equals("")){
            newFileName = album + "_"  + now.format(date);
        }
        else {
           newFileName = pathToFile.getName().split(".")[0] + "_" + now.format(date);
        }

        return new File(pathToFile.getAbsolutePath() + SLASH + newFileName+ DOT + EXTENSION);
    }

    /**
     * Checks if external storage is available for read and write
     * */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     *Checks if external storage is available to at least read
     */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    /**
     *  Get the directory for the user's public pictures directory.
     * @param albumName Folder/subfolder name assigned to save covers as image files
     * @return File representing the absolute path where images
     *                  are going to be saved
     */
    private static File getAlbumStorageDir(String albumName) {

        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.d("info","folder already created");
        }
        return file;
    }
}
