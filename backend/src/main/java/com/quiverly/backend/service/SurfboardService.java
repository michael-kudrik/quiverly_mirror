package com.quiverly.backend.service;

import com.quiverly.backend.exception.SurfboardNotFoundException;
import com.quiverly.backend.model.Surfboard;
import com.quiverly.backend.model.SurfboardImage;
import com.quiverly.backend.repository.SurfboardRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class SurfboardService {
    private final SurfboardRepository surfboardRepository;
    private final FileStorageService fileStorageService;

    public SurfboardService(SurfboardRepository surfboardRepository, FileStorageService fileStorageService) {
        this.surfboardRepository = surfboardRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Surfboard> getSurfboards() {
        return surfboardRepository.findAll();
    }

    public List<Surfboard> getSurfboardsByUser(Long userId) {
        return surfboardRepository.findAllByOwnerId(userId);
    }

    public Surfboard addNewSurfboard(Surfboard surfboard) {
        if (surfboard.getOwner() == null) {
            throw new IllegalStateException("Surfboard must have an owner!");
        }
        return surfboardRepository.save(surfboard);
    }

    @Transactional
    public void deleteSurfboard(Long surfboardId) {
        Surfboard surfboard = getSurfboardOrThrow(surfboardId);

        List<String> imageUrls = surfboard.getImages().stream()
                .map(SurfboardImage::getUrl)
                .filter(url -> url != null && !url.isEmpty())
                .toList();

        surfboardRepository.delete(surfboard);

        deleteFilesAfterCommit(imageUrls);
    }

    @Transactional
    public void updateSurfboard(Long surfboardId, String model, String shaper, Double length, Double width, Double volume, String boardType, LocalDate purchasedAt) {
        Surfboard surfboard = getSurfboardOrThrow(surfboardId);

        if (model != null && !model.isEmpty()) surfboard.setModel(model);
        if (shaper != null && !shaper.isEmpty()) surfboard.setShaper(shaper);
        if (length != null) surfboard.setLength(length);
        if (width != null) surfboard.setWidth(width);
        if (volume != null) surfboard.setVolume(volume);
        if (boardType != null) surfboard.setBoardType(boardType);
        if (purchasedAt != null) surfboard.setPurchasedAt(purchasedAt);

        surfboardRepository.save(surfboard);
    }

    @Transactional
    public SurfboardImage addImageToSurfboard(Long surfboardId, MultipartFile file, boolean isCover, String username) {
        Surfboard surfboard = getSurfboardOrThrow(surfboardId);
        checkOwnership(surfboard, username);

        if (isCover) {
            clearCoverFlags(surfboard);
        } else if (surfboard.getImages().isEmpty()) {
            isCover = true;
        }

        SurfboardImage surfboardImage = fileStorageService.storeSurfboardImage(file, isCover);
        surfboard.addImage(surfboardImage);
        surfboardRepository.save(surfboard);

        return surfboardImage;
    }

    // Deletes images from surfboard.
    // If the deleted image was the cover, but another image remains after deletion, we make the remaining image the cover.
    // File only deleted after the transaction is completed
    @Transactional
    public void deleteImageFromSurfboard(Long surfboardId, Long imageId, String username) {
        Surfboard surfboard = getSurfboardOrThrow(surfboardId);
        checkOwnership(surfboard, username);
        SurfboardImage targetImage = getImageOrThrow(surfboard, imageId);

        boolean wasCover = targetImage.isCover();
        String fileUrl = targetImage.getUrl();

        surfboard.removeImage(targetImage);

        // conditions for promoting an image to cover
        if (wasCover && !surfboard.getImages().isEmpty()) {
            clearCoverFlags(surfboard);
            surfboard.getImages().getFirst().setCover(true);
        }

        surfboardRepository.save(surfboard);
        deleteFilesAfterCommit(List.of(fileUrl)); // wait for database change before physical file deletion
    }

    // sets image as cover image
    @Transactional
    public void setCoverImage(Long surfboardId, Long imageId, String username) {
        Surfboard surfboard = getSurfboardOrThrow(surfboardId);
        checkOwnership(surfboard, username);
        SurfboardImage targetImage = getImageOrThrow(surfboard, imageId);

        clearCoverFlags(surfboard);
        targetImage.setCover(true);
        surfboardRepository.save(surfboard);
    }

    // get surfboard by id or throw exception
    private Surfboard getSurfboardOrThrow(Long surfboardId) {
        return surfboardRepository.findById(surfboardId)
                .orElseThrow(() -> new SurfboardNotFoundException(surfboardId));
    }

    // make sure the username owns surfboard
    private void checkOwnership(Surfboard surfboard, String username) {
        if (!surfboard.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to modify this surfboard (¬_¬)");
        }
    }

    // find image belonging to given surfboard id, or throw error
    private SurfboardImage getImageOrThrow(Surfboard surfboard, Long imageId) {
        return surfboard.getImages().stream()
                .filter(img -> img.getId().equals(imageId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Image with id " + imageId + " not found on surfboard " + surfboard.getId()));
    }

    // clear cover flag on images and flushes
    // since we have a mariaDB unique_cover marker, it means that at most one image per board is a cover image
    // so without doing saveAndFlush we might get a race condition on updating the is_cover boolean
    private void clearCoverFlags(Surfboard surfboard) {
        for (SurfboardImage img : surfboard.getImages()) {
            img.setCover(false);
        }
        surfboardRepository.saveAndFlush(surfboard);
    }

    // deletes files after the current transaction completes, or just straight up delete it
    // trying to prevent orphaning 0_0
    private void deleteFilesAfterCommit(List<String> fileUrls) {
        if (fileUrls.isEmpty()) return;
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    for (String url : fileUrls) {
                        fileStorageService.deleteFile(url);
                    }
                }
            });
        } else {
            for (String url : fileUrls) {
                fileStorageService.deleteFile(url);
            }
        }
    }
}
