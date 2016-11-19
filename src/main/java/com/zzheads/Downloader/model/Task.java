package com.zzheads.Downloader.model;

/**
 * Created by zzheads on 19.11.16.
 */
public class Task {
    private String path;
    private String fileName;

    public Task(String path, String fileName) {
        this.path = path;
        this.fileName = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
