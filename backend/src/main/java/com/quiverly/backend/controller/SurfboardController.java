package com.quiverly.backend.controller;

import com.quiverly.backend.model.Surfboard;
import com.quiverly.backend.model.SurfboardImage;
import com.quiverly.backend.model.User;
import com.quiverly.backend.service.SurfboardService;
import com.quiverly.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/surfboard")
public class SurfboardController {

    private final SurfboardService surfboardService;
    private final UserService userService;

    @Autowired
    public SurfboardController(SurfboardService surfboardService, UserService userService) {
        this.surfboardService = surfboardService;
        this.userService = userService;
    }

    @GetMapping
    public List<Surfboard> getSurfboards() {
        return surfboardService.getSurfboards();
    }

    // might want to remove entirely somewhere down the road
    @GetMapping(path = "/user/{userId}")
    public List<Surfboard> getSurfboardsByUser(@PathVariable Long userId) {
        return surfboardService.getSurfboardsByUser(userId);
    }

    //                   lol this is called kebab-case
    @GetMapping(path = "/my-boards")
    public List<Surfboard> getSurfboardsByUser(Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        return surfboardService.getSurfboardsByUser(user.getId());
    }

    @PostMapping
    public Surfboard addSurfboard(@Valid @RequestBody Surfboard surfboard, Principal principal) {
        User user = userService.getUserByUsername(principal.getName());
        surfboard.setOwner(user);
        return surfboardService.addNewSurfboard(surfboard);
    }

    @PostMapping(path = "/{surfboardId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    // expects a multipart data to be received
    public ResponseEntity<SurfboardImage> uploadImage(
            @PathVariable Long surfboardId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "isCover", defaultValue = "false") boolean isCover,
            Principal principal
    ) {
        // tell the SurfboardService to take care of image addition
        SurfboardImage image = surfboardService.addImageToSurfboard(surfboardId, file, isCover, principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(image); // 201 response
    }

    @DeleteMapping(path = "{surfboardId}")
    public void deleteSurfboard(@PathVariable Long surfboardId) {
        surfboardService.deleteSurfboard(surfboardId);
    }

    @PutMapping(path = "{surfboardId}")
    public void updateSurfboard(@PathVariable("surfboardId") Long surfBoardId,
                                @RequestBody Surfboard updatedSurfboard) {
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

