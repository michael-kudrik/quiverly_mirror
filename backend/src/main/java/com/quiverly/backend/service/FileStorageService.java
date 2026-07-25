package com.quiverly.backend.service;

import com.quiverly.backend.model.SurfboardImage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    // set the allowed types of images. I plan on using webp, but if I cant convert a mime type this will be fine
    private static final Set<String> ALLOWED_TYPES = Set.of("image/webp", "image/jpeg", "image/png", "application/octet-stream");

    @Value("${app.uploads:uploads}")
    private String uploadDir;

    public SurfboardImage storeSurfboardImage(MultipartFile file, boolean isCover) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file (¬_¬)");
        }

        // checks content type against the allowed types
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("Invalid file type. Only webp, jpeg, and png allowed.");
        }

        try {
            // get the subdirectory for surfboards and see where it is relative to root directory, normalize
            Path surfboardUploadPath = Path.of(uploadDir, "surfboards").toAbsolutePath().normalize();
            Files.createDirectories(surfboardUploadPath); // ensure that the surfboards directory exists

            // extract file extension
            String extension = getExtension(file.getOriginalFilename(), contentType);

            // generate filename based on UUID
            String filename = UUID.randomUUID() + extension;
            Path destinationFile = surfboardUploadPath.resolve(filename).normalize();

            // copy input stream to destination path, replacing existing
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // validate with ImageIO (turns out ImageIO does not support webp, so twelvemonkeys solves that also monke cool)
            BufferedImage img = ImageIO.read(destinationFile.toFile());
            if (img == null) {
                Files.deleteIfExists(destinationFile);
                throw new IllegalArgumentException("Only valid image uploads are allowed");
            }

            int width = img.getWidth();
            int height = img.getHeight();

            // populate surfboardImage with the image and specs
            SurfboardImage surfboardImage = new SurfboardImage();
            surfboardImage.setUrl("/uploads/surfboards/" + filename);
            surfboardImage.setImageWidth(width);
            surfboardImage.setImageHeight(height);
            surfboardImage.setCover(isCover);

            return surfboardImage;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file.", e);
        }
    }

    // whitelist extensions
    // this is what they mean by defense in depth :)
    private String getExtension(String originalFilename, String contentType) {
        if (originalFilename != null && originalFilename.contains(".")) {
            String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            if (Set.of(".jpg", ".jpeg", ".png", ".webp").contains(ext)) {
                return ext;
            }
        }
        return switch (contentType) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            default -> ".webp";
        };
    }
}
