package com.example.teju.racing.utility;

import android.util.Log;

import com.example.teju.racing.data.RacingConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Teju on 2/21/2018.
 */

public class RacingUtility {

    public static void createRacingUserImageBaseDirectory() {
        File tempDir;
        tempDir = new File(RacingConstants.BASE_FILE_PATH);

        tempDir.mkdirs();

        if (tempDir.listFiles() != null) {
            for (File file : tempDir.listFiles()) {
                file.delete();
            }
        }
    }

    public static String downloadFile(String url, String uid) {
        URLConnection connection = null;
        String file_path = "";

        try {
            connection = new URL(url).openConnection();

            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            connection.connect();
            InputStream inputStream = connection.getInputStream();

            RacingUtility.createRacingUserImageBaseDirectory();

            file_path = RacingConstants.BASE_FILE_PATH + uid + ".jpg";
            Log.d(RacingUtility.class.getSimpleName()+": downloadFile", "FILE PATH:" + file_path);

            OutputStream outputStream = new FileOutputStream(new File(file_path));

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
            return file_path;
        }

        return file_path;
    }
}
