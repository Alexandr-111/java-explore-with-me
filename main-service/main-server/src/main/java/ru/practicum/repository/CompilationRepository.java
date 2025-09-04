package ru.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Compilation;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    boolean existsByTitle(String title);

    @Query("SELECT c FROM Compilation c WHERE c.pinned = :pinned")
    Page<Compilation> findByPinned(@Param("pinned") Boolean pinned, Pageable pageable);
}