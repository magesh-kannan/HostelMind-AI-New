package com.hostelmind.infrastructure.storage;

import com.hostelmind.domain.port.FileStoragePort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Slf4j
@Component
public class LocalOrCloudinaryStorageAdapter implements FileStoragePort {

    @Value("${app.storage.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String uploadFile(String folder, String filename, InputStream content, String contentType) {
        try {
            Path targetDir = Paths.get(uploadDir, folder);
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
            }

            String uniqueFilename = UUID.randomUUID() + "_" + filename;
            Path targetPath = targetDir.resolve(uniqueFilename);

            Files.copy(content, targetPath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Uploaded file locally to: {}", targetPath);

            return "/files/" + folder + "/" + uniqueFilename;
        } catch (Exception e) {
            log.error("Failed to store file: {}", filename, e);
            throw new RuntimeException("File storage failure", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        log.info("Request to delete file at URL: {}", fileUrl);
    }
}
