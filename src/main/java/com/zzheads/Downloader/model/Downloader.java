package com.zzheads.Downloader.model;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by zzheads on 18.11.16.
 */

public class Downloader implements DownloaderInterface {

    private byte[] buffer;

    @Override
    public byte[] getFile(String pathToURL) throws IOException {
        URL connection = new URL(pathToURL);
        HttpURLConnection urlConnection;
        urlConnection = (HttpURLConnection) connection.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.connect();
        InputStream in = urlConnection.getInputStream();

        int buffSize = in.available();
        buffer = new byte[buffSize];
        in.read(buffer);

        in.close();

        return buffer;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }
}
