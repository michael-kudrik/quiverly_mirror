package com.quiverly.backend.service;

import com.quiverly.backend.model.Surfboard;
import com.quiverly.backend.repository.SurfboardRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;


@Service
public class SurfboardService {
    private final SurfboardRepository surfboardRepository;

    @Autowired
    public SurfboardService(SurfboardRepository surfboardRepository){
        this.surfboardRepository = surfboardRepository;
    }
    public List<Surfboard> getSurfboards(){
        return surfboardRepository.findAll();
    }

    public void addNewSurfboard(Surfboard surfboard){
        if (surfboard.getOwner() == null){
            throw new IllegalStateException("Surfboard must have an owner!");
        }
        surfboardRepository.save(surfboard);
    }

    public void deleteSurfboard(Long surfboardId){
        if (!surfboardRepository.existsById(surfboardId)){
            throw new IllegalStateException("Surfboard with id " + surfboardId + " does not exist!");
        }
        surfboardRepository.deleteById(surfboardId);
    }

    @Transactional
    public void updateSurfboard(Long surfboardId, String model, String shaper, Double length, Double width, Double volume, LocalDate purchasedAt){
        Surfboard surfboard = surfboardRepository.findById(surfboardId).orElseThrow(()-> new IllegalStateException("Surfboard with id: " + surfboardId + " does not exist!"));

        if (model != null && !model.isEmpty()) surfboard.setModel(model);
        if (shaper != null && !shaper.isEmpty()) surfboard.setShaper(shaper);
        if (length != null)surfboard.setLength(length);
        if (width != null)surfboard.setWidth(width);
        if (purchasedAt != null) surfboard.setPurchasedAt(purchasedAt);



    }
}
