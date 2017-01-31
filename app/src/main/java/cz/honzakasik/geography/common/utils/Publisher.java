package cz.honzakasik.geography.common.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public final class Publisher {

    private Logger logger = LoggerFactory.getLogger(Publisher.class);

    private Context context;

    public Publisher(Context context) {
        this.context = context;
    }

    /**
     * Publishes all files from folder in assets specified in 'publish.source.path'.
     * Checking for external storage availability is performed!
     * @throws IOException
     */
    public void publishFilesFromAssets() throws IOException {
        FileHelper.checkExternalStorageReady();

        AssetManager assetManager = context.getAssets();
        String sourcePath = PropUtils.get("publish.source.path"); //relative to assets folder

        String[] fileNamesToPublish = assetManager.list(sourcePath);
        if (fileNamesToPublish.length == 0) { //there is nothing to publish
            logger.warn("No files to publish were found in '{}'.", sourcePath);
        } else {
            logger.info("Found {} assets to publish: {}", fileNamesToPublish.length, fileNamesToPublish);
            copyFileOrDir(sourcePath);
            logger.info("All files should be published.");
        }
    }

    /**
     * If given path is a file, it will just copy this file, if it is a dir it will copy it and its
     * content recursively.
     * @param path path to copy
     */
    private void copyFileOrDir(String path) {
        AssetManager assetManager = context.getAssets();
        try {
            String[] assets = assetManager.list(path);
            if (assets.length == 0) {
                copyFile(path);
            } else {
                String fullPath = FileHelper.getApplicationExternalStoragePath(context)
                        .concat(stripAssetSourceDirFromPath(path));
                File dir = new File(fullPath);
                if (!dir.exists()) {
                    if (!dir.mkdirs()) {
                        throw new IOException("Failed to create directory '" + dir.getAbsolutePath() + "'!");
                    }
                }
                for (String asset : assets) {
                    copyFileOrDir(path + File.separator + asset);
                }
            }
        } catch (IOException ex) {
            logger.error("I/O Exception", ex);
        }
    }

    private void copyFile(String relativePathToFile) {
        logger.info("Trying to copy '{}' file.", relativePathToFile);
        AssetManager assetManager = context.getAssets();
        try {
            File targetPath = new File(FileHelper.getApplicationExternalStoragePath(context)
                    .concat(stripAssetSourceDirFromPath(relativePathToFile)));
            if (targetPath.exists()) {
               logger.info("File '{}' already exists!", targetPath.getAbsolutePath());
            } else {
                logger.info("Copying to '{}'.", targetPath);
                FileUtils.copyInputStreamToFile(assetManager.open(relativePathToFile), targetPath);
            }
        } catch (IOException e) {
            logger.error("Error during copying file!", e);
        }

    }

    private String stripAssetSourceDirFromPath(String path) {
        String[] nodes = path.split(File.separator);
        StringBuilder sb = new StringBuilder();
        for (String node : nodes) {
            if (!node.equals(PropUtils.get("publish.source.path"))) {
                sb.append(node).append(File.separator);
            }
        }
        return sb.toString();
    }
}
