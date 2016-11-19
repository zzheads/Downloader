package com.zzheads.Downloader.model;

import java.io.IOException;

/**
 * Created by zzheads on 18.11.16.
 */
public interface DownloaderInterface {
    byte[] getFile(String pathToURL) throws IOException;
}
