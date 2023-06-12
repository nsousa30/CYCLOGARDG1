package com.example.cyclogard;

import android.os.AsyncTask;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FTPUploader {
    private String server;
    private int port;
    private String username;
    private String password;

    public FTPUploader(String server, int port, String username, String password) {
        this.server = server;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public void uploadFolder(final File localFolderPath, final String remoteFolderPath) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                File[] files = localFolderPath.listFiles();

                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            String localFilePath = file.getAbsolutePath();
                            String remoteFilePath = file.getName();
                            uploadFile(localFilePath, remoteFilePath);
                        }
                    }
                }
            }
        });
    }

    private void uploadFile(String localFilePath, String remoteFilePath) {
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(server, port);
            ftpClient.login(username, password);


            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(localFilePath));
            ftpClient.storeFile(remoteFilePath, bis);
            bis.close();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
