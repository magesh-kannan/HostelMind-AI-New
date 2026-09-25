package com.hostelmind.domain.port;

import java.io.InputStream;

public interface FileStoragePort {
    String uploadFile(String folder, String filename, InputStream content, String contentType);
    void deleteFile(String fileUrl);
}
