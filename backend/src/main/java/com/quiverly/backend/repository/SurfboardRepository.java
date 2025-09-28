package com.quiverly.backend.repository;

import com.quiverly.backend.model.Surfboard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurfboardRepository extends JpaRepository<Surfboard, Long> {

    //SELECT * FROM surfboards WHERE id = ?;
    Optional<Surfboard> findSurfboardById(Long id);

    //SELECT * FROM surfboards WHERE user_id = ?;
    List<Surfboard> findAllByOwnerId(Long userId);

}
