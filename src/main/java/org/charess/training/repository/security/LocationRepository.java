package org.charess.training.repository.security;

import org.charess.training.domain.security.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("select z from Location z where z.name like Concat('%', Concat(?1,'%')) and lower(z.nature) <> 'department'")
    List<Location> search(String criteria);
}
