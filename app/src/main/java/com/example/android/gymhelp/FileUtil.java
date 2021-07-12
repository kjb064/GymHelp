package com.example.android.gymhelp;

import java.io.File;

/**
 * Utilities for performing various file operations.
 */
public class FileUtil {

    /**
     * Attempts to delete the file at the given path.
     *
     * @param path the path to the file to delete
     * @return whether the image was successfully deleted
     */
    public static boolean deleteFile(String path) {
        if (path != null && !path.equals(Constants.NO_IMAGE_PROVIDED)) {
            File deleteFile = new File(path);
            return deleteFile.delete();
        }
        return false;
    }
}
