package com.booking.utils;

import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileUploadUtil {
    private static final String uploadDir = System.getProperty("user.dir") + "/src/main/uploads";
    private static final String[] allowedTypes = {"image/png", "image/jpeg", "image/jpg", "image/gif"};

    public static void saveFile(String path, String fileName, MultipartFile file) throws Exception {
        if (!isAllowedType(file.getContentType())) {
            throw new Exception("File type not allowed");
        }
        Path uploadPath = Paths.get(uploadDir + path);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        try (InputStream inputStream = file.getInputStream()) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, REPLACE_EXISTING);
        } catch (Exception e) {
            throw new Exception("Could not save file: " + fileName, e);
        }
    }

    public static void deleteFile(String fileName) throws Exception {
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(fileName);
        Files.deleteIfExists(filePath);
    }

    private static boolean isAllowedType(String contentType) {
        for (String type : allowedTypes) {
            if (type.equalsIgnoreCase(contentType)) {
                return true;
            }
        }
        return false;
    }
}
