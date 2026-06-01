package com.haeun.repository;

import com.haeun.entity.HaeunMemory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HaeunMemoryRepository extends JpaRepository<HaeunMemory, Long> {
    List<HaeunMemory> findAllByOrderByCreatedAtDesc();
    List<HaeunMemory> findByTagIgnoreCase(String tag);
}
