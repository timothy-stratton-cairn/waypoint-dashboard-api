package com.cairn.waypoint.dashboard.repository;

import com.cairn.waypoint.dashboard.entity.HomeworkCategory;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HomeworkCategoryRepository extends JpaRepository<HomeworkCategory, Long> {

  Optional<HomeworkCategory> findById(Long id);

  Optional<HomeworkCategory> findByName(String name);
}
