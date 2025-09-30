package com.sfs.repository;

import com.sfs.entities.Groups;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // <-- Required import
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends JpaRepository<Groups, Long> {

    Optional<Groups> findByName(String name);
    
    /**
     * FINAL FIX: We must provide a minimal, valid HQL query (SELECT g FROM Groups g).
     * This instructs Spring Data JPA to use the Query annotation, preventing the "No property found" error.
     * The @EntityGraph then overrides the query's fetching strategy to safely load both 'employees' and 'devices' 
     * in a single round trip, resolving the original efficiency/MultipleBagFetchException issue.
     */
    @Query("SELECT g FROM Groups g") 
    @EntityGraph(attributePaths = {"employees", "devices"}) 
    List<Groups> fetchGroupsWithAllRelationships();
}