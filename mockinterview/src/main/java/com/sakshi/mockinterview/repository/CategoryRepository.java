package com.sakshi.mockinterview.repository;

import com.sakshi.mockinterview.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Find root categories (no parent)
    List<Category> findByParentIsNull();

    // Find subcategories by parent id
    List<Category> findByParentId(Long parentId);
}
