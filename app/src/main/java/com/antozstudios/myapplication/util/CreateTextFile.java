package com.antozstudios.myapplication.util;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CreateTextFile {



    public CreateTextFile(Context context,String data,String dir,String fileName) {
        File directory = new File(context.getExternalFilesDir(null), dir);
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                return;
            }
        }
        File file = new File(directory, fileName+".txt");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream);
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException ignored) {
        }
    }
}
