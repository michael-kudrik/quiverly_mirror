package com.quiverly.backend.controller;

import com.quiverly.backend.model.Surfboard;
import com.quiverly.backend.service.SurfboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "api/v1/surfboard")
public class SurfboardController {

    private final SurfboardService surfboardService;

    @Autowired
    public SurfboardController(SurfboardService surfboardService){
        this.surfboardService = surfboardService;
    }

    @GetMapping
    public List<Surfboard> getSurfboards(){
        return surfboardService.getSurfboards();
    }

    @PostMapping
    public void addSurfboard(@RequestBody Surfboard surfboard){
        surfboardService.addNewSurfboard(surfboard);
    }

    @DeleteMapping(path="{surfboardId}")
    public void deleteSurfboard(@PathVariable("surfboardId") Long surfboardId){
        surfboardService.deleteSurfboard(surfboardId);
    }

    @PutMapping(path="{surfboardId}")
    public void updateSurfboard(@PathVariable("surfboardId") Long surfBoardId,
                                @RequestBody Surfboard updatedSurfboard){
        surfboardService.updateSurfboard(
                surfBoardId,
                updatedSurfboard.getModel(),
                updatedSurfboard.getShaper(),
                updatedSurfboard.getLength(),
                updatedSurfboard.getWidth(),
                updatedSurfboard.getVolume(),
                updatedSurfboard.getPurchasedAt()
        );
    }

}
