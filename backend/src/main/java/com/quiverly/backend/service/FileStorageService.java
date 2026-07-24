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
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.uploads:uploads}")
    private String uploadDir;

    public SurfboardImage storeSurfboardImage(MultipartFile file, boolean isCover) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Empty file (¬_¬)");
        }

        try {
            // get the subdirectory for surfboards and see where it is relative to root directory, normalize
            Path surfboardUploadPath = Path.of(uploadDir, "surfboards").toAbsolutePath().normalize();
            Files.createDirectories(surfboardUploadPath);//ensure that the surfboards directory exists

            // extract file extension and default to jpg
            String originalFilename = file.getOriginalFilename();
            String extension = ".jpg";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
            }

            // generate filename based on UUID
            String filename = UUID.randomUUID() + extension;
            Path destinationFile = surfboardUploadPath.resolve(filename).normalize();

            // copy input stream to destination path, replacing existing
            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            // read image into memory and get file width
            BufferedImage img = ImageIO.read(destinationFile.toFile());
            int width = img != null ? img.getWidth() : 0;
            int height = img != null ? img.getHeight() : 0;

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
}
