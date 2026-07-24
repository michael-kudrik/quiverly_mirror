package com.quiverly.backend.service;

import com.quiverly.backend.exception.SurfboardNotFoundException;
import com.quiverly.backend.model.Surfboard;
import com.quiverly.backend.model.SurfboardImage;
import com.quiverly.backend.repository.SurfboardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
public class SurfboardService {
    private final SurfboardRepository surfboardRepository;
    private final FileStorageService fileStorageService;

    @Autowired
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

    public void deleteSurfboard(Long surfboardId) {
        if (!surfboardRepository.existsById(surfboardId)) {
            throw new SurfboardNotFoundException(surfboardId);
        }
        surfboardRepository.deleteById(surfboardId);
    }

    @Transactional
    public void updateSurfboard(Long surfboardId, String model, String shaper, Double length, Double width, Double volume, LocalDate purchasedAt) {
        // get existing surfboard by id from db
        Surfboard surfboard = surfboardRepository.findById(surfboardId)
                .orElseThrow(() -> new SurfboardNotFoundException(surfboardId));
        // update each field if a value is given
        if (model != null && !model.isEmpty()) surfboard.setModel(model);
        if (shaper != null && !shaper.isEmpty()) surfboard.setShaper(shaper);
        if (length != null) surfboard.setLength(length);
        if (width != null) surfboard.setWidth(width);
        if (volume != null) surfboard.setVolume(volume);
        if (purchasedAt != null) surfboard.setPurchasedAt(purchasedAt);
    }

    @Transactional
    public SurfboardImage addImageToSurfboard(Long surfboardId, MultipartFile file, boolean isCover, String username) {
        //fetch board by id
        Surfboard surfboard = surfboardRepository.findById(surfboardId)
                .orElseThrow(() -> new SurfboardNotFoundException(surfboardId));

        // confirm that the current authed user matches owner of the board
        if (!surfboard.getOwner().getUsername().equals(username)) {
            throw new AccessDeniedException("You do not have permission to modify this surfboard (¬_¬)");
        }

        // if image is new, set as cover and remove cover tag from others
        if (isCover) {
            for (SurfboardImage img : surfboard.getImages()) {
                img.setCover(false);
            }
        } else if (surfboard.getImages().isEmpty()) {
            isCover = true;
        }

        //call fileStorageService for dimension extraction and whatnot
        SurfboardImage surfboardImage = fileStorageService.storeSurfboardImage(file, isCover);
        surfboard.addImage(surfboardImage); // associate the new image entity with the surfboard
        surfboardRepository.save(surfboard);

        return surfboardImage;
    }
}

