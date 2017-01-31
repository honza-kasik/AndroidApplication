package cz.honzakasik.geography.common.utils;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;

public class FileHelper {

    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    public static String getApplicationExternalStoragePath(Context context) {
        return context.getExternalFilesDir(null) + File.separator;
    }

    /**
     * Checks if default external is both writable and readable.
     * @throws IOException
     */
    public static void checkExternalStorageReady() throws IOException {
        if (!(isExternalStorageWritable() && isExternalStorageReadable())) {
            throw new IOException("External storage is not ready!");
        }
    }
}