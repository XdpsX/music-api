package com.xdpsx.music.repository;

import com.xdpsx.music.model.entity.Playlist;
import com.xdpsx.music.repository.criteria.PlaylistCriteriaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PlaylistRepository extends JpaRepository<Playlist, Long>, PlaylistCriteriaRepository {
    @Query("select " +
            "case when count(p) > 0 " +
            "then true " +
            "else false " +
            "end " +
            "from Playlist p where p.owner.id = :ownerId AND p.name = :name")
    boolean existsByOwnerIdAndName(@Param("ownerId") Long ownerId, @Param("name") String name);

    Optional<Playlist> findByIdAndOwnerId(Long id, Long ownerId);
}
