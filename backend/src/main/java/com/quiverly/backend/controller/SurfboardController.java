package com.quiverly.backend.controller;

import com.quiverly.backend.model.Surfboard;
import com.quiverly.backend.model.User;
import com.quiverly.backend.service.SurfboardService;
import com.quiverly.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/surfboard")
public class SurfboardController {

    private final SurfboardService surfboardService;
    private final UserService userService;

    @Autowired
    public SurfboardController(SurfboardService surfboardService, UserService userService){
        this.surfboardService = surfboardService;
        this.userService = userService;
    }

    @GetMapping
    public List<Surfboard> getSurfboards(){
        return surfboardService.getSurfboards();
    }

    @GetMapping(path="/user/{userId}")
    public List<Surfboard> getSurfboardsByUser(@PathVariable Long userId){
        return surfboardService.getSurfboardsByUser(userId);
    }

    @PostMapping
    public void addSurfboard(@Valid @RequestBody Surfboard surfboard, Principal principal){
        User user = userService.getUserByUsername(principal.getName());
        surfboard.setOwner(user);
        surfboardService.addNewSurfboard(surfboard);
    }

    @DeleteMapping(path="{surfboardId}")
    public void deleteSurfboard(@PathVariable Long surfboardId){
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
