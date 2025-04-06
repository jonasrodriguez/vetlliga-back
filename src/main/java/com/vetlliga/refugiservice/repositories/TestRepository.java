package com.vetlliga.refugiservice.repositories;

import com.vetlliga.refugiservice.entities.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TestRepository extends JpaRepository<Test, Integer> {

}
