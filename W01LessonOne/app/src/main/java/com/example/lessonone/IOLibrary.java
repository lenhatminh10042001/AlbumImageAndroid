package com.example.lessonone;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class IOLibrary {
    public static String MakeFolder(Context context,String path){
        String root = context.getFilesDir().toString();
        File dir = new File(root+"/"+path);
        if (dir.exists())
            return "The folder already exists";
        if (dir.mkdirs())
            return "";
        else
            return "Cannot make new folder";
    }

    public static String MakeFolderWithImage(Context context,String path,ArrayList<MImage> images,ArrayList<Integer> selectedIndexes){
        String root = context.getFilesDir().toString();
        File dir = new File(root+"/"+path);
        if (dir.exists())
            return "The folder already exists";
        if (dir.mkdirs()){
            for(int i=0;i<selectedIndexes.size();i++){
                File file=new File(dir,images.get((int) selectedIndexes.get(i)).getName());
                try {
                    FileOutputStream fOut = new FileOutputStream(file);
                    images.get(i).getBitmap().compress(Bitmap.CompressFormat.PNG, 85, fOut);
                    fOut.flush();
                    fOut.close();
                }
                catch (Exception ex) {
                    return ex.toString();
                }
            }
            return "";
        }
        else
            return "Cannot make new folder";
    }

    public static String SaveBitmap(Context context,String path, Bitmap bitmap){
        String root = context.getFilesDir().toString();
        File dir = new File(root+"/"+path);
        if (!dir.exists())
            return "The folder does not exist";
        File file=new File(dir,Long.toString(new Date().getTime())+".jpg");
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        }
        catch (Exception ex) {
            return ex.toString();
        }
        return "";
    }

    public static ArrayList<File> ListFolders(Context context,String path){
        String root = context.getFilesDir().toString();
        File dir = new File(root);
        File[] items = dir.listFiles();
        ArrayList<File> folders = new ArrayList<File>();
        if (items==null)
            return folders;
        for (int i = 0; i < items.length; ++i)
            if (items[i].isDirectory())
               folders.add(items[i]);
        return folders;
    }

    public static ArrayList<File> ListFiles(Context context,String path){
        String root = context.getFilesDir().toString();
        File dir = new File(root,path);
        File[] items = dir.listFiles();
        ArrayList<File> files = new ArrayList<File>();
        if (items==null)
            return files;
        for (int i = 0; i < items.length; ++i)
            if (!items[i].isDirectory())
                files.add(items[i]);
        return files;
    }

    public static String RemoveFile(Context context,String path){
        String root = context.getFilesDir().toString();
        File file = new File(root,path);
        if (!file.exists())
            return "The file does not exist";
        if (file.delete())
            return "";
        else
            return "Delete failed";
    }

    public static String RemoveFolder(Context context, String path){
        String root = context.getFilesDir().toString();
        File currentFolder = new File(root,path);
        if (!currentFolder.exists())
            return "The folder does not exist";

        ArrayList<File> files =ListFiles(context,path);
        for (int i=0;i< files.size();i++)
            IOLibrary.RemoveFile(context,path+"/"+files.get(i).getName());
        ArrayList<File> folders = ListFolders(context,path);
        for (int i=0;i<folders.size();i++)
            IOLibrary.RemoveFolder(context,path+"/"+folders.get(i).getName());

        if (currentFolder.delete())
            return "";
        else
            return "Delete failed";
    }
}
